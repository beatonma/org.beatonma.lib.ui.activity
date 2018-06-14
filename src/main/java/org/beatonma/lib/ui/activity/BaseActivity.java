package org.beatonma.lib.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.snackbar.Snackbar;

import org.beatonma.lib.log.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

/**
 * Created by Michael on 05/08/2016.
 * A wrapper for AppCompatActivity that automatically handles night mode and provides
 * convenience methods for databinding, snackbars and animations.
 * Also creates a weak context.
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected final static String TAG = "BaseActivity";

    public final static String THEME_PREFS = "app";
    public final static String UI_DARK_THEME = "pref_dark_theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs = getSharedPreferences(THEME_PREFS, MODE_PRIVATE);
        final boolean forceNight = prefs.getBoolean(UI_DARK_THEME, false);
        AppCompatDelegate.setDefaultNightMode(
                forceNight
                        ? AppCompatDelegate.MODE_NIGHT_YES
                        : AppCompatDelegate.MODE_NIGHT_AUTO);
        getDelegate().applyDayNight();

        initIntent(getIntent());
    }

    protected ViewDataBinding setLayout(int layoutId) {
        return DataBindingUtil.setContentView(this, layoutId);
    }

    public Snackbar getSnackbar(final String message) {
        final View v = findViewById(R.id.top_level_container);
        if (v != null) {
            return Snackbar.make(v, message, Snackbar.LENGTH_LONG);
        }
        Log.e(TAG, "Error building snackbar for message: " + message);
        return null;
    }

    public void showSnackbar(final String message) {
        final Snackbar snackbar = getSnackbar(message);
        if (snackbar != null) {
            snackbar.show();
        }
    }

    /**
     * @return  A 'stub' view which is invisible and tiny.
     *           This is used in some 'shared' view transitions.
     *           the view will be added at runtime if necessary
     *           but Activities should include this stub in their layout
     *           for better performance
     */
    @Nullable
    public View getSharedViewStub() {
        return findViewById(R.id.stub);
//        if (v == null) {
//            View parent = getBinding().getRoot();
//            if (parent instanceof ViewGroup) {
//                v = LayoutInflater.from(this).inflate(R.layout.stub, (ViewGroup) parent);
//                Log.w(TAG, "Injected a stub view into layout parent for activity transition - please include a stub in the activity layout file to improve performance!");
//            }
//        }
//        return v;
    }

    @Nullable
    public SharedView[] getSharedViews() {
        final Resources res = getResources();
        final View v = findViewById(R.id.stub);
        if (v == null) {
            Log.w(TAG, "View stub is missing so activity animation will fail - please add  <include layout=\"@layout/stub\"/> to your layout");
            return null;
        }
        return new SharedView[] {
                new SharedView(v, res.getString(R.string.transition_card))
        };
    }

    @CallSuper
    protected void initIntent(final Intent intent) {
        if (intent != null) {
            initExtras(intent.getExtras());
        }
        else {
            initExtras(null);
        }
    }

    @CallSuper
    protected void initExtras(final Bundle extras) {

    }

    protected void setupWindowTransitions() {
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            return;
//        }
//        CircularTransform.setup(this, findViewById(R.id.transition_layout));
//        Log.d(TAG, "setupWindowTransitions() CircularTransform");
    }

    @CallSuper
    public void close() {
        supportFinishAfterTransition();
    }

    public void forceNight(final boolean forceNight) {
        getSharedPreferences(THEME_PREFS, MODE_PRIVATE).edit()
                .putBoolean(UI_DARK_THEME, forceNight)
                .commit();
        recreate();
    }

    /**
     * Toggle forceNight()
     */
    public void toggleForceNight() {
        forceNight(!getSharedPreferences(THEME_PREFS, MODE_PRIVATE)
                .getBoolean(UI_DARK_THEME, true));
    }
}
