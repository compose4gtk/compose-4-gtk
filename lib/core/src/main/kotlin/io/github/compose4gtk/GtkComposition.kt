package io.github.compose4gtk

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext

@Composable
fun <T : GtkComposeNode> gtkSubComposition(
    createNode: () -> T,
    content: @Composable (T) -> Unit,
): T {
    val compositionContext = rememberCompositionContext()
    val node = remember { createNode() }
    val composition = remember {
        Composition(
            GtkApplier(node),
            compositionContext,
        )
    }
    remember(content) {
        composition.setContent {
            content(node)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            composition.dispose()
        }
    }
    return node
}
