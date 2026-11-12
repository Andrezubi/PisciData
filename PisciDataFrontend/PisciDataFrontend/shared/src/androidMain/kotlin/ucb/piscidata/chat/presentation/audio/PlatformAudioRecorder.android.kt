package ucb.piscidata.chat.presentation.audio

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Composable
actual fun rememberAudioRecorder(): AudioRecorderController {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    var permissionCallback by remember { mutableStateOf<((Boolean) -> Unit)?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionCallback?.invoke(granted)
        permissionCallback = null
    }

    val controller = remember(context) {
        AndroidAudioRecorderController(
            context = context,
            hasPermissionProvider = { hasPermission },
            requestPermission = { callback ->
                permissionCallback = callback
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        )
    }

    DisposableEffect(Unit) {
        onDispose {
            controller.cancelRecording()
        }
    }

    return controller
}

private class AndroidAudioRecorderController(
    private val context: Context,
    private val hasPermissionProvider: () -> Boolean,
    private val requestPermission: ((Boolean) -> Unit) -> Unit
) : AudioRecorderController {

    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    @Volatile
    private var recording = false
    override val isRecording: Boolean
        get() = recording

    private var audioRecord: AudioRecord? = null
    private var recordingThread: Thread? = null
    private var outputStream: ByteArrayOutputStream? = null

    @SuppressLint("MissingPermission")
    override fun startRecording(onError: (String) -> Unit) {
        if (!hasPermissionProvider()) {
            requestPermission { granted ->
                if (granted) {
                    startRecordingInternal(onError)
                } else {
                    onError("Se requiere permiso para usar el micrófono.")
                }
            }
            return
        }

        startRecordingInternal(onError)
    }

    @SuppressLint("MissingPermission")
    private fun startRecordingInternal(onError: (String) -> Unit) {
        if (recording) return

        try {
            val minBufSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
            val bufferSize = maxOf(minBufSize, 4096)

            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )

            if (record.state != AudioRecord.STATE_INITIALIZED) {
                record.release()
                onError("No se pudo inicializar la grabación de audio.")
                return
            }

            audioRecord = record
            val stream = ByteArrayOutputStream()
            outputStream = stream
            recording = true

            record.startRecording()

            val thread = Thread({
                val buffer = ByteArray(bufferSize)
                while (recording) {
                    val read = record.read(buffer, 0, buffer.size)
                    if (read > 0) {
                        synchronized(stream) {
                            stream.write(buffer, 0, read)
                        }
                    }
                }
            }, "PisciDataAudioRecordThread")

            recordingThread = thread
            thread.start()
        } catch (e: Exception) {
            recording = false
            audioRecord?.release()
            audioRecord = null
            onError("Error al iniciar grabación: ${e.message ?: e.toString()}")
        }
    }

    override fun stopRecording(onAudioReady: (ByteArray) -> Unit, onError: (String) -> Unit) {
        if (!recording) return
        recording = false

        try {
            recordingThread?.join(1000)
            recordingThread = null

            audioRecord?.run {
                try {
                    stop()
                } catch (_: Exception) {}
                release()
            }
            audioRecord = null

            val pcm = outputStream?.toByteArray() ?: ByteArray(0)
            outputStream = null

            // 16000 samples/sec * 2 bytes/sample = 32000 bytes/sec
            // Menos de 0.25 seg (8000 bytes) es probablemente un toque accidental
            if (pcm.size < 8000) {
                onError("Grabación muy corta. Mantén presionado mientras hablas.")
                return
            }

            val wavBytes = pcmToWav(pcm, sampleRate, 1, 16)
            onAudioReady(wavBytes)
        } catch (e: Exception) {
            onError("Error al finalizar grabación: ${e.message ?: e.toString()}")
        }
    }

    override fun cancelRecording() {
        recording = false
        try {
            recordingThread?.join(500)
            recordingThread = null
            audioRecord?.run {
                try { stop() } catch (_: Exception) {}
                release()
            }
            audioRecord = null
            outputStream = null
        } catch (_: Exception) {}
    }

    private fun pcmToWav(
        pcmData: ByteArray,
        sampleRate: Int = 16000,
        channels: Short = 1,
        bitsPerSample: Short = 16
    ): ByteArray {
        val totalDataLen = pcmData.size + 36
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val header = ByteArray(44)
        val bb = ByteBuffer.wrap(header).order(ByteOrder.LITTLE_ENDIAN)

        // RIFF header
        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        bb.putInt(4, totalDataLen)
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()

        // fmt chunk
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        bb.putInt(16, 16)
        bb.putShort(20, 1) // PCM
        bb.putShort(22, channels)
        bb.putInt(24, sampleRate)
        bb.putInt(28, byteRate)
        bb.putShort(32, (channels * bitsPerSample / 8).toShort())
        bb.putShort(34, bitsPerSample)

        // data chunk
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        bb.putInt(40, pcmData.size)

        return header + pcmData
    }
}
