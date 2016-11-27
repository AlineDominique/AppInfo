package com.example.sim.appinfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import controlador.Requisicao;
import modelo.Aula;

/**
 * Created by Aline on 27/11/2016.
 */

public class ActPrincipal extends AppCompatActivity {

    public int tabSelecionada;
    private ProgressDialog pd;
    private ArrayList<Aula> listaAulaPorSala = new ArrayList<>();
    private ArrayList<Aula> listaAulaPorProfessor = new ArrayList<>();
    private int processos = 0;
    private SwipeRefreshLayout scHorarioSala, scHorarioProfessor;
    ListView lvHorarioSala;
    ArrayAdapter<Aula> adpAulaSala;
    ArrayAdapter<Aula> adpAulaProfessor;
    ListView lvHorarioProfessor;
    TextView tvDisciplina;
    TextView tvProfessor;
    TextView tvHorarioInicio;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_principal);

        // Procura os containers da vista do Swipe
        scHorarioSala = (SwipeRefreshLayout) findViewById(R.id.scHorarioSala);
        scHorarioProfessor = (SwipeRefreshLayout) findViewById(R.id.scHorarioProfessor);

        /**
         * Mostra o Swipe Refresh no momento em que a activity é criada
         */
        scHorarioSala.post(new Runnable() {
            @Override
            public void run() {

                scHorarioSala.setRefreshing(true);

                //Monta lista de animais perdidos
                listaHorarioPorSala();

                //Monta lista de compromissos
                listaHorarioPorProfessor();

            }
        });

        // Seta o listener do refresh que é o gatilho de novas datas
        scHorarioSala.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Monta lista de animais perdidos
                listaAulaPorSala();

            }
        });
        scHorarioProfessor.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Monta lista de compromissos
                listaAulaPorProfessor();

            }
        });

        // Configuração das cores do swipe
        scHorarioSala.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_dark,
                android.R.color.holo_red_dark);
        scHorarioProfessor.setColorSchemeResources(android.R.color.holo_red_light,
                android.R.color.holo_blue_dark,
                android.R.color.holo_red_dark);

        //Configura e carrega toolbar
        Toolbar t = (Toolbar) findViewById(R.id.toolbar);
        t.setTitleTextColor(ContextCompat.getColor(this, R.color.fontColorPrimary));
        t.setLogo(R.drawable.ic_calendario);
        setSupportActionBar(t);

        //Adiciona as opções nas tabs
        configuraTabs();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Carrega layout do toolbar
        getMenuInflater().inflate(R.menu.toolbar_principal, menu);
        this.menu = menu;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Trata click dos menus do toolbar
        switch (item.getItemId()) {
            case R.id.menuSobre:
                Intent intent1 = new Intent(ActPrincipal.this, ActSobre.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Monta a lista de Horário por Sala
    public void listaHorarioPorSala() {
        adpAulaSala = new ArrayAdapter<Aula>(this, R.layout.item_aula) {
            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_aula, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                final int index = position;

                TextView tvDisciplina = (TextView) convertView.findViewById(R.id.tvDisciplina);
                TextView tvProfessor = (TextView) convertView.findViewById(R.id.tvProfessor);
                TextView tvHorarioInicio = (TextView) convertView.findViewById(R.id.tvHorarioInicio);

                Aula aula = (Aula) getItem(position);

                tvDisciplina.setText(aula.getDisciplina().getNome());
                tvProfessor.setText(aula.getProfessor().getNome());
                tvHorarioInicio.setText(aula.getHorainicio());

                return convertView;
            }
        };
        lvHorarioSala = (ListView) findViewById(R.id.lvHorarioSala);
        lvHorarioSala.setAdapter(adpAulaSala);

        //Adiciona o evento de click nos items da lista
        lvHorarioSala.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //Carrega lista de Horario Por Sala
        listaHorarioPorSala().clear();
        try {
            JSONObject json = new JSONObject();
            new RequisicaoAsyncTask().execute("ListaAulasPorSala", "0", json.toString());
        } catch (Exception ex) {
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    //Configura as tabs da tela principal
    public void configuraTabs() {
        //Adiciona as opções nas tabs da tela principal
        TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);

        abas.setup();

        TabHost.TabSpec descritor = abas.newTabSpec("Sala");
        descritor.setContent(R.id.llHorarioSala);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_home, getTheme()));
        abas.addTab(descritor);

        descritor = abas.newTabSpec("Professor");
        descritor.setContent(R.id.llHorarioProfessor);
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_face, getTheme()));
        abas.addTab(descritor);


        //Seta o fundo da primeira tab selecionada
        tabSelecionada = abas.getCurrentTab();
        abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.buttonColorPrimary));

        abas.setOnTabChangedListener(new TabHost.OnTabChangeListener() {

            public void onTabChanged(String arg0) {
                //Seta a cor de fundo da tab selecionada
                TabHost abas = (TabHost) findViewById(R.id.tbPrincipal);
                for (int i = 0; i < abas.getTabWidget().getChildCount(); i++) {
                    abas.getTabWidget().getChildAt(i).setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                }
                abas.getTabWidget().getChildAt(abas.getCurrentTab()).setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.buttonColorPrimary));

                //Anima a transição de tabs
                View viewSelecionada = abas.getCurrentView();
                if (abas.getCurrentTab() > tabSelecionada) {
                    viewSelecionada.setAnimation(direita());
                } else {
                    viewSelecionada.setAnimation(esquerda());
                }
                tabSelecionada = abas.getCurrentTab();

            }

        });
    }

    //Anima a transição vinda da direita
    public Animation direita() {
        Animation direita = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        direita.setDuration(240);
        direita.setInterpolator(new AccelerateInterpolator());
        return direita;
    }

    //Anima a transição vinda da esquerda
    public Animation esquerda() {
        Animation esquerda = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        esquerda.setDuration(240);
        esquerda.setInterpolator(new AccelerateInterpolator());
        return esquerda;
    }

    private class RequisicaoAsyncTask extends AsyncTask<String, Void, JSONArray> {

        private String metodo;
        private int id;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected JSONArray doInBackground(String... params) {
            JSONArray resultado = new JSONArray();

            try {
                //Recupera parâmetros e realiza a requisição
                metodo = params[0];
                id = Integer.parseInt(params[1]);
                String conteudo = params[2];

                //Chama método da API
                resultado = Requisicao.chamaMetodo(metodo, id, conteudo);

            } catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }
        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                if (resultado.length() > 0) {
                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActPrincipal.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (Exception e) {
            Log.e("Erro", e.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            return resultado;
        }
        @Override
        protected void onPostExecute(JSONArray resultado) {
            try {
                if (resultado.length() > 0) {
                    //Verifica se o objeto retornado foi uma mensagem ou um objeto
                    JSONObject json = resultado.getJSONObject(0);
                    if (Mensagem.isMensagem(json)) {
                        Mensagem msg = Mensagem.jsonToMensagem(json);
                        Toast.makeText(ActPrincipal.this, msg.getMensagem(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    if (metodo == "ListaAulasPorSala") {
                        //Monta lista de Horario Por Sala
                        for (int i = 0; i < resultado.length(); i++) {
                            listaAulaPorSala.add(Aula.jsonToAula(resultado.getJSONObject(i)));
                        }
                        adpAulaSala.clear();
                        adpAulaSala.addAll(listaAulaPorSala);
                    } else {
                        if (metodo == "ListAulasPorProfessor") {
                            //Monta lista de Horario Por Professor
                            for (int i = 0; i < resultado.length(); i++) {
                                listaAulaPorProfessor.add(Aula.jsonToAula(resultado.getJSONObject(i)));
                            }
                            adpAulaProfessor.clear();
                            adpAulaProfessor.addAll(listaAulaPorProfessor);
                        }
                    }
                }
            }catch (Exception e) {
                Log.e("Erro", e.getMessage());
                Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
            }
            // Para o Swipe Refreshing
            scHorarioSala.setRefreshing(false);
            scHorarioProfessor.setRefreshing(false);
        }
    }
}