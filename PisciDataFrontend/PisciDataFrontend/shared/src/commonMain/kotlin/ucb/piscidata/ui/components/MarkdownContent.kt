
package ucb.piscidata.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mikepenz.markdown.m3.Markdown

import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
fun MarkdownContent(
    content: String,
    modifier: Modifier = Modifier
) {
    val markdownState = rememberMarkdownState(
        content,
        retainState = true
    )

    Markdown(
        markdownState = markdownState,
        modifier = modifier
    )
}
