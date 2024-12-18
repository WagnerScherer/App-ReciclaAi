package com.wagner.reciclaai.Util;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.PhoneNumberFormatter;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class CadPontoColetaActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextFone, editTextWhatsapp, editTextEndereco,
            editTextNumero, editTextBairro, editTextComplemento, editTextCidade, editTextUF, editTextSite,
            editTextPessoaContato, editTextHorarioFuncionamento;
    private CheckBox checkBoxRealizaColeta, checkBoxPilhasBaterias, checkBoxOleoCozinha,
            checkBoxLampadas, checkBoxEletronicos;
    private FirebaseFirestore db;
    private String idPontoColeta; // ID do ponto de coleta (se for edição)
    private Button btnCadastrarPontoColeta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pontocoleta);

        db = FirebaseFirestore.getInstance();

        // Referenciando os campos do layout
        editTextName = findViewById(R.id.editTextNamePontoColeta);
        editTextEmail = findViewById(R.id.editTextEmailPontoColeta);
        editTextFone = findViewById(R.id.editTextFonePontoColeta);
        editTextWhatsapp = findViewById(R.id.editTextWhatsappPontoColeta);
        editTextEndereco = findViewById(R.id.editTextEnderecoPontoColeta);
        editTextNumero = findViewById(R.id.editTextNroPontoColeta);
        editTextBairro = findViewById(R.id.editTextBairroPontoColeta);
        editTextComplemento = findViewById(R.id.editTextComplementoPontoColeta);
        editTextCidade = findViewById(R.id.editTextCidadePontoColeta);
        editTextUF = findViewById(R.id.editTextUFPontoColeta);
        editTextSite = findViewById(R.id.editTextSitePontoColeta);
        editTextPessoaContato = findViewById(R.id.editTextPessoaContato);
        editTextHorarioFuncionamento = findViewById(R.id.editTextHorarioFuncionamento);

        checkBoxRealizaColeta = findViewById(R.id.checkBoxRealizaColeta);
        checkBoxPilhasBaterias = findViewById(R.id.checkBoxPilhasBaterias);
        checkBoxOleoCozinha = findViewById(R.id.checkBoxOleoCozinha);
        checkBoxLampadas = findViewById(R.id.checkBoxLampadas);
        checkBoxEletronicos = findViewById(R.id.checkBoxEletronicos);

        editTextFone.addTextChangedListener(new PhoneNumberFormatter(editTextFone));
        editTextWhatsapp.addTextChangedListener(new PhoneNumberFormatter(editTextWhatsapp));

        // Verificar se foi passado um ID de ponto de coleta pela Intent (para edição)
        idPontoColeta = getIntent().getStringExtra("idPontoColeta");

        if (idPontoColeta != null) {
            carregarDadosPontoColeta(idPontoColeta); // Carregar os dados do ponto de coleta existente
        }

        // Ação do botão de cadastrar/atualizar
        findViewById(R.id.buttonCadastrarPontoColeta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (idPontoColeta == null) {
                    cadastrarPontoColeta(); // Se o ID for nulo, é um novo cadastro
                } else {
                    atualizarPontoColeta(idPontoColeta); // Caso contrário, atualizar o cadastro existente
                }
            }
        });

        btnCadastrarPontoColeta = findViewById(R.id.buttonCadastrarPontoColeta);

        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("isFromConsulta", false)) {
            btnCadastrarPontoColeta.setText("Salvar");
        }
    }

    // Função que carrega os dados do ponto de coleta para edição
    private void carregarDadosPontoColeta(String idPontoColeta) {
        db.collection("PONTOSCOLETA").document(idPontoColeta)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Preencher os campos com os dados recuperados
                        editTextName.setText(documentSnapshot.getString("nome"));
                        editTextEmail.setText(documentSnapshot.getString("email"));
                        editTextFone.setText(documentSnapshot.getString("fone"));
                        editTextWhatsapp.setText(documentSnapshot.getString("whatsapp"));
                        editTextEndereco.setText(documentSnapshot.getString("endereco"));
                        editTextNumero.setText(documentSnapshot.getString("numero"));
                        editTextBairro.setText(documentSnapshot.getString("bairro"));
                        editTextComplemento.setText(documentSnapshot.getString("complemento"));
                        editTextCidade.setText(documentSnapshot.getString("cidade"));
                        editTextUF.setText(documentSnapshot.getString("uf"));
                        editTextSite.setText(documentSnapshot.getString("site"));
                        editTextPessoaContato.setText(documentSnapshot.getString("pessoa_contato"));
                        editTextHorarioFuncionamento.setText(documentSnapshot.getString("horario_funcionamento"));
                        checkBoxRealizaColeta.setChecked(documentSnapshot.getBoolean("realizaColeta"));

                        // Agora, carregar os materiais associados ao ponto de coleta
                        carregarMateriais(idPontoColeta);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CadPontoColetaActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
                });
    }

    // Função que carrega os materiais coletados pelo ponto de coleta
    private void carregarMateriais(String idPontoColeta) {
        db.collection("PONTOSCOLETA_MATERIAIS")
                .whereEqualTo("id_ponto_coleta", idPontoColeta)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        int idMaterial = document.getLong("id_material").intValue();
                        switch (idMaterial) {
                            case 1:
                                checkBoxPilhasBaterias.setChecked(true);
                                break;
                            case 2:
                                checkBoxOleoCozinha.setChecked(true);
                                break;
                            case 3:
                                checkBoxLampadas.setChecked(true);
                                break;
                            case 4:
                                checkBoxEletronicos.setChecked(true);
                                break;
                        }
                    }
                });
    }

    // Função para atualizar o ponto de coleta existente
    private void atualizarPontoColeta(String idPontoColeta) {
        String nome = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String fone = editTextFone.getText().toString();
        String whatsapp = editTextWhatsapp.getText().toString();
        String endereco = editTextEndereco.getText().toString();
        String numero = editTextNumero.getText().toString();
        String bairro = editTextBairro.getText().toString();
        String complemento = editTextComplemento.getText().toString();
        String cidade = editTextCidade.getText().toString();
        String uf = editTextUF.getText().toString();
        String site = editTextSite.getText().toString();
        String pessoa_contato = editTextPessoaContato.getText().toString();
        String horario_funcionamento = editTextHorarioFuncionamento.getText().toString();
        boolean realizaColeta = checkBoxRealizaColeta.isChecked();

        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(endereco) ||
                TextUtils.isEmpty(numero) || TextUtils.isEmpty(cidade) || TextUtils.isEmpty(uf)) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pontoColeta = new HashMap<>();
        pontoColeta.put("nome", nome);
        pontoColeta.put("email", email);
        pontoColeta.put("fone", fone);
        pontoColeta.put("whatsapp", whatsapp);
        pontoColeta.put("endereco", endereco);
        pontoColeta.put("numero", numero);
        pontoColeta.put("bairro", bairro);
        pontoColeta.put("complemento", complemento);
        pontoColeta.put("cidade", cidade);
        pontoColeta.put("uf", uf);
        pontoColeta.put("site", site);
        pontoColeta.put("pessoa_contato", pessoa_contato);
        pontoColeta.put("horario_funcionamento", horario_funcionamento);
        pontoColeta.put("realizaColeta", realizaColeta);

        db.collection("PONTOSCOLETA").document(idPontoColeta)
                .update(pontoColeta)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(CadPontoColetaActivity.this, "Ponto de coleta atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                    salvarMateriais(idPontoColeta); // Atualizar os materiais
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CadPontoColetaActivity.this, "Erro ao atualizar ponto de coleta", Toast.LENGTH_SHORT).show();
                });
    }


    // Função para salvar ou atualizar os materiais coletados
    private void salvarMateriais(String pontoColetaId) {
        // Verificar os checkboxes e salvar cada material na coleção PONTOSCOLETA_MATERIAIS
        if (checkBoxPilhasBaterias.isChecked()) {
            Map<String, Object> pontoMaterial = new HashMap<>();
            pontoMaterial.put("id_ponto_coleta", pontoColetaId);
            pontoMaterial.put("id_material", 1); // Código 1 para Pilhas/Baterias
            db.collection("PONTOSCOLETA_MATERIAIS").add(pontoMaterial)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Material Pilhas/Baterias salvo com sucesso");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Erro ao salvar material Pilhas/Baterias", e);
                    });
        }

        if (checkBoxOleoCozinha.isChecked()) {
            Map<String, Object> pontoMaterial = new HashMap<>();
            pontoMaterial.put("id_ponto_coleta", pontoColetaId);
            pontoMaterial.put("id_material", 2); // Código 2 para Óleo de Cozinha
            db.collection("PONTOSCOLETA_MATERIAIS").add(pontoMaterial)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Material Óleo de Cozinha salvo com sucesso");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Erro ao salvar material Óleo de Cozinha", e);
                    });
        }

        if (checkBoxLampadas.isChecked()) {
            Map<String, Object> pontoMaterial = new HashMap<>();
            pontoMaterial.put("id_ponto_coleta", pontoColetaId);
            pontoMaterial.put("id_material", 3); // Código 3 para Lâmpadas
            db.collection("PONTOSCOLETA_MATERIAIS").add(pontoMaterial)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Material Lâmpadas salvo com sucesso");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Erro ao salvar material Lâmpadas", e);
                    });
        }

        if (checkBoxEletronicos.isChecked()) {
            Map<String, Object> pontoMaterial = new HashMap<>();
            pontoMaterial.put("id_ponto_coleta", pontoColetaId);
            pontoMaterial.put("id_material", 4); // Código 4 para Eletrônicos
            db.collection("PONTOSCOLETA_MATERIAIS").add(pontoMaterial)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Material Eletrônicos salvo com sucesso");
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Erro ao salvar material Eletrônicos", e);
                    });
        }
        //Toast.makeText(CadPontoColetaActivity.this, "Materiais salvos com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private void cadastrarPontoColeta() {
        // Coletar os dados dos campos
        String nome = editTextName.getText().toString();
        String email = editTextEmail.getText().toString();
        String fone = editTextFone.getText().toString();
        String whatsapp = editTextWhatsapp.getText().toString();
        String endereco = editTextEndereco.getText().toString();
        String numero = editTextNumero.getText().toString();
        String bairro = editTextBairro.getText().toString();
        String complemento = editTextComplemento.getText().toString();
        String cidade = editTextCidade.getText().toString();
        String uf = editTextUF.getText().toString();
        String site = editTextSite.getText().toString();
        String pessoa_contato = editTextPessoaContato.getText().toString();
        String horario_funcionamento = editTextHorarioFuncionamento.getText().toString();
        boolean realizaColeta = checkBoxRealizaColeta.isChecked();

        // Verificar se os campos obrigatórios estão preenchidos
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(endereco) || TextUtils.isEmpty(numero) ||
                TextUtils.isEmpty(cidade) || TextUtils.isEmpty(uf)) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar um mapa com os dados do ponto de coleta
        Map<String, Object> pontoColeta = new HashMap<>();
        pontoColeta.put("nome", nome);
        pontoColeta.put("email", email);
        pontoColeta.put("fone", fone);
        pontoColeta.put("whatsapp", whatsapp);
        pontoColeta.put("endereco", endereco);
        pontoColeta.put("numero", numero);
        pontoColeta.put("bairro", bairro);
        pontoColeta.put("complemento", complemento);
        pontoColeta.put("cidade", cidade);
        pontoColeta.put("uf", uf);
        pontoColeta.put("site", site);
        pontoColeta.put("pessoa_contato", pessoa_contato);
        pontoColeta.put("horario_funcionamento", horario_funcionamento);
        pontoColeta.put("realizaColeta", realizaColeta);

        // Adicionar o novo ponto de coleta ao Firestore
        db.collection("PONTOSCOLETA")
                .add(pontoColeta)
                .addOnSuccessListener(documentReference -> {
                    String novoIdPontoColeta = documentReference.getId();  // ID gerado pelo Firestore
                    Toast.makeText(CadPontoColetaActivity.this, "Ponto de coleta cadastrado com sucesso!", Toast.LENGTH_SHORT).show();

                    // Salvar os materiais associados ao novo ponto de coleta
                    salvarMateriais(novoIdPontoColeta);

                    // Enviar a notificação para todos os usuários
                    enviarNotificacaoParaTodosUsuarios(nome);

                    // Limpar os campos após o cadastro
                    limparCampos();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(CadPontoColetaActivity.this, "Erro ao cadastrar ponto de coleta", Toast.LENGTH_SHORT).show();
                    Log.e("Firestore", "Erro ao cadastrar o ponto de coleta", e);
                });
    }

    // Método para limpar os campos após o cadastro ou atualização
    private void limparCampos() {
        editTextName.setText("");
        editTextEmail.setText("");
        editTextFone.setText("");
        editTextWhatsapp.setText("");
        editTextEndereco.setText("");
        editTextNumero.setText("");
        editTextBairro.setText("");
        editTextComplemento.setText("");
        editTextCidade.setText("");
        editTextUF.setText("");
        editTextSite.setText("");
        editTextPessoaContato.setText("");
        editTextHorarioFuncionamento.setText("");
        checkBoxRealizaColeta.setChecked(false);
        checkBoxPilhasBaterias.setChecked(false);
        checkBoxOleoCozinha.setChecked(false);
        checkBoxLampadas.setChecked(false);
        checkBoxEletronicos.setChecked(false);
    }

    private void enviarNotificacaoParaTodosUsuarios(String nomePontoColeta) {
        db.collection("USUARIOS").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        String uidUsuario = document.getId(); // UID do usuário

                        // Criar a notificação para o usuário
                        Map<String, Object> notificacao = new HashMap<>();
                        notificacao.put("id_usuario", uidUsuario);
                        notificacao.put("titulo", "Há um novo ponto de coleta!");
                        notificacao.put("mensagem", "Recentemente foi cadastrado um novo ponto de coleta: "  + nomePontoColeta + ".");
                        notificacao.put("status", false); // Status não lido (false)

                        // Adicionar a notificação na coleção NOTIFICACOES
                        db.collection("NOTIFICACOES")
                                .add(notificacao)
                                .addOnSuccessListener(documentReference -> Log.d("Firestore", "Notificação enviada para: " + uidUsuario))
                                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao enviar notificação", e));
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Erro ao buscar usuários", e));
    }
}