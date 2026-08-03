package com.example.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.ui.theme.MyApplicationTheme

class FontNovaInputMethodService : InputMethodService(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    override val viewModelStore = ViewModelStore()
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private var activeFontId by mutableStateOf("bold_sans")

    // FIX 1: Cache ComposeView — never recreate it, reuse same instance
    private var cachedView: ComposeView? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        // FIX 2: Start full lifecycle in onCreate — simpler, no race conditions
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onCreateInputView(): View {
        // FIX 3: Return cached view — do NOT create new ComposeView each time
        cachedView?.let { return it }

        return ComposeView(this).also { view ->
            // FIX 4: Strategy — only dispose when service is destroyed
            view.setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            // Set tree owners on the view itself
            view.setViewTreeLifecycleOwner(this)
            view.setViewTreeViewModelStoreOwner(this)
            view.setViewTreeSavedStateRegistryOwner(this)

            view.setContent {
                MyApplicationTheme {
                    FontNovaKeyboardView(
                        activeFontId = activeFontId,
                        onFontSelected = { id -> activeFontId = id },
                        onKeyTyped = { text ->
                            currentInputConnection?.commitText(text, 1)
                        },
                        onBackspace = {
                            currentInputConnection?.deleteSurroundingText(1, 0)
                        },
                        onEnter = {
                            currentInputConnection?.let { ic ->
                                ic.sendKeyEvent(
                                    android.view.KeyEvent(
                                        android.view.KeyEvent.ACTION_DOWN,
                                        android.view.KeyEvent.KEYCODE_ENTER
                                    )
                                )
                                ic.sendKeyEvent(
                                    android.view.KeyEvent(
                                        android.view.KeyEvent.ACTION_UP,
                                        android.view.KeyEvent.KEYCODE_ENTER
                                    )
                                )
                            }
                        },
                        onSpace = {
                            currentInputConnection?.commitText(" ", 1)
                        },
                        onCursorLeft = {
                            currentInputConnection?.sendKeyEvent(
                                android.view.KeyEvent(
                                    android.view.KeyEvent.ACTION_DOWN,
                                    android.view.KeyEvent.KEYCODE_DPAD_LEFT
                                )
                            )
                        },
                        onCursorRight = {
                            currentInputConnection?.sendKeyEvent(
                                android.view.KeyEvent(
                                    android.view.KeyEvent.ACTION_DOWN,
                                    android.view.KeyEvent.KEYCODE_DPAD_RIGHT
                                )
                            )
                        }
                    )
                }
            }

            cachedView = view
        }
    }

    override fun onWindowShown() {
        super.onWindowShown()
        // FIX 5: Set tree owners on the IME window's decor view too
        // This ensures Compose can always find LifecycleOwner up the view tree
        try {
            window?.window?.decorView?.let { decor ->
                decor.setViewTreeLifecycleOwner(this)
                decor.setViewTreeViewModelStoreOwner(this)
                decor.setViewTreeSavedStateRegistryOwner(this)
            }
        } catch (_: Exception) { /* ignore if window not ready */ }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        cachedView = null
    }
}
