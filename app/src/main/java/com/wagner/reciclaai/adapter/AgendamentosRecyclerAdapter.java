package com.wagner.reciclaai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.Agendamento;
import java.util.List;

public class AgendamentosRecyclerAdapter extends RecyclerView.Adapter<AgendamentosRecyclerAdapter.ViewHolder> {

    private final List<Agendamento> agendamentoList;

    public AgendamentosRecyclerAdapter(List<Agendamento> agendamentoList) {
        this.agendamentoList = agendamentoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_item_agendamento, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Agendamento agendamento = agendamentoList.get(position);
        holder.textView.setText("Agendamento ID: " + agendamento.getId_Agendamento());
    }

    @Override
    public int getItemCount() {
        return agendamentoList != null ? agendamentoList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textViewItem);
        }
    }
}
