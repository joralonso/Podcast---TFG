package es.usal.podcast.modelo;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase que representa los distintos programas de radios
 * @author Jorge Alonso Merchán
 */
public class Programa {

    private int id;
    private String titulo, web, descripcion, categoria, idioma;

    public Programa(JSONObject json){

        try {
            this.id = json.getInt("programaid");
        } catch (JSONException e) {
            this.id = 0;
        }
        try {
            this.titulo = json.getString("titulo");
        } catch (JSONException e) {
            this.titulo = "";
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

    public Programa (Bundle b){
        this.id = b.getInt("programa_id", 0);
        this.titulo = b.getString("programa_titulo", "");
        this.web = b.getString("programa_web", "");
        this.descripcion = b.getString("programa_descripcion", "");
        this.categoria = b.getString("programa_categoria", "");
        this.idioma = b.getString("programa_idioma", "");
    }

    public void setIntent(Intent i){
        i.putExtra("programa_id", this.id);
        i.putExtra("programa_titulo", this.titulo);
        i.putExtra("programa_web", this.web);
        i.putExtra("programa_descripcion", this.descripcion);
        i.putExtra("programa_categoria", this.categoria);
        i.putExtra("programa_idioma", this.idioma);
    }

    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getWeb() {
        return web;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getIdioma() {
        return idioma;
    }

    /**
     * Devuelve la url de la imagen (pequeña) asociada a este programa
     * @return url de la imagen
     */

    public String getImagenSmall(){
        return "http://tfgpodcast.esy.es/images/programas/small/"+this.id+".jpg";
    }

    /**
     * Devuelve la url de la imagen (grande) asociada a este programa
     * @return url de la imagen
     */
    public String getImageHeader(){
        Log.d("getImageHeader", "http://tfgpodcast.esy.es/images/programas/medium/"+this.id+".jpg");
        return "http://tfgpodcast.esy.es/images/programas/medium/"+this.id+".jpg";
    }
}
