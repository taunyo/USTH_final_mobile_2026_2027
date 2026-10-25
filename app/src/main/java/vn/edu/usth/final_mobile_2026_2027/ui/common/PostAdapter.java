package vn.edu.usth.final_mobile_2026_2027.ui.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.community.CommunityActivity;
import vn.edu.usth.final_mobile_2026_2027.ui.discussion.DiscussionActivity;
import vn.edu.usth.final_mobile_2026_2027.ui.profile.UserProfileActivity;
import vn.edu.usth.final_mobile_2026_2027.utils.FormatUtils;

/**
 * List of posts shared by the Home, Community and Profile pages.
 *
 * Usage:
 * <pre>
 * PostAdapter adapter = new PostAdapter();
 * recyclerView.setAdapter(adapter);
 * adapter.setPosts(posts);  // replaces the list
 * adapter.addPosts(posts);  // adds the next page at the end (infinite scroll)
 * </pre>
 * The adapter opens the pages itself: a click on a post opens its discussion,
 * a click on "c/..." its community and a click on "u/..." the profile of its author.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<Post> posts = new ArrayList<>();

    /** Replaces the posts of the list, e.g. after the first load or a refresh. */
    @SuppressLint("NotifyDataSetChanged") // the whole list changes
    public void setPosts(List<Post> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    /**
     * Adds the next page of posts at the end of the list.
     * The posts already shown are skipped: with the "Hot" order the ranking changes over time,
     * so the next page can contain a post of the previous one.
     */
    public void addPosts(List<Post> newPosts) {
        // Ids of the posts already in the list
        Set<Integer> shownIds = new HashSet<>();
        for (Post post : posts) {
            shownIds.add(post.getId());
        }

        int start = posts.size();
        for (Post post : newPosts) {
            if (!shownIds.contains(post.getId())) {
                posts.add(post);
                shownIds.add(post.getId());
            }
        }
        // Only redraw the new rows
        notifyItemRangeInserted(start, posts.size() - start);
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    /**
     * One row of the list. The RecyclerView reuses the rows when the user scrolls,
     * so bind() must set every view, even the ones that stay empty.
     */
    public static class PostViewHolder extends RecyclerView.ViewHolder {

        private final TextView community;
        private final TextView author;
        private final TextView date;
        private final TextView title;
        private final ImageView thumbnail;
        private final TextView score;
        private final TextView comments;

        PostViewHolder(View itemView) {
            super(itemView);
            community = itemView.findViewById(R.id.post_community);
            author = itemView.findViewById(R.id.post_author);
            date = itemView.findViewById(R.id.post_date);
            title = itemView.findViewById(R.id.post_title);
            thumbnail = itemView.findViewById(R.id.post_thumbnail);
            score = itemView.findViewById(R.id.post_score);
            comments = itemView.findViewById(R.id.post_comments);
        }

        void bind(Post post) {
            Context context = itemView.getContext();

            // Only the short names are displayed, the pages receive the full names (see the clicks)
            community.setText(context.getString(R.string.title_community, FormatUtils.shortName(post.getCommunity())));
            author.setText(context.getString(R.string.title_user, FormatUtils.shortName(post.getAuthor())));
            date.setText(FormatUtils.formatTimeAgo(context, post.getCreatedUtc()));
            title.setText(post.getTitle());

            // Plurals choose between "1 point" and "2 points" with the number given as 2nd argument
            score.setText(context.getResources().getQuantityString(
                    R.plurals.post_points, post.getScore(), FormatUtils.formatCount(post.getScore())));
            comments.setText(context.getResources().getQuantityString(
                    R.plurals.post_comments, post.getNumComments(), FormatUtils.formatCount(post.getNumComments())));

            showThumbnail(post.getThumbnailUrl());

            // v.getContext() is the activity that shows the list
            itemView.setOnClickListener(v ->
                    v.getContext().startActivity(DiscussionActivity.newIntent(v.getContext(), post.getId())));
            community.setOnClickListener(v ->
                    v.getContext().startActivity(CommunityActivity.newIntent(v.getContext(), post.getCommunity())));
            author.setOnClickListener(v ->
                    v.getContext().startActivity(UserProfileActivity.newIntent(v.getContext(), post.getAuthor())));
        }

        private void showThumbnail(String url) {
            if (url == null) {
                // The row may come from a post with an image: cancel its loading and remove it
                Glide.with(thumbnail).clear(thumbnail);
                thumbnail.setVisibility(View.GONE);
            } else {
                thumbnail.setVisibility(View.VISIBLE);
                Glide.with(thumbnail).load(url).centerCrop().into(thumbnail);
            }
        }
    }
}
