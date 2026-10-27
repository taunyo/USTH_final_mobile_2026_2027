package vn.edu.usth.final_mobile_2026_2027.ui.common;

import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static vn.edu.usth.final_mobile_2026_2027.ui.common.EndlessScrollListener.shouldLoadMore;

/**
 * Unit tests of EndlessScrollListener (infinite scroll).
 * ./gradlew testDebugUnitTest --tests "*EndlessScrollListenerTest"
 */
public class EndlessScrollListenerTest {

    private EndlessScrollListener listener;

    @Before
    public void setUp() {
        listener = new EndlessScrollListener() {
            @Override
            protected void onLoadMore(int page) {
                // Not used: the tests call onPageLoaded() / onPageFailed() themselves
            }
        };
    }

    // ---------------------------------------------------------------------------------------------
    // shouldLoadMore(): 20 posts in the list, THRESHOLD = 5
    // ---------------------------------------------------------------------------------------------

    @Test
    public void shouldLoadMore_nearTheEnd() {
        assertTrue(shouldLoadMore(15, 20, false, false));
    }

    @Test
    public void shouldLoadMore_farFromTheEnd() {
        assertFalse(shouldLoadMore(10, 20, false, false));
    }

    @Test
    public void shouldLoadMore_notTwice() {
        // A page is already loading
        assertFalse(shouldLoadMore(19, 20, true, false));
    }

    @Test
    public void shouldLoadMore_notAfterLastPage() {
        assertFalse(shouldLoadMore(19, 20, false, true));
    }

    @Test
    public void shouldLoadMore_notOnEmptyList() {
        assertFalse(shouldLoadMore(RecyclerView.NO_POSITION, 0, false, false));
    }

    // ---------------------------------------------------------------------------------------------
    // Page numbers
    // ---------------------------------------------------------------------------------------------

    @Test
    public void pages_goUpThenStop() {
        // Page 1 is loaded by the page itself, the listener starts at page 2
        assertEquals(2, listener.getNextPage());

        listener.onPageLoaded(false);
        assertEquals(3, listener.getNextPage());

        // An empty page means the end of the list
        listener.onPageLoaded(true);
        assertTrue(listener.isLastPage());

        // After a pull to refresh
        listener.reset();
        assertEquals(2, listener.getNextPage());
        assertFalse(listener.isLastPage());
    }

    @Test
    public void failedPage_isAskedAgain() {
        listener.onPageFailed();
        assertEquals(2, listener.getNextPage());
        assertFalse(listener.isLastPage());
    }
}
