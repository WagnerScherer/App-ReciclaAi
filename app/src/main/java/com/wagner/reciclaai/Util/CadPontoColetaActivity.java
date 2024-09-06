package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.PhoneNumberFormatter;

import java.util.HashMap;
import java.util.Map;

public class CadPontoColetaActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextFone, editTextWhatsapp, editTextEndereco,
            editTextNumero, editTextBairro, editTextComplemento, editTextCidade, editTextUF, editTextSite;
    private CheckBox checkBoxRealizaColeta, checkBoxPilhasBaterias, checkBoxOleoCozinha,
            checkBoxLampadas, checkBoxEletronicos;

    private FirebaseFirestore db;

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

        checkBoxRealizaColeta = findViewById(R.id.checkBoxRealizaColeta);
        checkBoxPilhasBaterias = findViewById(R.id.checkBoxPilhasBaterias);
        checkBoxOleoCozinha = findViewById(R.id.checkBoxOleoCozinha);
        checkBoxLampadas = findViewById(R.id.checkBoxLampadas);
        checkBoxEletronicos = findViewById(R.id.checkBoxEletronicos);

        editTextFone.addTextChangedListener(new PhoneNumberFormatter(editTextFone));
        editTextWhatsapp.addTextChangedListener(new PhoneNumberFormatter(editTextWhatsapp));



        // Verificação de duplicidade ao sair do campo "nome"
        editTextName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) { // Quando o campo perde o foco
                    verificarDuplicidadeNome();
                }
            }
        });


        findViewById(R.id.buttonCadastrarPontoColeta).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrarPontoColeta();
            }
        });
    }

    // Função que verifica se o nome já existe
    private void verificarDuplicidadeNome() {
        String nome = editTextName.getText().toString();

        if (TextUtils.isEmpty(nome)) {
            editTextName.setError("O nome do ponto de coleta é obrigatório");
            return;
        }

        // Consulta para verificar se já existe um ponto com o mesmo nome
        db.collection("PONTOSCOLETA")
                .whereEqualTo("nome", nome)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (!task.getResult().isEmpty()) {
                                // Ponto de coleta com o mesmo nome já existe
                                editTextName.setError("Já existe um ponto de coleta com este nome");
                                editTextName.requestFocus();
                            }
                        } else {
                            Toast.makeText(CadPontoColetaActivity.this, "Erro ao verificar nome duplicado", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void cadastrarPontoColeta() {
        // Coleta os dados dos campos
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
        boolean realizaColeta = checkBoxRealizaColeta.isChecked();

        // Verifica se os campos obrigatórios estão preenchidos
        if (TextUtils.isEmpty(nome) || TextUtils.isEmpty(email) || TextUtils.isEmpty(endereco) ||
                TextUtils.isEmpty(numero) || TextUtils.isEmpty(cidade) || TextUtils.isEmpty(uf)) {
            Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
            return;
        }

        // Cria um mapa com os dados para salvar na coleção PONTOSCOLETA
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
        pontoColeta.put("realizaColeta", realizaColeta);

        // Salva os dados na coleção PONTOSCOLETA
        db.collection("PONTOSCOLETA")
                .add(pontoColeta)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CadPontoColetaActivity.this, "Ponto de coleta cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                        String pontoColetaId = documentReference.getId();
                        salvarMateriais(pontoColetaId);
                        limparCampos(); // Limpar campos após o cadastro
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CadPontoColetaActivity.this, "Erro ao cadastrar ponto de coleta", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void salvarMateriais(String pontoColetaId) {
        if (checkBoxPilhasBaterias.isChecked()) {
            salvarPontoColetaMaterial(pontoColetaId, 1);
        }
        if (checkBoxOleoCozinha.isChecked()) {
            salvarPontoColetaMaterial(pontoColetaId, 2);
        }
        if (checkBoxLampadas.isChecked()) {
            salvarPontoColetaMaterial(pontoColetaId, 3);
        }
        if (checkBoxEletronicos.isChecked()) {
            salvarPontoColetaMaterial(pontoColetaId, 4);
        }
    }

    private void salvarPontoColetaMaterial(String pontoColetaId, int idMaterial) {
        Map<String, Object> pontoColetaMaterial = new HashMap<>();
        pontoColetaMaterial.put("id_ponto_coleta", pontoColetaId);
        pontoColetaMaterial.put("id_material", idMaterial);

        db.collection("PONTOSCOLETA_MATERIAIS")
                .add(pontoColetaMaterial)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(CadPontoColetaActivity.this, "Material salvo com sucesso!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CadPontoColetaActivity.this, "Erro ao salvar material", Toast.LENGTH_SHORT).show();
                    }
                });
    }

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

        // Limpar os checkboxes
        checkBoxRealizaColeta.setChecked(false);
        checkBoxPilhasBaterias.setChecked(false);
        checkBoxOleoCozinha.setChecked(false);
        checkBoxLampadas.setChecked(false);
        checkBoxEletronicos.setChecked(false);
    }
}
