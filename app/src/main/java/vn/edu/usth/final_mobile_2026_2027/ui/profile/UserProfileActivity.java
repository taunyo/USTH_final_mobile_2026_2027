package vn.edu.usth.final_mobile_2026_2027.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.api.ApiCallback;
import vn.edu.usth.final_mobile_2026_2027.api.LemmyApi;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.model.User;
import vn.edu.usth.final_mobile_2026_2027.ui.common.EndlessScrollListener;
import vn.edu.usth.final_mobile_2026_2027.ui.common.ErrorView;
import vn.edu.usth.final_mobile_2026_2027.ui.common.PostAdapter;
import vn.edu.usth.final_mobile_2026_2027.utils.FormatUtils;
import vn.edu.usth.final_mobile_2026_2027.utils.ViewUtils;

/**
 * Page 5 - User profile: public information about a user and the posts they wrote.
 */
public class UserProfileActivity extends AppCompatActivity {

    private static final String EXTRA_USERNAME = "username";

    private String username;
    private PostAdapter adapter;
    private ProgressBar progressBar;
    private ErrorView errorView;
    private EndlessScrollListener scrollListener;

    private ShapeableImageView avatarImage;
    private TextView usernameText;
    private TextView memberSinceText;
    private TextView postsCountText;
    private TextView commentsCountText;
    private TextView bannedText;

    /**
     * Creates the Intent to open this page, e.g.
     * startActivity(UserProfileActivity.newIntent(context, post.getAuthor()));
     */
    public static Intent newIntent(Context context, String username) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.putExtra(EXTRA_USERNAME, username);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_profile);
        ViewUtils.applySystemBarsPadding(findViewById(R.id.main));

        username = getIntent().getStringExtra(EXTRA_USERNAME);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_user, username));
        toolbar.setNavigationOnClickListener(v -> finish());

        avatarImage = findViewById(R.id.avatar_image);
        usernameText = findViewById(R.id.username_text);
        memberSinceText = findViewById(R.id.member_since_text);
        postsCountText = findViewById(R.id.posts_count_text);
        commentsCountText = findViewById(R.id.comments_count_text);
        bannedText = findViewById(R.id.banned_text);

        progressBar = findViewById(R.id.progress_bar);
        errorView = new ErrorView(findViewById(R.id.error_view));

        adapter = new PostAdapter();
        RecyclerView recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        scrollListener = new EndlessScrollListener() {
            @Override
            protected void onLoadMore(int page) {
                loadMoreUserPosts(page);
            }
        };
        recyclerView.addOnScrollListener(scrollListener);

        loadUser();
    }

    private void loadUser() {
        progressBar.setVisibility(View.VISIBLE);
        errorView.hide();

        LemmyApi.getInstance(this).getUser(username, new ApiCallback<User>() {
            @Override
            public void onSuccess(User user) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                displayUser(user);
                loadUserPosts();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                errorView.showError(message, () -> loadUser());
            }
        });
    }

    private void displayUser(User user) {
        String displayName = user.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            usernameText.setText(displayName);
        } else {
            usernameText.setText(getString(R.string.title_user, FormatUtils.shortName(user.getName())));
        }

        memberSinceText.setText(getString(R.string.user_member_since, FormatUtils.formatTimeAgo(this, user.getCreatedUtc())));
        postsCountText.setText(getString(R.string.user_posts_count, FormatUtils.formatCount(user.getPostCount())));
        commentsCountText.setText(getString(R.string.user_comments_count, FormatUtils.formatCount(user.getCommentCount())));

        if (user.isBanned()) {
            bannedText.setVisibility(View.VISIBLE);
        } else {
            bannedText.setVisibility(View.GONE);
        }

        String iconUrl = user.getIconUrl();
        if (iconUrl == null) {
            Glide.with(avatarImage).clear(avatarImage);
            avatarImage.setImageResource(R.drawable.ic_community);
        } else {
            Glide.with(avatarImage).load(iconUrl).circleCrop().into(avatarImage);
        }
    }

    private void loadUserPosts() {
        progressBar.setVisibility(View.VISIBLE);
        errorView.hide();

        LemmyApi.getInstance(this).getUserPosts(username, 1, new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                adapter.setPosts(posts);
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
                errorView.showError(message, () -> loadUserPosts());
            }
        });
    }

    private void loadMoreUserPosts(int page) {
        LemmyApi.getInstance(this).getUserPosts(username, page, new ApiCallback<List<Post>>() {
            @Override
            public void onSuccess(List<Post> posts) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                adapter.addPosts(posts);
                scrollListener.onPageLoaded(posts.isEmpty());
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                Toast.makeText(UserProfileActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
