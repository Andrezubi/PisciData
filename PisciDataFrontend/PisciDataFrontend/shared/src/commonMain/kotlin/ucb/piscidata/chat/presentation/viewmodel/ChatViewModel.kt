package ucb.piscidata.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import ucb.piscidata.chat.domain.repository.ChatRepository

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<ChatEffect>()
    val effect = _effect.asSharedFlow()

    init {
        loadMessages()
    }

    fun emitEvent(event: ChatEvent) {
        when (event) {
            ChatEvent.OnLoadMessages -> loadMessages()
            is ChatEvent.OnInputChanged -> _state.update { it.copy(inputMessage = event.text) }
            ChatEvent.OnSendText -> sendMessage()
            ChatEvent.OnStartRecording -> _state.update { it.copy(isRecording = true, recordSeconds = 0) }
            is ChatEvent.OnStopRecording -> sendAudio(event.duration)
            ChatEvent.OnConfirmAction -> confirmAction()
        }
    }

    private fun loadMessages() {
        viewModelScope.launch {
            chatRepository.getInitialMessages().onSuccess { msgs ->
                _state.update { it.copy(messages = msgs) }
            }
        }
    }

    private fun sendMessage() {
        val text = _state.value.inputMessage.trim()
        if (text.isEmpty()) return
        _state.update { it.copy(inputMessage = "") }
        viewModelScope.launch {
            chatRepository.sendMessage(text).onSuccess { newMsgs ->
                _state.update { it.copy(messages = it.messages + newMsgs) }
            }
        }
    }

    private fun sendAudio(duration: Int) {
        _state.update { it.copy(isRecording = false, recordSeconds = 0) }
        viewModelScope.launch {
            chatRepository.sendAudio(duration).onSuccess { newMsgs ->
                _state.update { it.copy(messages = it.messages + newMsgs) }
            }
        }
    }

    private fun confirmAction() {
        viewModelScope.launch {
            _effect.emit(ChatEffect.ShowToast("Ciclo productivo creado exitosamente"))
            chatRepository.sendMessage("¡Ciclo confirmado y creado con éxito!").onSuccess { newMsgs ->
                _state.update { it.copy(messages = it.messages + newMsgs) }
            }
        }
    }
}
