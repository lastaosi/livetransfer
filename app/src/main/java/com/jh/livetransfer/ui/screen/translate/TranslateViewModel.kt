package com.jh.livetransfer.ui.screen.translate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jh.livetransfer.domain.usecase.GetRealtimeTranslationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TranslateViewModel @Inject constructor(
    private val getRealtimeTranslationUseCase: GetRealtimeTranslationUseCase
) : ViewModel() {

    private val _translationResult = MutableStateFlow<String?>(null)
    val translationResult: StateFlow<String?> = _translationResult.asStateFlow()

//    fun startTranslation(audioData: ByteArray) {
//        viewModelScope.launch {
//            // Flow.collect 대신 suspend 호출의 결과를 직접 할당합니다.
//            val result = getRealtimeTranslationUseCase(audioData)
//            _translationResult.value = result
//        }
//    }
}
