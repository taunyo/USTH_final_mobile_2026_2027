package vn.edu.usth.final_mobile_2026_2027.ui.subreddit;

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
 * Page 3 - Subreddit: information about a subreddit and its posts.
 */
public class SubredditActivity extends AppCompatActivity {

    private static final String EXTRA_SUBREDDIT_NAME = "subreddit_name";

    /**
     * Creates the Intent to open this page, e.g.
     * startActivity(SubredditActivity.newIntent(context, "android"));
     */
    public static Intent newIntent(Context context, String subredditName) {
        Intent intent = new Intent(context, SubredditActivity.class);
        intent.putExtra(EXTRA_SUBREDDIT_NAME, subredditName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_subreddit);
        ViewUtils.applySystemBarsPadding(findViewById(R.id.main));

        String subredditName = getIntent().getStringExtra(EXTRA_SUBREDDIT_NAME);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_subreddit, subredditName));
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: implement RedditApi.getSubreddit() and fill the header (icon, title, members, description)
        // TODO: implement RedditApi.getSubredditPosts() and reuse the PostAdapter of the Home page
        // TODO (bonus): let the user choose the sort order (hot, new, top)
    }
}
