package org.beatonma.lib.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.snackbar.Snackbar

/**
 * Created by Michael on 05/08/2016.
 * A wrapper for AppCompatActivity that automatically handles night mode and provides
 * convenience methods for databinding, snackbars and animations.
 * Also creates a weak context.
 */
private const val TAG = "BaseActivity"
const val THEME_PREFS = "app"
const val UI_DARK_THEME = "pref_dark_theme"

abstract class BaseActivity : AppCompatActivity() {

    abstract val layoutID: Int
    /**
     * @return  A 'stub' view which is invisible and tiny.
     * This is used in some 'shared' view transitions.
     * the view will be added at runtime if necessary
     * but Activities should include this stub in their layout
     * for better performance
     */
    val sharedViewStub: View?
        get() = findViewById(R.id.stub)

    open val sharedViews: Array<SharedView>?
        get() {
            val res = resources
            val v = findViewById<View>(R.id.stub)
            if (v == null) {
                Log.w(TAG, "View stub is missing so activity animation will fail - please add  <include layout=\"@layout/stub\"/> to your layout")
                return null
            }
            return arrayOf(SharedView(v, res.getString(R.string.transition_card)))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up day/night theming
        val forceNight = getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
                .getBoolean(UI_DARK_THEME, false)
        AppCompatDelegate.setDefaultNightMode(
                if (forceNight)
                    AppCompatDelegate.MODE_NIGHT_YES
                else
                    AppCompatDelegate.MODE_NIGHT_AUTO)
        delegate.applyDayNight()

        // Create ViewModels or whatever
        onPreLayout()

        // Load/refresh data
        loadState(savedInstanceState)

        // Read extras
        initIntent(intent)
        initLayout(DataBindingUtil.setContentView(this, layoutID))

        // Set up custom sharedview or other transitions
        setupWindowTransitions()
    }

    /**
     * Set up the UI
     */
    abstract fun initLayout(binding: ViewDataBinding)

    /**
     * Called at the beginning of onCreate(), before any layout, etc has been set up
     */
    @CallSuper
    protected open fun onPreLayout() {

    }


    /**
     * Reload data. Warning: Views have not been bound yet!
     */
    @CallSuper
    open fun loadState(savedState: Bundle?) {

    }

    fun getSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG): Snackbar? {
        val v = findViewById<View>(R.id.top_level_container)
        if (v != null) {
            return Snackbar.make(v, message, duration)
        }
        Log.e(TAG, "Error building snackbar for message: $message")
        return null
    }

    fun showSnackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        getSnackbar(message, duration)?.show()
    }

    @CallSuper
    protected open fun initIntent(intent: Intent?) {
        initExtras(intent?.extras)
    }

    /**
     * Read any Intent extras
     */
    @CallSuper
    protected open fun initExtras(extras: Bundle?) {

    }

    /**
     * Called after initLayout so Views are accessible
     */
    protected open fun setupWindowTransitions() {

    }

    @CallSuper
    fun close() {
        supportFinishAfterTransition()
    }

    fun forceNight(forceNight: Boolean) {
        getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE).edit()
                .putBoolean(UI_DARK_THEME, forceNight)
                .commit()
        recreate()
    }

    /**
     * Toggle forceNight()
     */
    fun toggleForceNight() {
        forceNight(!getSharedPreferences(THEME_PREFS, Context.MODE_PRIVATE)
                .getBoolean(UI_DARK_THEME, true))
    }
}
