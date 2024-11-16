package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.AgendamentoRecyclerAdapter;
import com.wagner.reciclaai.model.AgendamentoSolicitado;
import com.wagner.reciclaai.Util.RecargaListener;

import java.util.ArrayList;
import java.util.List;

public class SolicitacoesRecebidasFragment extends Fragment implements RecargaListener {

    private RecyclerView recyclerView;
    private TextView textViewTitulo;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SolicitacoesRecebidasFragment", "onCreateView chamado");
        View view = inflater.inflate(R.layout.fragment_solicitacoes_recebidas, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewSolicitacoesRecebidas);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        carregarNomePontoColeta();
        carregarSolicitacoes();

        return view;
    }

    private void carregarNomePontoColeta() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Log.e("SolicitaçõesRecebidas", "Usuário não autenticado.");
            return;
        }

        //Buscar o idPontoColeta do usuário autenticado
        db.collection("USUARIOS").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idPontoColeta = documentSnapshot.getString("idPontoColeta");
                        if (idPontoColeta != null) {
                            buscarNomePontoColeta(idPontoColeta);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Erro ao carregar usuário."));
    }

    private void buscarNomePontoColeta(String idPontoColeta) {
        // Buscar o nome do ponto de coleta usando o idPontoColeta
        db.collection("PONTOSCOLETA").document(idPontoColeta)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nomePontoColeta = documentSnapshot.getString("nome");
                        if (nomePontoColeta != null) {
                            textViewTitulo.setText(nomePontoColeta);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("FirebaseError", "Erro ao carregar ponto de coleta", e));
    }

    private void carregarSolicitacoes() {
        db.collection("AGENDAMENTOS").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AgendamentoSolicitado> agendamentos = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String idUsuario = document.getString("idUsuario");
                        Timestamp dataTimestamp = document.getTimestamp("data_coleta");
                        String dataColeta = AgendamentoSolicitado.formatarData(dataTimestamp);
                        List<String> tipoMaterial = (List<String>) document.get("tipo_material");
                        String idPontoColeta = document.getString("id_ponto_coleta");
                        int statusAgendamento = document.getLong("status_agendamento").intValue();

                        // Ajuste para utilizar o construtor correto
                        AgendamentoSolicitado agendamento = new AgendamentoSolicitado(
                                idUsuario,
                                dataColeta,
                                dataTimestamp,
                                tipoMaterial,
                                idPontoColeta,
                                statusAgendamento
                        );

                        agendamento.setId(document.getId()); // Não se esqueça de definir o ID
                        agendamentos.add(agendamento);
                    }

                    AgendamentoRecyclerAdapter adapter = new AgendamentoRecyclerAdapter(agendamentos, getContext(), this);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao carregar agendamentos", e));
    }

    @Override
    public void recarregarSolicitacoes() {
        Log.d("SolicitacoesRecebidasFragment", "Recarregando lista de solicitações");
        carregarSolicitacoes();
    }

}
