package com.wagner.reciclaai.Util;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.Agendamento;

import java.text.SimpleDateFormat;
import java.util.*;

public class SolicitarAgendamentoFragment extends Fragment {

    private Spinner spinnerPontoColeta;
    private EditText editTextDataColeta, editTextEndereco, editTextObservacoes;
    private CheckBox checkBoxOleoCozinha, checkBoxPilhasBaterias, checkBoxLampadas, checkBoxEletronicos;
    private Button btnAgendar;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private List<String> pontosColetaIds = new ArrayList<>();
    private List<String> pontosColetaNomes = new ArrayList<>();
    private String idPontoSelecionado; // Agora está sendo usado corretamente

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SolicitarAgendamentoFragment", "onCreateView chamado");
        View view = inflater.inflate(R.layout.fragment_solicitar_agendamento, container, false);

        // Inicializar componentes
        spinnerPontoColeta = view.findViewById(R.id.spinnerPontoColeta);
        editTextDataColeta = view.findViewById(R.id.editTextDataColeta);
        editTextEndereco = view.findViewById(R.id.editTextEndereco);
        editTextObservacoes = view.findViewById(R.id.editTextObservacoes);
        checkBoxOleoCozinha = view.findViewById(R.id.checkBoxOleoCozinha);
        checkBoxPilhasBaterias = view.findViewById(R.id.checkBoxPilhasBaterias);
        checkBoxLampadas = view.findViewById(R.id.checkBoxLampadas);
        checkBoxEletronicos = view.findViewById(R.id.checkBoxEletronicos);
        btnAgendar = view.findViewById(R.id.btnAgendar);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        configurarDatePicker();
        carregarPontosColeta();
        carregarEnderecoUsuario();
        configurarBotaoAgendar();

        return view;
    }

    private void configurarDatePicker() {
        editTextDataColeta.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    getContext(),
                    (view, year1, month1, dayOfMonth) -> {
                        String selectedDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                        editTextDataColeta.setText(selectedDate);
                    },
                    year, month, day
            );
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });
    }

    private void carregarPontosColeta() {
        db.collection("PONTOSCOLETA")
                .whereEqualTo("realizaColeta", true)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pontosColetaIds.clear();
                    pontosColetaNomes.clear();

                    pontosColetaIds.add(null); // Adiciona item vazio
                    pontosColetaNomes.add("Selecione um ponto de coleta");

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        pontosColetaIds.add(doc.getId());
                        pontosColetaNomes.add(doc.getString("nome"));
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, pontosColetaNomes);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerPontoColeta.setAdapter(adapter);

                    // Configurar listener para o Spinner
                    spinnerPontoColeta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position > 0) {
                                idPontoSelecionado = pontosColetaIds.get(position);
                            } else {
                                idPontoSelecionado = null;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            idPontoSelecionado = null;
                        }
                    });
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Erro ao carregar pontos de coleta", Toast.LENGTH_SHORT).show());
    }

    private void carregarEnderecoUsuario() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(getContext(), "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("USUARIOS").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String endereco = documentSnapshot.getString("endereco");
                        String numero = documentSnapshot.getString("numero");
                        String bairro = documentSnapshot.getString("bairro");
                        String cidade = documentSnapshot.getString("cidade");
                        String uf = documentSnapshot.getString("estado");
                        String complemento = documentSnapshot.getString("complemento");

                        // Construir o endereço completo
                        StringBuilder enderecoCompleto = new StringBuilder();

                        if (endereco != null && !endereco.isEmpty()) {
                            enderecoCompleto.append(endereco);
                        }
                        if (numero != null && !numero.isEmpty()) {
                            enderecoCompleto.append(", ").append(numero);
                        }
                        if (bairro != null && !bairro.isEmpty()) {
                            enderecoCompleto.append(", ").append(bairro);
                        }
                        if (cidade != null && !cidade.isEmpty()) {
                            enderecoCompleto.append(", ").append(cidade);
                        }
                        if (uf != null && !uf.isEmpty()) {
                            enderecoCompleto.append("-").append(uf);
                        }
                        // Tratamento para o complemento
                        if (complemento == null || complemento.isEmpty()) {
                            enderecoCompleto.append(", Complemento: sem complemento");
                        } else {
                            enderecoCompleto.append(", Complemento: ").append(complemento);
                        }
                        // Preencher o campo EditText com o endereço formatado
                        editTextEndereco.setText(enderecoCompleto.toString());
                    } else {
                        Toast.makeText(getContext(), "Endereço não encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao carregar endereço", Toast.LENGTH_SHORT).show();
                    Log.e("SolicitarAgendamento", "Erro ao carregar endereço: ", e);
                });
    }

    private void configurarBotaoAgendar() {
        btnAgendar.setOnClickListener(v -> validarEMarcarAgendamento());
    }

    private void validarEMarcarAgendamento() {
        if (idPontoSelecionado == null) {
            Toast.makeText(getContext(), "Selecione um ponto de coleta.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> materiaisSelecionados = new ArrayList<>();
        if (checkBoxOleoCozinha.isChecked()) materiaisSelecionados.add("2");
        if (checkBoxPilhasBaterias.isChecked()) materiaisSelecionados.add("1");
        if (checkBoxLampadas.isChecked()) materiaisSelecionados.add("3");
        if (checkBoxEletronicos.isChecked()) materiaisSelecionados.add("4");

        if (materiaisSelecionados.isEmpty()) {
            Toast.makeText(getContext(), "Selecione pelo menos um material para a coleta.", Toast.LENGTH_SHORT).show();
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
                            materiaisNaoAceitos.add(obterDescricaoMaterial(material));
                        }
                    }
                    if (!materiaisNaoAceitos.isEmpty()) {
                        String materiaisIncompativeis = String.join(", ", materiaisNaoAceitos);
                        Toast.makeText(getContext(), "Os materiais " + materiaisIncompativeis + " não são aceitos neste ponto de coleta.", Toast.LENGTH_LONG).show();
                        Log.d("AgendamentosActivity", "Materiais incompatíveis: " + materiaisNaoAceitos);
                    } else {
                        salvarAgendamento(materiaisSelecionados);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("AgendamentosActivity", "Erro ao acessar Firestore: ", e);
                    Toast.makeText(getContext(), "Erro ao verificar os materiais no ponto de coleta.", Toast.LENGTH_SHORT).show();
                });
    }

    // Método para obter a descrição de um material com base no ID
    private String obterDescricaoMaterial(String idMaterial) {
        switch (idMaterial) {
            case "1":
                return "Pilha/Baterias";
            case "2":
                return "Óleo de Cozinha";
            case "3":
                return "Lâmpadas";
            case "4":
                return "Eletrônicos";
            default:
                return "Material desconhecido (ID: " + idMaterial + ")";
        }
    }

    private void salvarAgendamento(List<String> materiaisSelecionados) {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(getContext(), "Usuário não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (idPontoSelecionado == null) {
            Toast.makeText(getContext(), "Ponto de coleta não selecionado.", Toast.LENGTH_SHORT).show();
            return;
        }
        // Converter a data do campo editTextDataColeta para uma instância de Date
        Date dataColeta;
        try {
            String dataColetaTexto = editTextDataColeta.getText().toString();
            if (dataColetaTexto.isEmpty()) {
                Toast.makeText(getContext(), "Informe uma data de coleta válida.", Toast.LENGTH_SHORT).show();
                return;
            }
            dataColeta = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(dataColetaTexto);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Erro ao interpretar a data de coleta.", Toast.LENGTH_SHORT).show();
            Log.e("SalvarAgendamento", "Erro ao interpretar a data de coleta", e);
            return;
        }

        // Criar o objeto de agendamento
        Agendamento agendamento = new Agendamento(
                uid,
                idPontoSelecionado,
                materiaisSelecionados,
                dataColeta, // Utiliza a data escolhida pelo usuário
                new Date(), // Data de criação do agendamento
                1
        );

        // Salvar no Firestore
        db.collection("AGENDAMENTOS").add(agendamento)
                .addOnSuccessListener(agendamentoDocRef -> {
                    Toast.makeText(getContext(), "Agendamento realizado com sucesso!", Toast.LENGTH_SHORT).show();

                    // Criação da notificação para o usuário
                    Map<String, Object> notificacao = new HashMap<>();
                    notificacao.put("id_usuario", uid);
                    notificacao.put("titulo", "Agendamento de coleta.");
                    notificacao.put("mensagem", "Sua solicitação de agendamento foi enviada ao ponto de coleta " + pontosColetaNomes.get(spinnerPontoColeta.getSelectedItemPosition()) + ", aguarde pela confirmação da coleta.");
                    notificacao.put("status", false); // Status não lido (false)

                    // Salvar a notificação na coleção NOTIFICACOES
                    db.collection("NOTIFICACOES").add(notificacao)
                            .addOnSuccessListener(notificacaoDocRef -> Log.d("AgendamentosActivity", "Notificação registrada com sucesso para o usuário."))
                            .addOnFailureListener(e -> Log.e("AgendamentosActivity", "Erro ao registrar notificação: ", e));

                    limparCampos();
                })
                .addOnFailureListener(e -> Toast.makeText(getContext(), "Erro ao agendar.", Toast.LENGTH_SHORT).show());
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
