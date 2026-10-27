package vn.edu.usth.final_mobile_2026_2027.ui.common;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Infinite scroll: asks for the next page of posts when the user gets near the end of the list.
 *
 * Usage, the page loads page 1 itself, then:
 * <pre>
 * scrollListener = new EndlessScrollListener() {
 *     protected void onLoadMore(int page) {
 *         // load the page, then call scrollListener.onPageLoaded(posts.isEmpty())
 *         // or scrollListener.onPageFailed() on error
 *     }
 * };
 * recyclerView.addOnScrollListener(scrollListener);
 * </pre>
 * After a pull to refresh, call reset() to start again at page 2.
 */
public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {

    /** The next page is asked when the last visible post is one of the last 5 posts. */
    static final int THRESHOLD = 5;

    private int nextPage = 2;
    private boolean loading = false;
    private boolean lastPage = false;

    /** Called with 2, 3, 4... The page must answer with onPageLoaded() or onPageFailed(). */
    protected abstract void onLoadMore(int page);

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        // Only when the user scrolls down
        if (dy <= 0) {
            return;
        }
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
        int itemCount = layoutManager.getItemCount();

        if (shouldLoadMore(lastVisiblePosition, itemCount, loading, lastPage)) {
            loading = true;
            onLoadMore(nextPage);
        }
    }

    /** @param empty true when the page has no post: it was the end of the list */
    public void onPageLoaded(boolean empty) {
        loading = false;
        if (empty) {
            lastPage = true;
        } else {
            nextPage++;
        }
    }

    /** The same page will be asked again on the next scroll. */
    public void onPageFailed() {
        loading = false;
    }

    /** Back to the beginning, e.g. after a pull to refresh which loaded page 1 again. */
    public void reset() {
        nextPage = 2;
        loading = false;
        lastPage = false;
    }

    /** Decides if the next page must be loaded. Static so it can be tested without a RecyclerView. */
    static boolean shouldLoadMore(int lastVisiblePosition, int itemCount, boolean loading, boolean lastPage) {
        if (itemCount == 0 || loading || lastPage) {
            return false;
        }
        return lastVisiblePosition >= itemCount - THRESHOLD;
    }

    int getNextPage() {
        return nextPage;
    }

    boolean isLastPage() {
        return lastPage;
    }
}
