package vn.edu.usth.final_mobile_2026_2027.model;

/**
 * A community of Reddit, like r/android (kind "t5" in the Reddit API).
 */
public class Subreddit {

    private String name;         // name without the "r/" prefix, e.g. "android"
    private String title;
    private String description;
    private long subscribers;
    private String iconUrl;      // null when the subreddit has no icon

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(long subscribers) {
        this.subscribers = subscribers;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
