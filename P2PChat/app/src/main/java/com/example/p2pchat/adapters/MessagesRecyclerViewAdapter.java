package com.example.p2pchat.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2pchat.R;
import com.example.p2pchat.data.model.Message;
import com.example.p2pchat.data.model.MessageStatus;

import java.util.List;

public class MessagesRecyclerViewAdapter extends RecyclerView.Adapter<MessagesRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "MessagesRecyclerViewAda";
    List<Message> messages;
    int[] viewTypes = {R.layout.message_align_left, R.layout.message_align_right};

    public MessagesRecyclerViewAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: viewType: " + viewType);
        View view = LayoutInflater.from(parent.getContext()).inflate(viewTypes[viewType], parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.messageText.setText(messages.get(position).getMessageText() + "\n" + messages.get(position).getMessageTime());
    }

    @Override
    public int getItemCount() {
        if (messages==null) return 0;
        return messages.size();
    }

    public void updateDataSet(List<Message> messages){
        this.messages = messages;
        notifyDataSetChanged();
    }

    //todo implement properly based on message status
    @Override
    public int getItemViewType(int position) {
        String messageStatus = messages.get(position).getMessageStatus();
        Log.d(TAG, "getItemViewType: messageStatus is: " +  messageStatus);
        if (messageStatus.equals(MessageStatus.RECEIVED)){
            return 0;
        } else {
            return 1;
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView messageText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //int viewId = itemView.getId();
            this.messageText = itemView.findViewById(R.id.textView_message);
        }
    }
}
