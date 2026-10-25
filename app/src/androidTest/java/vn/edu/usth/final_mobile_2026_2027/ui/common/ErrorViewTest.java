package vn.edu.usth.final_mobile_2026_2027.ui.common;

import android.content.Context;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import vn.edu.usth.final_mobile_2026_2027.R;

import static org.junit.Assert.assertEquals;

/**
 * Tests of ErrorView. They run on a phone or an emulator:
 * ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=vn.edu.usth.final_mobile_2026_2027.ui.common.ErrorViewTest
 */
@RunWith(AndroidJUnit4.class)
public class ErrorViewTest {

    private View root;
    private ErrorView errorView;
    private int retries;

    @Before
    public void setUp() {
        // Material components can only be created with the theme of the app
        Context context = new ContextThemeWrapper(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                R.style.Theme_USTH_final_mobile_2026_2027);
        root = LayoutInflater.from(context).inflate(R.layout.view_error, new FrameLayout(context), false);
        // Hidden at the start, like the <include> of the pages
        root.setVisibility(View.GONE);

        errorView = new ErrorView(root);
        retries = 0;
    }

    @Test
    public void showError_showsMessageAndRetry() {
        errorView.showError("Boom", () -> retries++);

        assertEquals(View.VISIBLE, root.getVisibility());
        assertEquals("Boom", messageText());
        assertEquals(View.VISIBLE, root.findViewById(R.id.retry_button).getVisibility());
    }

    @Test
    public void retryClick_hidesAndRetries() {
        errorView.showError("Boom", () -> retries++);

        root.findViewById(R.id.retry_button).performClick();

        assertEquals(1, retries);
        assertEquals(View.GONE, root.getVisibility());
    }

    @Test
    public void showEmpty_showsMessageWithoutRetry() {
        errorView.showEmpty("Nothing");

        assertEquals(View.VISIBLE, root.getVisibility());
        assertEquals("Nothing", messageText());
        assertEquals(View.GONE, root.findViewById(R.id.retry_button).getVisibility());
    }

    @Test
    public void showError_afterShowEmpty_showsRetryAgain() {
        errorView.showEmpty("Nothing");
        errorView.showError("Boom", () -> retries++);

        assertEquals(View.VISIBLE, root.findViewById(R.id.retry_button).getVisibility());
    }

    @Test
    public void hide_hidesTheView() {
        errorView.showEmpty("Nothing");

        errorView.hide();

        assertEquals(View.GONE, root.getVisibility());
    }

    private String messageText() {
        return ((TextView) root.findViewById(R.id.error_message)).getText().toString();
    }
}
