package vn.edu.usth.final_mobile_2026_2027.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.model.Post;

/**
 * Converts the JSON returned by Lemmy into model objects.
 *
 * Lemmy returns "views": an item grouped with everything needed to display it, e.g. a post view:
 *   { "post": { ... }, "creator": { ... }, "community": { ... }, "counts": { "score": ..., "comments": ... } }
 * and a list of posts is:  { "posts": [ post views... ], "next_page": "..." }
 */
public class LemmyParser {

    private LemmyParser() {
        // Only static methods
    }

    /** Parses a list of posts (home feed, community). */
    public static List<Post> parsePosts(String json) throws JSONException {
        JSONArray views = new JSONObject(json).getJSONArray("posts");

        List<Post> posts = new ArrayList<>();
        for (int i = 0; i < views.length(); i++) {
            posts.add(parsePost(views.getJSONObject(i)));
        }
        return posts;
    }

    // TODO (Search page): parseCommunities(String json) for /search, the views are in "communities"
    // TODO (Community page): parseCommunity(String json) for /community, the view is in "community_view"
    // TODO (Discussion page): parsePost(String json) for /post, the view is in "post_view"
    // TODO (Discussion page): parseComments(String json), rebuild the tree with the "path" of each comment
    // TODO (Profile page): parseUser(String json) for /user, the view is in "person_view"

    private static Post parsePost(JSONObject view) throws JSONException {
        JSONObject data = view.getJSONObject("post");
        JSONObject counts = view.getJSONObject("counts");

        Post post = new Post();
        post.setId(data.getInt("id"));
        post.setTitle(getString(data, "name"));
        post.setAuthor(getFullName(view.getJSONObject("creator")));
        post.setCommunity(getFullName(view.getJSONObject("community")));
        post.setBody(getString(data, "body"));
        post.setUrl(getString(data, "url"));
        post.setThumbnailUrl(getImageUrl(data, "thumbnail_url"));
        post.setPermalink(getString(data, "ap_id"));
        post.setScore(counts.optInt("score"));
        post.setNumComments(counts.optInt("comments"));
        post.setCreatedUtc(getDate(data, "published"));

        // For an image post, url is the full size image (url_content_type is e.g. "image/jpeg").
        // For a link post it is a web page ("text/html"): no image then.
        if (getString(data, "url_content_type").startsWith("image/")) {
            post.setImageUrl(getString(data, "url"));
        }
        return post;
    }

    /**
     * Returns the name to give to the API and to the pages for a user or a community:
     * "technology" for a community of our instance, "technology@lemmy.ml" for one of another instance.
     */
    private static String getFullName(JSONObject json) {
        String name = getString(json, "name");
        if (json.optBoolean("local")) {
            return name;
        }
        // actor_id is the address of the item on its own instance, e.g. https://lemmy.ml/c/technology
        return name + "@" + URI.create(getString(json, "actor_id")).getHost();
    }

    /**
     * Converts a date like "2026-10-25T08:22:47.712233Z" into seconds since 01/01/1970.
     * Returns 0 if the field is missing or can't be read.
     */
    private static long getDate(JSONObject json, String key) {
        try {
            return Instant.parse(getString(json, key)).getEpochSecond();
        } catch (DateTimeParseException e) {
            return 0;
        }
    }

    /** Returns the text value of a field, or "" if it is missing or null. */
    private static String getString(JSONObject json, String key) {
        if (json.isNull(key)) {
            return "";
        }
        return json.optString(key);
    }

    /** Returns an image url, or null if the field is missing or doesn't contain an url. */
    private static String getImageUrl(JSONObject json, String key) {
        String value = getString(json, key);
        if (value.startsWith("http")) {
            return value;
        }
        return null;
    }
}
