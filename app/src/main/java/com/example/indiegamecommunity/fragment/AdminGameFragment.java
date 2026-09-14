package com.example.indiegamecommunity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.indiegamecommunity.R;
import com.example.indiegamecommunity.database.DatabaseHelper;
import com.example.indiegamecommunity.model.Crowdfunding;
import com.example.indiegamecommunity.model.GamePromo;
import com.example.indiegamecommunity.model.Vote;

import java.util.List;

public class AdminGameFragment extends Fragment {
    private RecyclerView rvGames;
    private DatabaseHelper databaseHelper;

    private View tabPromos, tabVotes, tabFundings;
    private TextView tvPromos, tvVotes, tvFundings;
    private int currentTab = 0;
    private int selectedColor, unselectedColor;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_admin_games, container, false);
        rvGames = view.findViewById(R.id.rvGames);

        tabPromos = view.findViewById(R.id.tab_promos);
        tabVotes = view.findViewById(R.id.tab_votes);
        tabFundings = view.findViewById(R.id.tab_fundings);
        tvPromos = view.findViewById(R.id.tv_promos);
        tvVotes = view.findViewById(R.id.tv_votes);
        tvFundings = view.findViewById(R.id.tv_fundings);

        databaseHelper = new DatabaseHelper(getContext());
        rvGames.setLayoutManager(new LinearLayoutManager(getContext()));

        selectedColor = ContextCompat.getColor(requireContext(), R.color.acfun_text_white);
        unselectedColor = ContextCompat.getColor(requireContext(), R.color.acfun_text_white);
        // 未选中时半透明
        unselectedColor = (unselectedColor & 0x00FFFFFF) | 0x99000000;

        tabPromos.setOnClickListener(v -> setTab(0));
        tabVotes.setOnClickListener(v -> setTab(1));
        tabFundings.setOnClickListener(v -> setTab(2));

        setTab(0);
        return view;
    }

    private void setTab(int index) {
        currentTab = index;
        tvPromos.setAlpha(index == 0 ? 1f : 0.5f);
        tvVotes.setAlpha(index == 1 ? 1f : 0.5f);
        tvFundings.setAlpha(index == 2 ? 1f : 0.5f);
        loadData();
    }

    private void loadData() {
        if (currentTab == 0) {
            List<GamePromo> list = databaseHelper.getAllGamePromos();
            rvGames.setAdapter(new GameListAdapter<GamePromo>(list) {
                @Override String getName(GamePromo g) { return g.getName(); }
                @Override String getDesc(GamePromo g) { return g.getDescription(); }
                @Override void onDelete(GamePromo g) { databaseHelper.deleteGamePromo(g.getId()); }
            });
        } else if (currentTab == 1) {
            List<Vote> list = databaseHelper.getAllVotes();
            rvGames.setAdapter(new GameListAdapter<Vote>(list) {
                @Override String getName(Vote v) { return v.getGameName(); }
                @Override String getDesc(Vote v) { return v.getVoteCount() + " 票"; }
                @Override void onDelete(Vote v) { databaseHelper.deleteVote(v.getId()); }
            });
        } else {
            List<Crowdfunding> list = databaseHelper.getAllCrowdfundings();
            rvGames.setAdapter(new GameListAdapter<Crowdfunding>(list) {
                @Override String getName(Crowdfunding c) { return c.getName(); }
                @Override String getDesc(Crowdfunding c) { return "¥" + c.getCurrentAmount() + " / ¥" + c.getTargetAmount() + "  " + c.getSupporterCount() + "人支持"; }
                @Override void onDelete(Crowdfunding c) { databaseHelper.deleteCrowdfunding(c.getId()); }
            });
        }
    }

    private abstract class GameListAdapter<T> extends RecyclerView.Adapter<ViewHolder> {
        private List<T> items;
        GameListAdapter(List<T> items) { this.items = items; }
        abstract String getName(T item);
        abstract String getDesc(T item);
        abstract void onDelete(T item);

        @NonNull @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_promo, parent, false);
            return new ViewHolder(v);
        }
        @Override
        public void onBindViewHolder(@NonNull ViewHolder h, int pos) {
            T item = items.get(pos);
            h.tvName.setText(getName(item));
            h.tvDesc.setText(getDesc(item));
            h.itemView.setOnClickListener(v -> {
                new AlertDialog.Builder(getContext())
                        .setTitle("管理")
                        .setMessage(getName(item) + "\n" + getDesc(item))
                        .setPositiveButton("删除", (d, w) -> { onDelete(item); loadData(); })
                        .setNegativeButton("取消", null)
                        .show();
            });
        }
        @Override public int getItemCount() { return items.size(); }
    }

    @Override
    public void onResume() { super.onResume(); loadData(); }

    private static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvDesc;
        ViewHolder(View v) {
            super(v);
            tvName = v.findViewById(R.id.tvGameName);
            tvDesc = v.findViewById(R.id.tvGameDescription);
        }
    }
}
