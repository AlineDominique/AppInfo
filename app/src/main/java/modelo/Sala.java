package modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Aline Dominique on 27/11/2016.
 */

public class Sala {
    private int idSala;
    private String numero;

    public Sala(int idSala, String numero) {
        this.idSala = idSala;
        this.numero = numero;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public static Sala jsonToSala(JSONObject objeto) throws JSONException {
        if (objeto == null) {
            return null;
        } else {
            Sala sala = new Sala(objeto.getInt("idSala"), objeto.getString("Numero"));
            return sala;
        }
    }

    public JSONObject salaToJson() throws JSONException {
        JSONObject objeto = new JSONObject();
        objeto.put("idSala", this.getIdSala());
        objeto.put("Numero", this.getNumero());
        return objeto;
    }

}
