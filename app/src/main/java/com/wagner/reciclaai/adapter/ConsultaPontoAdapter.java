package com.wagner.reciclaai.adapter;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.Util.AvaliacaoActivity;
import com.wagner.reciclaai.Util.CadPontoColetaActivity;
import com.wagner.reciclaai.Util.ConsultaPontoActivity;
import com.wagner.reciclaai.Util.PrincipalActivity;
import com.wagner.reciclaai.model.Favorito;
import com.wagner.reciclaai.model.PontoColeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public class ConsultaPontoAdapter extends RecyclerView.Adapter<ConsultaPontoAdapter.PontoColetaViewHolder> {

    private List<PontoColeta> listaPontosColeta;
    //private Context context;
    private Activity activity;
    private PontoColeta pontoColeta;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Map<String, Boolean> cacheFavoritos; // Cache para armazenar os favoritos localmente

    public ConsultaPontoAdapter(List<PontoColeta> listaPontosColeta, Activity activity) {
        this.listaPontosColeta = listaPontosColeta;
        this.activity = activity;
        this.db = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.cacheFavoritos = new HashMap<>(); // Inicializar o cache de favoritos
    }

    @NonNull
    @Override
    public PontoColetaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_item_consulta_pontocoleta, parent, false);
        return new PontoColetaViewHolder(itemView);
    }

    public void onBindViewHolder(@NonNull PontoColetaViewHolder holder, int position) {
        PontoColeta pontoColeta = listaPontosColeta.get(position);

        // Preencher os TextViews com os dados do ponto de coleta
        if (holder.textViewNomePonto != null) {
            holder.textViewNomePonto.setText(pontoColeta.getNome());
            Log.d("Adapter", "Nome do Ponto: " + pontoColeta.getNome());
        }
        if (holder.textViewEnderecoPonto != null) {
            String enderecoCompleto = pontoColeta.getEndereco() + ", " + pontoColeta.getNumero();
            holder.textViewEnderecoPonto.setText(enderecoCompleto);
            Log.d("Adapter", "Endereço: " + enderecoCompleto);
        }
        if (holder.textViewBairroPonto != null) {
            holder.textViewBairroPonto.setText(pontoColeta.getBairro());
        }
        if (holder.textViewCidadeUfPonto != null) {
            String cidadeUfPonto = pontoColeta.getCidade() + "-" + pontoColeta.getUf();
            holder.textViewCidadeUfPonto.setText(cidadeUfPonto);
            Log.d("Adapter", "Cidade-UF: " + cidadeUfPonto);
        }
        if (holder.textViewPessoaContato != null) {
            holder.textViewPessoaContato.setText(pontoColeta.getPessoa_contato());
        }
        if (holder.textViewHorarioFuncionamento != null) {
            holder.textViewHorarioFuncionamento.setText(pontoColeta.getHorario_funcionamento());
        }
        if (holder.textViewTelefonePonto != null) {
            holder.textViewTelefonePonto.setText(pontoColeta.getFone());
        }
        if (holder.textViewWhatsapp != null) {
            holder.textViewWhatsapp.setText(pontoColeta.getWhatsApp());
        }

        // Exibir os materiais coletados
        if (pontoColeta.getMateriaisColetados() != null && !pontoColeta.getMateriaisColetados().isEmpty()) {
            // Separar os materiais por vírgula
            String materiaisFormatados = pontoColeta.getMateriaisColetados().replace("\n", ", ");
            holder.textViewMateriaisColetados.setText(materiaisFormatados);
        } else {
            holder.textViewMateriaisColetados.setText("Nenhum material registrado");
        }

        // Verificar se o ponto de coleta já é favorito
        String uidUsuario = auth.getCurrentUser().getUid();
        db.collection("FAVORITOS")
                .whereEqualTo("uidUsuario", uidUsuario)
                .whereEqualTo("idPontoColeta", pontoColeta.getId_PC())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_24_preenchida);
                    } else {
                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_border_24_vazia);
                    }
                });

        // Ação de favoritar
        holder.imageButtonFavoritar.setOnClickListener(v -> {
            db.collection("FAVORITOS")
                    .whereEqualTo("uidUsuario", uidUsuario)
                    .whereEqualTo("idPontoColeta", pontoColeta.getId_PC())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot favoritoDoc = task.getResult().getDocuments().get(0);
                            db.collection("FAVORITOS").document(favoritoDoc.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(activity, "Favorito removido", Toast.LENGTH_SHORT).show();
                                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_border_24_vazia);
                                    });
                        } else {
                            Favorito favorito = new Favorito(uidUsuario, pontoColeta.getId_PC());
                            db.collection("FAVORITOS").add(favorito)
                                    .addOnSuccessListener(documentReference -> {
                                        Toast.makeText(activity, "Favoritado", Toast.LENGTH_SHORT).show();
                                        holder.imageButtonFavoritar.setImageResource(R.drawable.baseline_star_24_preenchida);
                                    });
                        }
                    });
        });

        // botão "ver mais"
        holder.buttonAlterarPonto.setOnClickListener(v -> {
            Intent intent = new Intent(activity, CadPontoColetaActivity.class);
            intent.putExtra("isFromConsulta", true);
            intent.putExtra("idPontoColeta", pontoColeta.getId_PC());
            activity.startActivity(intent);
        });

        holder.imageViewPontoColeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Deseja ir até o ponto de coleta?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Chama o método para abrir o Google Maps, passando o ponto de coleta atual
                                openMapApps(pontoColeta);
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        holder.textViewTelefonePonto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log para verificar se o clique está sendo detectado
                Log.d("TelefoneClick", "Número de telefone clicado: " + pontoColeta.getFone());

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Deseja ligar para o ponto de coleta?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("TelefoneClick", "Usuário clicou em 'Sim' para fazer a ligação");

                                // Verifica se a permissão de chamada foi concedida
                                if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                    Log.d("TelefoneClick", "Permissão de chamada não concedida, solicitando...");
                                    // Solicita a permissão se não estiver concedida
                                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 2);
                                } else {
                                    try {
                                        // Tente abrir a tela de discagem com o número de telefone
                                        Log.d("TelefoneClick", "Permissão concedida, tentando iniciar a chamada...");
                                        Intent intent = new Intent(Intent.ACTION_CALL);
                                        intent.setData(Uri.parse("tel:" + pontoColeta.getFone())); // Corrigido de 'Tel' para 'tel'
                                        activity.startActivity(intent);
                                    } catch (SecurityException e) {
                                        Log.e("TelefoneClick", "Erro de segurança ao tentar fazer a chamada", e);
                                        Toast.makeText(activity, "Permissão de chamada não concedida.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d("TelefoneClick", "Usuário clicou em 'Não', fechando o diálogo.");
                                dialog.dismiss();
                            }
                        });
                builder.create().show();
            }
        });

        holder.textViewWhatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Criação do diálogo ao usuário "questionamento: sim/não"
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setMessage("Deseja abrir o WhatsApp?")
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //captura o numero de whatsapp ponto de coleta, passando com o 55 do brasil + somente números
                                String numeroWhatsApp = "55" + pontoColeta.getWhatsApp().replaceAll("[^\\d]", "");

                                // Log para verificar o número do WhatsApp
                                Log.d("WhatsAppClick", "Número do WhatsApp: " + numeroWhatsApp);

                                // Verifica se o WhatsApp está instalado e tenta abrir
                                try {
                                    // Cria a URL para enviar mensagem no WhatsApp
                                    String url = "https://api.whatsapp.com/send?phone=" + numeroWhatsApp;

                                    // Log para verificar a URL do WhatsApp
                                    Log.d("WhatsAppClick", "URL para WhatsApp: " + url);

                                    // Criar o Intent para abrir o WhatsApp via URL
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));

                                    // Tenta abrir a URL sem verificar explicitamente se o WhatsApp está instalado
                                    activity.startActivity(intent);

                                } catch (Exception e) {
                                    Log.e("WhatsAppClick", "Erro ao tentar abrir o WhatsApp", e);
                                    Toast.makeText(activity, "Erro ao tentar abrir o WhatsApp.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Fecha o diálogo e não faz nada
                            }
                        });
                builder.create().show();
            }
        });

        // Quando clicar em "Avaliações", enviar o nome corretamente
        holder.textViewAvaliacoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log para verificar o nome do ponto de coleta que está sendo passado
                Log.d("ConsultaPontoAdapter", "Nome do ponto de coleta (passando para AvaliacaoActivity): " + pontoColeta.getNome());

                // Criar o Intent e passar o nome do ponto de coleta
                Intent intent = new Intent(activity, AvaliacaoActivity.class);
                intent.putExtra("id_ponto_coleta", pontoColeta.getId_PC()); // Passar o ID do ponto de coleta
                intent.putExtra("nome_ponto_coleta", pontoColeta.getNome()); // Passar o nome do ponto de coleta
                activity.startActivity(intent);
            }
        });
    }

    private void openMapApps(PontoColeta pontoColeta) {

        String endereco = pontoColeta.getEndereco() + ", " + pontoColeta.getNumero() + ", " + pontoColeta.getBairro() + ", " + pontoColeta.getCidade() + ", " + pontoColeta.getUf();
        //teste String endereco = "Rua Mario Italvino Poletto, 120";

        Uri geoUri = Uri.parse("geo:0,0?q="+endereco); // Exemplo: Buscar restaurantes
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Para abrir diretamente no Google Maps (opcional)

        activity.startActivity(mapIntent);
    }

    @Override
    public int getItemCount() {
        return listaPontosColeta.size();
    }

    public static class PontoColetaViewHolder extends RecyclerView.ViewHolder {

        TextView textViewNomePonto, textViewEnderecoPonto, textViewBairroPonto, textViewPessoaContato, textViewHorarioFuncionamento;
        TextView textViewCidadePonto, textViewUfPonto, textViewCidadeUfPonto, textViewTelefonePonto, textViewMateriaisColetados;
        TextView textViewWhatsapp, textViewAvaliacoes;
        ImageButton imageButtonFavoritar;
        Button buttonAlterarPonto;
        ImageView imageViewPontoColeta;

        public PontoColetaViewHolder(@NonNull View itemView) {
            super(itemView);

            // Inicializar os TextViews/componentes
            textViewNomePonto = itemView.findViewById(R.id.textViewNomePonto);
            textViewEnderecoPonto = itemView.findViewById(R.id.textViewEnderecoPonto);
            textViewBairroPonto = itemView.findViewById(R.id.textViewBairroPonto);
            textViewCidadePonto = itemView.findViewById(R.id.editTextCidadePontoColeta);
            textViewCidadeUfPonto = itemView.findViewById(R.id.textViewCidadeUfPonto);
            textViewPessoaContato = itemView.findViewById(R.id.textViewPessoaContato);
            textViewHorarioFuncionamento = itemView.findViewById(R.id.textViewHorarioFuncionamento);
            textViewTelefonePonto = itemView.findViewById(R.id.textViewTelefonePonto);
            textViewWhatsapp = itemView.findViewById(R.id.textViewWhatsApp);
            textViewMateriaisColetados = itemView.findViewById(R.id.textViewMateriaisColetados);
            imageViewPontoColeta = itemView.findViewById(R.id.imageViewPontoColeta);

            // Inicializar o botão de favoritar e alterar
            imageButtonFavoritar = itemView.findViewById(R.id.imageButtonFavoritar);
            textViewAvaliacoes = itemView.findViewById(R.id.textViewAvaliacoes);
            buttonAlterarPonto = itemView.findViewById(R.id.buttonAlterarPonto);

            // Adicionar logs para verificar se todos os componentes foram corretamente inicializados
            if (textViewNomePonto == null) {
                Log.e("ViewHolder", "textViewNomePonto é null");
            }
            if (textViewEnderecoPonto == null) {
                Log.e("ViewHolder", "textViewEnderecoPonto é null");
            }
            if (textViewBairroPonto == null) {
                Log.e("ViewHolder", "textViewBairroPonto é null");
            }
            if (textViewCidadePonto == null) {
                Log.e("ViewHolder", "textViewCidadePonto é null");
            }
            if (textViewUfPonto == null) {
                Log.e("ViewHolder", "textViewUfPonto é null");
            }
            if (textViewPessoaContato == null) {
                Log.e("ViewHolder", "textViewUfPonto é null");
            }
            if (textViewHorarioFuncionamento == null) {
                Log.e("ViewHolder", "textViewUfPonto é null");
            }
            if (textViewTelefonePonto == null) {
                Log.e("ViewHolder", "textViewTelefonePonto é null");
            }
            if (textViewWhatsapp == null) {
                Log.e("ViewHolder", "textViewWhatsapp é null");
            }
            if (textViewMateriaisColetados == null) {
                Log.e("ViewHolder", "textViewMateriaisColetados é null");
            }
            if (imageButtonFavoritar == null) {
                Log.e("ViewHolder", "imageButtonFavoritar é null");
            }
            if (buttonAlterarPonto == null) {
                Log.e("ViewHolder", "buttonAlterarPonto é null");
            }
        }
    }
}