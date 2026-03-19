# LiveTransfer — AI 실시간 음성 통역 앱

마이크로 말하면 AI가 실시간으로 번역하고 읽어주는 Android 앱입니다.
Firebase Vertex AI (Gemini 2.5 Flash)를 활용한 스트리밍 번역으로 자연스러운 대화형 통역을 지원합니다.

---

## 주요 기능

- **음성 실시간 번역** — 마이크 입력을 VAD(Voice Activity Detection)로 자동 감지, Gemini에 스트리밍 전송하여 결과를 채팅 UI에 실시간 출력
- **TTS 읽기** — 번역 완료 후 Android TextToSpeech로 자동 낭독, 중간 중지 버튼 제공
- **이미지 번역** — 카메라로 촬영한 이미지를 Gemini Vision으로 분석·번역
- **오디오 파형 시각화** — 녹음 중 실시간 진폭을 Canvas로 렌더링
- **언어 설정** — A/B 양방향 언어 코드 및 발화 속도를 DataStore에 영속화

---

## 기술 스택

| 분류 | 사용 기술 |
|------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture (Presentation / Domain / Data) |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| AI | Firebase Vertex AI (Gemini 2.5 Flash) |
| Audio | AudioRecord, VAD 상태 머신 (callbackFlow) |
| TTS | Android TextToSpeech |
| Storage | DataStore Preferences |
| Min SDK | 26 (Android 8.0) |

---

## 아키텍처

```
presentation (ui/)
    └── MainViewModel  ← 비즈니스 흐름 집중
domain/
    ├── TranslationRepository (interface)
    └── model: ChatMessage, SpeechSpeed, LanguageOption
data/
    ├── TranslationRepositoryImpl  ← Vertex AI 스트리밍 번역
    └── SettingsRepository         ← DataStore 언어/속도 설정
util/
    ├── VadProcessor       ← VAD 상태 머신 (callbackFlow)
    ├── AudioCaptureManager ← AudioRecord 래핑
    ├── AudioUtil          ← WAV 헤더 생성, PCM 진폭 계산
    ├── LanguageDetector   ← 정규식 기반 언어 판별
    └── TtsManager         ← TextToSpeech 래핑
```

### 핵심 데이터 흐름

```
마이크
  → AudioCaptureManager: Flow<ByteArray>
  → VadProcessor: Flow<VadEvent>
      ├── AmplitudeUpdate  →  파형 UI 갱신
      └── ChunkReady(PCM)  →  WAV 헤더 추가
            → TranslationRepository.translateAudioStream(): Flow<String>
                → 스트리밍 청크  →  채팅 UI 실시간 업데이트
                                →  TtsManager.speak()
```

---

## 빌드 방법

1. [Firebase 콘솔](https://console.firebase.google.com)에서 프로젝트 생성 후 `google-services.json`을 `app/` 디렉토리에 추가
2. Vertex AI (Gemini) API 활성화

```bash
./gradlew assembleDebug
```

> `google-services.json`은 보안상 이 저장소에 포함되어 있지 않습니다.

---

## 스크린샷

*추가 예정*

---

## License

```
Copyright 2025. All rights reserved.
```
