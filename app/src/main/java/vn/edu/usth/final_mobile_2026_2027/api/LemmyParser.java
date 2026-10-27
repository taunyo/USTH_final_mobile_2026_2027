package vn.edu.usth.final_mobile_2026_2027.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vn.edu.usth.final_mobile_2026_2027.model.Comment;
import vn.edu.usth.final_mobile_2026_2027.model.Community;
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

    /**
     * Parses the communities found by a search: { "communities": [ community views... ], ... }
     * The NSFW communities are skipped.
     */
    public static List<Community> parseCommunities(String json) throws JSONException {
        JSONArray views = new JSONObject(json).getJSONArray("communities");

        List<Community> communities = new ArrayList<>();
        for (int i = 0; i < views.length(); i++) {
            JSONObject view = views.getJSONObject(i);
            if (view.getJSONObject("community").optBoolean("nsfw")) {
                continue;
            }
            communities.add(parseCommunity(view));
        }
        return communities;
    }

    /**
     * Parses the information about a community: { "community_view": { community view }, ... }
     * It is the same view as in the search.
     */
    public static Community parseCommunity(String json) throws JSONException {
        return parseCommunity(new JSONObject(json).getJSONObject("community_view"));
    }

    /** Parses a post alone, from /post: { "post_view": { post view }, ... } */
    public static Post parsePost(String json) throws JSONException {
        return parsePost(new JSONObject(json).getJSONObject("post_view"));
    }

    /** A comment view: { "comment": { ... }, "creator": { ... }, "counts": { "score": ... } } */
    private static Comment parseComment(JSONObject view) throws JSONException {
        JSONObject data = view.getJSONObject("comment");
        JSONObject counts = view.getJSONObject("counts");

        Comment comment = new Comment();
        comment.setId(data.getInt("id"));
        comment.setAuthor(getFullName(view.getJSONObject("creator")));
        comment.setBody(getString(data, "content"));
        comment.setScore(counts.optInt("score"));
        comment.setCreatedUtc(getDate(data, "published"));

        // "0.12" is a direct answer to the post (depth 0), "0.12.45" a reply to it (depth 1)
        String[] path = getString(data, "path").split("\\.");
        comment.setDepth(path.length - 2);
        return comment;
    }

    /**
     * Parses the comments of a post: { "comments": [ comment views... ] }
     * Lemmy gives them in no particular order.
     */
    public static List<Comment> parseComments(String json) throws JSONException {
        JSONArray views = new JSONObject(json).getJSONArray("comments");

        List<Comment> comments = new ArrayList<>();
        Map<Integer, Integer> parentIds = new HashMap<>();
        for (int i = 0; i < views.length(); i++) {
            JSONObject view = views.getJSONObject(i);
            Comment comment = parseComment(view);
            comments.add(comment);
            parentIds.put(comment.getId(), getParentId(view));
        }

        Map<Integer, List<Comment>> replies = new HashMap<>();
        for (Comment comment : comments) {
            int parentId = parentIds.get(comment.getId());
            if (parentId != 0 && !parentIds.containsKey(parentId)) {
                parentId = 0;
            }
            if (!replies.containsKey(parentId)) {
                replies.put(parentId, new ArrayList<>());
            }
            replies.get(parentId).add(comment);
        }

        List<Comment> ordered = new ArrayList<>();
        addInTreeOrder(0, replies, ordered);
        return ordered;
    }

    /**
     * Adds the replies to parentId to the list, each one directly followed by its own replies.
     * The method calls itself for each reply, so it goes down the tree as deep as needed.
     */
    private static void addInTreeOrder(int parentId, Map<Integer, List<Comment>> replies, List<Comment> ordered) {
        List<Comment> children = replies.get(parentId);
        if (children == null) {
            return; // no replies
        }
        for (Comment child : children) {
            ordered.add(child);
            addInTreeOrder(child.getId(), replies, ordered);
        }
    }

    /** The parent of a comment is the next to last id of its path: "0.12.45" gives 12, "0.12" gives 0. */
    private static int getParentId(JSONObject view) throws JSONException {
        String[] path = getString(view.getJSONObject("comment"), "path").split("\\.");
        if (path.length < 2) {
            return 0;
        }
        return Integer.parseInt(path[path.length - 2]);
    }


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
     * A community view: { "community": { ... }, "counts": { "subscribers": ... }, ... }
     * The search and the community page both return it.
     */
    private static Community parseCommunity(JSONObject view) throws JSONException {
        JSONObject data = view.getJSONObject("community");
        JSONObject counts = view.getJSONObject("counts");

        Community community = new Community();
        community.setName(getFullName(data));
        community.setTitle(getString(data, "title"));
        community.setDescription(getString(data, "description"));
        community.setSubscribers(counts.optLong("subscribers"));
        community.setIconUrl(getImageUrl(data, "icon"));
        return community;
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
