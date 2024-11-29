package com.wagner.reciclaai.Util;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wagner.reciclaai.R;
import com.wagner.reciclaai.adapter.AgendamentoTabsAdapter;

public class AgendamentosActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private FirebaseAuth auth;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamentos_abas);

        // Inicializar FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Inicializar o TabLayout e o ViewPager2
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Obter o valor de isAdmin da Intent
        //teste fixando o parametro isAdmin = true;
        isAdmin = getIntent().getBooleanExtra("IS_ADMIN", false);

        Log.d("AgendamentosActivity", "isAdmin: " + isAdmin);

        // Configurar o adapter do ViewPager2
        configurarViewPager();
    }

    private void configurarViewPager() {
        // Criar e configurar o adapter
        AgendamentoTabsAdapter adapter = new AgendamentoTabsAdapter(this, isAdmin);
        viewPager.setAdapter(adapter);

        // Vincular o TabLayout com o ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Solicitar Agendamento");
            } else if (position == 1) {
                tab.setText("Solicitações Recebidas");
            }
        }).attach();

        // Log para depuração
        Log.d("AgendamentosActivity", "Número de abas: " + adapter.getItemCount());
    }

}
