package ucb.piscidata.data.remote

object ApiEndpoints {
    // Punto único de configuración para la URL base del backend
    // Usar "http://127.0.0.1:5007" para Desktop/iOS
    // Usar "http://10.0.2.2:5007" para Android Emulator
    const val BASE_URL = "http://127.0.0.1:5007"

    object Auth {
        const val LOGIN = "$BASE_URL/api/users/login"
        const val REGISTER = "$BASE_URL/api/users"
    }

    object Farms {
        const val MY_FARMS = "$BASE_URL/api/Farms/my"
    }

    object AiChat {
        const val CONVERSATIONS = "$BASE_URL/api/AiConversations"
        fun messages(conversationId: Int) = "$CONVERSATIONS/$conversationId/messages"
    }
}
