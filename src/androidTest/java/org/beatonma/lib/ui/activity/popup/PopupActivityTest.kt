@file:JvmName("PopupActivityTest")

package org.beatonma.lib.ui.activity.popup

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.MediumTest
import androidx.test.rule.ActivityTestRule
import org.beatonma.lib.testing.espresso.action.click
import org.beatonma.lib.testing.kotlin.extensions.assertions.assertTrue
import org.beatonma.lib.testing.kotlin.extensions.testRule
import org.beatonma.lib.ui.activity.R
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@Suite.SuiteClasses(
        LargePopupActivityTest::class,
        MediumPopupActivityTest::class,
        SmallPopupActivityTest::class
)
class PopupActivityTestSuite

/**
 * Base class with tests that must pass for any PopupActivity, regardless of content
 */
@MediumTest
abstract class PopupActivityTest {
    @get:Rule
    abstract val rule: ActivityTestRule<out BaseTestPopup>

    val activity: BaseTestPopup?
        get() = rule.activity

    @Test
    fun popup_withButtons_cardShouldBeCompletelyVisible() {
        activity?.showButtons()

        onView(withId(R.id.card))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_withTitle_cardShouldBeCompletelyVisible() {
        activity?.showTitle()

        onView(withId(R.id.card))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_withButtonsAndTitle_cardShouldBeCompletelyVisible() {
        activity?.apply {
            showButtons()
            showTitle()
        }

        onView(withId(R.id.card))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_withPositiveButtonEnabled_positiveButtonShouldBeCompletelyVisible() {
        activity?.showPositiveButton()
        onView(withId(R.id.button_positive))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_withNegativeButtonEnabled_negativeButtonShouldBeCompletelyVisible() {
        activity?.showNegativeButton()
        onView(withId(R.id.button_negative))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_withCustomActionButton_customButtonShouldBeCompletelyVisible() {
        activity?.showCustomActionButton()
        onView(withId(R.id.button_custom_action))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_withTitle_titleShouldBeCompletelyVisible() {
        activity?.showTitle()
        onView(withId(R.id.title))
                .check(matches(isCompletelyDisplayed()))
    }

    @Test
    fun popup_withButtonsVisible_buttonSpacerShouldBeVisible() {
        activity?.showButtons()
        onView(withId(R.id.button_spacer))
                .check(matches(isDisplayed()))
    }

    @Test
    fun popup_withButtonsGone_buttonSpacerShouldBeGone() {
        activity?.hideButtons()
        onView(withId(R.id.button_spacer))
                .check(matches(not(isDisplayed())))
    }

    @Test
    fun popup_background_shouldBeVisible() {
        onView(withId(R.id.overlay))
                .check(matches(isDisplayed()))
    }

    @Test
    fun popup_clickOnBackground_shouldClosePopup() {
        // Tap just above the card - the background overlay should be accessible there
        onView(withId(R.id.card))
                .perform(click(y = -.1F))

        activity?.isFinishing.assertTrue() // Also fine if activity is null
    }
}

@MediumTest
class SmallPopupActivityTest : PopupActivityTest() {
    override val rule = SmallPopupTestActivity::class.testRule

    @Test
    fun popup_withSmallContent_shouldNotScroll() {
        // TextView should be same size as its parent ScrollView
        onView(withId(R.id.text))
                .check(matches(ViewSizeMatchesParentMatcher()))
                .check(matches(isCompletelyDisplayed()))
    }
}

@MediumTest
class MediumPopupActivityTest : PopupActivityTest() {
    override val rule = MediumPopupTestActivity::class.testRule

    @Test
    fun popup_withMediumContent_shouldNotScroll() {
        // TextView should be same size as its parent ScrollView
        onView(withId(R.id.text))
                .check(matches(ViewSizeMatchesParentMatcher()))
                .check(matches(isCompletelyDisplayed()))
    }
}

@MediumTest
class LargePopupActivityTest : PopupActivityTest() {
    override val rule = LargePopupTestActivity::class.testRule

    @Test
    fun popup_withLargeContent_shouldScroll() {
        // TextView should be larger than its parent ScrollView so should only be partially visible
        onView(withId(R.id.text))
                .check(matches(isDisplayed()))
                .check(matches(not(isCompletelyDisplayed())))
    }
}
