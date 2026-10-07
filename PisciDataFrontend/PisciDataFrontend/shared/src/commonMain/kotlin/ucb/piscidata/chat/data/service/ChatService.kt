package ucb.piscidata.chat.data.service

import kotlinx.coroutines.delay
import ucb.piscidata.chat.data.datasource.ChatRemoteDataSource
import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.model.MessageSender

class ChatService : ChatRemoteDataSource {
    override suspend fun getInitialMessages(): Result<List<ChatMessageModel>> {
        return Result.success(
            listOf(
                ChatMessageModel(id = "1", sender = MessageSender.IA, text = "¡Hola, Carlos! Puedo ayudarte a registrar datos o consultar la información de tu piscigranja. ¿Qué deseas hacer?"),
                ChatMessageModel(id = "2", sender = MessageSender.USER, text = "Registra un nuevo ciclo productivo para el estanque 3, especie tilapia"),
                ChatMessageModel(id = "3", sender = MessageSender.IA, isConfirmCard = true)
            )
        )
    }

    override suspend fun sendMessage(text: String): Result<List<ChatMessageModel>> {
        delay(600)
        val rand = kotlin.random.Random.nextInt()
        return Result.success(
            listOf(
                ChatMessageModel(id = "user_$rand", sender = MessageSender.USER, text = text),
                ChatMessageModel(id = "ia_$rand", sender = MessageSender.IA, text = "Entendido. Procesando tu solicitud de manera inteligente...")
            )
        )
    }

    override suspend fun sendAudio(duration: Int): Result<List<ChatMessageModel>> {
        delay(800)
        val rand = kotlin.random.Random.nextInt()
        return Result.success(
            listOf(
                ChatMessageModel(id = "audio_$rand", sender = MessageSender.AUDIO, durationSeconds = duration),
                ChatMessageModel(id = "resp_$rand", sender = MessageSender.IA, text = "He recibido tu audio. ¿Deseas que lo procese como un registro de datos?")
            )
        )
    }
}
