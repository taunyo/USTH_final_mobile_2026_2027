package vn.edu.usth.final_mobile_2026_2027.api;

import com.android.volley.NetworkResponse;

import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests of the reading of the answers of Lemmy (LemmyApi.readText()).
 * ./gradlew testDebugUnitTest --tests "*LemmyApiEncodingTest"
 */
public class LemmyApiEncodingTest {

    @Test
    public void readText_withoutCharset_isUtf8() {
        // Lemmy answers "application/json" without charset: Volley would read it as ISO-8859-1
        // and show "Musk’s" as "Muskâ€™s"
        assertEquals("{\"name\":\"Musk’s café\"}",
                LemmyApi.readText(response("{\"name\":\"Musk’s café\"}", "application/json")));
    }

    @Test
    public void readText_withUtf8Charset_isUtf8() {
        assertEquals("{\"name\":\"Hà Nội\"}",
                LemmyApi.readText(response("{\"name\":\"Hà Nội\"}", "application/json; charset=utf-8")));
    }

    /** Builds the answer that Volley receives: the text sent as UTF-8 bytes, like Lemmy does. */
    private NetworkResponse response(String text, String contentType) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", contentType);
        return new NetworkResponse(text.getBytes(StandardCharsets.UTF_8), headers);
    }
}
