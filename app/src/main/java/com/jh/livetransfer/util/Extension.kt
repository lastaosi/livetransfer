package com.jh.livetransfer.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

// Int용 (예 : 16.nonScaledSp)
val Int.nonScaledSp : TextUnit
    @Composable
    get() = (this / LocalDensity.current.fontScale).sp

// Float용 ( 예 : 16.5f.nonScaledSp)
val Float.nonScaledSp: TextUnit
    @Composable
    get() = (this/LocalDensity.current.fontScale).sp