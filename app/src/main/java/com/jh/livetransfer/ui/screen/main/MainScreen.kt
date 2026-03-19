package com.jh.livetransfer.ui.screen.main

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.jh.livetransfer.domain.model.ChatMessage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onSettingsClick: () -> Unit
) {
    // 1. 상태 수집 (State Hoisting)
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val isImageLoading by viewModel.isImageLoading.collectAsState()
    val audioAmplitudes by viewModel.audioAmplitudes.collectAsState()
    val isTtsSpeaking by viewModel.isTtsSpeaking.collectAsState()

    // One-shot 이벤트 수집 — SharedFlow이므로 collectAsState 대신 LaunchedEffect 사용
    val context = LocalContext.current
    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowToast ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 2. 전체 레이아웃 (Box를 최상위에 두어 로딩 화면을 덮어씌움)
    Box(modifier = Modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                MainTopBar(onSettingsClick)
            },
            floatingActionButton = {
                // 카메라 버튼 (권한 체크 및 실행 로직 포함)
                CameraFloatingActionButton(
                    onImageCaptured = { bitmap -> viewModel.translateCapturedImage(bitmap) }
                )
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 3. 채팅 리스트 (화면의 대부분 차지)
                ChatListSection(
                    messages = chatMessages,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 4. 오디오 파형 (녹음 중일 때 움직임)
                AudioVisualizerSection(amplitudes = audioAmplitudes)

                Spacer(modifier = Modifier.height(24.dp))

                // 5. 하단 컨트롤러 (마이크 & TTS 중지 버튼)
                BottomControlsSection(
                    isRecording = isRecording,
                    isTtsSpeaking = isTtsSpeaking,
                    onToggleRecording = { viewModel.toggleRecording() },
                    onStopTts = { viewModel.stopTts() }
                )
            }
        }

        // 6. 로딩 오버레이 (이미지 분석 중일 때)
        if (isImageLoading) {
            LoadingOverlay("이미지 분석 중...")
        }
    }
}

// ========================================================================
// 👇 하위 컴포넌트 구현부 (복잡도 분리)
// ========================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(onSettingsClick: () -> Unit) {
    TopAppBar(
        title = { Text("AI 실시간 통역") },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "설정")
            }
        }
    )
}

@Composable
private fun ChatListSection(
    messages: List<ChatMessage>, // 패키지 경로 주의
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    // 메시지 추가 시 자동 스크롤
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(messages) { message ->
            ChatMessageItem(message)
        }
    }
}

@Composable
private fun ChatMessageItem(message: ChatMessage) {
    // 내 메시지: 오른쪽(노랑), AI 메시지: 왼쪽(회색)
    val alignment = if (message.isMine) Alignment.CenterEnd else Alignment.CenterStart
    val backgroundColor = if (message.isMine) Color(0xFFFFF59D) else Color(0xFFE0E0E0)
    val textColor = Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = message.text,
            style = MaterialTheme.typography.bodyMedium,
            color = textColor,
            modifier = Modifier
                .widthIn(max = 280.dp) // 말풍선 최대 너비 제한
                .background(backgroundColor, shape = RoundedCornerShape(12.dp))
                .padding(12.dp)
        )
    }
}

@Composable
private fun AudioVisualizerSection(amplitudes: List<Float>) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Live Audio",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))

        // 파형 그리기
        AudioWaveform(
            amplitudes = amplitudes,
            modifier = Modifier.fillMaxWidth(),
            barColor = MaterialTheme.colorScheme.primary
        )
    }
}

/**
 * [AudioWaveform]
 * - 오디오 진폭 데이터를 받아 캔버스에 그리는 함수
 * - 중앙을 기준으로 위아래로 퍼지는 미러링 효과 적용
 */

@Composable
private fun BottomControlsSection(
    isRecording: Boolean,
    isTtsSpeaking: Boolean,
    onToggleRecording: () -> Unit,
    onStopTts: () -> Unit
) {
    val context = LocalContext.current

    // 권한 요청 런처
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val audioGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: false
        if (audioGranted) {
            onToggleRecording()
        } else {
            Toast.makeText(context, "음성 통역을 위해 마이크 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. 마이크 버튼 (메인)
        IconButton(
            onClick = {
                val hasAudio = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED

                // 카메라는 필수는 아니지만, 보통 같이 요청하면 편함. 여기선 오디오만 체크.
                if (hasAudio) {
                    onToggleRecording()
                } else {
                    permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA))
                }
            },
            modifier = Modifier
                .size(72.dp)
                .background(
                    color = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                contentDescription = "녹음",
                tint = Color.White,
                modifier = Modifier.size(36.dp)
            )
        }

        // 2. TTS 중지 버튼 (말하고 있을 때만 옆에 뿅 나타남)
        AnimatedVisibility(
            visible = isTtsSpeaking,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Row {
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(
                    onClick = onStopTts,
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.Gray, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeOff,
                        contentDescription = "TTS 중지",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraFloatingActionButton(onImageCaptured: (Bitmap) -> Unit) {
    val context = LocalContext.current

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) onImageCaptured(bitmap)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.CAMERA] == true) {
            cameraLauncher.launch(null)
        }
    }

    FloatingActionButton(
        onClick = {
            val hasCamera = ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED

            if (hasCamera) {
                cameraLauncher.launch(null)
            } else {
                permissionLauncher.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO))
            }
        },
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    ) {
        Icon(Icons.Default.CameraAlt, contentDescription = "사진 번역")
    }
}

@Composable
private fun LoadingOverlay(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(enabled = false) {},
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, color = Color.White, style = MaterialTheme.typography.bodyLarge)
        }
    }
}