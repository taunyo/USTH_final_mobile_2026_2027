# USTH Reddit

Final project for the Mobile Application Development course (USTH, 2026-2027).

An Android client for Reddit, written in Java. It reads Reddit's public data (popular posts, subreddits, discussions, user profiles) through the Reddit API. **No Reddit account is needed** and the app has no login page.

## Team

| Member | Main responsibility |
|--------|---------------------|
| _name_ | Home page + shared post list |
| _name_ | Search page + Subreddit page |
| _name_ | Discussion page |
| _name_ | User profile page + formatting helpers |

## The 5 pages

| # | Page | Class | Reddit endpoint(s) | Opened from |
|---|------|-------|--------------------|-------------|
| 1 | Home | `ui/home/HomeFragment` | `/r/popular/hot.json` | bottom navigation |
| 2 | Search | `ui/search/SearchFragment` | `/subreddits/search.json?q=...` | bottom navigation |
| 3 | Subreddit | `ui/subreddit/SubredditActivity` | `/r/{name}/about.json`, `/r/{name}/hot.json` | a search result, a click on "r/..." |
| 4 | Discussion | `ui/discussion/DiscussionActivity` | `/comments/{postId}.json` | a click on a post |
| 5 | User profile | `ui/profile/UserProfileActivity` | `/user/{name}/about.json`, `/user/{name}/submitted.json` | a click on "u/..." |

```
            ┌──────────── bottom navigation ────────────┐
            │                                           │
         [Home] ──── post ───► [Discussion]          [Search]
            │                       │                   │
            │ "r/..."               │ "u/..."           │ result
            ▼                       ▼                   ▼
       [Subreddit] ── post ──► [Discussion]        [Subreddit]
            │
            │ "u/..."
            ▼
     [User profile] ── post ──► [Discussion]
```

## Tech stack

- **Java 11** with **XML layouts** and **Material 3** components
- **Volley**: HTTP requests
- **org.json** (built into Android): JSON parsing
- **Glide**: image loading (thumbnails, avatars, icons)
- **RecyclerView**: every list of the app
- `minSdk` / `targetSdk` / `compileSdk` = **37** (Android 17)

## Getting started

1. Install a recent version of **Android Studio** (it must support Android Gradle Plugin 9.4).
2. Clone the repository and open the folder in Android Studio (`File > Open`).
3. Wait for the Gradle sync to finish. Android Studio downloads the SDK 37 if it is missing.
4. Create an emulator with **Android 17 (API 37)** in the Device Manager.
5. Click on **Run ▶**.

> ⚠️ `minSdk` is 37, so the app only installs on Android 17 devices. If you want to test on an older phone, the team must agree to lower `minSdk` in `app/build.gradle.kts` (for example to 26).

## Project structure

✅ = already created, ⬜ = to create

```
app/src/main/java/vn/edu/usth/final_mobile_2026_2027/
├── MainActivity.java ........................ ✅ bottom navigation, shows Home or Search
├── api/
│   ├── ApiCallback.java ..................... ✅ receives the result of a request
│   ├── RedditApi.java ....................... ✅ one method per endpoint (only getPopularPosts is done)
│   └── RedditParser.java .................... ✅ JSON -> model objects (only parsePosts is done)
├── model/
│   ├── Post.java, Comment.java .............. ✅
│   └── Subreddit.java, User.java ............ ✅
├── ui/
│   ├── common/
│   │   └── PostAdapter.java ................. ⬜ list of posts, shared by Home, Subreddit and Profile
│   ├── home/HomeFragment.java ............... ✅ skeleton
│   ├── search/
│   │   ├── SearchFragment.java .............. ✅ skeleton
│   │   └── SubredditAdapter.java ............ ⬜
│   ├── subreddit/SubredditActivity.java ..... ✅ skeleton
│   ├── discussion/
│   │   ├── DiscussionActivity.java .......... ✅ skeleton
│   │   └── CommentAdapter.java .............. ⬜
│   └── profile/UserProfileActivity.java ..... ✅ skeleton
└── utils/
    ├── ViewUtils.java ....................... ✅ keeps the content away from the system bars
    └── FormatUtils.java ..................... ⬜ "5h ago", "1.2k points"...

app/src/main/res/
├── layout/
│   ├── activity_main.xml, fragment_home.xml, fragment_search.xml ........ ✅
│   ├── activity_subreddit.xml, activity_discussion.xml ................. ✅
│   ├── activity_user_profile.xml ....................................... ✅
│   └── item_post.xml, item_subreddit.xml, item_comment.xml ............. ⬜
├── menu/bottom_navigation.xml .......................................... ✅
└── values/strings.xml, colors.xml, themes.xml .......................... ✅
```

## Architecture

The code is split into 4 layers. A screen never sends HTTP requests itself: it always goes through `RedditApi`.

```
 ui (Activity / Fragment + Adapter)
        │  1. RedditApi.getInstance(context).getPopularPosts(callback)
        ▼
 api/RedditApi ──── 2. Volley sends the request in the background
        │
        ▼
 api/RedditParser ─ 3. JSON text -> model objects (Post, Comment...)
        │
        ▼
 callback.onSuccess(List<Post>) ─ 4. back on the main thread, the screen updates its adapter
```

Example of a call from a fragment:

```java
RedditApi.getInstance(requireContext()).getPopularPosts(new ApiCallback<List<Post>>() {
    @Override
    public void onSuccess(List<Post> posts) {
        if (!isAdded()) return; // the user left the page before the answer arrived
        progressBar.setVisibility(View.GONE);
        adapter.setPosts(posts);
    }

    @Override
    public void onError(String message) {
        if (!isAdded()) return;
        progressBar.setVisibility(View.GONE);
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }
});
```

To add an endpoint, copy `getPopularPosts()` in `RedditApi` and write the matching method in `RedditParser`.

To open a page, use the `newIntent()` method of its activity:

```java
startActivity(DiscussionActivity.newIntent(context, post.getId()));
startActivity(SubredditActivity.newIntent(context, post.getSubreddit()));
startActivity(UserProfileActivity.newIntent(context, post.getAuthor()));
```

## Reddit API

- Adding `.json` at the end of a reddit.com url returns its data, e.g. https://www.reddit.com/r/android/hot.json
- Reddit refuses requests that don't have a descriptive **User-Agent**. `RedditApi.get()` adds it to every request.
- Add `raw_json=1` to the url, otherwise the urls inside the answer contain `&amp;` instead of `&`.
- Every object is wrapped in `{ "kind": ..., "data": { ... } }`. Kinds: `t1` comment, `t2` user, `t3` post, `t5` subreddit.
- A list is a `Listing`: the items are in `data.children`. To get the next page, send `data.after` back as the `after` parameter (`limit` is 25 by default, 100 max).
- Comments are a tree: the replies of a comment are in its `replies` field (an empty string when there is no reply). The `depth` field tells how far to indent a comment. Items of kind `more` are links to load more comments.
- Anonymous access is rate limited. Don't refresh in a loop.

### Known issue: HTTP 403 "blocked by network security"

On 2026-10-22, every anonymous request sent from our development network was refused with an **HTTP 403** error: Reddit blocks part of the anonymous traffic, depending on the network. If the app shows "Reddit returned an error (HTTP 403)":

1. Try another network (for example the 4G of your phone).
2. If it still fails, switch to **OAuth "application only"**. The user still doesn't log in, only the app identifies itself:
   - Register an app of type *installed app* on https://www.reddit.com/prefs/apps to get a `client_id`. Reddit may review new API apps, so ask for it early.
   - `POST https://www.reddit.com/api/v1/access_token` with Basic authentication (`client_id` as user name, empty password) and the body `grant_type=https://oauth.reddit.com/grants/installed_client&device_id=DO_NOT_TRACK_THIS_DEVICE`.
   - Call `https://oauth.reddit.com/...` (without `.json`) with the header `Authorization: bearer <access_token>`. The token expires after about one hour.
   - Only `RedditApi` has to change. Keep the `client_id` in `local.properties`, which is not committed.

Documentation: https://www.reddit.com/dev/api and https://github.com/reddit-archive/reddit/wiki/OAuth2

## Work to do

- [ ] **Shared**: `PostAdapter` + `item_post.xml` (title, subreddit, author, score, comments, thumbnail with Glide). Do this first: Home, Subreddit and Profile need it.
- [ ] **Shared**: `FormatUtils` (relative date, big numbers), loading and error states on every page
- [ ] **Home**: show the popular posts, open the discussion on click
- [ ] **Search**: search subreddits from the keyboard, `SubredditAdapter`, open the subreddit on click
- [ ] **Subreddit**: header (icon, title, members, description) + posts of the subreddit
- [ ] **Discussion**: post (title, text, image) + `CommentAdapter` with indentation by depth
- [ ] **Profile**: header (avatar, karma, account age) + posts of the user
- [ ] Bonus: infinite scroll (`after`), sort order (hot / new / top), pull to refresh, markdown rendering

## Conventions

- **Language**: code, comments and commit messages in English.
- **Commits**: one commit per logical change, in the form `type(scope): description`
  - types: `feat`, `fix`, `refactor`, `style`, `docs`, `test`, `build`, `chore`
  - scopes: `home`, `search`, `subreddit`, `discussion`, `profile`, `api`, `model`, `utils`
  - example: `feat(discussion): indent replies according to their depth`
- **Branches**: one branch per feature (`feature/home-feed`, `feature/comments`...), merged into `main` with a pull request. Pull `main` often to avoid big conflicts.
- Every text displayed to the user goes in `res/values/strings.xml`.
- Views are retrieved with `findViewById`.
- In an API callback, check `isAdded()` (fragment) or `isFinishing()` (activity) before touching the views.
- Remove a `TODO` when the task is done.

## Useful commands

```bash
./gradlew assembleDebug   # build the app
./gradlew test            # run the unit tests
./gradlew lint            # check the code with Android lint
```
