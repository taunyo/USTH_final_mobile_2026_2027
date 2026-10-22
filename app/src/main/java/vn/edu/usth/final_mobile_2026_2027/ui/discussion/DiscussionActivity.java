package vn.edu.usth.final_mobile_2026_2027.ui.discussion;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.utils.ViewUtils;

/**
 * Page 4 - Discussion: a post with all its comments.
 */
public class DiscussionActivity extends AppCompatActivity {

    private static final String EXTRA_POST_ID = "post_id";

    /**
     * Creates the Intent to open this page, e.g.
     * startActivity(DiscussionActivity.newIntent(context, post.getId()));
     */
    public static Intent newIntent(Context context, String postId) {
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

        String postId = getIntent().getStringExtra(EXTRA_POST_ID);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: implement RedditApi.getComments(postId, ...) and RedditParser.parseComments()
        // TODO: create a CommentAdapter, indent each comment according to comment.getDepth()
        // TODO: show the post itself (title, text, image) above the comments
    }
}
