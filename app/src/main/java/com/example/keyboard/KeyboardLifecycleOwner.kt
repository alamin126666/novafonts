package com.example.keyboard

import android.view.View
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

/**
 * A standalone LifecycleOwner for use inside InputMethodService.
 * The IME service itself should NOT implement LifecycleOwner because
 * the service lifecycle doesn't map cleanly to the Compose lifecycle.
 * This class provides a clean, independent lifecycle for the keyboard view.
 */
class KeyboardLifecycleOwner : LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val registry = LifecycleRegistry(this)
    private val vmStore = ViewModelStore()
    private val savedStateController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = registry
    override val viewModelStore: ViewModelStore get() = vmStore
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateController.savedStateRegistry

    init {
        savedStateController.performRestore(null)
        registry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    /** Call once the keyboard window and view are ready. */
    fun resume() {
        if (!registry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            registry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        }
        if (!registry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            registry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        }
    }

    /** Call when keyboard window is hidden. */
    fun pause() {
        if (registry.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            registry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        }
        if (registry.currentState.isAtLeast(Lifecycle.State.STARTED)) {
            registry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        }
    }

    /** Call when the IME service is destroyed. */
    fun destroy() {
        if (registry.currentState != Lifecycle.State.DESTROYED) {
            registry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        }
        vmStore.clear()
    }

    /**
     * Set this as the LifecycleOwner for a view so Compose can find it
     * by traversing up the view tree.
     */
    fun attachToView(view: View) {
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(this)
    }
}
