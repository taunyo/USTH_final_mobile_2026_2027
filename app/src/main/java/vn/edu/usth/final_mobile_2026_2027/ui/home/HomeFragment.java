package vn.edu.usth.final_mobile_2026_2027.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.api.ApiCallback;
import vn.edu.usth.final_mobile_2026_2027.api.LemmyApi;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.common.EndlessScrollListener;
import vn.edu.usth.final_mobile_2026_2027.ui.common.ErrorView;
import vn.edu.usth.final_mobile_2026_2027.ui.common.PostAdapter;

/**
 * Page 1 - Home: feed of the popular posts of all the communities.
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private ErrorView errorView;
    private EndlessScrollListener scrollListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = view.findViewById(R.id.progress_bar);
        errorView = new ErrorView(view.findViewById(R.id.error_view));

        // The adapter opens the Discussion, Community and Profile pages itself
        adapter = new PostAdapter();
        recyclerView = view.findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        // Pull to refresh: the circle of the SwipeRefreshLayout replaces the progress bar
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> loadPosts(true));

        // Infinite scroll: the next page is loaded when the user gets near the end of the list
        scrollListener = new EndlessScrollListener() {
            @Override
            protected void onLoadMore(int page) {
                loadMorePosts(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        loadPosts(false);
    }

    /** Goes back to the first post, e.g. when the user taps the Home tab again. */
    public void scrollToTop() {
        // The view may not be created yet
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * Loads the first page of posts.
     *
     * @param refreshing true for a pull to refresh: its circle is already shown
     */
    private void loadPosts(boolean refreshing) {
        if (!refreshing) {
            progressBar.setVisibility(View.VISIBLE);
        }
        errorView.hide();

        LemmyApi.getInstance(requireContext()).getPopularPosts(1, new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                // The user may have left the page while the request was running
                if (!isAdded()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                adapter.setPosts(posts);
                // The list starts again from page 1: the next page to load is page 2
                scrollListener.reset();
                if (posts.isEmpty()) {
                    errorView.showEmpty(getString(R.string.empty_posts));
                }
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                // After a pull to refresh the old posts are still there: remove them,
                // otherwise the error message is drawn on top of them
                adapter.setPosts(new ArrayList<>());
                // "Retry" loads the posts again
                errorView.showError(message, () -> loadPosts(false));
            }
        });
    }

    /** Loads page 2, 3... and adds its posts at the end of the list. */
    private void loadMorePosts(int page) {
        LemmyApi.getInstance(requireContext()).getPopularPosts(page, new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (!isAdded()) {
                    return;
                }
                adapter.addPosts(posts);
                scrollListener.onPageLoaded(posts.isEmpty());
            }

            @Override
            public void onError(String message) {
                if (!isAdded()) {
                    return;
                }
                // The posts already shown stay: a short message is enough,
                // the same page is asked again when the user scrolls
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
                scrollListener.onPageFailed();
            }
        });
    }
}
