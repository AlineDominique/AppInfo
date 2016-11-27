package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline Dominique on 27/11/2016.
 */
public class Disciplina {
    private int idDisciplina;
    private String nome;

    public Disciplina(int idDisciplina, String nome) {
        this.idDisciplina = idDisciplina;
        this.nome = nome;
    }

    public int getIdDisciplina() {
        return idDisciplina;
    }

    public void setIdDisciplina(int idDisciplina) {
        this.idDisciplina = idDisciplina;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public static Disciplina jsonToDisciplina(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Disciplina disciplina = new Disciplina(objeto.getInt("idDisciplina"),objeto.getString("Nome"));
            return disciplina;
        }
    }

    public JSONObject disciplinaToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idDisciplina",this.getIdDisciplina());
        objeto.put("Nome",this.getNome());
        return objeto;
    }

    @Override
    public String toString() {
        return nome;
    }
}