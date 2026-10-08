package ucb.piscidata

import androidx.compose.ui.window.ComposeUIViewController
import ucb.piscidata.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initKoin() }
) { App() }
