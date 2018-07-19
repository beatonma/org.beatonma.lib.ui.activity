package org.beatonma.lib.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import org.beatonma.lib.ui.activity.popup.BasePopupActivity
import org.beatonma.lib.ui.activity.popup.EXTRA_CALLED_FROM_POPUP
import org.beatonma.lib.ui.activity.transition.BaseTransform
import org.beatonma.lib.util.Sdk
import org.beatonma.lib.util.kotlin.extensions.autotag
import org.beatonma.lib.util.kotlin.extensions.stringCompat
import java.io.Serializable
import java.lang.ref.WeakReference
import kotlin.reflect.KClass

/**
 * Created by Michael on 09/09/2016. Rewritten for Kotlin 14/06/2018.
 *
 * A builder to simplify initiation of non-trivial Activity instances
 * This is intended for subclasses of BaseActivity, but should work for any Activity
 */
@SuppressWarnings("unused")
class ActivityBuilder(
        context: Any,   // Any Context instance, an attached Fragment, or an attached View
        cls: Class<*>,
        var requestCode: Int? = null,
        fragment: Fragment? = null,
        var animationSource: View? = null
) {
    private val weakContext: WeakReference<Context>
    private val weakFragment: WeakReference<Fragment>?

    private val intent: Intent
    val extras = Bundle()

    val sharedViews = mutableListOf<SharedView>()

    var usePopupAnimation: Boolean = true

    var transform: Class<out BaseTransform>? = null

    constructor(context: Any, cls: KClass<*>): this(context, cls.java)

    init {
        // Get a concrete Context object from the given source object (Context, Fragment, View)
        val strongContext: Context = when (context) {
            is Context -> context
            is Fragment -> {
                context.context ?: throw IllegalArgumentException(
                        "Fragment is not attached - unable to get Context")
            }
            // If a View is given as context it is set as the animation source
            // (unless a source is already defined)
            is View -> {
                animationSource = animationSource ?: context
                context.context ?: throw IllegalArgumentException(
                        "View is not attached - unable to get Context")
            }
            else -> throw IllegalArgumentException(
                    "context must be a Context instance, or an attached Fragment or View")
        }
        weakContext = WeakReference(strongContext)

        // Try to get a concrete Fragment object from either context or fragment parameters.
        val strongFragment = fragment ?: context as? Fragment
        weakFragment = if (strongFragment == null) null else WeakReference(strongFragment)

        intent = Intent(strongContext, cls)
    }

    fun addSharedView(view: View?, transitionName: String) {
        view ?: return
        sharedViews.add(SharedView(view, transitionName))
    }

    fun addSharedView(view: View?, @StringRes transitionNameResId: Int) {
        view ?: return
        weakContext.get()?.stringCompat(transitionNameResId)?.let {
            sharedViews.add(SharedView(view, it))
        }
    }

    fun addSharedViews(vararg sharedViews: SharedView) {
        this.sharedViews.addAll(sharedViews)
    }

    fun putExtra(key: String, value: Any) {
        when (value) {
            is Boolean -> extras.putBoolean(key, value)
            is Bundle -> extras.putBundle(key, value)
            is Int -> extras.putInt(key, value)
            is Float -> extras.putFloat(key, value)
            is Long -> extras.putLong(key, value)
            is String -> extras.putString(key, value)
            is Serializable -> extras.putSerializable(key, value)
        }
    }

    fun putExtras(bundle: Bundle) {
        extras.putAll(bundle)
    }

    fun putExtras(intent: Intent) {
        extras.putAll(intent.extras)
    }

    /**
     * Convenience for builder.apply {
     *     ...
     *     start()
     * }
     */
    inline fun startWith(block: (ActivityBuilder.() -> Unit)) {
        this.block()
        start()
    }

    fun start() {
        intent.putExtras(extras)
        val context = weakContext.get()

        if (context is BaseActivity) {
            startWithBaseActivityContext(context)
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context?.startActivity(intent)
        }
    }

    private fun startWithBaseActivityContext(activity: BaseActivity) {
        if (usePopupAnimation) {
            activity.sharedViews?.let { addSharedViews(*it) }
            if (activity is BasePopupActivity) {
                intent.putExtra(EXTRA_CALLED_FROM_POPUP, true)
            }
        }

        if (animationSource != null) {
            if (Sdk.isKitkat) addTransitionExtras()
            if (sharedViews.isEmpty()) {
                Log.w(autotag,
                        "An animation source has been defined but there are no shared views.")
            }
        }

        val sharedViewsBundle = createSharedViewsBundle(activity)
        val fragment = weakFragment?.get()
        if (fragment != null) fragment.startActivity(intent, requestCode, sharedViewsBundle)
        else activity.startActivity(intent, requestCode, sharedViewsBundle)
    }

    private fun createSharedViewsBundle(activity: BaseActivity): Bundle? {
        val sharedViews = Array(this.sharedViews.size) { sharedViews[it].asPair }

        return ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity, *sharedViews).toBundle()
    }

    private fun addTransitionExtras() {
        transform?.let { t ->
            try {
                t.getMethod(
                        "addExtras", Intent::class.java, View::class.java)
                        ?.invoke(null, intent, animationSource)
            } catch (e: Exception) {
                // This doesn't matter too much - just an animation error
                Log.d(autotag, "addTransitionExtras() failed: $e")
            }
        }
        BaseTransform.addExtras(intent, animationSource)
    }
}

data class SharedView(val view: View, val transitionName: String) {
    val asPair: Pair<View, String> by lazy { Pair(view, transitionName) }
}

private fun Fragment.startActivity(
        intent: Intent,
        requestCode: Int? = null,
        optionsCompatBundle: Bundle? = null
) {
    if (requestCode != null) {
        if (Sdk.isLollipop) startActivityForResult(intent, requestCode, optionsCompatBundle)
        else startActivityForResult(intent, requestCode)
    } else {
        if (Sdk.isLollipop) startActivity(intent, optionsCompatBundle)
        else startActivity(intent)
    }
}

private fun Activity.startActivity(
        intent: Intent,
        requestCode: Int? = null,
        optionsCompatBundle: Bundle? = null
) {
    if (requestCode != null) {
        if (Sdk.isLollipop) startActivityForResult(intent, requestCode, optionsCompatBundle)
        else startActivityForResult(intent, requestCode)
    } else {
        if (Sdk.isLollipop) startActivity(intent, optionsCompatBundle)
        else startActivity(intent)
    }
}

/**
 * Convenience for ActivityBuilder(Context, KClass).startWith{ ... }
 */
inline fun Context.startActivity(cls: KClass<*>, block: ActivityBuilder.() -> Unit = {}) {
    ActivityBuilder(this, cls).startWith(block)
}

/**
 * Convenience for ActivityBuilder(Context, KClass).startWith{ ... }
 */
inline fun Fragment.startActivity(cls: KClass<*>, block: ActivityBuilder.() -> Unit = {}) {
    ActivityBuilder(this, cls).startWith(block)
}

/**
 * Convenience for ActivityBuilder(Context, KClass).startWith{ ... }
 */
inline fun View.startActivity(cls: KClass<*>, block: ActivityBuilder.() -> Unit = {}) {
    ActivityBuilder(this, cls).startWith(block)
}
