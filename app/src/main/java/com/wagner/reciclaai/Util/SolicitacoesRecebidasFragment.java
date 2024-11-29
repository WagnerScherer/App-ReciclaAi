package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
    private CheckBox checkBoxPendente, checkBoxConfirmada, checkBoxRecusada;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private AgendamentoRecyclerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SolicitacoesRecebidasFragment", "onCreateView chamado");
        View view = inflater.inflate(R.layout.fragment_solicitacoes_recebidas, container, false);

        checkBoxPendente = view.findViewById(R.id.checkBoxPendente);
        checkBoxConfirmada = view.findViewById(R.id.checkBoxConfirmada);
        checkBoxRecusada = view.findViewById(R.id.checkBoxRecusada);

        checkBoxPendente.setChecked(false);
        checkBoxConfirmada.setChecked(false);
        checkBoxRecusada.setChecked(false);

        recyclerView = view.findViewById(R.id.recyclerViewSolicitacoesRecebidas);
        textViewTitulo = view.findViewById(R.id.textViewTitulo);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        //configurando os listeners para os checkboxes
        configurarListeners();

        carregarNomePontoColeta();
        carregarSolicitacoes();

        return view;
    }

    private void configurarListeners () {
        checkBoxPendente.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarFiltro());
        checkBoxConfirmada.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarFiltro());
        checkBoxRecusada.setOnCheckedChangeListener((buttonView, isChecked) -> atualizarFiltro());
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
                        try {
                            String idUsuario = document.getString("idUsuario");
                            Timestamp dataTimestamp = document.getTimestamp("data_coleta");
                            String dataColeta = AgendamentoSolicitado.formatarData(dataTimestamp);
                            List<String> tipoMaterial = (List<String>) document.get("tipo_material");
                            String idPontoColeta = document.getString("id_ponto_coleta");
                            int statusAgendamento = document.getLong("status_agendamento").intValue();

                            AgendamentoSolicitado agendamento = new AgendamentoSolicitado(
                                    idUsuario,
                                    dataColeta,
                                    dataTimestamp,
                                    tipoMaterial,
                                    idPontoColeta,
                                    statusAgendamento
                            );
                            agendamento.setId(document.getId());
                            agendamentos.add(agendamento);
                        } catch (Exception e) {
                            Log.e("CarregarSolicitacoes", "Erro ao processar documento: " + document.getId(), e);
                        }
                    }

                    // Inicializar o adapter com todos os dados
                    adapter = new AgendamentoRecyclerAdapter(agendamentos, requireContext(), this);
                    recyclerView.setAdapter(adapter);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao carregar agendamentos", e));
    }

    private void atualizarFiltro() {
        boolean filtrarPendente = checkBoxPendente.isChecked();
        boolean filtrarConfirmada = checkBoxConfirmada.isChecked();
        boolean filtrarRecusada = checkBoxRecusada.isChecked();

        // Se nenhum filtro estiver marcado, liste todos os registros
        if (!filtrarPendente && !filtrarConfirmada && !filtrarRecusada) {
            carregarSolicitacoes(); // Recarrega todos os registros
            return;
        }

        // Aplicar filtros apenas se algum checkbox estiver marcado
        db.collection("AGENDAMENTOS").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<AgendamentoSolicitado> agendamentosFiltrados = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        int statusAgendamento = document.getLong("status_agendamento").intValue();

                        if ((filtrarPendente && statusAgendamento == 1) || // 0: Pendente
                                (filtrarConfirmada && statusAgendamento == 3) || // 1: Confirmada
                                (filtrarRecusada && statusAgendamento == 2)) { // 2: Recusada

                            String idUsuario = document.getString("idUsuario");
                            Timestamp dataTimestamp = document.getTimestamp("data_coleta");
                            String dataColeta = AgendamentoSolicitado.formatarData(dataTimestamp);
                            List<String> tipoMaterial = (List<String>) document.get("tipo_material");
                            String idPontoColeta = document.getString("id_ponto_coleta");

                            AgendamentoSolicitado agendamento = new AgendamentoSolicitado(
                                    idUsuario,
                                    dataColeta,
                                    dataTimestamp,
                                    tipoMaterial,
                                    idPontoColeta,
                                    statusAgendamento
                            );
                            agendamento.setId(document.getId());
                            agendamentosFiltrados.add(agendamento);
                        }
                    }

                    // Atualizar a lista no adapter
                    adapter.atualizarLista(agendamentosFiltrados);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao aplicar filtro", e));
    }

    @Override
    public void recarregarSolicitacoes() {
        Log.d("SolicitacoesRecebidasFragment", "Recarregando lista de solicitações");
        carregarSolicitacoes();
        atualizarFiltro();
    }
}
