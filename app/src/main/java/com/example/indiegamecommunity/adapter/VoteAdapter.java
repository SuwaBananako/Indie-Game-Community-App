package com.example.indiegamecommunity.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.model.Vote;

import java.util.List;

public class VoteAdapter extends RecyclerView.Adapter<VoteAdapter.ViewHolder> {
    private Context context;
    private List<Vote> voteList;
    private OnItemClickListener voteListener;
    private OnItemClickListener itemListener;

    public interface OnItemClickListener {
        void onVoteClick(Vote vote);
    }

    public VoteAdapter(Context context, List<Vote> voteList, OnItemClickListener voteListener, OnItemClickListener itemListener) {
        this.context = context;
        this.voteList = voteList;
        this.voteListener = voteListener;
        this.itemListener = itemListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_vote, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Vote vote = voteList.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvGameName.setText(vote.getGameName());
        holder.tvVoteCount.setText(String.valueOf(vote.getVoteCount()));

        // 加载游戏图标
        String iconPath = vote.getIconPath();
        if (iconPath != null && !iconPath.isEmpty()) {
            try {
                holder.ivGameIcon.setImageBitmap(BitmapFactory.decodeFile(iconPath));
            } catch (Exception e) {
                holder.ivGameIcon.setImageResource(R.drawable.ic_game_controller);
            }
        } else {
            holder.ivGameIcon.setImageResource(R.drawable.ic_game_controller);
        }

        holder.btnVote.setOnClickListener(v -> {
            if (voteListener != null && position >= 0 && position < voteList.size()) {
                voteListener.onVoteClick(voteList.get(position));
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (itemListener != null && position >= 0 && position < voteList.size()) {
                itemListener.onVoteClick(voteList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return voteList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvGameName, tvVoteCount;
        View btnVote;
        ImageView ivGameIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvGameName = itemView.findViewById(R.id.tvGameName);
            tvVoteCount = itemView.findViewById(R.id.tvVoteCount);
            btnVote = itemView.findViewById(R.id.btnVote);
            ivGameIcon = itemView.findViewById(R.id.ivGameIcon);
        }
    }
}
