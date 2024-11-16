package com.wagner.reciclaai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.Ranking;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {
    private List<Ranking> rankingList;

    public RankingAdapter(List<Ranking> rankingList) {
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_ranking, parent, false);
        return new RankingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Ranking model = rankingList.get(position);

        // Configurar ícone da posição
        switch (position) {
            case 0: holder.imgPosition.setImageResource(R.drawable.ic_primeiro_colocado); break;
            case 1: holder.imgPosition.setImageResource(R.drawable.ic_segundo_colocado); break;
            case 2: holder.imgPosition.setImageResource(R.drawable.ic_terceiro_colocado); break;
            default: holder.imgPosition.setImageResource(R.drawable.ic_quarto_colocado);
        }

        holder.txtPosition.setText(String.valueOf(position + 1));
        holder.txtUsername.setText(model.getUsername());
        holder.txtColetas.setText(model.getColetas() + " coletas");
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPosition;
        TextView txtPosition, txtUsername, txtColetas;

        public RankingViewHolder(@NonNull View itemView) {
            super(itemView);
            imgPosition = itemView.findViewById(R.id.imgPosition);
            txtPosition = itemView.findViewById(R.id.txtPosition);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtColetas = itemView.findViewById(R.id.txtColetas);
        }
    }
}
