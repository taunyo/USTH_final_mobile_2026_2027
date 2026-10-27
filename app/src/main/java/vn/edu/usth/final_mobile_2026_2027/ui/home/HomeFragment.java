package vn.edu.usth.final_mobile_2026_2027.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

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

        loadPosts(false);
    }

    /** Goes back to the first post, e.g. when the user taps the Home tab again. */
    public void scrollToTop() {
        // The view may not be created yet
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /** @param refreshing true for a pull to refresh: its circle is already shown */
    private void loadPosts(boolean refreshing) {
        if (!refreshing) {
            progressBar.setVisibility(View.VISIBLE);
        }
        errorView.hide();

        LemmyApi.getInstance(requireContext()).getPopularPosts(new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                // The user may have left the page while the request was running
                if (!isAdded()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                adapter.setPosts(posts);
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
}
