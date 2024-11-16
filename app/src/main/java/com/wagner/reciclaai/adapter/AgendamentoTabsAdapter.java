package com.wagner.reciclaai.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.wagner.reciclaai.Util.SolicitarAgendamentoFragment;
import com.wagner.reciclaai.Util.SolicitacoesRecebidasFragment;
import android.util.Log;

public class AgendamentoTabsAdapter extends FragmentStateAdapter {
    private final boolean isAdmin;

    public AgendamentoTabsAdapter(@NonNull FragmentActivity fragmentActivity, boolean isAdmin) {
        super(fragmentActivity);
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Log.d("AgendamentoTabsAdapter", "Criando fragment para posição: " + position);
        if (position == 0) {
            return new SolicitarAgendamentoFragment();
        } else if (position == 1 && isAdmin) {
            return new SolicitacoesRecebidasFragment();
        }
        return new SolicitarAgendamentoFragment();
    }

    @Override
    public int getItemCount() {
        int count = isAdmin ? 2 : 1;
        Log.d("AgendamentoTabsAdapter", "Número de abas configuradas: " + count);
        return count;
    }
}
