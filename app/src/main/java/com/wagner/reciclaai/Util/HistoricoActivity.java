package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.wagner.reciclaai.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wagner.reciclaai.adapter.HistoricoAdapter;
import com.wagner.reciclaai.model.Historico;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HistoricoActivity extends AppCompatActivity {
    private RecyclerView recyclerViewHistorico;
    private HistoricoAdapter historicoAdapter;
    private List<Historico> historicoList;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        // Inicializar o Firebase
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Configurar o RecyclerView
        recyclerViewHistorico = findViewById(R.id.recyclerViewHistorico);
        recyclerViewHistorico.setLayoutManager(new LinearLayoutManager(this));
        historicoList = new ArrayList<>();
        historicoAdapter = new HistoricoAdapter(historicoList, this);
        recyclerViewHistorico.setAdapter(historicoAdapter);

        carregarHistorico();
    }

    private void carregarHistorico() {
        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = auth.getCurrentUser().getUid();
        Log.d("HistoricoActivity", "Usuário autenticado com UID: " + uid);

        firestore.collection("AGENDAMENTOS")
                .whereEqualTo("idUsuario", uid)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    historicoList.clear();
                    Log.d("HistoricoActivity", "Consulta ao Firestore realizada com sucesso.");

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.d("HistoricoActivity", "Nenhum histórico encontrado para o usuário.");
                        Toast.makeText(this, "Nenhum histórico encontrado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        try {
                            Historico historico = new Historico();

                            historico.setIdUsuario(document.getString("idUsuario"));
                            historico.setIdPontoColeta(document.getString("id_ponto_coleta"));

                            // Converter o campo data_agendamento de Timestamp para String
                            Timestamp timestamp = document.getTimestamp("data_agendamento");
                            if (timestamp != null) {
                                historico.setDataAgendamento(Historico.formatarData(timestamp));
                            } else {
                                historico.setDataAgendamento("Data não disponível");
                            }

                            // Verificar se tipo_material é uma lista válida
                            List<String> materiais = (List<String>) document.get("tipo_material");
                            if (materiais != null) {
                                historico.setTipoMaterial(materiais);
                            } else {
                                Log.e("HistoricoActivity", "tipo_material está nulo ou vazio.");
                                historico.setTipoMaterial(new ArrayList<>());
                            }

                            //recuperar o ststus_agendamento
                            Long statusLong = document.getLong("status_agendamento");
                            int statusAgendamento = (statusLong != null) ? statusLong.intValue() : 0;
                            historico.setStatusAgendamento(statusAgendamento);

                            historicoList.add(historico);
                            Log.d("HistoricoActivity", "Documento adicionado: " + document.getId());
                        } catch (Exception e) {
                            Log.e("HistoricoActivity", "Erro ao mapear documento para Historico: " + e.getMessage());
                        }
                    }
                    historicoAdapter.notifyDataSetChanged();
                    Log.d("HistoricoActivity", "Dados carregados no adapter.");
                })
                .addOnFailureListener(e -> {
                    Log.e("HistoricoActivity", "Erro ao carregar histórico: " + e.getMessage());
                    Toast.makeText(HistoricoActivity.this, "Erro ao carregar histórico", Toast.LENGTH_SHORT).show();
                });
    }
}

