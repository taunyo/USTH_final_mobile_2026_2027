package vn.edu.usth.final_mobile_2026_2027.utils;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Small helpers shared by the screens.
 */
public class ViewUtils {

    private ViewUtils() {
        // Only static methods
    }

    /**
     * Since Android 15 the app is drawn behind the status bar and the navigation bar (edge-to-edge).
     * Call this on the root view of a screen so its content is not hidden behind these bars.
     */
    public static void applySystemBarsPadding(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
    }
}
