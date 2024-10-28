package com.wagner.reciclaai.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.Util.NotificacaoActivity;
import com.wagner.reciclaai.model.Notificacoes;

import java.util.List;

public class NotificacaoAdapter extends RecyclerView.Adapter<NotificacaoAdapter.ViewHolder> {

    private List<Notificacoes> listaNotificacoes;

    public NotificacaoAdapter(List<Notificacoes> listaNotificacoes) {
        this.listaNotificacoes = listaNotificacoes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_notificacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacoes notificacao = listaNotificacoes.get(position);
        holder.titulo.setText(notificacao.getTitulo());
        holder.mensagem.setText(notificacao.getMensagem());

        // Definir listener no checkbox
        holder.checkBoxLida.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Marcar a notificação como lida no Firestore
                NotificacaoActivity activity = (NotificacaoActivity) buttonView.getContext();
                activity.marcarNotificacaoComoLida(notificacao.getIdNotificacao());
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificacoes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo;
        public TextView mensagem;
        public CheckBox checkBoxLida;

        public ViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tituloNotificacao);
            mensagem = itemView.findViewById(R.id.textoNotificacao);
            checkBoxLida = itemView.findViewById(R.id.checkBoxNotificacao);
        }
    }
}
