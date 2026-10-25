package vn.edu.usth.final_mobile_2026_2027.ui.community;

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
 * Page 3 - Community: information about a community and its posts.
 */
public class CommunityActivity extends AppCompatActivity {

    private static final String EXTRA_COMMUNITY_NAME = "community_name";

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

        String communityName = getIntent().getStringExtra(EXTRA_COMMUNITY_NAME);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_community, communityName));
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: implement LemmyApi.getCommunity() and fill the header (icon, title, members, description)
        // TODO: implement LemmyApi.getCommunityPosts() and reuse the PostAdapter of the Home page
        // TODO (bonus): let the user choose the sort order (Hot, New, TopDay)
    }
}
