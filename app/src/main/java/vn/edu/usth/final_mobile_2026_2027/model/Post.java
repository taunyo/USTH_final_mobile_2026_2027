package vn.edu.usth.final_mobile_2026_2027.model;

/**
 * A post of a Lemmy community.
 */
public class Post {

    private int id;
    private String title;
    private String author;        // "name", or "name@instance" for a user of another instance
    private String community;     // "name", or "name@instance" for a community of another instance
    private String body;          // text of the post (markdown), empty for link or image posts
    private String url;           // where the post points to (image, article...), empty for text posts
    private String thumbnailUrl;  // small preview, null when there is none
    private String imageUrl;      // full size image, null when there is none
    private String permalink;     // address of the post on its own instance, e.g. https://lemmy.ml/post/123
    private int score;
    private int numComments;
    private long createdUtc;      // creation date in seconds since 01/01/1970

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumComments() {
        return numComments;
    }

    public void setNumComments(int numComments) {
        this.numComments = numComments;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }
}
