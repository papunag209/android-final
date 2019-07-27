package com.example.p2pchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2pchat.R;
import com.example.p2pchat.data.model.Session;

import java.util.List;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> {
    List<Session> sessions;

    public HistoryRecyclerViewAdapter(List<Session> sessions) {
        this.sessions = sessions;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chatMessagesCount.setText("133");
        holder.phoneName.setText(sessions.get(position).getPeerPhoneName());
        holder.sessionStartDate.setText(sessions.get(position).getSessionStartTime());
    }

    @Override
    public int getItemCount() {
        if (sessions == null) return 0;
        return sessions.size();
    }

    public void updateDataSet(List<Session> sessions){
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView phoneName;
        TextView sessionStartDate;
        TextView chatMessagesCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.phoneName = itemView.findViewById(R.id.textView_phoneName);
            this.sessionStartDate = itemView.findViewById(R.id.textView_sessionStartDate);
            this.chatMessagesCount = itemView.findViewById(R.id.textView_chatMessagesCount);
        }
    }
}
