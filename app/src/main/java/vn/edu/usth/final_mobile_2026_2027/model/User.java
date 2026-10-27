package vn.edu.usth.final_mobile_2026_2027.model;

/**
 * Public information about a Lemmy account.
 * Lemmy doesn't publish the karma of its users: the profile shows their number of posts and comments.
 */
public class User {

    private String name;         // "name", or "name@instance" for a user of another instance
    private String displayName;
    private boolean banned;
    private int postCount;
    private int commentCount;
    private long createdUtc;     // account creation date in seconds since 01/01/1970
    private String iconUrl;      // null when the user has no avatar

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }

    public int getPostCount() {
        return postCount;
    }

    public void setPostCount(int postCount) {
        this.postCount = postCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public long getCreatedUtc() {
        return createdUtc;
    }

    public void setCreatedUtc(long createdUtc) {
        this.createdUtc = createdUtc;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
