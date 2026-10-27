package vn.edu.usth.final_mobile_2026_2027.ui.discussion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.api.ApiCallback;
import vn.edu.usth.final_mobile_2026_2027.api.LemmyApi;
import vn.edu.usth.final_mobile_2026_2027.model.Comment;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.common.ErrorView;
import vn.edu.usth.final_mobile_2026_2027.utils.ViewUtils;

/**
 * Page 4 - Discussion: a post with all its comments.
 */
public class DiscussionActivity extends AppCompatActivity {

    private static final String EXTRA_POST_ID = "post_id";

    private int postId;
    private CommentAdapter adapter;
    private ProgressBar progressBar;
    private ErrorView errorView;

    /**
     * Creates the Intent to open this page, e.g.
     * startActivity(DiscussionActivity.newIntent(context, post.getId()));
     */
    public static Intent newIntent(Context context, int postId) {
        Intent intent = new Intent(context, DiscussionActivity.class);
        intent.putExtra(EXTRA_POST_ID, postId);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_discussion);
        ViewUtils.applySystemBarsPadding(findViewById(R.id.main));

        postId = getIntent().getIntExtra(EXTRA_POST_ID, 0);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        progressBar = findViewById(R.id.progress_bar);
        errorView = new ErrorView(findViewById(R.id.error_view));

        // The adapter shows the post at the top, then the comments
        adapter = new CommentAdapter();
        RecyclerView recyclerView = findViewById(R.id.recycler_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        loadPost();
    }

    /** Loads the post first, then the comments: for an unknown post only its error is shown. */
    private void loadPost() {
        progressBar.setVisibility(View.VISIBLE);
        errorView.hide();

        LemmyApi.getInstance(this).getPost(postId, new ApiCallback<Post>() {
            @Override
            public void onSuccess(Post post) {
                // The user may have left the page or rotated the screen while the request was running
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                adapter.setPost(post);
                loadComments();
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                // "Retry" loads the whole page again
                errorView.showError(message, () -> loadPost());
            }
        });
    }

    private void loadComments() {
        LemmyApi.getInstance(this).getComments(postId, new ApiCallback<List<Comment>>() {
            @Override
            public void onSuccess(List<Comment> comments) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                adapter.setComments(comments);
                if (comments.isEmpty()) {
                    errorView.showEmpty(getString(R.string.empty_comments));
                }
            }

            @Override
            public void onError(String message) {
                if (isFinishing() || isDestroyed()) {
                    return;
                }
                progressBar.setVisibility(View.GONE);
                errorView.showError(message, () -> loadPost());
            }
        });
    }
}