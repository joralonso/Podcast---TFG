package es.usal.podcast.modelo;

import android.content.Intent;
import android.os.Bundle;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase que representa las distintas radios online que podemos escuchar
 * @author Jorge Alonso Merchán
 */
public class Radio {

    private String titulo, url, categoria, idioma, descripcion, web;
    private int id;

    public Radio(JSONObject json){

        try {
            this.id = json.getInt("radioid");
        } catch (JSONException e) {
            this.id = 0;
        }
        try {
            this.titulo = json.getString("titulo");
        } catch (JSONException e) {
            this.titulo = "";
        }
        try {
            this.url = json.getString("url");
        } catch (JSONException e) {
            this.url = "";
        }
        try {
            this.web = json.getString("web");
        } catch (JSONException e) {
            this.web = "";
        }
        try {
            this.descripcion = json.getString("descripcion");
        } catch (JSONException e) {
            this.descripcion = "";
        }
        try {
            this.categoria = json.getString("categoria");
        } catch (JSONException e) {
            this.categoria = "";
        }
        try {
            this.idioma = json.getString("idioma");
        } catch (JSONException e) {
            this.idioma = "";
        }

    }

    public Radio (Bundle b){
        this.id = b.getInt("radio_id", 0);
        this.titulo = b.getString("radio_titulo", "");
        this.web = b.getString("radio_web", "");
        this.descripcion = b.getString("radio_descripcion", "");
        this.categoria = b.getString("radio_categoria", "");
        this.idioma = b.getString("radio_idioma", "");
        this.url = b.getString("radio_url", "");
    }

    public void setIntent(Intent i){
        i.putExtra("radio_id", this.id);
        i.putExtra("radio_titulo", this.titulo);
        i.putExtra("radio_web", this.web);
        i.putExtra("radio_descripcion", this.descripcion);
        i.putExtra("radio_categoria", this.categoria);
        i.putExtra("radio_idioma", this.idioma);
        i.putExtra("radio_url", this.url);
    }

    public String getTitulo() {
        return titulo;
    }

    public String getUrl() {
        return url;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getIdioma() {
        return idioma;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getWeb() {
        return web;
    }

    public int getId() {
        return id;
    }

    public String getImagenSmall(){
        return "http://tfgpodcast.esy.es/images/radios/small/"+this.id+".jpg";
    }
}
