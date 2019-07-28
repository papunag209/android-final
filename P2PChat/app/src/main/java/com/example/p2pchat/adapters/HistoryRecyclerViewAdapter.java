package com.example.p2pchat.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2pchat.R;
import com.example.p2pchat.data.model.Session;
import com.example.p2pchat.data.model.helperModel.SessionWithMessageCount;

import java.util.List;


public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder> implements OnRecycleItem{
    List<SessionWithMessageCount> sessions;
    OnItemAction<Session> onItemAction;

    public HistoryRecyclerViewAdapter(List<SessionWithMessageCount> sessions, OnItemAction<Session> onItemAction) {
        this.sessions = sessions;
        this.onItemAction = onItemAction;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view, this);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.chatMessagesCount.setText(""+sessions.get(position).getMessageCount());
        holder.phoneName.setText(sessions.get(position).getPeerPhoneName());
        holder.sessionStartDate.setText(sessions.get(position).getSessionStartTime());
    }

    @Override
    public int getItemCount() {
        if (sessions == null) return 0;
        return sessions.size();
    }

    public void updateDataSet(List<SessionWithMessageCount> sessions){
        this.sessions = sessions;
        notifyDataSetChanged();
    }

    @Override
    public void onLongPress(int position) {
        onItemAction.onLongPress(sessions.get(position));
    }

    @Override
    public void onClick(int position) {
        onItemAction.onClick(sessions.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private static final String TAG = "ViewHolder";
        TextView phoneName;
        TextView sessionStartDate;
        TextView chatMessagesCount;
        OnRecycleItem onRecycleItem;
        public ViewHolder(@NonNull View itemView, OnRecycleItem onRecycleItem) {
            super(itemView);
            this.phoneName = itemView.findViewById(R.id.textView_phoneName);
            this.sessionStartDate = itemView.findViewById(R.id.textView_sessionStartDate);
            this.chatMessagesCount = itemView.findViewById(R.id.textView_chatMessagesCount);
            this.onRecycleItem = onRecycleItem;
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);
        }
        @Override
        public boolean onLongClick(View view) {
            Log.d(TAG, "onLongClick: inside the adapter pos: " + getAdapterPosition());
            onRecycleItem.onLongPress(getAdapterPosition());
            return false;
        }

        @Override
        public void onClick(View view) {
            onRecycleItem.onClick(getAdapterPosition());
        }
    }

}

interface OnRecycleItem{
    void onLongPress(int position);
    void onClick(int position);
}