package com.example.curhatku.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curhatku.R;
import com.example.curhatku.models.Comment;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private List<Comment> commentList;
    private OnCommentClickListener onCommentClickListener;

    public interface OnCommentClickListener {
        void onCommentClick(Comment comment);
        void onCommentLongClick(Comment comment);
    }

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public CommentAdapter(List<Comment> commentList, OnCommentClickListener listener) {
        this.commentList = commentList;
        this.onCommentClickListener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void updateComments(List<Comment> newComments) {
        this.commentList.clear();
        this.commentList.addAll(newComments);
        notifyDataSetChanged();
    }

    public void addComment(Comment comment) {
        commentList.add(0, comment);
        notifyItemInserted(0);
    }

    public void removeComment(int position) {
        if (position >= 0 && position < commentList.size()) {
            commentList.remove(position);
            notifyItemRemoved(position);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardComment;
        private TextView tvCommentContent;
        private TextView tvAnonymousUser;
        private TextView tvCommentTime;
        private View indicatorAnonymous;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            cardComment = itemView.findViewById(R.id.card_comment);
            tvCommentContent = itemView.findViewById(R.id.tv_comment_content);
            tvAnonymousUser = itemView.findViewById(R.id.tv_anonymous_user);
            tvCommentTime = itemView.findViewById(R.id.tv_comment_time);
            indicatorAnonymous = itemView.findViewById(R.id.indicator_anonymous);
        }

        public void bind(Comment comment) {
            tvCommentContent.setText(comment.getContent());

            if (comment.isAnonymous()) {
                tvAnonymousUser.setText("👤 Anonim");
                indicatorAnonymous.setVisibility(View.VISIBLE);
            } else {
                tvAnonymousUser.setText("👤 Pengguna");
                indicatorAnonymous.setVisibility(View.GONE);
            }

            tvCommentTime.setText(getTimeAgo(comment.getTimestamp()));

            if (onCommentClickListener != null) {
                cardComment.setOnClickListener(v ->
                        onCommentClickListener.onCommentClick(comment)
                );

                cardComment.setOnLongClickListener(v -> {
                    onCommentClickListener.onCommentLongClick(comment);
                    return true;
                });
            }

            cardComment.setOnClickListener(v -> {
                cardComment.setCardElevation(8f);
                cardComment.postDelayed(() -> cardComment.setCardElevation(2f), 150);

                if (onCommentClickListener != null) {
                    onCommentClickListener.onCommentClick(comment);
                }
            });
        }

        private String getTimeAgo(long timestamp) {
            long now = System.currentTimeMillis();
            long diff = now - timestamp;

            long seconds = diff / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long weeks = days / 7;
            long months = days / 30;

            if (seconds < 60) {
                return "baru saja";
            } else if (minutes < 60) {
                return minutes + " menit lalu";
            } else if (hours < 24) {
                return hours + " jam lalu";
            } else if (days < 7) {
                return days + " hari lalu";
            } else if (weeks < 4) {
                return weeks + " minggu lalu";
            } else if (months < 12) {
                return months + " bulan lalu";
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault());
                return sdf.format(new Date(timestamp));
            }
        }
    }
}