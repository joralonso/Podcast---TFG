package es.usal.podcast.modelo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase que representa los distintos usuarios del programa
 * @author Jorge Alonso Merchán
 */
public class Usuario {

    private String nombre, password, correo;
    private int id;

    public Usuario(String nombre) {
        this.nombre = nombre;
    }

    public Usuario (String nombre, String correo, int id){
        this.nombre = nombre;
        this.correo = correo;
        this.id = id;
    }

    public Usuario(String nombre, String password, String correo, int id) {
        this.nombre = nombre;
        this.password = password;
        this.correo = correo;
        this.id = id;
    }


    public Usuario(String nombre, String correo, String password) {
        this.nombre = nombre;
        this.password = password;
        this.correo = correo;
    }

    public Usuario(JSONObject json) {

        try {
            this.id = json.getInt("usuarioid");
        } catch (JSONException e) {
            this.id = 0;
        }

        try {
            this.nombre = json.getString("nombre");
        } catch (JSONException e) {
            this.nombre = "";
        }

        try {
            this.correo = json.getString("correo");
        } catch (JSONException e) {
            this.correo = "";
        }

    }

    public String getNombre() {
        return nombre;
    }

    public String getPassword() {
        return password;
    }

    public String getCorreo() {
        return correo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}
