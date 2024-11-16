package com.wagner.reciclaai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.Faq;

import java.util.List;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
    private List<Faq> faqList;

    public FaqAdapter(List<Faq> faqList) {
        this.faqList = faqList;
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_faq, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        Faq faq = faqList.get(position);
        holder.txtPergunta.setText(faq.getPergunta());
        holder.txtResposta.setText(faq.getResposta());

        holder.itemView.setOnClickListener(v -> {
            if (holder.txtResposta.getVisibility() == View.GONE) {
                holder.txtResposta.setVisibility(View.VISIBLE);
            } else {
                holder.txtResposta.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return faqList.size();
    }

    public static class FaqViewHolder extends RecyclerView.ViewHolder {
        TextView txtPergunta, txtResposta;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPergunta = itemView.findViewById(R.id.txtPergunta);
            txtResposta = itemView.findViewById(R.id.txtResposta);
        }
    }
}

