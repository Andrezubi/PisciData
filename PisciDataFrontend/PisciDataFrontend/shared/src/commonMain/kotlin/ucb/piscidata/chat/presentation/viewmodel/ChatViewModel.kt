package ucb.piscidata.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.chat.domain.model.ChatMessageModel
import ucb.piscidata.chat.domain.model.MessageSender
import ucb.piscidata.chat.domain.repository.ChatRepository

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ChatEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadConversationAndMessages()
    }

    fun emitEvent(event: ChatEvent) {
        when (event) {
            ChatEvent.OnLoadMessages -> loadConversationAndMessages()
            is ChatEvent.OnInputChanged -> _state.update { it.copy(inputMessage = event.text) }
            ChatEvent.OnSendText -> sendMessage()
            ChatEvent.OnStartRecording -> _state.update { it.copy(isRecording = true, recordSeconds = 0) }
            is ChatEvent.OnStopRecording -> sendAudio(event.duration)
            ChatEvent.OnConfirmAction -> confirmAction()
        }
    }

    private fun loadConversationAndMessages() {
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val convResult = chatRepository.getOrCreateActiveConversation()
            convResult.onSuccess { convId ->
                _state.update { it.copy(conversationId = convId) }
                chatRepository.getMessages(convId).onSuccess { msgs ->
                    val finalMessages = if (msgs.isEmpty()) {
                        listOf(
                            ChatMessageModel(
                                id = "welcome",
                                sender = MessageSender.IA,
                                text = "¡Hola! Soy tu asistente PisciData. ¿En qué puedo ayudarte hoy con tu piscigranja?"
                            )
                        )
                    } else {
                        msgs
                    }
                    _state.update { it.copy(messages = finalMessages, isLoading = false) }
                }.onFailure { err ->
                    _state.update { it.copy(isLoading = false, errorMessage = err.message) }
                    _effect.emit(ChatEffect.ShowToast(err.message ?: "Error al cargar mensajes"))
                }
            }.onFailure { err ->
                _state.update { it.copy(isLoading = false, errorMessage = err.message) }
                _effect.emit(ChatEffect.ShowToast(err.message ?: "Error al conectar con el chat"))
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputMessage.trim()
        if (text.isEmpty() || _state.value.isLoading) return

        val convId = _state.value.conversationId
        if (convId == null) {
            loadConversationAndMessages()
            return
        }

        // 1. Optimistic UI: El mensaje del usuario se muestra de inmediato en pantalla
        val userMsg = ChatMessageModel(
            id = "user_${io.ktor.util.date.getTimeMillis()}",
            sender = MessageSender.USER,
            text = text
        )
        _state.update {
            it.copy(
                inputMessage = "",
                messages = it.messages + userMsg,
                isLoading = true
            )
        }

        // 2. Llamada asíncrona al backend
        viewModelScope.launch {
            chatRepository.sendMessage(convId, text).onSuccess { aiMsg ->
                _state.update { it.copy(messages = it.messages + aiMsg, isLoading = false) }
            }.onFailure { err ->
                _state.update { it.copy(isLoading = false) }
                _effect.emit(ChatEffect.ShowToast(err.message ?: "No se pudo obtener respuesta"))
            }
        }
    }

    private fun sendAudio(duration: Int) {
        _state.update { it.copy(isRecording = false, recordSeconds = 0) }
        sendMessage()
    }

    private fun confirmAction() {
        val convId = _state.value.conversationId ?: return
        viewModelScope.launch {
            _effect.emit(ChatEffect.ShowToast("Confirmando acción con el asistente..."))
            chatRepository.sendMessage(convId, "¡Confirmado! Procede con la acción.").onSuccess { aiMsg ->
                _state.update { it.copy(messages = it.messages + aiMsg) }
            }
        }
    }
}
