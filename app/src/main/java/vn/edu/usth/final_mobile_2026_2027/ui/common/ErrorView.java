package vn.edu.usth.final_mobile_2026_2027.ui.common;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import vn.edu.usth.final_mobile_2026_2027.R;

/**
 * Controls the shared layout view_error, used by every page to show an error or an empty list.
 *
 * In the layout of the page, next to the RecyclerView:
 * <pre>
 * &lt;include
 *     android:id="@+id/error_view"
 *     layout="@layout/view_error"
 *     android:visibility="gone" /&gt;
 * </pre>
 * In the code of the page:
 * <pre>
 * ErrorView errorView = new ErrorView(view.findViewById(R.id.error_view));
 * errorView.showError(message, this::loadPosts);  // message + "Retry" button
 * errorView.showEmpty(message);                   // message only, e.g. "No posts yet"
 * errorView.hide();
 * </pre>
 */
public class ErrorView {

    private final View root;
    private final TextView message;
    private final Button retryButton;

    /** @param root the included view_error, found with findViewById(R.id.error_view) */
    public ErrorView(View root) {
        this.root = root;
        this.message = root.findViewById(R.id.error_message);
        this.retryButton = root.findViewById(R.id.retry_button);
    }

    /** Shows an error message with a "Retry" button. A click on the button calls onRetry. */
    public void showError(String text, Runnable onRetry) {
        message.setText(text);
        retryButton.setVisibility(View.VISIBLE);
        retryButton.setOnClickListener(v -> {
            // Hide the error first: onRetry usually shows the progress bar and loads again
            hide();
            onRetry.run();
        });
        root.setVisibility(View.VISIBLE);
    }

    /** Shows a message without button, when the list is empty. */
    public void showEmpty(String text) {
        message.setText(text);
        retryButton.setVisibility(View.GONE);
        root.setVisibility(View.VISIBLE);
    }

    public void hide() {
        root.setVisibility(View.GONE);
    }
}
