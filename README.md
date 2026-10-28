# USTH Lemmy

Final project for the Mobile Application Development course (USTH, 2026-2027).

An Android client for [Lemmy](https://join-lemmy.org), written in Java. Lemmy is a free alternative to Reddit: communities, posts, comments and votes, spread over many servers ("instances") that share their content. The app reads public data (popular posts, communities, discussions, user profiles) through the API of the **lemmy.world** instance. **No account and no API key are needed** and the app has no login page.

> **Why Lemmy and not Reddit?** The project first used the Reddit API. Since 2026, Reddit refuses every request sent without an approved app (HTTP 403 "blocked by network security", checked on 2026-10-25), and it stops accepting new API apps on 2026-10-31. The API of Lemmy is open and works the same way, so the app keeps the same 5 pages.

## The 5 pages

| # | Page | Class | Lemmy endpoint(s), after `https://lemmy.world/api/v3` | Opened from |
|---|------|-------|-------------------------------------------------------|-------------|
| 1 | Home | `ui/home/HomeFragment` | `/post/list?type_=All&sort=Hot` | bottom navigation |
| 2 | Search | `ui/search/SearchFragment` | `/search?q=...&type_=Communities` | bottom navigation |
| 3 | Community | `ui/community/CommunityActivity` | `/community?name={name}`, `/post/list?community_name={name}` | a search result, a click on "c/..." |
| 4 | Discussion | `ui/discussion/DiscussionActivity` | `/post?id={id}`, `/comment/list?post_id={id}` | a click on a post |
| 5 | User profile | `ui/profile/UserProfileActivity` | `/user?username={name}` (information + posts) | a click on "u/..." |

```
            ┌──────────── bottom navigation ────────────┐
            │                                           │
         [Home] ──── post ───► [Discussion]          [Search]
            │                       │                   │
            │ "c/..."               │ "u/..."           │ result
            ▼                       ▼                   ▼
       [Community] ── post ──► [Discussion]        [Community]
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
- **Lemmy API v3** (Lemmy 0.19): the data, see [Lemmy API](#lemmy-api)
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
│   ├── LemmyApi.java ........................ ✅ one method per endpoint (only getPopularPosts is done)
│   └── LemmyParser.java ..................... ✅ JSON -> model objects (only parsePosts is done)
├── model/
│   ├── Post.java, Comment.java .............. ✅
│   └── Community.java, User.java ............ ✅
├── ui/
│   ├── common/
│   │   └── PostAdapter.java ................. ⬜ list of posts, shared by Home, Community and Profile
│   ├── home/HomeFragment.java ............... ✅ skeleton
│   ├── search/
│   │   ├── SearchFragment.java .............. ✅ skeleton
│   │   └── CommunityAdapter.java ............ ⬜
│   ├── community/CommunityActivity.java ..... ✅ skeleton
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
│   ├── activity_community.xml, activity_discussion.xml ................. ✅
│   ├── activity_user_profile.xml ....................................... ✅
│   ├── item_post.xml, item_community.xml, item_comment.xml ............. ⬜
│   └── item_discussion_header.xml, view_error.xml ...................... ⬜
├── menu/bottom_navigation.xml .......................................... ✅
└── values/strings.xml, colors.xml, themes.xml .......................... ✅
```

## Architecture

The code is split into 4 layers. A screen never sends HTTP requests itself: it always goes through `LemmyApi`.

```
 ui (Activity / Fragment + Adapter)
        │  1. LemmyApi.getInstance(context).getPopularPosts(callback)
        ▼
 api/LemmyApi ───── 2. Volley sends the request in the background
        │
        ▼
 api/LemmyParser ── 3. JSON text -> model objects (Post, Comment...)
        │
        ▼
 callback.onSuccess(List<Post>) ─ 4. back on the main thread, the screen updates its adapter
```

Example of a call from a fragment:

```java
LemmyApi.getInstance(requireContext()).getPopularPosts(new ApiCallback<List<Post>>() {
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

To add an endpoint, copy `getPopularPosts()` in `LemmyApi` and write the matching method in `LemmyParser`.

To open a page, use the `newIntent()` method of its activity:

```java
startActivity(DiscussionActivity.newIntent(context, post.getId()));
startActivity(CommunityActivity.newIntent(context, post.getCommunity()));
startActivity(UserProfileActivity.newIntent(context, post.getAuthor()));
```

## Lemmy API

The app reads everything from **lemmy.world** (Lemmy 0.19, API v3). To use another instance, only change `BASE_URL` in `LemmyApi`.

- Every request is a `GET` on `https://lemmy.world/api/v3/...` with its parameters in the url. Open one in a browser to see the JSON, e.g. https://lemmy.world/api/v3/post/list?type_=All&sort=Hot&limit=5
- No account and no API key. `LemmyApi.get()` adds a User-Agent that tells the admins of the instance who we are.
- The answers are made of **views**: an item grouped with what is needed to display it. A post view is `{ "post": {...}, "creator": {...}, "community": {...}, "counts": {...} }`. The title of a post is `post.name`, its text `post.body`, its score `counts.score` and its number of comments `counts.comments`.
- **Other instances**: a community or a user belongs to one instance. For the API, its name is `technology` if it is on lemmy.world, and `technology@lemmy.ml` if it is on another instance. `LemmyParser.getFullName()` builds this name from the `local` and `actor_id` fields: always give this full name to the pages and to the API. On screen, you can show only the part before `@`.
- **Dates** are texts like `"2026-10-25T08:22:47.712233Z"`. `LemmyParser.getDate()` converts them into seconds since 01/01/1970.
- **Pages of results**: `limit` (50 max) and `page` (1, 2, 3...). **Sort order**: `sort=Hot`, `New`, `TopDay`, `TopWeek`, `TopAll`...
- **Comments**: `/comment/list` returns a flat list **in no particular order**. The `path` of a comment lists the ids of its parents, then its own id: `"0.123"` is a direct answer to the post (depth 0), `"0.123.456"` is a reply to the comment 123 (depth 1). To display them, rebuild the tree with the paths, then go through it so that the replies come right after their parent. `max_depth` limits the depth, `counts.child_count` is the number of replies.
- **Deleted comments**: when `comment.deleted` or `comment.removed` is `true`, the text can be empty. Show "Deleted comment" or "Removed by a moderator" instead.
- **NSFW**: posts and communities have an `nsfw` field. Hide them in the search.
- **Errors**: an unknown item returns HTTP 400 or 404 with `{"error": "couldnt_find_community"}` (also `couldnt_find_person`, `couldnt_find_post`).
- **Images** are often in WebP or AVIF. Glide reads AVIF only from Android 12 (API 31): keep it in mind if `minSdk` goes down.
- lemmy.world limits the number of requests per IP address. Don't refresh in a loop.

Documentation: [overview of the API](https://join-lemmy.org/docs/contributors/04-api.html), [types of every request and answer for 0.19](https://join-lemmy.org/lemmy-js-client-docs/v0.19/), [unofficial OpenAPI reference for 0.19](https://mv-gh.github.io/lemmy_openapi_spec/).

## Task distribution

The work is split between 4 developers, **A**, **B**, **C** and **D**. Each developer owns whole pages, so the 4 can work in parallel with few conflicts. Write your name in the table.

| Dev | Name | Owns | Main files |
|-----|------|------|------------|
| **A** | | Shared post list, Home, navigation | `ui/common/`, `ui/home/`, `utils/FormatUtils`, `MainActivity`, `item_post.xml`, `view_error.xml` |
| **B** | | Search, Community | `ui/search/`, `ui/community/`, `item_community.xml` |
| **C** | | Discussion | `ui/discussion/`, `item_comment.xml`, `item_discussion_header.xml` |
| **D** | | User profile, error messages, tests, final QA | `ui/profile/`, `LemmyApi.getErrorMessage()`, `app/src/test/`, `app/build.gradle.kts` |

- `LemmyApi`, `LemmyParser` and `strings.xml` are shared: only edit the section of your page (in `strings.xml`, put your strings under a `<!-- Page name -->` comment). The other files belong to their owner: ask them before changing one.
- One task (or a few small ones) = one branch and one pull request, e.g. `feature/post-adapter` for A2.
- Pull requests are reviewed in pairs: A ↔ B and C ↔ D.

### Planning

| Phase | What | Finished when |
|-------|------|---------------|
| 0. Setup (day 1) | everyone runs the app, the team agrees on the contracts below | the 4 developers run the app and see the JSON of the Home endpoint in their browser |
| 1. Foundations | A builds the shared pieces, B, C and D write the API + parser of their page | `FormatUtils`, `PostAdapter` and `view_error.xml` are merged in `main` |
| 2. Pages | every page works from end to end | the 5 pages and every arrow of the navigation diagram work |
| 3. Bonus | pull to refresh, infinite scroll, sort order, markdown | the bonus tasks are merged |
| 4. Finish | test plan, bug fixes, cleanup, demo | the final checklist is all ticked |

Who waits for whom:

```
A1 FormatUtils ──► every page (dates, numbers)
A2 PostAdapter ──► A5 Home, B6 Community, D4 Profile
A3 view_error ───► every page (error and empty states)
A4 imageUrl ─────► C4 post at the top of the Discussion page
D1 JSON samples ─► everyone (to write and test the parsers)
D5 errors ───────► nobody (only LemmyApi.getErrorMessage() changes)
B6 + D4 merged ──► A7 pull to refresh and A8 infinite scroll on Community and Profile
```

A merges A1 to A4 first, as fast as possible. Until then, B, C and D work on the API, the parser and the layouts of their page, which don't need them.

### Contracts to agree on (phase 0)

They let B, C and D write code that uses the work of A before it is merged.

```java
// ui/common/PostAdapter (A), used by Home (A), Community (B) and Profile (D)
PostAdapter adapter = new PostAdapter();
recyclerView.setAdapter(adapter);
adapter.setPosts(posts);  // replaces the list
adapter.addPosts(posts);  // adds the next page at the end (infinite scroll)
// The adapter handles the clicks itself: post -> Discussion, "c/..." -> Community, "u/..." -> Profile

// utils/FormatUtils (A), used by everyone
FormatUtils.formatTimeAgo(context, createdUtc);  // "5h ago", "3y ago"
FormatUtils.formatCount(1234);                   // "1.2k"
FormatUtils.shortName("technology@lemmy.ml");    // "technology" (to display only, the pages need the full name)
```

```xml
<!-- res/layout/view_error.xml (A): put it next to the RecyclerView of every page -->
<include
    android:id="@+id/error_view"
    layout="@layout/view_error"
    android:visibility="gone" />
<!-- it contains the TextView error_message and the Button retry_button -->
```

```java
// ui/common/ErrorView (A) shows and hides it
ErrorView errorView = new ErrorView(view.findViewById(R.id.error_view));
errorView.showError(message, this::loadPosts);  // message + "Retry" button that calls loadPosts()
errorView.showEmpty(message);                   // message only, e.g. "No posts yet"
errorView.hide();
```

### Phase 0: everyone

- [ ] **Everyone**: clone the project, run the app on an Android 17 emulator, open https://lemmy.world/api/v3/post/list?type_=All&sort=Hot&limit=5 in a browser to see what Lemmy answers
- [ ] **Everyone**: agree on the contracts above
- [ ] **Team**: decide if `minSdk` stays at 37 or goes down to 26 to test on real phones (D applies it)

### Developer A: shared post list, Home, navigation

- [ ] **A1** `utils/FormatUtils`: relative date ("just now", "5h ago", "3y ago") and big numbers ("950", "1.2k", "3.4M"), with unit tests
- [ ] **A2** `item_post.xml` + `ui/common/PostAdapter`: c/community, u/author, date, title, thumbnail (Glide, hidden when `null`), score, number of comments. Show only the part of the names before `@`, but give the full names to the pages. A click on the post opens the Discussion, on "c/..." the Community, on "u/..." the Profile
- [ ] **A3** `view_error.xml`: shared view for the error and empty states (a message + a "Retry" button)
- [ ] **A4** `LemmyParser.parsePost()`: fill `imageUrl` with `url` when `url_content_type` starts with `image/` (C needs it)
- [ ] **A5** Home: load the popular posts with the progress bar, error view with retry, open the discussion on click
- [ ] **A6** `MainActivity`: keep the two fragments when switching tabs (`show()` / `hide()` instead of `replace()`), so Home doesn't send a new request each time (rate limit). Tapping Home again scrolls back to the top
- [ ] **A7** Bonus: pull to refresh (`SwipeRefreshLayout`, add the dependency) on Home, Community and Profile
- [ ] **A8** Bonus: infinite scroll on Home, Community and Profile: add a `page` parameter (1, 2, 3...) to the 3 post endpoints, load the next page when the list reaches the bottom, stop when a page comes back empty

### Developer B: Search, Community

- [ ] **B1** `LemmyParser.parseCommunities()` + `LemmyApi.searchCommunities()`: encode the text with `Uri.encode()`, add `sort=TopAll` so the biggest communities come first, skip the NSFW communities (`community.nsfw`)
- [ ] **B2** `item_community.xml` + `ui/search/CommunityAdapter`: icon (Glide, default icon when `null`), title, c/name, members (`counts.subscribers` with `FormatUtils.formatCount`), description on 2 lines max
- [ ] **B3** Search: start the search with the "search" key of the keyboard, hide the keyboard, progress bar, "No community found" message, error view with retry, open the community on click
- [ ] **B4** `LemmyParser.parseCommunity()` + `LemmyApi.getCommunity()` (`/community?name={name}`, the view is in `community_view`). It is the same view as in the search: share the parsing code with B1
- [ ] **B5** `LemmyApi.getCommunityPosts()` (`/post/list?community_name={name}&sort=Hot`, reuse `parsePosts()`)
- [ ] **B6** Community: header in `activity_community.xml` (icon, title, members, description) + posts with `PostAdapter`. Show a message when the community doesn't exist (HTTP 404)
- [ ] **B7** Bonus: sort order Hot / New / TopDay with chips above the list: `getCommunityPosts(name, sort, callback)`

### Developer C: Discussion

- [ ] **C1** `LemmyParser.parsePost(String json)` + `LemmyApi.getPost()` (`/post?id={postId}`, the view is in `post_view`: reuse the private `parsePost(JSONObject)`)
- [ ] **C2** `LemmyParser.parseComments()` + `LemmyApi.getComments()` (`/comment/list?post_id={postId}&max_depth=8`): rebuild the tree with the `path` of each comment (see [Lemmy API](#lemmy-api)) and return a flat list in display order, with the right `depth`
- [ ] **C3** `item_comment.xml` + `ui/discussion/CommentAdapter`: u/author, date, score, text. Indent according to `depth` (margin + colored vertical line, stop indenting after about 6 levels)
- [ ] **C4** The post at the top of the list (second view type of `CommentAdapter`, `item_discussion_header.xml`): c/community, u/author, date, title, text, big image (`imageUrl` with Glide), score, number of comments. For a link post, a click opens `url` in the browser (`Intent.ACTION_VIEW`)
- [ ] **C5** `DiscussionActivity`: load the post and the comments, progress bar, error view with retry, "No comments yet" message, text of the deleted and removed comments. "u/..." opens the Profile, "c/..." the Community
- [ ] **C6** Bonus: markdown rendering of the post text and of the comments (Lemmy texts are written in markdown) with the Markwon library, in a `utils/MarkdownUtils` the other pages can reuse
- [ ] **C7** Bonus: tap a comment to fold / unfold its replies

### Developer D: User profile, error messages, tests, QA

- [x] **D1** Day 1: save a real answer of each endpoint of [the 5 pages](#the-5-pages) in `app/src/test/resources/`, with users and communities of other instances among them. Everyone uses them to write their parser. Apply the `minSdk` chosen by the team
- [x] **D2** `LemmyParser.parseUser()` + `LemmyApi.getUser()` (`/user?username={name}`, the view is in `person_view`): full name (`getFullName()`), avatar (`person.avatar`), creation date (`person.published`), `counts.post_count`, `counts.comment_count`
- [x] **D3** `LemmyApi.getUserPosts()` (the same endpoint with `&sort=New`: the posts are in `posts`, so `parsePosts()` works as it is)
- [x] **D4** Profile: header in `activity_user_profile.xml` (round avatar, display name `person.display_name` or u/name, number of posts, number of comments, age of the account, a "Banned" label when `person.banned`) + posts with `PostAdapter`, "No posts" message. Show a message for an unknown user (HTTP 404)
- [x] **D5** Readable errors in `LemmyApi.getErrorMessage()`: read the `error` field of the answer (`error.networkResponse.data`) and show a clear message for `couldnt_find_community`, `couldnt_find_person`, `couldnt_find_post` and `rate_limit_error`. The other developers have nothing to change
- [x] **D6** Unit tests of `LemmyParser` with the answers saved in D1: `parsePosts` (full names, dates), `parseCommunities`, `parseCommunity`, `parsePost`, `parseComments` (order and depths), `parseUser`. Add `testImplementation("org.json:json:...")`: the `org.json` of Android doesn't work in local unit tests
- [/] **D7** Phase 4: run the test plan below, open a GitHub issue for each bug and assign it to the owner of the page, run `./gradlew lint` and fix the warnings with the owners

### Phase 4: everyone

- [ ] Fix the bugs assigned to you and remove the `TODO`s of your files
- [ ] Update this README: ✅ in the project structure, tick the tasks
- [ ] Rehearse the demo: Home → Discussion → Profile → Discussion, then Search → Community → Discussion

Final checklist (checked by D):

- [ ] `./gradlew assembleDebug test lint` passes
- [ ] No `TODO` left in the code, every displayed text is in `strings.xml`
- [ ] Every page has a loading state, an error state with retry and an empty state
- [ ] Test plan done on the emulator:
  - every arrow of the navigation diagram
  - a community and a user of another instance (`name@instance`)
  - rotation of the screen on each page
  - dark mode
  - airplane mode, then "Retry" once the network is back
  - text post, image post (WebP and AVIF), link post, post with a very long title
  - deleted comment, comment removed by a moderator
  - unknown community or user, banned user
  - search with spaces, with accents, without result

## Conventions

- **Language**: code, comments and commit messages in English.
- **Commits**: one commit per logical change, in the form `type(scope): description`
  - types: `feat`, `fix`, `refactor`, `style`, `docs`, `test`, `build`, `chore`
  - scopes: `home`, `search`, `community`, `discussion`, `profile`, `common` (shared code of `ui/common`), `api`, `model`, `utils`
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
