package com.jh.livetransfer.ui.screen.translation

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}
