package vn.edu.usth.final_mobile_2026_2027.ui.discussion;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import vn.edu.usth.final_mobile_2026_2027.R;
import vn.edu.usth.final_mobile_2026_2027.model.Comment;
import vn.edu.usth.final_mobile_2026_2027.model.Post;
import vn.edu.usth.final_mobile_2026_2027.ui.community.CommunityActivity;
import vn.edu.usth.final_mobile_2026_2027.ui.profile.UserProfileActivity;
import vn.edu.usth.final_mobile_2026_2027.utils.FormatUtils;


/**
 * Adapter of the Discussion page: the post at position 0, then the comments.
 * The replies of a folded comment are not shown.
 */
public class CommentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Post post;
    /** All the comments, in the order of the tree. */
    private final List<Comment> comments = new ArrayList<>();
    /** The comments really shown: the replies of a folded comment are left out. */
    private final List<Comment> visibleComments = new ArrayList<>();
    /** The ids of the folded comments. */
    private final Set<Integer> collapsedIds = new HashSet<>();
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_COMMENT = 1;
    private static final int TYPE_EMPTY = 2;
    /** False until setComments() is called, so "No comments yet" is not shown while loading. */
    private boolean commentsLoaded = false;



    /** Called by DiscussionActivity when the post is loaded: shows it at the top of the list. */
    @SuppressLint("NotifyDataSetChanged")
    public void setPost(Post post) {
        this.post = post;
        notifyDataSetChanged();
    }

    /**
     * Called by DiscussionActivity when the comments are loaded.
     * The list must already be in the order of the tree (see LemmyParser.parseComments()).
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setComments(List<Comment> newComments){
        comments.clear();
        comments.addAll(newComments);
        commentsLoaded = true;
        collapsedIds.clear();
        updateVisibleComments();
        notifyDataSetChanged();
    }

    /** Folds the comment if it is open, opens it if it is folded. */
    @SuppressLint("NotifyDataSetChanged")
    private void toggleCollapse(Comment comment) {
        if (collapsedIds.contains(comment.getId())) {
            collapsedIds.remove(comment.getId());
        } else {
            collapsedIds.add(comment.getId());
        }
        updateVisibleComments();
        notifyDataSetChanged();
    }

    /**
     * Rebuilds visibleComments. The comments are in the order of the tree, so the replies
     * of a comment are the comments right after it that are deeper.
     */
    private void updateVisibleComments() {
        visibleComments.clear();
        // Depth of the folded comment whose replies are being skipped, -1 if none
        int hiddenBelowDepth = -1;
        for (Comment comment : comments) {
            if (hiddenBelowDepth != -1 && comment.getDepth() > hiddenBelowDepth) {
                continue;
            }
            hiddenBelowDepth = -1;
            visibleComments.add(comment);
            if (collapsedIds.contains(comment.getId())) {
                hiddenBelowDepth = comment.getDepth();
            }
        }
    }

    /** Number of replies under a comment, at every depth (shown in "[+ N]"). */
    private int countReplies(Comment comment) {
        int count = 0;
        for (int i = comments.indexOf(comment) + 1; i < comments.size(); i++) {
            if (comments.get(i).getDepth() <= comment.getDepth()) {
                break;
            }
            count++;
        }
        return count;
    }

    /** Creates an empty row: inflates the XML of the type given by getItemViewType(). */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_discussion_header, parent, false);
            return new HeaderViewHolder(view);
        } else if (viewType == TYPE_EMPTY) {
            // Nothing to fill: the text is already in the XML
            View view = inflater.inflate(R.layout.item_comments_empty, parent, false);
            return new RecyclerView.ViewHolder(view) { };
        } else {
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }
    }

    /** Fills the row at this position with the post or with a comment. */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // The holder was created by onCreateViewHolder() for the type of this position
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind(post);
        } else if (holder instanceof CommentViewHolder) {
            Comment comment = visibleComments.get(getCommentIndex(position));
            boolean collapsed = collapsedIds.contains(comment.getId());
            ((CommentViewHolder) holder).bind(comment, collapsed, countReplies(comment), v -> toggleCollapse(comment));
        }
    }

    /** Number of rows: the visible comments, plus 1 for the post. */
    @Override
    public int getItemCount() {
        int size = visibleComments.size();
        if (showEmpty()) {
            // The post, then the "No comments yet" row
            return 2;
        }
        if(!hasHeader()){
            return size;
        }else{
            return size + 1;
        }
    }

    /** Type of the row at this position: the post at 0, then comments, or the "No comments yet" row. */
    @Override
    public int getItemViewType(int position) {
        if(position == 0 && hasHeader()){
            return TYPE_HEADER;
        } else if (showEmpty()) {
            return TYPE_EMPTY;
        } else{
            return TYPE_COMMENT;
        }
    }

    /** Converts a position of the list into an index of visibleComments: the post takes position 0. */
    private int getCommentIndex(int position) {
        if (hasHeader()){
            return position - 1;
        }else{
            return position;
        }
    }

    /** True once the post is loaded, the list then starts with it. */
    private boolean hasHeader() {
        return post != null;
    }

    /** True when the comments are loaded and there is none. */
    private boolean showEmpty() {
        return hasHeader() && commentsLoaded && comments.isEmpty();
    }

    /**
     * The post at the top of the discussion (item_discussion_header.xml).
     */
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView community;
        private final TextView instance;
        private final TextView author;
        private final TextView date;
        private final TextView title;
        private final MaterialCardView imageCard;
        private final ImageView image;
        private final TextView body;
        private final MaterialCardView linkCard;
        private final TextView link;
        private final TextView score;
        private final TextView comments;

        /** Finds the views of item_discussion_header.xml once, when the row is created. */
        HeaderViewHolder(View itemView) {
            super(itemView);
            community = itemView.findViewById(R.id.header_community);
            instance = itemView.findViewById(R.id.header_instance);
            author = itemView.findViewById(R.id.header_author);
            date = itemView.findViewById(R.id.header_date);
            title = itemView.findViewById(R.id.header_title);
            imageCard = itemView.findViewById(R.id.header_image_card);
            image = itemView.findViewById(R.id.header_image);
            body = itemView.findViewById(R.id.header_body);
            linkCard = itemView.findViewById(R.id.header_link_card);
            link = itemView.findViewById(R.id.header_link);
            score = itemView.findViewById(R.id.header_score);
            comments = itemView.findViewById(R.id.header_comments);
        }

        /** Fills the header with the post: community, author, date, title, body, image or link, score. */
        void bind(Post post) {
            Context context = itemView.getContext();

            // Only the short names are displayed, the pages receive the full names (see the clicks)
            community.setText(context.getString(R.string.title_community, FormatUtils.shortName(post.getCommunity())));
            showInstance(post.getCommunity());
            author.setText(context.getString(R.string.title_user, FormatUtils.shortName(post.getAuthor())));
            date.setText(context.getString(R.string.discussion_date,FormatUtils.formatTimeAgo(context, post.getCreatedUtc())));
            title.setText(post.getTitle());

            score.setText(context.getString(R.string.comment_score, FormatUtils.formatCount(post.getScore())));
            comments.setText(context.getResources().getQuantityString(R.plurals.post_comments, post.getNumComments(), FormatUtils.formatCount(post.getNumComments())));

            // Text posts have a body, link and image posts often don't
            if (TextUtils.isEmpty(post.getBody())) {
                body.setVisibility(View.GONE);
            } else {
                body.setText(post.getBody());
                body.setVisibility(View.VISIBLE);
            }

            showImage(post.getImageUrl());
            showLink(post);

            community.setOnClickListener(v -> v.getContext().startActivity(CommunityActivity.newIntent(v.getContext(), post.getCommunity())));
            author.setOnClickListener(v -> v.getContext().startActivity(UserProfileActivity.newIntent(v.getContext(), post.getAuthor())));
        }

        /** Shows "@lemmy.ml" next to a community of another instance, nothing for lemmy.world. */
        private void showInstance(String fullName) {
            int at = fullName.indexOf('@');
            if (at == -1) {
                instance.setVisibility(View.GONE);
            } else {
                instance.setText(fullName.substring(at));
                instance.setVisibility(View.VISIBLE);
            }
        }

        /** Loads the image of an image post with Glide, hides the frame when there is none. */
        private void showImage(String url) {
            if (url == null) {
                Glide.with(image).clear(image);
                imageCard.setVisibility(View.GONE);
            } else {
                imageCard.setVisibility(View.VISIBLE);
                Glide.with(image).load(url).into(image);
            }
        }

        /** The link of a link post. An image post already shows its url as the image. */
        private void showLink(Post post) {
            String url = post.getUrl();
            if (TextUtils.isEmpty(url) || post.getImageUrl() != null) {
                linkCard.setVisibility(View.GONE);
                linkCard.setOnClickListener(null);
            } else {
                link.setText(itemView.getContext().getString(R.string.discussion_link, url));
                linkCard.setVisibility(View.VISIBLE);
                // Opens the page in the browser of the phone
                linkCard.setOnClickListener(v ->
                        v.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url))));
            }
        }
    }

    /**
     * One row of the list. The RecyclerView reuses the rows when the user scrolls,
     * so bind() must set every view, even the ones that stay empty.
     */
    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        private static final int MAX_INDENT_DEPTH = 6;
        private static final int SLOT_DP = 14;
        private static final int LINE_DP = 2;
        private static final int CORNER_DP = 12;

        private final TextView author;
        private final TextView date;
        private final TextView score;
        private final TextView body;
        private final TextView collapse;
        private final LinearLayout indent;
        private final LinearLayout box;

        /** Finds the views of item_comment.xml once, when the row is created. */
        CommentViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.comment_author);
            date = itemView.findViewById(R.id.comment_date);
            score = itemView.findViewById(R.id.comment_score);
            body = itemView.findViewById(R.id.comment_body);
            collapse = itemView.findViewById(R.id.comment_collapse);
            indent = itemView.findViewById(R.id.comment_indent);
            box = itemView.findViewById(R.id.comment_box);
        }

        /**
         * Fills the row with a comment. A folded comment hides its text and shows "[+ N]".
         * @param collapsed  true if the user folded this comment
         * @param replyCount number of replies under it, at every depth
         * @param onToggle   called on a click on [–] / [+ N]
         */
        void bind(Comment comment, boolean collapsed, int replyCount, View.OnClickListener onToggle) {
            Context context = itemView.getContext();

            author.setText(context.getString(R.string.title_user, FormatUtils.shortName(comment.getAuthor())));
            date.setText(FormatUtils.formatTimeAgo(context, comment.getCreatedUtc()));
            score.setText(context.getString(R.string.comment_score, FormatUtils.formatCount(comment.getScore())));
            // Lemmy keeps the empty lines at the end of a comment, they made some boxes too tall
            body.setText(comment.getBody().trim());

            // A folded comment only keeps its first line, its replies are hidden by the adapter
            if (collapsed) {
                body.setVisibility(View.GONE);
                if (replyCount == 0) {
                    collapse.setText(R.string.comment_expand_no_replies);
                } else {
                    collapse.setText(context.getString(R.string.comment_expand, replyCount));
                }
            } else {
                body.setVisibility(View.VISIBLE);
                collapse.setText(R.string.comment_collapse);
            }
            collapse.setOnClickListener(onToggle);

            showDepth(context, comment.getDepth());
        }

        /**
         * Draws one thread line per parent on the left, then gives the comment a rounded box.
         * The lines take the whole height of the row, so they join from one row to the next.
         */
        private void showDepth(Context context, int depth) {
            // After MAX_INDENT_DEPTH levels the text would get too narrow: stop shifting
            int indentLevel = Math.min(depth, MAX_INDENT_DEPTH);
            // The sizes of the views are in pixels, not dp
            float density = context.getResources().getDisplayMetrics().density;
            int slotPx = Math.round(SLOT_DP * density);
            int linePx = Math.round(LINE_DP * density);
            int lineColor = MaterialColors.getColor(itemView, com.google.android.material.R.attr.colorOutlineVariant);

            // The row may be reused from a comment of another depth: rebuild the lines
            indent.removeAllViews();
            for (int level = 0; level < indentLevel; level++) {
                // A thin line in the middle of a slot, the slot gives the shift to the right
                FrameLayout slot = new FrameLayout(context);
                View line = new View(context);
                line.setBackgroundColor(lineColor);
                slot.addView(line, new FrameLayout.LayoutParams(linePx, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_HORIZONTAL));
                indent.addView(slot, new LinearLayout.LayoutParams(slotPx, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            // Rounded box with a thin border, like a MaterialCardView
            GradientDrawable background = new GradientDrawable();
            background.setCornerRadius(CORNER_DP * density);
            background.setColor(getDepthColor(context, depth));
            background.setStroke(Math.max(1, Math.round(density)), lineColor);
            box.setBackground(background);
        }

        /** White for the even depths, light green for the odd ones. */
        private int getDepthColor(Context context, int depth) {
            int colorRes;
            if (depth % 2 == 0) {
                colorRes = R.color.comment_background_even;
            } else {
                colorRes = R.color.comment_background_odd;
            }
            return ContextCompat.getColor(context, colorRes);
        }
    }
}
