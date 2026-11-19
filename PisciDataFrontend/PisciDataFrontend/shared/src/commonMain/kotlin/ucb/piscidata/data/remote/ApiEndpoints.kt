package ucb.piscidata.data.remote

object ApiEndpoints {
    // Usa la URL remota si está configurada en RemoteBackendConfig; si no, mantiene 127.0.0.1:5007
    val BASE_URL: String
        get() = RemoteBackendConfig.activeBaseUrl

    object Auth {
        val LOGIN: String get() = "$BASE_URL/api/users/login"
        val REGISTER: String get() = "$BASE_URL/api/users"
    }

    object Farms {
        val MY_FARMS: String get() = "$BASE_URL/api/Farms/my"
    }

    object AiChat {
        val CONVERSATIONS: String get() = "$BASE_URL/api/AiConversations"
        fun messages(conversationId: Int) = "$CONVERSATIONS/$conversationId/messages"
        val TRANSCRIBE: String get() = "$CONVERSATIONS/transcribe"
    }

    object Tasks {
        fun byFarm(farmId: Int) = "$BASE_URL/api/tasks/farm/$farmId"
        val BASE: String get() = "$BASE_URL/api/tasks"
        fun toggle(taskId: Long) = "$BASE_URL/api/tasks/$taskId/toggle"
    }

    object Reports {
        fun byFarm(farmId: Int) = "$BASE_URL/api/reports/farm/$farmId"
    }
}
