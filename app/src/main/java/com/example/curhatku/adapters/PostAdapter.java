package com.example.curhatku.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curhatku.R;
import com.example.curhatku.activities.PostDetailActivity;
import com.example.curhatku.models.Post;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private OnPostClickListener onPostClickListener;
    private Context context;

    public interface OnPostClickListener {
        void onPostClick(Post post);
        void onSupportClick(Post post);
    }

    public PostAdapter(List<Post> postList, OnPostClickListener listener) {
        this.postList = postList;
        this.onPostClickListener = listener;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.postList.clear();
        this.postList.addAll(newPosts);
        notifyDataSetChanged();
    }

    public void addPost(Post post) {
        postList.add(0, post);
        notifyItemInserted(0);
    }

    public void updatePost(Post updatedPost) {
        for (int i = 0; i < postList.size(); i++) {
            if (postList.get(i).getId().equals(updatedPost.getId())) {
                postList.set(i, updatedPost);
                notifyItemChanged(i);
                break;
            }
        }
    }

    class PostViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardPost;
        private TextView tvContent;
        private TextView tvCategory;
        private TextView tvMood;
        private TextView tvTimeAgo;
        private MaterialButton btnSupport;
        private MaterialButton btnComment;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPost = itemView.findViewById(R.id.card_post);
            tvContent = itemView.findViewById(R.id.tv_content);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvMood = itemView.findViewById(R.id.tv_mood);
            tvTimeAgo = itemView.findViewById(R.id.tv_time_ago);
            btnSupport = itemView.findViewById(R.id.btn_support);
            btnComment = itemView.findViewById(R.id.btn_comment);
        }

        public void bind(Post post) {
            tvContent.setText(post.getContent());
            tvCategory.setText(getCategoryEmoji(post.getCategory()) + " " + post.getCategory());
            tvMood.setText(getMoodEmoji(post.getMood()) + " " + post.getMood());
            tvTimeAgo.setText(getTimeAgo(post.getTimestamp()));
            btnSupport.setText("💜 " + post.getSupportCount());
            cardPost.setOnClickListener(v -> {
                if (onPostClickListener != null) {
                    onPostClickListener.onPostClick(post);
                }

                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("POST_ID", post.getId());
                context.startActivity(intent);
            });
            btnSupport.setOnClickListener(v -> {
                if (onPostClickListener != null) {
                    onPostClickListener.onSupportClick(post);
                }
            });
            btnComment.setOnClickListener(v -> {
                Intent intent = new Intent(context, PostDetailActivity.class);
                intent.putExtra("POST_ID", post.getId());
                context.startActivity(intent);
            });
            cardPost.setOnLongClickListener(v -> {
                return true;
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

        private String getCategoryEmoji(String category) {
            switch (category.toLowerCase()) {
                case "keluarga": return "👨‍👩‍👧‍👦";
                case "cinta": return "💕";
                case "pekerjaan": return "💼";
                case "kesehatan mental": return "🧠";
                case "pendidikan": return "📚";
                case "keuangan": return "💰";
                case "persahabatan": return "👫";
                case "hobi": return "🎨";
                default: return "💭";
            }
        }

        private String getMoodEmoji(String mood) {
            switch (mood.toLowerCase()) {
                case "senang": return "😊";
                case "bahagia": return "😄";
                case "sedih": return "😢";
                case "marah": return "😠";
                case "bingung": return "😕";
                case "takut": return "😰";
                case "kecewa": return "😞";
                case "lelah": return "😴";
                case "stress": return "😫";
                case "cemas": return "😰";
                case "optimis": return "😌";
                case "syukur": return "🙏";
                default: return "😐";
            }
        }
    }
}
