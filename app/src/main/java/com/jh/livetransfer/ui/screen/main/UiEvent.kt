package com.jh.livetransfer.ui.screen.main

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
}
