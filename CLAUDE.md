# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

---

## 응답 스타일 (Response Style)

- **언어:** 모든 답변은 **한국어**로 작성한다.
- **용어 병기:** 주요 IT 기술 용어는 영어 원어를 병기한다. (예: 리팩토링(Refactoring), 의존성 주입(Dependency Injection))
- **간결성:** 장황한 설명 없이 핵심 위주로 답변한다.
- **코드 우선:** 가독성을 최우선으로 하며, 불필요한 보일러플레이트는 생략한다.
- **눈높이:** 17년 차 안드로이드 개발자 대상 — 초보적 설명 없이 아키텍처 관점의 조언을 선호한다.

---

## 빌드 및 실행 (Build & Run)

```bash
# 디버그 빌드
./gradlew assembleDebug

# 릴리즈 빌드 (현재 minifyEnabled = false)
./gradlew assembleRelease

# 린트
./gradlew lint

# 테스트 (현재 테스트 코드 미작성 상태)
./gradlew test
./gradlew connectedAndroidTest
```

**Firebase 연동 필수:** `google-services.json`이 `app/` 디렉토리에 있어야 빌드 가능하다. Vertex AI는 Firebase 프로젝트에 연결된 Gemini 모델을 사용한다.

---

## 아키텍처 (Architecture)

Clean Architecture 3계층 구조. 단일 Activity (`MainActivity`) + Jetpack Compose 기반.

```
presentation (ui/)
    └── MainViewModel  ← 앱의 핵심 두뇌. 대부분의 비즈니스 흐름이 여기에 집중됨.
domain/
    ├── TranslationRepository (interface)
    └── model: ChatMessage, SpeechSpeed, LanguageOption, TranslationResult
data/
    ├── TranslationRepositoryImpl  ← Vertex AI(Gemini) 호출, WAV 스트리밍 번역
    └── SettingsRepository         ← DataStore 기반 언어/속도 설정 영속화
util/
    ├── VadProcessor       ← VAD 상태 머신. callbackFlow<VadEvent> 반환
    ├── AudioCaptureManager ← AudioRecord 래핑. flow<ByteArray> 반환
    ├── LanguageDetector   ← 정규식 기반 언어 판별 (object)
    ├── AudioUtil          ← WAV 헤더 생성, PCM 진폭 계산 (object)
    └── TtsManager         ← Android TextToSpeech 래핑
```

### 핵심 데이터 흐름 (Core Data Flow)

```
마이크 → AudioCaptureManager.startRecording(): Flow<ByteArray>
       → VadProcessor.startCapture(): Flow<VadEvent>
            ├── VadEvent.AmplitudeUpdate → _audioAmplitudes (파형 UI)
            └── VadEvent.ChunkReady(pcmData) → processVadChunk()
                    └── AudioUtil.addWavHeader()
                        → TranslationRepository.translateAudioStream(): Flow<String>
                            → 스트리밍 청크 → _chatMessages 실시간 업데이트
                                → 세션 누적 → TtsManager.speak()
```

### VAD(Voice Activity Detection) 설계

`VadProcessor`는 `callbackFlow` 내부에서 `silenceJob = launch { delay(...) }` 패턴으로 침묵 타이머를 관리한다. 녹음 정지 시 남은 버퍼를 마지막 `ChunkReady`로 자동 플러시한다.

- `vadProcessor.stopRecording()` 호출 → `AudioCaptureManager.isRecording = false` → 내부 `while` 루프 종료 → Flow 자연 완료 → `awaitClose` 클린업
- `AudioCaptureManager.stopRecording()`은 `@Synchronized` + `recordingState` 체크로 멀티스레드 이중 호출을 방어한다.

### DI 구성 (Dependency Injection)

| 모듈 | 역할 |
|------|------|
| `AppModule` | `GenerativeModel` (Gemini 2.5 Flash) 싱글톤 제공 |
| `RepositoryModule` | `TranslationRepository` ← `TranslationRepositoryImpl` 바인딩 |
| `UseCaseModule` | (현재 미사용) |

`VadProcessor`, `AudioCaptureManager`, `TtsManager`, `SettingsRepository`는 `@Singleton @Inject constructor`로 자동 제공된다.

### 설정 구독 패턴

`MainViewModel.init`에서 DataStore의 5개 Flow를 `combine`으로 단일 구독한다:
```kotlin
combine(langAName, langBName, langACode, langBCode, speechSpeedDelay) { ... }
    .collect { settings -> /* 내부 상태 일괄 업데이트 */ }
```

---

## 미완성 영역 (Incomplete Areas)

- **`TranslateScreen` / `TranslateViewModel`:** 핵심 로직 전부 주석 처리된 stub 상태
- **`GetRealtimeTranslationUseCase`:** invoke 전체 주석 처리, 실질적으로 미사용
- **`VertexAiDataSource`:** 하드코딩된 fake 데이터, DI에 연결되지 않음
- **`TranslationRepository.translateAudio()`:** 정의만 있고 호출 없음 (스트리밍 버전만 사용)

## 알려진 기술 부채 (Known Tech Debt)

1. ~~**Race condition:** `sessionTranslatedText`가 여러 코루틴에서 동시 접근~~ ✅ 해결
   - `var sessionTranslatedText` → `MutableStateFlow<String>` + `update {}` (CAS 원자적 append)
   - `processVadChunk` 코루틴을 `chunkScope` (`SupervisorJob`)으로 추적, `delay(500)` → `children.forEach { it.join() }`
   - `_chatMessages` 스트리밍 업데이트를 `messageIndex` 고정으로 타 청크 덮어쓰기 방지
2. ~~**에러 처리:** 번역 에러가 일반 `String`으로 emit되어 UI가 에러/결과를 구분 불가. `UiEvent(SharedFlow)` 미구현~~ ✅ 해결
   - Repository try-catch 제거 → 예외를 caller(ViewModel)로 자연 전파
   - `MutableSharedFlow<UiEvent>(extraBufferCapacity = 1)` 추가, catch 블록에서 `tryEmit`
   - `MainScreen` `LaunchedEffect`로 수집 → Toast 표시
3. **TTS 언어 감지:** `stopAudioCapture`의 TTS 발화 언어를 정규식으로 재판별 — `processVadChunk`에서 이미 결정된 `resultLangCode` 활용이 필요
