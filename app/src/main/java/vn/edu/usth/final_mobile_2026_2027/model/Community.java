package vn.edu.usth.final_mobile_2026_2027.model;

/**
 * A Lemmy community, like c/technology.
 */
public class Community {

    private String name;         // "technology", or "technology@lemmy.ml" for a community of another instance
    private String title;
    private String description;  // markdown
    private long subscribers;
    private String iconUrl;      // null when the community has no icon

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
