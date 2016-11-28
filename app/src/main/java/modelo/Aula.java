package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline Dominique on 27/11/2016.
 */
public class Aula {
    private int idAula;
    private String data;
    private String horainicio;
    private String horafim;
    private int semestre;
    private Professor professor;
    private Sala sala;
    private Disciplina disciplina;

    public Aula(int idAula, String data, String horainicio, String horafim, int semestre, Professor professor, Sala sala, Disciplina disciplina) {
        this.idAula = idAula;
        this.data = data;
        this.horainicio = horainicio;
        this.horafim = horafim;
        this.semestre = semestre;
        this.professor = professor;
        this.sala = sala;
        this.disciplina = disciplina;
    }

    public int getIdAula() {
        return idAula;
    }

    public void setIdAula(int idAula) {
        this.idAula = idAula;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getHorainicio() {
        return horainicio;
    }

    public void setHorainicio(String horainicio) {
        this.horainicio = horainicio;
    }

    public String getHorafim() {
        return horafim;
    }

    public void setHorafim(String horafim) {
        this.horafim = horafim;
    }

    public int getSemestre() {
        return semestre;
    }

    public void setSemestre(int semestre) {
        this.semestre = semestre;
    }

    public Professor getProfessor() {
        return professor;
    }

    public void setProfessor(Professor professor) {
        this.professor = professor;
    }

    public Sala getSala() {
        return sala;
    }

    public void setSala(Sala sala) {
        this.sala = sala;
    }

    public Disciplina getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(Disciplina disciplina) {
        this.disciplina = disciplina;
    }

    public static Aula jsonToAula (JSONObject objeto) throws JSONException {
        if(objeto == null){
            return null;
        }else {
            Professor professor = new Professor(objeto.getInt("idProfessor"),objeto.getString("Nome"));
            Sala sala = new Sala(objeto.getInt("idSala"),objeto.getString("Numero"));
            Disciplina disciplina = new Disciplina(objeto.getInt("idDisciplina"),objeto.getString("Nome"));
            Aula aula = new Aula(objeto.getInt("idAula"),objeto.getString("Data"),
                    objeto.getString("HoraInicio"),objeto.getString("HoraFim"),
                    objeto.getInt("Semetre"),professor,sala,disciplina);
            return aula;
        }
    }

    public JSONObject aulaToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idAula",this.getIdAula());
        objeto.put("Data",this.getData());
        objeto.put("HoraInicio",this.getHorainicio());
        objeto.put("HoraFim",this.getHorafim());
        objeto.put("Semestre",this.getSemestre());
        objeto.put("idProfessor",this.professor.getIdProfessor());
        objeto.put("idSala",this.sala.getIdSala());
        objeto.put("idDisciplina",this.disciplina.getIdDisciplina());
        return objeto;
    }
}