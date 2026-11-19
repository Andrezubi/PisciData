package ucb.piscidata.chat.presentation.audio

import androidx.compose.runtime.Composable

interface AudioRecorderController {
    val isRecording: Boolean
    fun startRecording(onError: (String) -> Unit)
    fun stopRecording(onAudioReady: (ByteArray) -> Unit, onError: (String) -> Unit)
    fun cancelRecording()
}

@Composable
expect fun rememberAudioRecorder(): AudioRecorderController
