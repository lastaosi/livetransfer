package com.jh.livetransfer.domain.usecase

import com.jh.livetransfer.domain.repository.TranslationRepository
import javax.inject.Inject

/**
 * 실시간 번역 UseCase. [STUB]
 *
 * 현재 invoke 전체가 주석 처리되어 있으며 실질적으로 미사용.
 * ViewModel이 Repository를 직접 호출하는 구조로 운영 중.
 *
 * 추후 UseCase 레이어를 활성화할 경우:
 * - 언어 감지 → 번역 방향 결정 로직을 이 레이어로 이동
 * - ViewModel에서 Repository 직접 의존성 제거
 */
class GetRealtimeTranslationUseCase @Inject constructor(
    private val repository: TranslationRepository
) {
//    suspend operator fun invoke(audioData: ByteArray): String {
//        return repository.translateAudio(audioData,"",langB = "")
//    }
}
