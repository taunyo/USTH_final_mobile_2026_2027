package vn.edu.usth.final_mobile_2026_2027.ui.discussion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.model.Comment;
import vn.edu.usth.final_mobile_2026_2027.model.Community;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.community.CommunityActivity;
import vn.edu.usth.final_mobile_2026_2027.utils.FormatUtils;


public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Post post;
    private final List<Comment> comments = new ArrayList<>();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COMMENT = 1;

    @SuppressLint("NotifyDataSetChanged")
    public void setPost(Post post) {
        this.post = post;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setComments(List<Comment> newComments){
        comments.clear();
        comments.addAll(newComments);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // TODO: inflate item_discussion_header or item_comment depending on viewType
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // TODO: bind the post or comments.get(getCommentIndex(position))
    }

    @Override
    public int getItemCount() {
        int size = comments.size();
        if(!hasHeader()){
            return size;
        }else{
            return size + 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position == 0 && hasHeader()){
            return TYPE_HEADER;
        } else{
            return TYPE_COMMENT;
        }
    }

    private int getCommentIndex(int position) {
        if (hasHeader()){
            return position - 1;
        }else{
            return position;
        }
    }

    private boolean hasHeader() {
        return post != null;
    }

    /**
     * One row of the list. The RecyclerView reuses the rows when the user scrolls,
     * so bind() must set every view, even the ones that stay empty.
     */
    public static class CommunityViewHolder extends RecyclerView.ViewHolder {

        private final ImageView icon;
        private final TextView title;
        private final TextView name;
        private final TextView members;
        private final TextView description;

        CommunityViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.community_icon);
            title = itemView.findViewById(R.id.community_title);
            name = itemView.findViewById(R.id.community_name);
            members = itemView.findViewById(R.id.community_members);
            description = itemView.findViewById(R.id.community_description);
        }

        void bind(Community community) {
            Context context = itemView.getContext();

            title.setText(community.getTitle());
            // The full name, e.g. "c/technology@lemmy.ml": the search often finds
            // communities with the same name on several instances
            name.setText(context.getString(R.string.title_community, community.getName()));

            // Plurals choose between "1 member" and "2 members" with the number given as 2nd argument
            members.setText(context.getResources().getQuantityString(R.plurals.community_members,
                    (int) community.getSubscribers(), FormatUtils.formatCount(community.getSubscribers())));

            description.setText(community.getDescription());
            if (community.getDescription().isEmpty()) {
                description.setVisibility(View.GONE);
            } else {
                description.setVisibility(View.VISIBLE);
            }

            showIcon(community.getIconUrl());

            // v.getContext() is the activity that shows the list. The page needs the full name
            itemView.setOnClickListener(v ->
                    v.getContext().startActivity(CommunityActivity.newIntent(v.getContext(), community.getName())));
        }

        private void showIcon(String url) {
            if (url == null) {
                // The row may come from a community with an icon: cancel its loading first
                Glide.with(icon).clear(icon);
                icon.setImageResource(R.drawable.ic_community);
            } else {
                Glide.with(icon).load(url).centerCrop().into(icon);
            }
        }
    }
}
