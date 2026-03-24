package com.jh.livetransfer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * 앱 진입점. @HiltAndroidApp이 Hilt DI 컴포넌트 계층을 초기화한다.
 * 별도 onCreate 로직 없이 Hilt 트리거 역할만 담당.
 */
@HiltAndroidApp
class LiveTransferApplication : Application()
