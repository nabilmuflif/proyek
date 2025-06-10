package com.example.curhatku.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curhatku.R;
import com.example.curhatku.models.Journal;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private List<Journal> journalList;
    private OnJournalClickListener onJournalClickListener;

    public interface OnJournalClickListener {
        void onJournalClick(Journal journal);
        void onJournalLongClick(Journal journal);
    }

    public JournalAdapter(List<Journal> journalList, OnJournalClickListener listener) {
        this.journalList = journalList;
        this.onJournalClickListener = listener;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_journal, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        holder.bind(journal);
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public void updateJournals(List<Journal> newJournals) {
        this.journalList.clear();
        this.journalList.addAll(newJournals);
        notifyDataSetChanged();
    }

    public void addJournal(Journal journal) {
        journalList.add(0, journal);
        notifyItemInserted(0);
    }

    public void removeJournal(int position) {
        if (position >= 0 && position < journalList.size()) {
            journalList.remove(position);
            notifyItemRemoved(position);
        }
    }

    class JournalViewHolder extends RecyclerView.ViewHolder {

        private MaterialCardView cardJournal;
        private TextView tvJournalTitle;
        private TextView tvJournalDate;
        private TextView tvJournalContentPreview;

        public JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            cardJournal = itemView.findViewById(R.id.card_journal);
            tvJournalTitle = itemView.findViewById(R.id.tv_journal_title);
            tvJournalDate = itemView.findViewById(R.id.tv_journal_date);
            tvJournalContentPreview = itemView.findViewById(R.id.tv_journal_content_preview);
        }

        public void bind(Journal journal) {
            tvJournalTitle.setText(journal.getTitle().isEmpty() ? "(No Title)" : journal.getTitle());

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM, HH:mm", Locale.getDefault());
            tvJournalDate.setText(sdf.format(new Date(journal.getDateTimestamp())));

            tvJournalContentPreview.setText(journal.getContent());

            if (onJournalClickListener != null) {
                cardJournal.setOnClickListener(v ->
                        onJournalClickListener.onJournalClick(journal)
                );

                cardJournal.setOnLongClickListener(v -> {
                    onJournalClickListener.onJournalLongClick(journal);
                    return true;
                });
            }
        }
    }
}