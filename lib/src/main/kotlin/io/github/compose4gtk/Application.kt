package io.github.compose4gtk

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MonotonicFrameClock
import androidx.compose.runtime.Recomposer
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import org.gnome.gtk.Application
import org.gnome.gtk.Window
import kotlin.system.exitProcess

private class GtkApplicationComposeNode : GtkComposeNode {
    override fun addNode(index: Int, child: GtkComposeNode) {
        if (child !is GtkComposeWidget<*> || child.widget !is Window) {
            throw UnsupportedOperationException("Only windows can be attached to Application")
        }
    }

    override fun removeNode(index: Int) = Unit

    override fun clearNodes() = Unit
}

@Stable
interface ApplicationScope {
    val application: Application
    fun exitApplication()
}

val LocalApplication = staticCompositionLocalOf<Application> { throw IllegalStateException("not in a GTK application") }

internal fun Application.initializeApplication(
    args: Array<String>,
    content: @Composable ApplicationScope.() -> Unit,
) {
    val app = this
    val dispatcher = GtkDispatcher
    val handler = CoroutineExceptionHandler { _, exception ->
        println("CoroutineExceptionHandler got $exception")
    }

    runBlocking(dispatcher) {
        withContext(handler + YieldFrameClock) {
            startSnapshotManager()

            val recomposer = Recomposer(coroutineContext)
            var isOpen by mutableStateOf(true)

            val appScope = object : ApplicationScope {
                override val application = app
                override fun exitApplication() {
                    isOpen = false
                }
            }

            launch {
                recomposer.runRecomposeAndApplyChanges()
            }

            val composition = Composition(GtkApplier(GtkApplicationComposeNode()), recomposer)
            app.onActivate {
                GtkDispatcher.active = true
                composition.setContent {
                    if (isOpen) {
                        CompositionLocalProvider(LocalApplication provides app) {
                            appScope.content()
                        }
                    }
                }
            }
            val status = app.run(args)
            GtkDispatcher.active = false
            try {
                recomposer.close()
                recomposer.join()
            } finally {
                composition.dispose()
            }
            exitProcess(status)
        }
    }
}

private fun CoroutineScope.startSnapshotManager() {
    val channel = Channel<Unit>(Channel.CONFLATED)
    launch {
        channel.consumeEach {
            Snapshot.sendApplyNotifications()
        }
    }
    Snapshot.registerGlobalWriteObserver {
        val channelResult = channel.trySend(Unit)
        check(channelResult.isSuccess) {
            channelResult.exceptionOrNull()?.printStackTrace()
            "Channel result = $channelResult"
        }
    }
}

private object YieldFrameClock : MonotonicFrameClock {
    override suspend fun <R> withFrameNanos(
        onFrame: (frameTimeNanos: Long) -> R,
    ): R {
        yield()
        return onFrame(System.nanoTime())
    }
}
