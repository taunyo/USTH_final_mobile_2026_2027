package vn.edu.usth.final_mobile_2026_2027.api;

import android.content.Context;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.model.Comment;
import vn.edu.usth.final_mobile_2026_2027.model.Community;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.model.User;

/**
 * Single entry point to call the Lemmy API. Every screen goes through this class.
 *
 * Usage from an Activity or a Fragment:
 * <pre>
 * LemmyApi.getInstance(this).getPopularPosts(new ApiCallback&lt;List&lt;Post&gt;&gt;() { ... });
 * </pre>
 *
 * Lemmy is made of many servers ("instances") that share their communities. The app reads
 * everything through the public API of one instance, e.g. https://lemmy.world/api/v3/post/list
 * (see the README for the list of endpoints). No account and no API key are needed.
 */
public class LemmyApi {

    // Lemmy 0.19 instance used by the app. To use another instance, only change this line.
    private static final String BASE_URL = "https://lemmy.world/api/v3";
    // Lemmy doesn't require it, but it tells the admins of the instance who sends the requests
    private static final String USER_AGENT = "USTH-Lemmy/1.0 (Android, USTH student project)";

    private static LemmyApi instance;

    private final Context context;
    private final RequestQueue queue;

    private LemmyApi(Context context) {
        this.context = context.getApplicationContext();
        this.queue = Volley.newRequestQueue(this.context);
    }

    /** Only one LemmyApi (and one Volley queue) is created for the whole app. */
    public static synchronized LemmyApi getInstance(Context context) {
        if (instance == null) {
            instance = new LemmyApi(context);
        }
        return instance;
    }

    // ---------------------------------------------------------------------------------------------
    // Home
    // ---------------------------------------------------------------------------------------------

    /**
     * Posts of the home feed, from all the communities: GET /post/list?type_=All&sort=Hot
     * This method is the example to follow for the other ones.
     */
    public void getPopularPosts(ApiCallback<List<Post>> callback) {
        String url = BASE_URL + "/post/list?type_=All&sort=Hot&limit=20";

        get(url, response -> {
            try {
                callback.onSuccess(LemmyParser.parsePosts(response));
            } catch (JSONException e) {
                callback.onError(context.getString(R.string.error_parsing));
            }
        }, callback);
    }

    // ---------------------------------------------------------------------------------------------
    // Search
    // ---------------------------------------------------------------------------------------------

    /**
     * Communities matching a text: GET /search?q={query}&type_=Communities&listing_type=All&sort=TopAll
     * sort=TopAll puts the biggest communities first.
     */
    public void searchCommunities(String query, ApiCallback<List<Community>> callback) {
        // The text can contain spaces or accents: Uri.encode() makes it safe to put in the url
        String url = BASE_URL + "/search?q=" + Uri.encode(query)
                + "&type_=Communities&listing_type=All&sort=TopAll";

        get(url, response -> {
            try {
                callback.onSuccess(LemmyParser.parseCommunities(response));
            } catch (JSONException e) {
                callback.onError(context.getString(R.string.error_parsing));
            }
        }, callback);
    }

    // ---------------------------------------------------------------------------------------------
    // Community
    // ---------------------------------------------------------------------------------------------

    /**
     * Information about a community: GET /community?name={name}
     * An unknown community gives an HTTP 404 error.
     */
    public void getCommunity(String name, ApiCallback<Community> callback) {
        String url = BASE_URL + "/community?name=" + name;

        get(url, response -> {
            try {
                callback.onSuccess(LemmyParser.parseCommunity(response));
            } catch (JSONException e) {
                callback.onError(context.getString(R.string.error_parsing));
            }
        }, callback);
    }

    /**
     * Posts of a community: GET /post/list?community_name={name}&sort={sort}
     * sort is "Hot", "New", "TopDay"...
     */
    public void getCommunityPosts(String name, String sort, ApiCallback<List<Post>> callback) {
        String url = BASE_URL + "/post/list?community_name=" + name + "&sort=" + sort;

        // Same answer as the home feed: { "posts": [ post views... ] }
        get(url, response -> {
            try {
                callback.onSuccess(LemmyParser.parsePosts(response));
            } catch (JSONException e) {
                callback.onError(context.getString(R.string.error_parsing));
            }
        }, callback);
    }

    // ---------------------------------------------------------------------------------------------
    // Discussion
    // ---------------------------------------------------------------------------------------------

    /** A post alone: GET /post?id={postId} */
    public void getPost(int postId, ApiCallback<Post> callback) {
        // TODO (Discussion page)
    }

    /**
     * Comments of a post: GET /comment/list?post_id={postId}&max_depth=8
     * The comments come as a flat list in no particular order: the "path" of each comment
     * tells where it is in the tree (see the README).
     */
    public void getComments(int postId, ApiCallback<List<Comment>> callback) {
        // TODO (Discussion page)
    }

    // ---------------------------------------------------------------------------------------------
    // User profile
    // ---------------------------------------------------------------------------------------------

    /** Public information about a user: GET /user?username={username}, in "person_view" */
    public void getUser(String username, ApiCallback<User> callback) {
        // TODO (Profile page)
    }

    /** Posts written by a user: GET /user?username={username}&sort=New, in "posts" */
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

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return Response.success(readText(response),
                        HttpHeaderParser.parseCacheHeaders(response));
            }
        };
        queue.add(request);
    }

    /**
     * Returns the text of an answer. JSON is always UTF-8, but Lemmy doesn't say it
     * ("Content-Type: application/json" without charset), and then StringRequest reads it
     * as ISO-8859-1: "Musk’s" would become "Muskâ€™s". So we always read it as UTF-8.
     */
    static String readText(NetworkResponse response) {
        return new String(response.data, StandardCharsets.UTF_8);
    }

    private String getErrorMessage(VolleyError error) {
        if (error.networkResponse == null) {
            return context.getString(R.string.error_network);
        }
        return context.getString(R.string.error_http, error.networkResponse.statusCode);
    }
}
