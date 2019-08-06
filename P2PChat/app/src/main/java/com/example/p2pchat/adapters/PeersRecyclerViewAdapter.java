package com.example.p2pchat.adapters;

import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.p2pchat.R;
import com.example.p2pchat.data.model.dataholder.PeerStatusHolder;

import java.util.ArrayList;
import java.util.List;

import static com.example.p2pchat.MainActivity.getDeviceStatus;


public class PeersRecyclerViewAdapter extends RecyclerView.Adapter<PeersRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "PeersRecyclerViewAdapter";
    List<WifiP2pDevice> peers;
    OnRecycleItem onRecycleItem;

    public List<WifiP2pDevice> getDataset(){
        return peers;
    }

    public PeersRecyclerViewAdapter(ArrayList<WifiP2pDevice> peers) {
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
    public void setRecycleItem(OnRecycleItem onRecycleItem){
        this.onRecycleItem = onRecycleItem;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_peer_recycler_item, parent, false);
        ViewHolder holder = new ViewHolder(view, this.onRecycleItem);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.peerNameTextView.setText(peers.get(position).deviceName);
        holder.peerStatusTextView.setText(getDeviceStatus(peers.get(position).status));
    }

    @Override
    public int getItemCount() {
        if (peers == null) return 0;
        return peers.size();
    }

    public void setDataSet(List<WifiP2pDevice> s) {
        peers = s;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView peerNameTextView,peerStatusTextView;
        OnRecycleItem onRecycleItem;

        public ViewHolder(@NonNull View itemView, final OnRecycleItem onRecycleItem) {
            super(itemView);
            peerNameTextView = itemView.findViewById(R.id.textView_peerName);
            peerStatusTextView = itemView.findViewById(R.id.textView_peerStatus);
            this.onRecycleItem = onRecycleItem;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onRecycleItem.onClick(getAdapterPosition());
                }
            });
        }
    }

    public interface OnRecycleItem {
        void onClick(int position);
    }
}
