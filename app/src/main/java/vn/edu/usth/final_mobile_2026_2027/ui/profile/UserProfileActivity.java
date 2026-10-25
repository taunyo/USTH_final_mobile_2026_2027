package vn.edu.usth.final_mobile_2026_2027.ui.profile;

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
 * Page 5 - User profile: public information about a user and the posts they wrote.
 */
public class UserProfileActivity extends AppCompatActivity {

    private static final String EXTRA_USERNAME = "username";

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

        String username = getIntent().getStringExtra(EXTRA_USERNAME);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.title_user, username));
        toolbar.setNavigationOnClickListener(v -> finish());

        RecyclerView recyclerView = findViewById(R.id.recycler_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // TODO: implement LemmyApi.getUser() and fill the header (avatar, number of posts and comments, account age)
        // TODO: implement LemmyApi.getUserPosts() and reuse the PostAdapter of the Home page
        // Note: a user of another instance is called "name@instance", give this full name to the API
    }
}
