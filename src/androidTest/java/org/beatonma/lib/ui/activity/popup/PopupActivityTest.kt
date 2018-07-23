@file:JvmName("PopupActivityTest")

package org.beatonma.lib.ui.activity.popup

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isCompletelyDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import org.beatonma.lib.testing.espresso.ViewSizeMatchesParentMatcher
import org.beatonma.lib.ui.activity.R
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite
import kotlin.reflect.KClass


@RunWith(Suite::class)
@Suite.SuiteClasses(
        LargePopupActivityTest::class,
        MediumPopupActivityTest::class,
        SmallPopupActivityTest::class
)
class PopupActivityTestSuite


class PopupTestRule<T: BasePopupActivity>(private val cls: KClass<T>): ActivityTestRule<T>(cls.java, true, true) {
//    override fun getActivityIntent(): Intent {
//        return Intent(InstrumentationRegistry.getInstrumentation().targetContext, cls.java)
//    }
}

/**
 * Base class with tests that must pass for any PopupActivity, regardless of content
 */
@MediumTest
abstract class PopupActivityTest {
    @Rule
    abstract fun rule(): ActivityTestRule<out BasePopupActivity>

    val activity: BasePopupActivity?
        get() = rule().activity

    @Before
    fun setUp() {

    }

    @Test
    fun popup_card_shouldBeCompletelyVisible() {
        onView(withId(R.id.card))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_positiveButton_shouldBeCompletelyVisible() {
        onView(withId(R.id.button_positive))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_negativeButton_shouldBeCompletelyVisible() {
        onView(withId(R.id.button_negative))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_customActionButton_shouldBeCompletelyVisible() {
        onView(withId(R.id.button_custom_action))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_title_shouldBeCompletelyVisible() {
        onView(withId(R.id.title))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_background_shouldBeVisible() {
        onView(withId(R.id.overlay))
                .check(matches(isDisplayed()))
    }

    @Test
    fun popup_clickOnBackground_shouldClosePopup() {
        TODO("Activity is always null but I'm not sure why - may need to use UI automator instead?")
//        Log.w(autotag, "IS NULL: ${rule().activity == null}")

//        Log.d(autotag, "result: ${rule().activityResult}")
//        activity.assertNotNull()
//        onView(withId(R.id.overlay))
//                .perform(click())
//
//        activity?.isFinishing?.assertTrue() ?: activity.assertNull()
    }
}

@MediumTest
class SmallPopupActivityTest: PopupActivityTest() {
    @Rule
    override fun rule() = PopupTestRule(SmallPopupTestActivity::class)

    @Test
    fun popup_withSmallContent_shouldNotScroll() {
        // TextView should be same size as its parent ScrollView
        onView(withId(R.id.text))
                .check(matches(ViewSizeMatchesParentMatcher()))
                .check(matches(isCompletelyDisplayed()))
    }
}

@MediumTest
class MediumPopupActivityTest: PopupActivityTest() {
    @Rule
    override fun rule() = ActivityTestRule(MediumPopupTestActivity::class.java, true, true)

    @Test
    fun popup_withMediumContent_shouldNotScroll() {
        // TextView should be same size as its parent ScrollView
        onView(withId(R.id.text))
                .check(matches(ViewSizeMatchesParentMatcher()))
                .check(matches(isCompletelyDisplayed()))
    }
}

@MediumTest
class LargePopupActivityTest: PopupActivityTest() {
    @Rule
    override fun rule() = ActivityTestRule(LargePopupTestActivity::class.java, true, true)

    @Test
    fun popup_withLargeContent_shouldScroll() {
        // TextView should be larger than its parent ScrollView so should only be partially visible
        onView(withId(R.id.text))
                .check(matches(isDisplayed()))
                .check(matches(not(isCompletelyDisplayed())))
    }
}
