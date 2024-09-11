/*
tela de consulta atual

package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.ConsultaPontoAdapter;
import com.wagner.reciclaai.model.PontoColeta;

import java.util.concurrent.atomic.AtomicReference;

import java.util.ArrayList;
import java.util.List;

public class ConsultaPontoActivity extends AppCompatActivity {

    private CheckBox checkBoxEletronicos, checkBoxLampadas, checkBoxPilhas, checkBoxOleo, checkBoxFavoritos;
    private Button botaoPesquisar;
    private RecyclerView recyclerViewPontosColeta;
    private ConsultaPontoAdapter consultaPontoAdapter;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<PontoColeta> listaCompletaPontos = new ArrayList<>(); // Lista completa dos pontos exibidos
    private List<PontoColeta> listaFavoritos = new ArrayList<>(); // Lista filtrada com os favoritos


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_pontocoleta);

        // Inicializar os componentes do layout
        checkBoxEletronicos = findViewById(R.id.checkBoxEletronicos);
        checkBoxLampadas = findViewById(R.id.checkBoxLampadas);
        checkBoxPilhas = findViewById(R.id.checkBoxPilhas);
        checkBoxOleo = findViewById(R.id.checkBoxOleo);
        checkBoxFavoritos = findViewById(R.id.checkBoxFavoritos);
        botaoPesquisar = findViewById(R.id.buttonPesquisar);
        recyclerViewPontosColeta = findViewById(R.id.recyclerViewPontosColeta);

        // Configurar o RecyclerView
        recyclerViewPontosColeta.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar Firestore e FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Ação do botão "Pesquisar"
        botaoPesquisar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chamar o método para pesquisar pontos de coleta
                pesquisarPontosColeta();
            }
        });

        checkBoxFavoritos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Filtrar lista completa para exibir apenas os favoritos
                listaFavoritos.clear();
                String uid = auth.getCurrentUser().getUid();

                db.collection("FAVORITOS")
                        .whereEqualTo("uidUsuario", uid)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<String> idsFavoritos = new ArrayList<>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    idsFavoritos.add(document.getString("idPontoColeta"));
                                }

                                // Filtrar os pontos de coleta que estão na lista de favoritos
                                for (PontoColeta ponto : listaCompletaPontos) {
                                    if (idsFavoritos.contains(ponto.getId_PC())) {
                                        listaFavoritos.add(ponto);
                                    }
                                }

                                // Atualizar o adapter com a lista de favoritos
                                consultaPontoAdapter = new ConsultaPontoAdapter(listaFavoritos, ConsultaPontoActivity.this);
                                recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);

                                if (listaFavoritos.isEmpty()) {
                                    Toast.makeText(ConsultaPontoActivity.this, "Nenhum favorito encontrado.", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ConsultaPontoActivity.this, "Erro ao buscar favoritos.", Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {
                // Restaurar a lista completa quando o checkbox for desmarcado
                consultaPontoAdapter = new ConsultaPontoAdapter(listaCompletaPontos, ConsultaPontoActivity.this);
                recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);
            }
        });


    }


    private void pesquisarPontosColeta() {
        List<Integer> materiaisFiltrados = new ArrayList<>();
        String nomeDigitado = ((EditText) findViewById(R.id.editTextNome)).getText().toString().trim().toLowerCase(); // Convertendo para minúsculas

        // Adicionar os filtros de material se os checkboxes estiverem selecionados
        if (checkBoxEletronicos.isChecked()) {
            materiaisFiltrados.add(4); // ID do material Eletrônicos
        }
        if (checkBoxLampadas.isChecked()) {
            materiaisFiltrados.add(3); // ID do material Lâmpadas
        }
        if (checkBoxPilhas.isChecked()) {
            materiaisFiltrados.add(1); // ID do material Pilhas/Baterias
        }
        if (checkBoxOleo.isChecked()) {
            materiaisFiltrados.add(2); // ID do material Óleo de Cozinha
        }

        // Usar AtomicReference para permitir mutabilidade dentro da Lambda
        AtomicReference<Query> consulta = new AtomicReference<>(db.collection("PONTOSCOLETA"));

        // Filtro por nome (se houver)
        if (!nomeDigitado.isEmpty()) {
            consulta.set(consulta.get().whereGreaterThanOrEqualTo("nome_lowercase", nomeDigitado)
                    .whereLessThanOrEqualTo("nome_lowercase", nomeDigitado + "\uf8ff")); // Para busca parcial, usando nome_lowercase
        }

        // Verificar se o checkbox de favoritos está marcado
        if (checkBoxFavoritos.isChecked()) {
            String uid = auth.getCurrentUser().getUid();
            db.collection("FAVORITOS")
                    .whereEqualTo("uidUsuario", uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> idsFavoritos = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                idsFavoritos.add(document.getString("idPontoColeta"));
                            }

                            if (!idsFavoritos.isEmpty()) {
                                // Ajustar a consulta para incluir favoritos
                                consulta.set(consulta.get().whereIn("id_PC", idsFavoritos));

                                // Executar consulta após filtrar favoritos
                                aplicarFiltrosMateriais(consulta, materiaisFiltrados);
                            } else {
                                // Se não houver favoritos, exibir mensagem apropriada
                                consultaPontoAdapter = new ConsultaPontoAdapter(new ArrayList<>(), ConsultaPontoActivity.this);
                                recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);
                                Toast.makeText(ConsultaPontoActivity.this, "Nenhum favorito encontrado.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ConsultaPontoActivity.this, "Erro ao buscar favoritos.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Se o checkbox de favoritos não estiver marcado, continuar a pesquisa normal
            aplicarFiltrosMateriais(consulta, materiaisFiltrados);
        }
    }


    private void aplicarFiltrosMateriais(AtomicReference<Query> consulta, List<Integer> materiaisFiltrados) {
        if (!materiaisFiltrados.isEmpty()) {
            // Buscar pontos de coleta que coletam os materiais selecionados
            db.collection("PONTOSCOLETA_MATERIAIS")
                    .whereIn("id_material", materiaisFiltrados)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            List<String> idsPontosMateriais = new ArrayList<>();
                            for (DocumentSnapshot document : task.getResult()) {
                                idsPontosMateriais.add(document.getString("id_ponto_coleta"));
                            }

                            if (!idsPontosMateriais.isEmpty()) {
                                // Usar AtomicReference para modificar a consulta
                                consulta.set(consulta.get().whereIn("id_PC", idsPontosMateriais));
                            }

                            // Executar a consulta final com todos os filtros aplicados
                            executarConsulta(consulta.get());
                        }
                    });
        } else {
            // Se não houver filtro de materiais, executar a consulta diretamente
            executarConsulta(consulta.get());
        }
    }


    private void executarConsulta(Query consulta) {
        consulta.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                listaCompletaPontos.clear(); // Limpar a lista antes de preencher novamente
                for (DocumentSnapshot document : task.getResult()) {
                    PontoColeta pontoColeta = document.toObject(PontoColeta.class);
                    pontoColeta.setId_PC(document.getId());
                    listaCompletaPontos.add(pontoColeta); // Armazenar a lista completa
                }

                if (!listaCompletaPontos.isEmpty()) {
                    // Exibir a lista completa inicialmente
                    consultaPontoAdapter = new ConsultaPontoAdapter(listaCompletaPontos, ConsultaPontoActivity.this);
                    recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);
                } else {
                    consultaPontoAdapter = new ConsultaPontoAdapter(new ArrayList<>(), ConsultaPontoActivity.this);
                    recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);
                    Toast.makeText(ConsultaPontoActivity.this, "Nenhum ponto de coleta encontrado.", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(ConsultaPontoActivity.this, "Erro ao buscar pontos de coleta", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
*/