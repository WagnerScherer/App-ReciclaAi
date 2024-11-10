package com.wagner.reciclaai.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.wagner.reciclaai.Util.SolicitarAgendamentoFragment;
import com.wagner.reciclaai.Util.SolicitacoesRecebidasFragment;

public class AgendamentoTabsAdapter extends FragmentStateAdapter {
    private final boolean isAdmin;

    public AgendamentoTabsAdapter(@NonNull FragmentActivity fragmentActivity, boolean isAdmin) {
        super(fragmentActivity);
        this.isAdmin = isAdmin;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new SolicitarAgendamentoFragment();
        } else {
            return new SolicitacoesRecebidasFragment();
        }
    }

    @Override
    public int getItemCount() {
        return isAdmin ? 2 : 1; // Exibe 2 abas se for admin, 1 aba caso contrário
    }
}
