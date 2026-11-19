package ucb.piscidata.chat.presentation.audio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberAudioRecorder(): AudioRecorderController {
    return remember {
        object : AudioRecorderController {
            override val isRecording: Boolean = false
            override fun startRecording(onError: (String) -> Unit) {
                onError("Grabación de audio no implementada en iOS.")
            }
            override fun stopRecording(onAudioReady: (ByteArray) -> Unit, onError: (String) -> Unit) {}
            override fun cancelRecording() {}
        }
    }
}
