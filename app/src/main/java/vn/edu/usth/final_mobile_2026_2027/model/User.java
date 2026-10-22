package vn.edu.usth.final_mobile_2026_2027.model;

/**
 * Public information about a Reddit account (kind "t2" in the Reddit API).
 */
public class User {

    private String name;         // name without the "u/" prefix
    private int linkKarma;       // karma earned with posts
    private int commentKarma;    // karma earned with comments
    private long createdUtc;     // account creation date in seconds since 01/01/1970
    private String iconUrl;      // null when the user has no avatar

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLinkKarma() {
        return linkKarma;
    }

    public void setLinkKarma(int linkKarma) {
        this.linkKarma = linkKarma;
    }

    public int getCommentKarma() {
        return commentKarma;
    }

    public void setCommentKarma(int commentKarma) {
        this.commentKarma = commentKarma;
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
