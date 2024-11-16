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
        db.collection("PONTOSCOLETA").get()
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
        if (checkBoxOleoCozinha.isChecked()) materiaisSelecionados.add("Óleo de Cozinha");
        if (checkBoxPilhasBaterias.isChecked()) materiaisSelecionados.add("Pilha/Baterias");
        if (checkBoxLampadas.isChecked()) materiaisSelecionados.add("Lâmpadas");
        if (checkBoxEletronicos.isChecked()) materiaisSelecionados.add("Eletrônicos");

        if (materiaisSelecionados.isEmpty()) {
            Toast.makeText(getContext(), "Selecione pelo menos um material.", Toast.LENGTH_SHORT).show();
            return;
        }

        salvarAgendamento(materiaisSelecionados);
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

        Agendamento agendamento = new Agendamento(
                uid,
                idPontoSelecionado,
                materiaisSelecionados,
                new Date(),
                new Date(),
                1
        );

        db.collection("AGENDAMENTOS").add(agendamento)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(getContext(), "Agendamento realizado!", Toast.LENGTH_SHORT).show();
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
