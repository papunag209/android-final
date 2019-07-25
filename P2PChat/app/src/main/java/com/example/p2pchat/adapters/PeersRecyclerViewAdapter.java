package com.example.p2pchat.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2pchat.R;

import java.util.ArrayList;
import java.util.List;


public class PeersRecyclerViewAdapter extends RecyclerView.Adapter<PeersRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "PeersRecyclerViewAdapter";
    List<String> peers;

    public PeersRecyclerViewAdapter(ArrayList<String> peers) {
        super();
        this.peers = peers;
//        peers = new ArrayList<>();
//        peers.add("abcd");
//        peers.add("1234");
//        peers.add("!@#$");
//        peers.add("abcd");
//        peers.add("1234");
//        peers.add("!@#$");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_peer_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(peers.get(position));
    }

    @Override
    public int getItemCount() {
        if (peers == null) return 0;
        return peers.size();
    }

    public void setDataSet(List<String> s){
        peers = s;
        notifyDataSetChanged();
    }

    class ViewHolder extends  RecyclerView.ViewHolder{
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView_peerName);
        }
    }
}
