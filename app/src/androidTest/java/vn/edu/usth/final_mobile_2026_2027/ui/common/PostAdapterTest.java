package vn.edu.usth.final_mobile_2026_2027.ui.common;

import android.content.Context;
import android.content.Intent;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.community.CommunityActivity;
import vn.edu.usth.final_mobile_2026_2027.ui.discussion.DiscussionActivity;
import vn.edu.usth.final_mobile_2026_2027.ui.profile.UserProfileActivity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Tests of PostAdapter. They run on a phone or an emulator:
 * ./gradlew connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=vn.edu.usth.final_mobile_2026_2027.ui.common.PostAdapterTest
 */
@RunWith(AndroidJUnit4.class)
public class PostAdapterTest {

    private RecordingContext context;
    private FrameLayout parent;
    private PostAdapter adapter;

    @Before
    public void setUp() {
        context = new RecordingContext(InstrumentationRegistry.getInstrumentation().getTargetContext());
        parent = new FrameLayout(context);
        adapter = new PostAdapter();
    }

    // ---------------------------------------------------------------------------------------------
    // Content of the list
    // ---------------------------------------------------------------------------------------------

    @Test
    public void setPosts_replacesTheList() {
        adapter.setPosts(asList(post(1), post(2)));
        adapter.setPosts(asList(post(3)));

        assertEquals(1, adapter.getItemCount());
    }

    @Test
    public void addPosts_skipsPostsAlreadyShown() {
        adapter.setPosts(asList(post(1), post(2)));
        // The ranking changed between the 2 pages: post 2 comes back on the second page
        adapter.addPosts(asList(post(2), post(3)));

        assertEquals(3, adapter.getItemCount());
    }

    // ---------------------------------------------------------------------------------------------
    // Content of a row
    // ---------------------------------------------------------------------------------------------

    @Test
    public void bind_showsTitleDateAndComments() {
        adapter.setPosts(asList(post(1)));

        View row = bind(0).itemView;

        assertEquals("Post 1", text(row, R.id.post_title));
        assertEquals("just now", text(row, R.id.post_date));
        assertEquals("1 comment", text(row, R.id.post_comments));
    }

    @Test
    public void bind_showsShortNames() {
        adapter.setPosts(asList(post(1, "technology@lemmy.ml", "akunohana@piefed.blahaj.zone", null)));

        View row = bind(0).itemView;

        assertEquals("c/technology", text(row, R.id.post_community));
        assertEquals("u/akunohana", text(row, R.id.post_author));
    }

    @Test
    public void bind_showsNegativeScore() {
        Post downvoted = post(1);
        downvoted.setScore(-1234);
        adapter.setPosts(asList(downvoted));

        assertEquals("-1.2k points", text(bind(0).itemView, R.id.post_score));
    }

    @Test
    public void bind_hidesThumbnailWhenNull() {
        adapter.setPosts(asList(
                post(1, "a", "b", "https://lemmy.world/pictrs/image/x.webp"),
                post(2, "a", "b", null)));
        PostAdapter.PostViewHolder row = newRow();

        show(row, 0);
        assertEquals(View.VISIBLE, row.itemView.findViewById(R.id.post_thumbnail).getVisibility());

        // Same row for another post, like a row recycled by the RecyclerView
        show(row, 1);
        assertEquals(View.GONE, row.itemView.findViewById(R.id.post_thumbnail).getVisibility());
    }

    // ---------------------------------------------------------------------------------------------
    // Clicks: toUri() writes the target activity and the extras of an Intent in one text
    // ---------------------------------------------------------------------------------------------

    @Test
    public void clickOnPost_opensItsDiscussion() {
        adapter.setPosts(asList(post(42)));

        bind(0).itemView.performClick();

        assertEquals(DiscussionActivity.newIntent(context, 42).toUri(0), context.startedIntent.toUri(0));
    }

    @Test
    public void clickOnCommunity_opensItWithTheFullName() {
        adapter.setPosts(asList(post(1, "technology@lemmy.ml", "u", null)));

        bind(0).itemView.findViewById(R.id.post_community).performClick();

        assertEquals(CommunityActivity.newIntent(context, "technology@lemmy.ml").toUri(0),
                context.startedIntent.toUri(0));
    }

    @Test
    public void clickOnAuthor_opensTheProfileWithTheFullName() {
        adapter.setPosts(asList(post(1, "c", "akunohana@piefed.blahaj.zone", null)));

        bind(0).itemView.findViewById(R.id.post_author).performClick();

        assertEquals(UserProfileActivity.newIntent(context, "akunohana@piefed.blahaj.zone").toUri(0),
                context.startedIntent.toUri(0));
    }

    // ---------------------------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------------------------

    /** A post titled "Post {id}", created just now, with 0 points and 1 comment. */
    private static Post post(int id, String community, String author, String thumbnailUrl) {
        Post post = new Post();
        post.setId(id);
        post.setTitle("Post " + id);
        post.setCommunity(community);
        post.setAuthor(author);
        post.setThumbnailUrl(thumbnailUrl);
        post.setNumComments(1);
        post.setCreatedUtc(System.currentTimeMillis() / 1000);
        return post;
    }

    private static Post post(int id) {
        return post(id, "c", "u", null);
    }

    /** Creates an empty row, like the RecyclerView does. */
    private PostAdapter.PostViewHolder newRow() {
        return adapter.onCreateViewHolder(parent, 0);
    }

    /** Shows the post at this position in the row. It runs on the main thread, like Glide requires. */
    private void show(PostAdapter.PostViewHolder row, int position) {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> adapter.onBindViewHolder(row, position));
    }

    /** Creates a row and shows in it the post at this position. */
    private PostAdapter.PostViewHolder bind(int position) {
        PostAdapter.PostViewHolder row = newRow();
        show(row, position);
        return row;
    }

    private static String text(View row, int id) {
        return ((TextView) row.findViewById(id)).getText().toString();
    }

    /**
     * Context with the theme of the app (Material components need it) that records
     * the activity a click wants to open, instead of really opening it.
     */
    private static class RecordingContext extends ContextThemeWrapper {
        Intent startedIntent;

        RecordingContext(Context base) {
            super(base, R.style.Theme_USTH_final_mobile_2026_2027);
        }

        @Override
        public void startActivity(Intent intent) {
            startedIntent = intent;
        }
    }
}
