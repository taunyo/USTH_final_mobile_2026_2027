package vn.edu.usth.final_mobile_2026_2027.api;

/**
 * Receives the result of a request to the Reddit API.
 * Both methods are called on the main thread, so the UI can be updated directly.
 */
public interface ApiCallback<T> {

    void onSuccess(T result);

    void onError(String message);
}
