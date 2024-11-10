package com.wagner.reciclaai.Util;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.AgendamentoTabsAdapter;
import com.wagner.reciclaai.model.Agendamento;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgendamentosActivity extends AppCompatActivity {

    private Spinner spinnerPontoColeta;
    private EditText editTextDataColeta, editTextEndereco, editTextObservacoes;
    private CheckBox checkBoxOleoCozinha, checkBoxPilhasBaterias, checkBoxLampadas, checkBoxEletronicos;
    private Button btnAgendar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private List<String> pontosColetaIds;
    private List<String> pontosColetaNomes;
    private String idPontoSelecionado;
    private HashMap<String, String> materialMap;

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_agendamentos);
        setContentView(R.layout.activity_agendamentos_abas);

    /*
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        materialMap = new HashMap<>();

        // Inicializa os componentes da tela
        spinnerPontoColeta = findViewById(R.id.spinnerPontoColeta);
        editTextDataColeta = findViewById(R.id.editTextDataColeta);
        editTextEndereco = findViewById(R.id.editTextEndereco);
        editTextObservacoes = findViewById(R.id.editTextObservacoes);
        checkBoxOleoCozinha = findViewById(R.id.checkBoxOleoCozinha);
        checkBoxPilhasBaterias = findViewById(R.id.checkBoxPilhasBaterias);
        checkBoxLampadas = findViewById(R.id.checkBoxLampadas);
        checkBoxEletronicos = findViewById(R.id.checkBoxEletronicos);
        btnAgendar = findViewById(R.id.btnAgendar);

        carregarMateriais();
        carregarPontosColeta();
        carregarEnderecoUsuario();

        btnAgendar.setOnClickListener(v -> validarEMarcarAgendamento());

        editTextDataColeta.setOnClickListener(v -> {
            // Obtém a data atual
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Abre o DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AgendamentosActivity.this,
                    (view, year1, monthOfYear, dayOfMonth) -> {
                        // Ajuste o mês (+1) para exibir corretamente
                        String selectedDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year1;
                        editTextDataColeta.setText(selectedDate);
                    }, year, month, day
            );

            // Define a data mínima (por exemplo, hoje)
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

     */

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Verifica se o usuário é administrador (exemplo de verificação)
        boolean isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        // Configura o adapter
        AgendamentoTabsAdapter adapter = new AgendamentoTabsAdapter(this, isAdmin);
        viewPager.setAdapter(adapter);

        // Conecta o TabLayout com o ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Solicitar Agendamento");
            } else if (position == 1) {
                tab.setText("Solicitações Recebidas");
            }
        }).attach();

    }

    private void carregarMateriais() {
        db.collection("MATERIAIS")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        String id = String.valueOf(doc.get("id"));
                        String descricao = doc.getString("descricao");
                        materialMap.put(id, descricao);
                    }
                    Log.d("AgendamentosActivity", "Mapa de materiais carregado: " + materialMap);
                })
                .addOnFailureListener(e -> Log.e("AgendamentosActivity", "Erro ao carregar materiais: ", e));
    }

    private void carregarPontosColeta() {
        CollectionReference pontosColetaRef = db.collection("PONTOSCOLETA");
        pontosColetaIds = new ArrayList<>();
        pontosColetaNomes = new ArrayList<>();

        // Adiciona o item placeholder no início da lista
        pontosColetaNomes.add("Selecione um ponto de coleta");
        pontosColetaIds.add(null);

        pontosColetaRef.whereEqualTo("realizaColeta", true)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot doc : task.getResult()) {
                            pontosColetaIds.add(doc.getId());
                            pontosColetaNomes.add(doc.getString("nome"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, pontosColetaNomes);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerPontoColeta.setAdapter(adapter);
                    }
                });

        spinnerPontoColeta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                idPontoSelecionado = pontosColetaIds.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void carregarEnderecoUsuario() {
        String uid = auth.getCurrentUser().getUid();
        db.collection("USUARIOS").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String endereco = documentSnapshot.getString("endereco") + ", " +
                                documentSnapshot.getString("numero") + ", " +
                                documentSnapshot.getString("bairro") + ", " +
                                documentSnapshot.getString("cidade");

                        // pega uf separado e evita os nulos
                        String uf = documentSnapshot.getString("estado") != null ? documentSnapshot.getString("estado") : "";

                        String complemento = documentSnapshot.getString("complemento") != null ? documentSnapshot.getString("complemento") : "";

                        //concatena cidade e estado e complemento
                        endereco += " - " + uf + " Complemento: " + complemento +".";

                        editTextEndereco.setText(endereco);
                    }
                });
    }

    private void validarEMarcarAgendamento() {
        if (spinnerPontoColeta.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Por favor, selecione um ponto de coleta.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> materiaisSelecionados = new ArrayList<>();
        if (checkBoxOleoCozinha.isChecked()) materiaisSelecionados.add("2");
        if (checkBoxPilhasBaterias.isChecked()) materiaisSelecionados.add("1");
        if (checkBoxLampadas.isChecked()) materiaisSelecionados.add("3");
        if (checkBoxEletronicos.isChecked()) materiaisSelecionados.add("4");

        if (materiaisSelecionados.isEmpty()) {
            Toast.makeText(this, "Selecione pelo menos um material para a coleta.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("PONTOSCOLETA_MATERIAIS")
                .whereEqualTo("id_ponto_coleta", idPontoSelecionado)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> materiaisNaoAceitos = new ArrayList<>();
                    for (String material : materiaisSelecionados) {
                        boolean aceita = queryDocumentSnapshots.getDocuments().stream()
                                .anyMatch(doc -> material.equals(String.valueOf(doc.get("id_material"))));
                        if (!aceita) {
                            String descricaoMaterial = materialMap.get(material);
                            if (descricaoMaterial == null) {
                                descricaoMaterial = "ID " + material;  // Usa apenas o ID se a descrição estiver ausente
                            } else {
                                descricaoMaterial = "ID " + material + " - " + descricaoMaterial;
                            }
                            materiaisNaoAceitos.add(descricaoMaterial);
                        }
                    }

                    if (!materiaisNaoAceitos.isEmpty()) {
                        String materiaisIncompativeis = String.join(", ", materiaisNaoAceitos);
                        Toast.makeText(this, "Os materiais " + materiaisIncompativeis + " não são aceitos neste ponto de coleta.", Toast.LENGTH_LONG).show();
                        Log.d("AgendamentosActivity", "Materiais incompatíveis: " + materiaisNaoAceitos);
                    } else {
                        salvarAgendamento(materiaisSelecionados);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AgendamentosActivity", "Erro ao acessar Firestore: ", e);
                    Toast.makeText(this, "Erro ao verificar os materiais no ponto de coleta.", Toast.LENGTH_SHORT).show();
                });
    }

    private void salvarAgendamento(List<String> materiaisSelecionados) {
        String uid = auth.getCurrentUser().getUid();
        Agendamento agendamento = new Agendamento(
                uid,
                idPontoSelecionado,
                materiaisSelecionados,
                new Date(), // Ajustar data conforme o editText
                new Date(),
                1 // Status: 1=pendente
        );

        db.collection("AGENDAMENTOS").add(agendamento)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Agendamento realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    // Criação da notificação para o usuário
                    Map<String, Object> notificacao = new HashMap<>();
                    notificacao.put("id_usuario", uid);
                    notificacao.put("titulo", "Agendamento de coleta.");
                    notificacao.put("mensagem", "Sua solicitação de agendamento foi enviada ao ponto de coleta " + pontosColetaNomes.get(spinnerPontoColeta.getSelectedItemPosition()) + ", aguarde pela confirmação da coleta.");
                    notificacao.put("status", false); // Status não lido (false)

                    // Salva a notificação na coleção NOTIFICACOES
                    db.collection("NOTIFICACOES").add(notificacao)
                            .addOnSuccessListener(docRef -> Log.d("AgendamentosActivity", "Notificação registrada com sucesso para o usuário."))
                            .addOnFailureListener(e -> Log.e("AgendamentosActivity", "Erro ao registrar notificação: ", e));

                    //Limpa os campos após a confirmação do agendamento, e do preenchimento da notificação.
                    limparCampos();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erro ao realizar agendamento.", Toast.LENGTH_SHORT).show());
    }

    private void limparCampos() {
        spinnerPontoColeta.setSelection(0);
        editTextDataColeta.setText("");
        checkBoxEletronicos.setChecked(false);
        checkBoxLampadas.setChecked(false);
        checkBoxPilhasBaterias.setChecked(false);
        checkBoxOleoCozinha.setChecked(false);
        editTextEndereco.setText("");
        editTextObservacoes.setText("");
    }
}
