package com.example.sim.appinfo;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import controlador.Requisicao;
import modelo.Aula;
import modelo.Mensagem;
import modelo.Professor;
import modelo.Sala;

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
    private Spinner spSala;
    private Spinner spDiaSala;
    private Spinner spProfessor;
    private Spinner spDiaProfessor;
    private ArrayList<Sala> salas = new ArrayList<>();
    private ArrayList<Professor> professores = new ArrayList<>();
    private ArrayList<String> diaSala = new ArrayList<>();
    private ArrayList<String> diaProfessor = new ArrayList<>();

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

                //Monta lista de Aula por sala
                listaHorarioPorSala();

                //Monta lista de Aula por professor
                listaHorarioPorProfessor();

            }
        });

        // Seta o listener do refresh que é o gatilho de novas datas
        scHorarioSala.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Monta lista de Aula por sala
                listaHorarioPorSala();

            }
        });
        scHorarioProfessor.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //Monta lista de Aula por professor
                listaHorarioPorProfessor();

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
        //Carrega spinners da tela com os valores
        CarregaSpinners();

        //Adiciona evento de click no botão de pesquisar por sala
        Button btPesquisarPorSala = (Button) findViewById(R.id.btPesquisarPorSala);
        btPesquisarPorSala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PesquisaAulaPorSala();
            }
        });

        //Adiciona evento de click no botão de pesquisar por professor
        Button btPesquisarPorProfessor = (Button) findViewById(R.id.btPesquisarPorProfessor);
        btPesquisarPorProfessor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PesquisaAulaPorProfessor();
            }
        });

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

                ImageView ivAula = (ImageView) convertView.findViewById(R.id.ivAula);
                TextView tvDisciplina = (TextView) convertView.findViewById(R.id.tvDisciplina);
                TextView tvProfessor = (TextView) convertView.findViewById(R.id.tvProfessor);
                TextView tvDia = (TextView) convertView.findViewById(R.id.tvDia);
                TextView tvHorarioInicio = (TextView) convertView.findViewById(R.id.tvHorarioInicio);
                TextView tvSala = (TextView) convertView.findViewById(R.id.tvSala);


                Aula aula = (Aula) getItem(position);

                ivAula.setImageResource(R.drawable.ic_watch_black);
                tvDisciplina.setText(aula.getDisciplina().getNome().toString());
                tvProfessor.setText(aula.getProfessor().getNome().toString());
                tvDia.setText(aula.getDia().toString());
                tvHorarioInicio.setText(aula.getHorainicio().toString());
                tvSala.setText(aula.getSala().getNumero().toString());


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
        listaAulaPorSala.clear();
        try {
            JSONObject json = new JSONObject();
            new RequisicaoAsyncTask().execute("ListaAulasPorSala", "0", json.toString());
        } catch (Exception ex) {
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }
    public void listaHorarioPorProfessor() {
        adpAulaProfessor = new ArrayAdapter<Aula>(this, R.layout.item_aula) {
            @Override
            public View getView(int position, View convertView, final ViewGroup parent) {

                if (convertView == null)
                    convertView = getLayoutInflater().inflate(R.layout.item_aula, null); /* obtém o objeto que está nesta posição do ArrayAdapter */

                final int index = position;

                ImageView ivAula = (ImageView) convertView.findViewById(R.id.ivAula);
                TextView tvDisciplina = (TextView) convertView.findViewById(R.id.tvDisciplina);
                TextView tvProfessor = (TextView) convertView.findViewById(R.id.tvProfessor);
                TextView tvDia = (TextView) convertView.findViewById(R.id.tvDia);
                TextView tvHorarioInicio = (TextView) convertView.findViewById(R.id.tvHorarioInicio);
                TextView tvSala = (TextView) convertView.findViewById(R.id.tvSala);


                Aula aula = (Aula) getItem(position);

                ivAula.setImageResource(R.drawable.ic_watch_black);
                tvDisciplina.setText(aula.getDisciplina().getNome().toString());
                tvProfessor.setText(aula.getProfessor().getNome().toString());
                tvDia.setText(aula.getDia().toString());
                tvHorarioInicio.setText(aula.getHorainicio().toString());
                tvSala.setText(aula.getSala().getNumero().toString());

                return convertView;
            }
        };
        lvHorarioProfessor = (ListView) findViewById(R.id.lvHorarioProfessor);
        lvHorarioProfessor.setAdapter(adpAulaProfessor);

        //Adiciona o evento de click nos items da lista
        lvHorarioProfessor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        //Carrega lista de Horario Por Sala
        listaAulaPorProfessor.clear();
        try {
            JSONObject json = new JSONObject();
            new RequisicaoAsyncTask().execute("ListaAulasPorProfessor", "0", json.toString());
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
        descritor.setIndicator("", ResourcesCompat.getDrawable(getResources(), R.drawable.ic_place, getTheme()));
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
                    } else {
                        if (metodo == "ListaAulasPorSala") {
                            //Monta lista de Aulas Por Sala
                            for (int i = 0; i < resultado.length(); i++) {
                                listaAulaPorSala.add(Aula.jsonToAula(resultado.getJSONObject(i)));
                            }
                            adpAulaSala.clear();
                            adpAulaSala.addAll(listaAulaPorSala);
                        } else {
                            if (metodo == "ListaAulasPorProfessor") {
                                //Monta lista de Aulas Por Professor
                                for (int i = 0; i < resultado.length(); i++) {
                                    listaAulaPorProfessor.add(Aula.jsonToAula(resultado.getJSONObject(i)));
                                }
                                adpAulaProfessor.clear();
                                adpAulaProfessor.addAll(listaAulaPorProfessor);
                            }else {
                                if(metodo == "ListaSalas") {
                                    //Recupera Salas
                                    for(int i=0;i<resultado.length();i++){
                                        salas.add(Sala.jsonToSala(resultado.getJSONObject(i)));
                                    }
                                }else{
                                    if(metodo == "ListaProfessores"){
                                        //Recupera professores
                                        for(int i=0;i<resultado.length();i++){
                                            professores.add(Professor.jsonToProfessor(resultado.getJSONObject(i)));
                                        }
                                    }else{
                                        if(metodo == "PesquisaAulaPorSala"){
                                            listaAulaPorSala.clear();
                                            adpAulaSala.clear();
                                            //Recupera pesquisa por Sala
                                            for (int i = 0; i < resultado.length(); i++) {
                                                listaAulaPorSala.add(Aula.jsonToAula(resultado.getJSONObject(i)));
                                            }
                                            adpAulaSala.addAll(listaAulaPorSala);
                                        }else{
                                            if(metodo == "PesquisaAulaPorProfessor"){
                                                int c = resultado.length();
                                                listaAulaPorProfessor.clear();
                                                adpAulaProfessor.clear();
                                                    //Recupera pesquisa por Professor
                                                    for (int i = 0; i < resultado.length(); i++) {
                                                        listaAulaPorProfessor.add(Aula.jsonToAula(resultado.getJSONObject(i)));
                                                    }
                                                adpAulaProfessor.addAll(listaAulaPorProfessor);
                                            }
                                        }
                                    }
                                }
                            }
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

    public void CarregaSpinners(){
        //Carrega spinner de Sala
        salas.clear();
        salas.add(new Sala(0,"Selecione a Sala"));
        spSala = (Spinner) findViewById(R.id.spSala);
        ArrayAdapter adSala = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,salas){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }
                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }

        };
        spSala.setAdapter(adSala);

        //Carrega lista de Salas
        //pd = ProgressDialog.show(ActPrincipal.this, "", "Por favor, aguarde...", false);
        //processos++;
        scHorarioSala.setRefreshing(true);
        new RequisicaoAsyncTask().execute("ListaSalas", "0", "");

        //Adiciona evento de item selecionado no spinner de Salas
        spSala.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*scHorarioSala.setRefreshing(true);

                Sala item = (Sala) parent.getItemAtPosition(position);

                //Carrega lista de Horario Por Sala
                listaAulaPorSala.clear();
                try {
                    JSONObject json = new JSONObject();
                    new RequisicaoAsyncTask().execute("ListaAulasPorSala", String.valueOf(item.getIdSala()) , json.toString());
                } catch (Exception ex) {
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Carrega spinner de professores
        professores.clear();
        professores.add(new Professor(0, "Selecione o Professor"));
        spProfessor = (Spinner) findViewById(R.id.spProfessor);
        ArrayAdapter adProfessor = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,professores){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }

                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spProfessor.setAdapter(adProfessor);

        //Carrega lista de Professores
        scHorarioProfessor.setRefreshing(true);
        new RequisicaoAsyncTask().execute("ListaProfessores", "0", "");

        //Adiciona evento de item selecionado no spinner de Salas
        spProfessor .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                /*scHorarioProfessor.setRefreshing(true);

                Professor item = (Professor) parent.getItemAtPosition(position);
                //Carrega lista de Horario Por Professor
                listaAulaPorProfessor.clear();
                try {
                    JSONObject json = new JSONObject();
                    new RequisicaoAsyncTask().execute("ListaAulasPorProfessor", String.valueOf(item.getIdProfessor()), json.toString());
                } catch (Exception ex) {
                    Log.e("Erro", ex.getMessage());
                    Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
                }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Recupera dia.
        String[] diaSala = new String[]{"Selecione o dia","Segunda-feira","Terça-feira","Quarta-feira","Quinta-feira","Sexta-feira","Sábado"};
        spDiaSala = (Spinner) findViewById(R.id.spDiaSala);
        ArrayAdapter adDiaSala = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,diaSala){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }

                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spDiaSala.setAdapter(adDiaSala);

        //Recupera data Professor.
        String[] diaProfessor = new String[]{"Selecione a data","Segunda-feira","Terça-feira","Quarta-feira","Quinta-feira","Sexta-feira","Sábado"};
        spDiaProfessor = (Spinner) findViewById(R.id.spDiaProfessor);
        ArrayAdapter adDiaProfessor = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,diaProfessor){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);

                //Seta cores nos items
                if(position == 0) {
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.placeholderColor));
                }else{
                    ((TextView) v).setTextColor(ContextCompat.getColor(getBaseContext(), R.color.textColor));
                }

                return v;
            }

            @Override
            public boolean isEnabled(int position){
                if(position == 0)
                {
                    //Desabilita o primeiro item da lista.
                    //O primeiro item será usado para a dica.
                    return false;
                }
                else
                {
                    return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if(position == 0){
                    // Coloca cor cinza no texto
                    tv.setTextColor(Color.GRAY);
                }
                else {
                    //Coloca cor preta no texto
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spDiaProfessor.setAdapter(adDiaProfessor);
    }

    //Pesquisa Aulas com base nas informacoes dos spinners
    public void PesquisaAulaPorSala(){
        String erro = "";

        //Valida dados fornecidos
        if(spSala.getSelectedItemPosition() == 0){
            erro = "Preencha o número da Sala!";
        }else{
            if(spDiaSala.getSelectedItemPosition() == 0){
                erro = "Preencha o dia da Aula!";
            }
        }

        try {
            //Verifica se foi encontrado algum problema
            if (erro.equals("")) {
                listaAulaPorSala.clear();
                JSONObject json = new JSONObject();
                json.put("Numero", spSala.getSelectedItem().toString());
                json.put("Dia", spDiaSala.getSelectedItem().toString());

                scHorarioSala.setRefreshing(true);
                new RequisicaoAsyncTask().execute("PesquisaAulaPorSala", "0", json.toString());

            } else {
                Toast.makeText(ActPrincipal.this, erro, Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

    //Pesquisa Aulas com base nas informacoes dos spinners
    public void PesquisaAulaPorProfessor(){
        String erro = "";

        //Valida dados fornecidos
        if(spProfessor.getSelectedItemPosition() == 0){
            erro = "Preencha o nome do Professor!";
        }else{
            if(spDiaProfessor.getSelectedItemPosition() == 0){
            erro = "Preencha o dia da Aula!";
        }
        }

        try {
            //Verifica se foi encontrado algum problema
            if (erro.equals("")) {
                listaAulaPorProfessor.clear();
                JSONObject json = new JSONObject();
                json.put("Nome", spProfessor.getSelectedItem().toString());
                json.put("Dia", spDiaProfessor.getSelectedItem().toString());

                scHorarioProfessor.setRefreshing(true);
                new RequisicaoAsyncTask().execute("PesquisaAulaPorProfessor", "0", json.toString());

            } else {
                Toast.makeText(ActPrincipal.this, erro, Toast.LENGTH_LONG).show();
            }
        }catch (Exception ex){
            Log.e("Erro", ex.getMessage());
            Toast.makeText(ActPrincipal.this, "Não foi possível completar a operação!", Toast.LENGTH_SHORT).show();
        }
    }

}