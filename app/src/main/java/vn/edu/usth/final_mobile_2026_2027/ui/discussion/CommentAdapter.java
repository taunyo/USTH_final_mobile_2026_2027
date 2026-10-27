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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;

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
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_discussion_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // The holder was created by onCreateViewHolder() for the type of this position
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(post);
        } else {
            ((CommentViewHolder) holder).bind(comments.get(getCommentIndex(position)));
        }
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
     * The post at the top of the discussion (item_discussion_header.xml).
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        // TODO: one field per view of item_discussion_header.xml

        HeaderViewHolder(View itemView) {
            super(itemView);
            // TODO: findViewById for each field
        }

        void bind(Post post) {
            // TODO: fill the views with the getters of post
        }
    }

    /**
     * One row of the list. The RecyclerView reuses the rows when the user scrolls,
     * so bind() must set every view, even the ones that stay empty.
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private static final int MAX_INDENT_DEPTH = 6;
        private static final int INDENT_DP = 16;

        private final TextView author;
        private final TextView date;
        private final TextView score;
        private final TextView body;
        private final TextView collapse;
        private final MaterialCardView card;

        CommentViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            date = itemView.findViewById(R.id.comment_date);
            score = itemView.findViewById(R.id.comment_score);
            body = itemView.findViewById(R.id.comment_body);
            collapse = itemView.findViewById(R.id.comment_collapse);
            card = (MaterialCardView) itemView;
        }

        void bind(Comment comment) {
            Context context = itemView.getContext();
            
            author.setText(context.getString(R.string.title_user, FormatUtils.shortName(comment.getAuthor())));
            date.setText(FormatUtils.formatTimeAgo(context, comment.getCreatedUtc()));
            score.setText(context.getString(R.string.comment_score, FormatUtils.formatCount(comment.getScore())));
            body.setText(comment.getBody());

            showDepth(context, comment.getDepth());
        }

        /**
         * Shifts the card to the right according to the depth of the comment, and alternates
         * its background color, so that the replies look nested in their parent.
         */
        private void showDepth(Context context, int depth) {
            // After MAX_INDENT_DEPTH levels the text would get too narrow: stop shifting
            int indentLevel = Math.min(depth, MAX_INDENT_DEPTH);
            // setMarginStart() takes pixels, not dp
            float density = context.getResources().getDisplayMetrics().density;
            int marginPx = Math.round(indentLevel * INDENT_DP * density);

            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) card.getLayoutParams();
            params.setMarginStart(marginPx);
            card.setLayoutParams(params);

            // The row may be reused from a comment of another depth: always set the color
            int colorRes;
            if (depth % 2 == 0) {
                colorRes = R.color.comment_background_even;
            } else {
                colorRes = R.color.comment_background_odd;
            }
            card.setCardBackgroundColor(ContextCompat.getColor(context, colorRes));
        }
    }
}
