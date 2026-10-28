package vn.edu.usth.final_mobile_2026_2027.api;

import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.model.Comment;
import vn.edu.usth.final_mobile_2026_2027.model.Community;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.model.User;

import static org.junit.Assert.*;

public class LemmyParserTest {

    private String loadResource(String filename) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(filename)) {
            assertNotNull("Resource not found: " + filename, is);
            byte[] bytes = new byte[is.available()];
            is.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    @Test
    public void testParsePosts() throws Exception {
        String json = loadResource("posts.json");
        List<Post> posts = LemmyParser.parsePosts(json);
        assertNotNull(posts);
        assertEquals(1, posts.size());
        Post post = posts.get(0);
        assertEquals(1, post.getId());
        assertEquals("Test Post", post.getTitle());
        assertEquals("alice", post.getAuthor());
        assertEquals("technology", post.getCommunity());
        assertEquals(10, post.getScore());
        assertEquals(5, post.getNumComments());
    }

    @Test
    public void testParseCommunities() throws Exception {
        String json = loadResource("communities.json");
        List<Community> communities = LemmyParser.parseCommunities(json);
        assertNotNull(communities);
        assertEquals(1, communities.size());
        Community community = communities.get(0);
        assertEquals("technology", community.getName());
        assertEquals("Technology", community.getTitle());
        assertEquals(1000, community.getSubscribers());
    }

    @Test
    public void testParseCommunity() throws Exception {
        String json = loadResource("community.json");
        Community community = LemmyParser.parseCommunity(json);
        assertNotNull(community);
        assertEquals("technology", community.getName());
        assertEquals("Technology", community.getTitle());
    }

    @Test
    public void testParsePost() throws Exception {
        String json = loadResource("post.json");
        Post post = LemmyParser.parsePost(json);
        assertNotNull(post);
        assertEquals(1, post.getId());
        assertEquals("Test Post", post.getTitle());
        assertEquals("alice", post.getAuthor());
    }

    @Test
    public void testParseComments() throws Exception {
        String json = loadResource("comments.json");
        List<Comment> comments = LemmyParser.parseComments(json);
        assertNotNull(comments);
        assertEquals(1, comments.size());
        Comment comment = comments.get(0);
        assertEquals(10, comment.getId());
        assertEquals("First comment", comment.getBody());
        assertEquals("bob", comment.getAuthor());
        assertEquals(3, comment.getScore());
        assertEquals(0, comment.getDepth());
    }

    @Test
    public void testParseUser() throws Exception {
        String json = loadResource("user.json");
        User user = LemmyParser.parseUser(json);
        assertNotNull(user);
        assertEquals("alice", user.getName());
        assertEquals("Alice Wonderland", user.getDisplayName());
        assertFalse(user.isBanned());
        assertEquals(12, user.getPostCount());
        assertEquals(34, user.getCommentCount());
    }
}
