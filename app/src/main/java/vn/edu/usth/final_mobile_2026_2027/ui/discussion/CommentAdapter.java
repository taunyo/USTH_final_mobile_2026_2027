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
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final TextView author;

        CommentViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
        }

        void bind(Comment comment) {
            // 3. remplir chaque vue avec les getters de comment
        }
    }
}
