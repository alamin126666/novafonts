package com.example.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.example.ui.theme.MyApplicationTheme

class FontNovaInputMethodService : InputMethodService() {

    // Single standalone lifecycle owner — service does NOT implement LifecycleOwner
    private val kbLifecycle = KeyboardLifecycleOwner()

    // Font selection persists across keyboard shows
    private var activeFontId by mutableStateOf("bold_sans")

    override fun onCreateInputView(): View {
        // Start lifecycle before creating view
        kbLifecycle.resume()

        return ComposeView(this).apply {
            // Tell Compose: keep composition alive until lifecycle is destroyed
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )
            // Attach lifecycle to this view so Compose can find it
            kbLifecycle.attachToView(this)

            setContent {
                MyApplicationTheme {
                    FontNovaKeyboardView(
                        activeFontId = activeFontId,
                        onFontSelected = { activeFontId = it },
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
        }
    }

    override fun onWindowShown() {
        super.onWindowShown()
        kbLifecycle.resume()
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
        kbLifecycle.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        kbLifecycle.destroy()
    }
}
