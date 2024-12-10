package com.wagner.reciclaai.Util;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.ConsultaPontoAdapter;
import com.wagner.reciclaai.model.PontoColeta;

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

        //Aplicação de linha divisória entre os itens do recyclerView aqui
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerViewPontosColeta.getContext(),
                LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.linha_divisoria_itens_recyclerview));
        recyclerViewPontosColeta.addItemDecoration(dividerItemDecoration);

        // Inicializar Firestore e FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Ação do botão "Pesquisar"
        botaoPesquisar.setOnClickListener(v -> pesquisarPontosColeta());

        //Regra para mostrar apenas os favoritos
        //Faço a consulta: exibe todos os registros encontrados
        //Ao marcar o box, vai mostrar somente os favoritos, se houver. Ao desmarcar, exibe o resultado da consulta anterior
        //contendo todos os registros
        checkBoxFavoritos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                filtrarFavoritos();
            } else {
                // Restaurar a lista completa quando o checkbox for desmarcado
                consultaPontoAdapter = new ConsultaPontoAdapter(listaCompletaPontos, ConsultaPontoActivity.this);
                recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 2);
        }
    }

    private void pesquisarPontosColeta() {
        List<Integer> materiaisFiltrados = new ArrayList<>();
        String nomeDigitado = ((EditText) findViewById(R.id.editTextNome)).getText().toString().trim();

        // Adicionar os filtros de material se os checkboxes estiverem selecionados
        if (checkBoxEletronicos.isChecked()) materiaisFiltrados.add(4);
        if (checkBoxLampadas.isChecked()) materiaisFiltrados.add(3);
        if (checkBoxPilhas.isChecked()) materiaisFiltrados.add(1);
        if (checkBoxOleo.isChecked()) materiaisFiltrados.add(2);

        // Cenário 1: Nome vazio e checkboxes vazios
        if (nomeDigitado.isEmpty() && materiaisFiltrados.isEmpty()) {
            buscarTodosPontosColeta();
        }
        // Cenário 2: Nome preenchido e nenhum checkbox de material marcado
        else if (!nomeDigitado.isEmpty() && materiaisFiltrados.isEmpty()) {
            buscarPontosPorNome(nomeDigitado);
        }
        // Cenário 3: Nome preenchido e algum checkbox de material marcado
        else if (!nomeDigitado.isEmpty() && !materiaisFiltrados.isEmpty()) {
            buscarPontosPorNomeEMaterial(nomeDigitado, materiaisFiltrados);
        }
        // Cenário 4: Nome vazio e algum checkbox de material marcado
        else if (nomeDigitado.isEmpty() && !materiaisFiltrados.isEmpty()) {
            buscarPontosPorMaterial(materiaisFiltrados);
        }
    }

    //Consulta do cenário 1
    private void buscarTodosPontosColeta() {
        db.collection("PONTOSCOLETA")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaCompletaPontos.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            PontoColeta pontoColeta = document.toObject(PontoColeta.class);
                            pontoColeta.setId_PC(document.getId());

                            // Carregar os materiais coletados para cada ponto de coleta
                            carregarMateriaisColetados(pontoColeta);

                            // Adicionar o ponto de coleta à lista
                            listaCompletaPontos.add(pontoColeta);
                        }
                        // Após adicionar os pontos e carregar os materiais, atualizar o RecyclerView
                        atualizarRecyclerView();
                    } else {
                        Toast.makeText(this, "Erro ao buscar pontos de coleta.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Consulta do cenário 2
    private void buscarPontosPorNome(String nomeDigitado) {
        String nomeDigitadoLower = nomeDigitado.toLowerCase();
        db.collection("PONTOSCOLETA")
                .whereGreaterThanOrEqualTo("nome_lowercase", nomeDigitadoLower)
                .whereLessThanOrEqualTo("nome_lowercase", nomeDigitadoLower + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaCompletaPontos.clear();
                        for (DocumentSnapshot document : task.getResult()) {
                            PontoColeta pontoColeta = document.toObject(PontoColeta.class);
                            pontoColeta.setId_PC(document.getId());

                            // Carregar os materiais coletados para cada ponto de coleta
                            carregarMateriaisColetados(pontoColeta);

                            // Adicionar o ponto de coleta à lista
                            listaCompletaPontos.add(pontoColeta);
                        }
                        // Após adicionar os pontos e carregar os materiais, atualizar o RecyclerView
                        atualizarRecyclerView();
                    } else {
                        Toast.makeText(this, "Erro ao buscar pontos de coleta.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //Consulta do cenário 3
    private void buscarPontosPorNomeEMaterial(String nomeDigitado, List<Integer> materiaisFiltrados) {
        String nomeDigitadoLower = nomeDigitado.toLowerCase();

        db.collection("PONTOSCOLETA_MATERIAIS")
                .whereIn("id_material", materiaisFiltrados)
                .get()
                .addOnCompleteListener(materialTask -> {
                    if (materialTask.isSuccessful()) {
                        List<String> idsPontos = new ArrayList<>();
                        for (DocumentSnapshot document : materialTask.getResult()) {
                            idsPontos.add(document.getString("id_ponto_coleta"));
                        }

                        if (!idsPontos.isEmpty()) {
                            listaCompletaPontos.clear(); // Limpar a lista antes de adicionar novos pontos
                            for (String idPonto : idsPontos) {
                                db.collection("PONTOSCOLETA")
                                        .document(idPonto)
                                        .get()
                                        .addOnCompleteListener(pontoTask -> {
                                            if (pontoTask.isSuccessful()) {
                                                DocumentSnapshot document = pontoTask.getResult();
                                                if (document.exists()) {
                                                    PontoColeta pontoColeta = document.toObject(PontoColeta.class);
                                                    if (pontoColeta != null && pontoColeta.getNomeLowercase() != null) {
                                                        if (pontoColeta.getNomeLowercase().contains(nomeDigitadoLower)) {
                                                            if (!listaCompletaPontos.contains(pontoColeta)) {
                                                                carregarMateriaisColetados(pontoColeta);
                                                                listaCompletaPontos.add(pontoColeta);
                                                                Log.d("Consulta", "Ponto de coleta adicionado: " + pontoColeta.getNome());
                                                            }
                                                        }
                                                    }
                                                }
                                                if (idPonto.equals(idsPontos.get(idsPontos.size() - 1))) {
                                                    atualizarRecyclerView();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "Nenhum ponto de coleta encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.e("Consulta", "Erro ao buscar materiais: ", materialTask.getException());
                    }
                });
    }

    //Consulta do cenário 4
    private void buscarPontosPorMaterial(List<Integer> materiaisFiltrados) {
        db.collection("PONTOSCOLETA_MATERIAIS")
                .whereIn("id_material", materiaisFiltrados)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> idsPontos = new ArrayList<>();
                        for (DocumentSnapshot document : task.getResult()) {
                            idsPontos.add(document.getString("id_ponto_coleta"));
                        }
                        if (!idsPontos.isEmpty()) {
                            buscarPontosColetaPorIds(idsPontos);
                        } else {
                            Toast.makeText(this, "Nenhum ponto de coleta encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Aqui vai ser utilizado quando não realizo busca por nome de ponto de coleta
    //apenas seleciono um tipo de material
    //Isso porque a estrutura de dados é separada, pontos numa tabela, materiais em outra, pontosmateriais em outra tabela
    private void buscarPontosColetaPorIds(List<String> idsPontos) {
        listaCompletaPontos.clear();

        for (String idPonto : idsPontos) {
            db.collection("PONTOSCOLETA")
                    .document(idPonto)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                PontoColeta pontoColeta = document.toObject(PontoColeta.class);
                                if (pontoColeta != null) {
                                    pontoColeta.setId_PC(document.getId());
                                    listaCompletaPontos.add(pontoColeta);

                                    // Carregar os materiais coletados para o ponto
                                    carregarMateriaisColetados(pontoColeta);
                                }
                            }
                        }
                        // Atualizar RecyclerView após todos os pontos serem carregados
                        atualizarRecyclerView();
                    });
        }
    }

    //Este método serve para preencher uma string de materiais coletados pelo ponto
    //será exibido em cada item do recyclerView
    private void carregarMateriaisColetados(PontoColeta pontoColeta) {
        db.collection("PONTOSCOLETA_MATERIAIS")
                .whereEqualTo("id_ponto_coleta", pontoColeta.getId_PC())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        StringBuilder materiais = new StringBuilder();
                        for (DocumentSnapshot document : task.getResult()) {
                            int idMaterial = document.getLong("id_material").intValue();
                            switch (idMaterial) {
                                case 1:
                                    materiais.append("Pilha/Bateria\n");
                                    break;
                                case 2:
                                    materiais.append("Óleo de Cozinha\n");
                                    break;
                                case 3:
                                    materiais.append("Lâmpada\n");
                                    break;
                                case 4:
                                    materiais.append("Eletrônicos\n");
                                    break;
                            }
                        }
                        // Adicionar os materiais coletados ao objeto PontoColeta
                        pontoColeta.setMateriaisColetados(materiais.toString());

                        // Atualizar a lista com os pontos e materiais carregados
                        atualizarRecyclerView();
                    } else {
                        pontoColeta.setMateriaisColetados("Erro ao buscar materiais");
                        atualizarRecyclerView();
                    }
                });
    }

    private void atualizarRecyclerView() {
        // Verificar se há pontos na lista
        if (!listaCompletaPontos.isEmpty()) {
            consultaPontoAdapter = new ConsultaPontoAdapter(listaCompletaPontos, this);
            recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);
        } else {
            Toast.makeText(this, "Nenhum ponto de coleta encontrado.", Toast.LENGTH_SHORT).show();
        }
    }

    private void filtrarFavoritos() {
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

                        List<PontoColeta> listaFavoritos = new ArrayList<>();
                        for (PontoColeta ponto : listaCompletaPontos) {
                            if (idsFavoritos.contains(ponto.getId_PC())) {
                                listaFavoritos.add(ponto);
                            }
                        }

                        consultaPontoAdapter = new ConsultaPontoAdapter(listaFavoritos, this);
                        recyclerViewPontosColeta.setAdapter(consultaPontoAdapter);

                        if (listaFavoritos.isEmpty()) {
                            Toast.makeText(this, "Nenhum favorito encontrado.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}