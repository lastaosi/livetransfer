# LiveTransfer — AI 실시간 음성 통역 앱

마이크로 말하면 AI가 실시간으로 번역·낭독하는 Android 앱입니다.
Firebase Vertex AI (Gemini 2.5 Flash) 스트리밍 번역, 날씨 조회, 환율 계산 기능을 탑재한 멀티탭 앱입니다.

---

## 주요 기능

### 번역 탭
- **음성 실시간 번역** — VAD(Voice Activity Detection)로 발화 구간을 자동 감지하여 Gemini에 스트리밍 전송, 채팅 UI에 실시간 출력
- **TTS 낭독** — 번역 완료 후 Android TextToSpeech로 자동 낭독, 중간 정지 지원
- **오디오 파형 시각화** — 녹음 중 실시간 진폭을 Canvas로 렌더링
- **양방향 언어 설정** — A/B 언어 코드·발화 속도 DataStore 영속화

### 날씨 탭
- **현재 위치 날씨** — GPS(FusedLocationProvider)로 자동 조회, OpenWeatherMap API 연동
- **도시 추가/삭제** — 도시명 검색으로 원하는 도시 날씨 목록 관리 (중복 체크 포함)
- **한국어 날씨 설명** — `lang=ko` 파라미터로 응답 현지화

### 환율 탭
- **실시간 환율 계산** — Frankfurter API를 통해 최신 환율 조회
- **통화 드롭다운** — USD, KRW, JPY, EUR, GBP, CNY, HKD, SGD, CHF 지원
- **금액 즉시 환산** — 기준/대상 통화 또는 금액 변경 시 자동 재계산

---

## 기술 스택

| 분류 | 사용 기술 |
|------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture (Presentation / Domain / Data) |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Navigation | Navigation Compose (Bottom Tab + 중첩 NavHost) |
| AI | Firebase Vertex AI (Gemini 2.5 Flash) |
| Network | Ktor Client + kotlinx.serialization |
| Audio | AudioRecord, VAD 상태 머신 (callbackFlow) |
| TTS | Android TextToSpeech |
| Location | FusedLocationProviderClient (Play Services) |
| Storage | DataStore Preferences |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 34 (Android 14) |

---

## 아키텍처

Single-Activity + Jetpack Compose 기반. 하단 탭 3개(번역 / 날씨 / 환율)로 구성되며 각 탭은 내부에 별도 중첩 NavHost를 갖는다.

```
presentation (ui/)
    ├── translation/
    │   ├── TranslationScreen      ← 채팅 UI + 파형 + 녹음 버튼
    │   ├── TranslationViewModel   ← VAD → Gemini 스트리밍 → TTS 흐름 조율
    │   └── TranslationNavigation  ← 번역 ↔ 설정 중첩 네비게이션
    ├── weather/
    │   ├── WeatherMainScreen      ← 현재 위치 + 도시 목록 날씨 표시
    │   ├── WeatherSettingScreen   ← 도시 추가 UI
    │   ├── WeatherViewModel       ← 위치·도시 날씨 상태 관리
    │   └── WeatherNavigation      ← weather_main ↔ weather_settings
    ├── exchangerate/
    │   ├── ExchangeRateScreen     ← 통화 드롭다운 + 금액 입력 + 환산 결과
    │   └── ExchangeViewModel      ← 환율 조회 및 환산 계산
    └── SettingsScreen             ← 번역 언어·속도 설정

domain/
    ├── TranslationRepository (interface)
    └── model: ChatMessage, SpeechSpeed, LanguageOption, TranslationResult

data/
    ├── TranslationRepositoryImpl  ← Vertex AI(Gemini) WAV 스트리밍 번역
    ├── SettingsRepository         ← DataStore 언어/속도 설정 영속화
    ├── WeatherRepository          ← OpenWeatherMap API (Ktor)
    ├── ExchangeRepository         ← Frankfurter API (Ktor)
    └── model: WeatherResponse, ExchangeResponse

util/
    ├── VadProcessor       ← VAD 상태 머신, callbackFlow<VadEvent> 반환
    ├── AudioCaptureManager ← AudioRecord 래핑, Flow<ByteArray> 반환
    ├── AudioUtil          ← WAV 헤더 생성, PCM 진폭 계산
    ├── LanguageDetector   ← 정규식 기반 언어 판별 (object)
    ├── TtsManager         ← TextToSpeech 래핑
    └── LocationUtil       ← FusedLocationProvider 래핑
```

### 핵심 데이터 흐름 (번역)

```
마이크
  → AudioCaptureManager: Flow<ByteArray>
  → VadProcessor: Flow<VadEvent>
      ├── AmplitudeUpdate  →  파형 Canvas 갱신
      └── ChunkReady(PCM)  →  WAV 헤더 추가
            → TranslationRepository.translateAudioStream(): Flow<String>
                → 스트리밍 청크  →  채팅 UI 실시간 업데이트
                                →  TtsManager.speak()
```

### 환율 데이터 흐름

```
통화/금액 변경 (onBaseCurrencyChanged / onAmountChanged)
  → ExchangeRepository.getExchangeRates(base): Result<ExchangeResponse>
      → calculateResult()  →  convertedAmount StateFlow 갱신  →  UI 반영
```

---

## 빌드 방법

1. [Firebase 콘솔](https://console.firebase.google.com)에서 프로젝트 생성 후 `google-services.json`을 `app/` 디렉토리에 추가
2. Vertex AI (Gemini) API 활성화

```bash
# 디버그 빌드
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease

# 린트
./gradlew lint
```

> `google-services.json`은 보안상 이 저장소에 포함되어 있지 않습니다.

---

## 미완성 / 알려진 이슈

| 항목 | 상태 |
|------|------|
| `GetRealtimeTranslationUseCase` | invoke 전체 주석 처리, 미사용 |
| `VertexAiDataSource` | 하드코딩 fake 데이터, DI 미연결 |
| `TranslationRepository.translateAudio()` | 정의만 있고 미호출 (스트리밍 버전만 사용) |
| 날씨·환율 도시 목록 영속화 | 현재 메모리에만 보관, 앱 재시작 시 초기화 |
| TTS 언어 감지 | 정규식 재판별 중 — processVadChunk 결정 langCode 활용 필요 |

---

## 스크린샷

*추가 예정*

---

## License

```
Copyright 2025. All rights reserved.
```
