package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.AgendamentosRecyclerAdapter;
import com.wagner.reciclaai.model.Agendamento;
import java.util.ArrayList;
import java.util.List;

public class SolicitacoesRecebidasFragment extends Fragment {

    private RecyclerView recyclerView;
    private AgendamentosRecyclerAdapter adapter;
    private List<Agendamento> agendamentoList;
    private FirebaseFirestore db;

    public SolicitacoesRecebidasFragment() {
        // Construtor vazio necessário
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitacoes_recebidas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSolicitacoesRecebidas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        agendamentoList = new ArrayList<>();
        adapter = new AgendamentosRecyclerAdapter(agendamentoList);
        recyclerView.setAdapter(adapter);

        carregarSolicitacoes();

        return view;
    }

    private void carregarSolicitacoes() {
        db = FirebaseFirestore.getInstance();
        db.collection("AGENDAMENTOS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Agendamento agendamento = doc.toObject(Agendamento.class);
                        agendamentoList.add(agendamento);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    // Tratar erro
                });
    }
}
