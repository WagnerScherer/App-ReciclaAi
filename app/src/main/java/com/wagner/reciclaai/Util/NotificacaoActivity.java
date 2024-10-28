package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.wagner.reciclaai.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wagner.reciclaai.adapter.NotificacaoAdapter;
import com.wagner.reciclaai.model.Notificacoes;

import java.util.ArrayList;
import java.util.List;

public class NotificacaoActivity extends AppCompatActivity {

    private RecyclerView recyclerViewNotificacoes;
    private NotificacaoAdapter notificacaoAdapter;
    private FirebaseFirestore db;
    private String userUid;
    private List<Notificacoes> listaNotificacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificacao);

        recyclerViewNotificacoes = findViewById(R.id.recyclerViewNotificacoes);
        recyclerViewNotificacoes.setLayoutManager(new LinearLayoutManager(this));

        listaNotificacoes = new ArrayList<>();
        notificacaoAdapter = new NotificacaoAdapter(listaNotificacoes);
        recyclerViewNotificacoes.setAdapter(notificacaoAdapter);

        // Inicializar Firestore e pegar UID do usuário logado
        db = FirebaseFirestore.getInstance();
        userUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Carregar notificações da coleção NOTIFICACOES
        carregarNotificacoes();
    }

    private void carregarNotificacoes() {
        db.collection("NOTIFICACOES")
                .whereEqualTo("id_usuario", userUid)
                .whereEqualTo("status", false) // Carregar notificações não lidas
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listaNotificacoes.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Notificacoes notificacao = document.toObject(Notificacoes.class);
                                notificacao.setIdNotificacao(document.getId());  // Setar ID para updates
                                listaNotificacoes.add(notificacao);
                            }
                            notificacaoAdapter.notifyDataSetChanged();
                        } else {
                            Log.w("NotificacaoActivity", "Erro ao carregar notificações", task.getException());
                        }
                    }
                });
    }

    // Método para atualizar a notificação como lida
    public void marcarNotificacaoComoLida(String notificacaoId) {
        db.collection("NOTIFICACOES").document(notificacaoId)
                .update("status", true) // Atualizar o campo 'status' para true (lida)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("NotificacaoActivity", "Notificação marcada como lida.");
                            carregarNotificacoes(); // Atualizar a lista de notificações
                        } else {
                            Log.w("NotificacaoActivity", "Erro ao marcar notificação como lida", task.getException());
                        }
                    }
                });
    }
}
