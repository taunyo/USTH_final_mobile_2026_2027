package vn.edu.usth.final_mobile_2026_2027.api;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.model.Comment;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.model.Subreddit;
import vn.edu.usth.final_mobile_2026_2027.model.User;

/**
 * Single entry point to call the Reddit API. Every screen goes through this class.
 *
 * Usage from an Activity or a Fragment:
 * <pre>
 * RedditApi.getInstance(this).getPopularPosts(new ApiCallback&lt;List&lt;Post&gt;&gt;() { ... });
 * </pre>
 *
 * The public API works by adding ".json" at the end of a reddit.com url,
 * e.g. https://www.reddit.com/r/android.json (see the README for the list of endpoints).
 */
public class RedditApi {

    private static final String BASE_URL = "https://www.reddit.com";
    // Reddit refuses requests that don't have a descriptive User-Agent
    private static final String USER_AGENT = "android:vn.edu.usth.final_mobile_2026_2027:v1.0 (USTH student project)";

    private static RedditApi instance;

    private final Context context;
    private final RequestQueue queue;

    private RedditApi(Context context) {
        this.context = context.getApplicationContext();
        this.queue = Volley.newRequestQueue(this.context);
    }

    /** Only one RedditApi (and one Volley queue) is created for the whole app. */
    public static synchronized RedditApi getInstance(Context context) {
        if (instance == null) {
            instance = new RedditApi(context);
        }
        return instance;
    }

    // ---------------------------------------------------------------------------------------------
    // Home
    // ---------------------------------------------------------------------------------------------

    /**
     * Posts of the home feed: GET /r/popular/hot.json
     * This method is the example to follow for the other ones.
     */
    public void getPopularPosts(ApiCallback<List<Post>> callback) {
        String url = BASE_URL + "/r/popular/hot.json?raw_json=1";

        get(url, response -> {
            try {
                callback.onSuccess(RedditParser.parsePosts(response));
            } catch (JSONException e) {
                callback.onError(context.getString(R.string.error_parsing));
            }
        }, callback);
    }

    // ---------------------------------------------------------------------------------------------
    // Search
    // ---------------------------------------------------------------------------------------------

    /** Subreddits matching a text: GET /subreddits/search.json?q={query} */
    public void searchSubreddits(String query, ApiCallback<List<Subreddit>> callback) {
        // TODO (Search page): encode the query with Uri.encode() and parse the result
    }

    // ---------------------------------------------------------------------------------------------
    // Subreddit
    // ---------------------------------------------------------------------------------------------

    /** Information about a subreddit: GET /r/{name}/about.json */
    public void getSubreddit(String name, ApiCallback<Subreddit> callback) {
        // TODO (Subreddit page)
    }

    /** Posts of a subreddit: GET /r/{name}/hot.json (also /new.json, /top.json) */
    public void getSubredditPosts(String name, ApiCallback<List<Post>> callback) {
        // TODO (Subreddit page)
    }

    // ---------------------------------------------------------------------------------------------
    // Discussion
    // ---------------------------------------------------------------------------------------------

    /**
     * Comments of a post: GET /comments/{postId}.json
     * The answer is a JSON array: [0] contains the post, [1] contains the comments.
     */
    public void getComments(String postId, ApiCallback<List<Comment>> callback) {
        // TODO (Discussion page)
    }

    // ---------------------------------------------------------------------------------------------
    // User profile
    // ---------------------------------------------------------------------------------------------

    /** Public information about a user: GET /user/{username}/about.json */
    public void getUser(String username, ApiCallback<User> callback) {
        // TODO (Profile page)
    }

    /** Posts written by a user: GET /user/{username}/submitted.json */
    public void getUserPosts(String username, ApiCallback<List<Post>> callback) {
        // TODO (Profile page)
    }

    // ---------------------------------------------------------------------------------------------
    // Common code
    // ---------------------------------------------------------------------------------------------

    /**
     * Sends a GET request and gives the JSON text to onResponse.
     * All the methods above must use it, so every request has the right headers.
     */
    private void get(String url, Response.Listener<String> onResponse, ApiCallback<?> callback) {
        StringRequest request = new StringRequest(Request.Method.GET, url, onResponse,
                error -> callback.onError(getErrorMessage(error))) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("User-Agent", USER_AGENT);
                return headers;
            }
        };
        queue.add(request);
    }

    private String getErrorMessage(VolleyError error) {
        if (error.networkResponse == null) {
            return context.getString(R.string.error_network);
        }
        return context.getString(R.string.error_http, error.networkResponse.statusCode);
    }
}
