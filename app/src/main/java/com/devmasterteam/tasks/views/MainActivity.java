package com.devmasterteam.tasks.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.devmasterteam.tasks.R;
import com.devmasterteam.tasks.constants.TaskConstants;
import com.devmasterteam.tasks.entities.PriorityEntity;
import com.devmasterteam.tasks.infra.operation.OperationListener;
import com.devmasterteam.tasks.infra.security.SecurityPreferences;
import com.devmasterteam.tasks.manager.PriorityManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private PriorityManager mPriorityManager;
    private SecurityPreferences mSecurityPreferences;
    private ViewHolder mViewHolder = new ViewHolder();
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Inciliza variáveis
        this.mPriorityManager = new PriorityManager(this);
        this.mSecurityPreferences = new SecurityPreferences(this);

        // Obtém elementos
        this.mViewHolder.textDateDescription = (TextView) this.findViewById(R.id.text_date_description);
        this.mViewHolder.textHello = (TextView) this.findViewById(R.id.text_hello);
        this.mViewHolder.textAllCount = (TextView) this.findViewById(R.id.text_task_count);
        this.mViewHolder.textDoneCount = (TextView) this.findViewById(R.id.text_task_done_count);

        // Formata data
        this.formatDate();

        // Formata boas-vindas
        this.formatUserName();

        // Faz o load inicial
        this.initialLoad();

        // Incia a fragment padrão
        this.startDefaultFragment();

        MobileAds.initialize(this, "YOUR_ADMOB_ID");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("YOUR_ADS_ID");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                // Load the next interstitial.
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }

        });

    }

    /**
     * Formata a data Toolbar
     */
    private void formatDate() {
        Calendar c = Calendar.getInstance();

        String[] days = {"Domingo", "Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira", "Sábado"};
        String[] months = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novemembro", "Dezembro"};

        String str = days[c.get(Calendar.DAY_OF_WEEK) - 1] + ", " + c.get(Calendar.DAY_OF_MONTH) + " de " + months[c.get(Calendar.MONTH)];
        this.mViewHolder.textDateDescription.setText(str);
    }

    /**
     * Formata boas-vindas
     */
    private void formatUserName() {
        String str = "Olá, " + this.mSecurityPreferences.getStoredString(TaskConstants.USER.NAME) + "!";
        this.mViewHolder.textHello.setText(str);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        TextView name = header.findViewById(R.id.text_name);
        TextView email = header.findViewById(R.id.text_email);
        name.setText(this.mSecurityPreferences.getStoredString(TaskConstants.USER.NAME));
        email.setText(this.mSecurityPreferences.getStoredString(TaskConstants.USER.EMAIL));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Fragment fragment = null;
        int id = item.getItemId();

        try {

            if (id == R.id.nav_all_tasks) {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
                fragment = TaskListFragment.newInstance(TaskConstants.TASKFILTER.NO_FILTER);
            } else if (id == R.id.nav_next_seven_days) {
                fragment = TaskListFragment.newInstance(TaskConstants.TASKFILTER.NEXT_7_DAYS);
            } else if (id == R.id.nav_overdue) {
                fragment = TaskListFragment.newInstance(TaskConstants.TASKFILTER.OVERDUE);
            } else if (id == R.id.nav_logout) {
                handleLogout();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insere fragment substituindo qualquer existente
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_content, fragment).commit();

        // Fecha a navegação
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Atualiza o número de tarefas
     */
    public void updateTaskCount(int size, int completedSize) {

        String strAll;
        if (size == 0) {
            strAll = getString(R.string.nenhuma_tarefa);
        } else if (size == 1) {
            strAll = getString(R.string.uma_tarefa);
        } else {
            strAll = String.valueOf(size) + " " + getString(R.string.tarefas);
        }
        this.mViewHolder.textAllCount.setText(strAll);

        String strDone;
        if (completedSize == 0) {
            strDone = getString(R.string.nenhuma_completa);
        } else if (completedSize == 1) {
            strDone = getString(R.string.uma_completa);
        } else {
            strDone = String.valueOf(completedSize) + " " + getString(R.string.completas);
        }
        this.mViewHolder.textDoneCount.setText(strDone);
    }

    /**
     * Incia a fragment padrão
     */
    private void startDefaultFragment() {

        Fragment fragment = null;
        try {
            fragment = TaskListFragment.newInstance(TaskConstants.TASKFILTER.NO_FILTER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insere fragment substituindo qualquer existente
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_content, fragment).commit();
    }

    /**
     * Carrega as prioridades
     */
    private void initialLoad() {
        this.mPriorityManager.getList(priorityListener());
    }

    /**
     * Faz logout do usuário
     */
    private void handleLogout() {

        // Limpa os valores armazenados para acesso rápido
        this.mSecurityPreferences.removeStoredString(TaskConstants.HEADER.PERSON_KEY);
        this.mSecurityPreferences.removeStoredString(TaskConstants.HEADER.TOKEY_KEY);
        this.mSecurityPreferences.removeStoredString(TaskConstants.USER.NAME);
        this.mSecurityPreferences.removeStoredString(TaskConstants.USER.EMAIL);

        // Inicia login novamente
        startActivity(new Intent(this, LoginActivity.class));

        // Impede que seja possível voltar
        finish();
    }

    /**
     * Listener para quando login for realizado com sucesso
     */
    private OperationListener<List<PriorityEntity>> priorityListener() {
        return new OperationListener<List<PriorityEntity>>() {

            @Override
            public void onSuccess(List<PriorityEntity> result) {
            }

            @Override
            public void onError(int error, String errorMessage) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_LONG).show();
            }

        };
    }

    /**
     * ViewHolder
     */
    private static class ViewHolder {
        private TextView textDateDescription;
        private TextView textHello;
        private TextView textAllCount;
        private TextView textDoneCount;
    }

}
