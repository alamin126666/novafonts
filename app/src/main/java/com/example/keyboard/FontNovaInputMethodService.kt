package com.example.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import android.view.inputmethod.InputConnection
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleOwner
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
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle = androidx.lifecycle.LifecycleRegistry(this)
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_CREATE)
    }

    override fun onCreateInputView(): View {
        lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_START)
        lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_RESUME)

        val composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@FontNovaInputMethodService)
            setViewTreeViewModelStoreOwner(this@FontNovaInputMethodService)
            setViewTreeSavedStateRegistryOwner(this@FontNovaInputMethodService)

            setContent {
                MyApplicationTheme {
                    FontNovaKeyboardView(
                        activeFontId = "bold_sans",
                        onFontSelected = { /* Saved to prefs */ },
                        onKeyTyped = { text ->
                            currentInputConnection?.commitText(text, 1)
                        },
                        onBackspace = {
                            currentInputConnection?.deleteSurroundingText(1, 0)
                        },
                        onEnter = {
                            currentInputConnection?.sendKeyEvent(
                                android.view.KeyEvent(
                                    android.view.KeyEvent.ACTION_DOWN,
                                    android.view.KeyEvent.KEYCODE_ENTER
                                )
                            )
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
        }
        return composeView
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.handleLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
    }
}
