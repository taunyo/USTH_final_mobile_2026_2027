package vn.edu.usth.final_mobile_2026_2027.ui.community;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.api.ApiCallback;
import vn.edu.usth.final_mobile_2026_2027.api.LemmyApi;
import vn.edu.usth.final_mobile_2026_2027.model.Community;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.common.ErrorView;
import vn.edu.usth.final_mobile_2026_2027.ui.common.PostAdapter;
import vn.edu.usth.final_mobile_2026_2027.utils.FormatUtils;
import vn.edu.usth.final_mobile_2026_2027.utils.ViewUtils;

/**
 * Page 3 - Community: information about a community and its posts.
 */
public class CommunityActivity extends AppCompatActivity {

    private static final String EXTRA_COMMUNITY_NAME = "community_name";

    private String communityName;
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private ErrorView errorView;

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

        loadCommunity();

        // TODO (bonus): let the user choose the sort order (Hot, New, TopDay)
    }

    /**
     * Loads the header first, then the posts.
     * So for a community that doesn't exist (HTTP 404), only its error is shown.
     */
    private void loadCommunity() {
        progressBar.setVisibility(View.VISIBLE);
        errorView.hide();

        LemmyApi.getInstance(this).getCommunity(communityName, new ApiCallback<Community>() {
            @Override
            public void onSuccess(Community community) {
                // The user may have left the page while the request was running
                if (isFinishing()) {
                    return;
                }
                showHeader(community);
                loadPosts();
            }

            @Override
            public void onError(String message) {
                if (isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                // "Retry" loads the whole page again
                errorView.showError(message, CommunityActivity.this::loadCommunity);
            }
        });
    }

    private void loadPosts() {
        progressBar.setVisibility(View.VISIBLE);
        errorView.hide();

        LemmyApi.getInstance(this).getCommunityPosts(communityName, new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                adapter.setPosts(posts);
                if (posts.isEmpty()) {
                    errorView.showEmpty(getString(R.string.empty_posts));
                }
            }

            @Override
            public void onError(String message) {
                if (isFinishing()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                // The header is already shown: "Retry" only loads the posts again
                errorView.showError(message, CommunityActivity.this::loadPosts);
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
