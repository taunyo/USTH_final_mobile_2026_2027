package vn.edu.usth.final_mobile_2026_2027.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.model.Post;

/**
 * Converts the JSON returned by Reddit into model objects.
 *
 * Every Reddit object is wrapped like this:  { "kind": "t3", "data": { ...fields... } }
 * and a list of objects is a "Listing":     { "kind": "Listing", "data": { "after": "...", "children": [ ... ] } }
 *
 * Kinds: t1 = comment, t2 = user, t3 = post, t5 = subreddit.
 */
public class RedditParser {

    private RedditParser() {
        // Only static methods
    }

    /** Parses a Listing of posts (home feed, subreddit, user posts). */
    public static List<Post> parsePosts(String json) throws JSONException {
        JSONArray children = new JSONObject(json).getJSONObject("data").getJSONArray("children");

        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < children.length(); i++) {
            JSONObject data = children.getJSONObject(i).getJSONObject("data");
            posts.add(parsePost(data));
        }
        return posts;
    }

    // TODO (Search page): parseSubreddits(String json) for /subreddits/search
    // TODO (Subreddit page): parseSubreddit(String json) for /r/{name}/about
    // TODO (Discussion page): parseComments(String json), replies are nested in "replies"
    // TODO (Profile page): parseUser(String json) for /user/{username}/about

    private static Post parsePost(JSONObject data) {
        Post post = new Post();
        post.setId(getString(data, "id"));
        post.setTitle(getString(data, "title"));
        post.setAuthor(getString(data, "author"));
        post.setSubreddit(getString(data, "subreddit"));
        post.setSelfText(getString(data, "selftext"));
        post.setUrl(getString(data, "url"));
        post.setThumbnailUrl(getImageUrl(data, "thumbnail"));
        post.setPermalink(getString(data, "permalink"));
        post.setScore(data.optInt("score"));
        post.setNumComments(data.optInt("num_comments"));
        post.setCreatedUtc(data.optLong("created_utc"));
        // TODO: read the big image from preview.images[0].source.url
        return post;
    }

    /** Returns the text value of a field, or "" if it is missing or null. */
    private static String getString(JSONObject json, String key) {
        if (json.isNull(key)) {
            return "";
        }
        return json.optString(key);
    }

    /**
     * Returns an image url, or null if the field does not contain one.
     * For example "thumbnail" can contain "self", "default" or "nsfw" instead of an url.
     */
    private static String getImageUrl(JSONObject json, String key) {
        String value = getString(json, key);
        if (value.startsWith("http")) {
            return value;
        }
        return null;
    }
}
