package vn.edu.usth.final_mobile_2026_2027.ui.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.api.ApiCallback;
import vn.edu.usth.final_mobile_2026_2027.api.LemmyApi;
import vn.edu.usth.final_mobile_2026_2027.model.Community;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.common.EndlessScrollListener;
import vn.edu.usth.final_mobile_2026_2027.ui.common.ErrorView;
import vn.edu.usth.final_mobile_2026_2027.ui.common.PostAdapter;
import vn.edu.usth.final_mobile_2026_2027.utils.FormatUtils;
import vn.edu.usth.final_mobile_2026_2027.utils.ViewUtils;

/**
 * Page 3 - Community: information about a community and its posts.
 */
public class CommunityActivity extends AppCompatActivity {

    private static final String EXTRA_COMMUNITY_NAME = "community_name";
    private static final String STATE_SORT = "sort";

    private String communityName;
    private String sort = "Hot";
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefresh;
    private ErrorView errorView;
    private EndlessScrollListener scrollListener;

    /**
     * Creates the Intent to open this page, e.g.
     * startActivity(CommunityActivity.newIntent(context, post.getCommunity()));
     * The name is "technology", or "technology@lemmy.ml" for a community of another instance.
     */
    public static Intent newIntent(Context context, String communityName) {
        Intent intent = new Intent(context, CommunityActivity.class);
        intent.putExtra(EXTRA_COMMUNITY_NAME, communityName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community);
        ViewUtils.applySystemBarsPadding(findViewById(R.id.main));

        communityName = getIntent().getStringExtra(EXTRA_COMMUNITY_NAME);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_community, communityName));
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar);
        errorView = new ErrorView(findViewById(R.id.error_view));

        // The adapter opens the Discussion, Community and Profile pages itself
        adapter = new PostAdapter();
        RecyclerView recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Pull to refresh: the circle of the SwipeRefreshLayout replaces the progress bar
        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setOnRefreshListener(() -> loadCommunity(true));

        // Infinite scroll: the next page is loaded when the user gets near the end of the list
        scrollListener = new EndlessScrollListener() {
            @Override
            protected void onLoadMore(int page) {
                loadMorePosts(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        // After a rotation, keep the sort order chosen by the user.
        // Android puts back the selected chip by itself
        if (savedInstanceState != null) {
            sort = savedInstanceState.getString(STATE_SORT, sort);
        }

        ChipGroup sortChips = findViewById(R.id.sort_chips);
        sortChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // selectionRequired="true": there is always one selected chip
            String newSort = getSort(checkedIds.get(0));
            // The listener is also called when Android puts back the chip after a rotation
            if (!newSort.equals(sort)) {
                sort = newSort;
                loadPosts(false);
            }
        });

        loadCommunity(false);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_SORT, sort);
    }

    /** Returns the value of the "sort" parameter of the API for a chip. */
    private static String getSort(int chipId) {
        if (chipId == R.id.chip_new) {
            return "New";
        }
        if (chipId == R.id.chip_top_day) {
            return "TopDay";
        }
        return "Hot";
    }

    /**
     * Loads the header first, then the posts.
     * So for a community that doesn't exist (HTTP 404), only its error is shown.
     *
     * @param refreshing true for a pull to refresh: its circle is already shown
     */
    private void loadCommunity(boolean refreshing) {
        if (!refreshing) {
            progressBar.setVisibility(View.VISIBLE);
        }
        errorView.hide();

        LemmyApi.getInstance(this).getCommunity(communityName, new ApiCallback<Community>() {
            @Override
            public void onSuccess(Community community) {
                // The user may have left the page while the request was running (isFinishing),
                // or rotated the screen: this page is then destroyed and a new one loads again
                // (isDestroyed, isFinishing stays false)
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                showHeader(community);
                loadPosts(refreshing);
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                // After a pull to refresh the old posts are still there: remove them,
                // otherwise the error message is drawn on top of them
                adapter.setPosts(new ArrayList<>());
                // "Retry" loads the whole page again
                errorView.showError(message, () -> loadCommunity(false));
            }
        });
    }

    /** @param refreshing true for a pull to refresh: the old posts stay until the new ones arrive */
    private void loadPosts(boolean refreshing) {
        if (!refreshing) {
            // Remove the posts of the previous sort order
            adapter.setPosts(new ArrayList<>());
            progressBar.setVisibility(View.VISIBLE);
        }
        errorView.hide();

        LemmyApi.getInstance(this).getCommunityPosts(communityName, sort, 1, new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (isFinishing() || isDestroyed()) {
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
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                adapter.setPosts(new ArrayList<>());
                // The header is already shown: "Retry" only loads the posts again
                errorView.showError(message, () -> loadPosts(false));
            }
        });
    }

    /** Loads page 2, 3... and adds its posts at the end of the list. */
    private void loadMorePosts(int page) {
        // Remember the sort order of this request: the user can change it while it is running
        String requestSort = sort;

        LemmyApi.getInstance(this).getCommunityPosts(communityName, requestSort, page, new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                // The sort order changed: these posts are not in the new order, forget them.
                // loadPosts() calls reset() when the first page of the new order arrives
                if (isFinishing() || isDestroyed() || !requestSort.equals(sort)) {
                    return;
                }
                adapter.addPosts(posts);
                scrollListener.onPageLoaded(posts.isEmpty());
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed() || !requestSort.equals(sort)) {
                    return;
                }
                // The posts already shown stay: a short message is enough,
                // the same page is asked again when the user scrolls
                Toast.makeText(CommunityActivity.this, message, Toast.LENGTH_SHORT).show();
                scrollListener.onPageFailed();
            }
        });
    }

    private void showHeader(Community community) {
        ImageView icon = findViewById(R.id.community_icon);
        TextView title = findViewById(R.id.community_title);
        TextView members = findViewById(R.id.community_members);
        TextView description = findViewById(R.id.community_description);

        if (community.getIconUrl() == null) {
            icon.setImageResource(R.drawable.ic_community);
        } else {
            Glide.with(this).load(community.getIconUrl()).centerCrop().into(icon);
        }

        title.setText(community.getTitle());

        // Plurals choose between "1 member" and "2 members" with the number given as 2nd argument
        members.setText(getResources().getQuantityString(R.plurals.community_members,
                (int) community.getSubscribers(), FormatUtils.formatCount(community.getSubscribers())));

        description.setText(community.getDescription());
        if (community.getDescription().isEmpty()) {
            description.setVisibility(View.GONE);
        } else {
            description.setVisibility(View.VISIBLE);
        }

        findViewById(R.id.community_header).setVisibility(View.VISIBLE);
    }
}
