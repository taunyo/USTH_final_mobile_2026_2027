package vn.edu.usth.final_mobile_2026_2027.model;

/**
 * A comment of a discussion (kind "t1" in the Reddit API).
 * The comment tree is stored as a flat list: the depth tells how far a reply is indented.
 */
public class Comment {

    private String id;
    private String author;
    private String body;
    private int score;
    private long createdUtc;  // creation date in seconds since 01/01/1970
    private int depth;        // 0 for a direct answer to the post, 1 for a reply to it, etc.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
