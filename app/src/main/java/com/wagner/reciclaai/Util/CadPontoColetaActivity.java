package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.model.PontoColeta;

import java.util.HashMap;
import java.util.Map;

public class CadPontoColetaActivity extends AppCompatActivity {

    PontoColeta pontoColeta;

    EditText campoNomeLocalColeta, campoEmailPC, campoFonePC, campoWhatsAppPC;
    EditText campoEnderecoPC, campoNumeroPC, campoComplementoPC;
    EditText campoBairroPC, campoCidadePC, campoEstadoPC, campoSitePC;
    CheckBox checkBoxRealizaColeta;
    Button botaoCadastrarPC;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_pontocoleta);
        inicializar();

    }

    private void inicializar() {
        campoNomeLocalColeta = findViewById(R.id.editTextNamePontoColeta);
        campoEmailPC = findViewById(R.id.editTextEmailPontoColeta);
        campoFonePC = findViewById(R.id.editTextFonePontoColeta);
        campoWhatsAppPC = findViewById(R.id.editTextWhatsappPontoColeta);
        campoEnderecoPC = findViewById(R.id.editTextEnderecoPontoColeta);
        campoNumeroPC = findViewById(R.id.editTextNroPontoColeta);
        campoComplementoPC = findViewById(R.id.editTextComplementoPontoColeta);
        campoBairroPC = findViewById(R.id.editTextBairroPontoColeta);
        campoCidadePC = findViewById(R.id.editTextCidadePontoColeta);
        campoEstadoPC = findViewById(R.id.editTextUFPontoColeta);
        campoSitePC = findViewById(R.id.editTextSitePontoColeta);
        checkBoxRealizaColeta = findViewById(R.id.checkBoxRealizaColeta);
        botaoCadastrarPC = findViewById(R.id.buttonCadastrarPontoColeta);

        // Inicializar o Firestore
        firestore = FirebaseFirestore.getInstance();

        botaoCadastrarPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarPontoColeta();
            }
        });

    }

    private void cadastrarPontoColeta() {

        // Capturar os valores dos campos
        String nomeLocalColeta = campoNomeLocalColeta.getText().toString();
        String emailPC = campoEmailPC.getText().toString();
        String fonePC = campoFonePC.getText().toString();
        String whatsappPC = campoWhatsAppPC.getText().toString();
        String enderecoPC = campoEnderecoPC.getText().toString();
        String numeroPC = campoNumeroPC.getText().toString();
        String complementoPC = campoComplementoPC.getText().toString();
        String bairroPC = campoBairroPC.getText().toString();
        String cidadePC = campoCidadePC.getText().toString();
        String estadoPC = campoEstadoPC.getText().toString();
        String sitePC = campoSitePC.getText().toString();

        boolean realizaColeta = checkBoxRealizaColeta.isChecked();

        // Verificar se os campos obrigatórios não estão vazios
        if (TextUtils.isEmpty(nomeLocalColeta) || TextUtils.isEmpty(enderecoPC) || TextUtils.isEmpty(numeroPC) ||
                TextUtils.isEmpty(bairroPC) || TextUtils.isEmpty(cidadePC) || TextUtils.isEmpty(estadoPC)) {

            Toast.makeText(this, "Por favor, preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Criar um mapa para armazenar os dados
        Map<String, Object> pontoColeta = new HashMap<>();
        pontoColeta.put("nome", nomeLocalColeta);
        pontoColeta.put("email", emailPC);
        pontoColeta.put("fone", fonePC);
        pontoColeta.put("celular", whatsappPC);
        pontoColeta.put("endereco", enderecoPC);
        pontoColeta.put("numero", numeroPC);
        pontoColeta.put("bairro", bairroPC);
        pontoColeta.put("complemento", complementoPC);
        pontoColeta.put("cidade", cidadePC);
        pontoColeta.put("estado", estadoPC);
        pontoColeta.put("site", sitePC);
        pontoColeta.put("realizaColeta", realizaColeta);

        // Gravar os dados no Firestore
        firestore.collection("PontosDeColeta")
                .add(pontoColeta)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(CadPontoColetaActivity.this, "Ponto de Coleta cadastrado com sucesso!", Toast.LENGTH_SHORT).show();
                            limparCampos(); // Opcional: limpar os campos após o cadastro
                        } else {
                            Toast.makeText(CadPontoColetaActivity.this, "Falha ao cadastrar o Ponto de Coleta. Tente novamente.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Método opcional para limpar os campos após o cadastro bem-sucedido
    private void limparCampos() {
        campoNomeLocalColeta.setText("");
        campoEmailPC.setText("");
        campoFonePC.setText("");
        campoWhatsAppPC.setText("");
        campoEnderecoPC.setText("");
        campoNumeroPC.setText("");
        campoBairroPC.setText("");
        campoComplementoPC.setText("");
        campoCidadePC.setText("");
        campoEstadoPC.setText("");
        campoSitePC.setText("");
        checkBoxRealizaColeta.setChecked(false);
    }

}