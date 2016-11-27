package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline Dominique on 27/11/2016.
 */
public class Professor {
    private int idProfessor;
    private String nome;

    public Professor(int idProfessor, String nome) {
        this.idProfessor = idProfessor;
        this.nome = nome;
    }

    public int getIdProfessor() {
        return idProfessor;
    }

    public void setIdProfessor(int idProfessor) {
        this.idProfessor = idProfessor;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public static Professor jsonToProfessor(JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Professor professor = new Professor(objeto.getInt("idProfessor"),objeto.getString("Nome"));
            return professor;
        }
    }

    public JSONObject professorToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idProfessor",this.getIdProfessor());
        objeto.put("Nome",this.getNome());
        return objeto;
    }

    @Override
    public String toString() {
        return nome;
    }
}