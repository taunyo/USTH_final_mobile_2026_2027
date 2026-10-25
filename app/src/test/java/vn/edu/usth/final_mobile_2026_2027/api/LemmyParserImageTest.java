package vn.edu.usth.final_mobile_2026_2027.api;

import org.json.JSONException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Unit tests of the full size image of a post (Post.getImageUrl()), read by LemmyParser.
 * ./gradlew testDebugUnitTest --tests "*LemmyParserImageTest"
 */
public class LemmyParserImageTest {

    @Test
    public void imagePost_hasImageUrl() throws JSONException {
        assertEquals("https://lemmy.zip/pictrs/image/a.avif",
                imageUrl("\"url\":\"https://lemmy.zip/pictrs/image/a.avif\",\"url_content_type\":\"image/avif\""));
    }

    @Test
    public void linkPost_hasNoImageUrl() throws JSONException {
        // A link to an article: the url is a web page, not an image
        assertNull(imageUrl("\"url\":\"https://example.com/article\",\"url_content_type\":\"text/html\""));
    }

    @Test
    public void textPost_hasNoImageUrl() throws JSONException {
        assertNull(imageUrl("\"url\":null,\"url_content_type\":null"));
    }

    /**
     * Builds the smallest answer of /post/list with one post containing postFields,
     * and returns the image url found by the parser.
     */
    private String imageUrl(String postFields) throws JSONException {
        String json = "{\"posts\":[{"
                + "\"post\":{\"id\":1,\"name\":\"t\"," + postFields + "},"
                + "\"creator\":{\"name\":\"u\",\"local\":true},"
                + "\"community\":{\"name\":\"c\",\"local\":true},"
                + "\"counts\":{}}]}";
        return LemmyParser.parsePosts(json).get(0).getImageUrl();
    }
}
