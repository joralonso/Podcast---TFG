package es.usal.podcast.modelo;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.usal.podcast.R;
import es.usal.podcast.Utiles.Constantes;

/**
 * Clase que representa un capítulo que pertenece a un programa
 * @author Jorge Alonso Merchán
 */
public class Capitulo {

    private int id, duracion, podcastid;
    private String titulo, url, descripcion, fecha, programaTitulo;

    public Capitulo(JSONObject json){

        try {
            this.id = json.getInt("capituloid");
        } catch (JSONException e) {
            this.id = 0;
        }
        try {
            this.podcastid = json.getInt("programaid");
        } catch (JSONException e) {
            this.podcastid = 0;
        }
        try {
            this.duracion = json.getInt("duracion");
        } catch (JSONException e) {
            this.duracion = 0;
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
            this.descripcion = json.getString("descripcion");
        } catch (JSONException e) {
            this.descripcion = "";
        }
        try {
            this.fecha = json.getString("fecha");
        } catch (JSONException e) {
            this.fecha = "";
        }
        try {
            this.programaTitulo = json.getString("programaTitulo");
        } catch (JSONException e) {
            this.programaTitulo = "";
        }

    }

    public Capitulo(int id, int _duracion, int podcastid, String titulo, String url, String descripcion, String fecha, String programaTitulo) {
        this.id = id;
        this.duracion = _duracion;
        Log.d("DURACION", ""+_duracion);
        this.podcastid = podcastid;
        this.titulo = titulo;
        this.url = url;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.programaTitulo = programaTitulo;
    }

    public Capitulo (Bundle b){
        this.id = b.getInt("capitulo_id", 0);
        this.duracion = b.getInt("capitulo_duracion", 0);
        this.podcastid = b.getInt("capitulo_podcastid", 0);

        this.titulo = b.getString("capitulo_titulo", "");
        this.url = b.getString("capitulo_url", "");
        this.descripcion = b.getString("capitulo_descripcion", "");
        this.fecha = b.getString("capitulo_fecha", "");
        this.programaTitulo = b.getString("capitulo_programaTitulo", "");
    }

    public void setIntent(Intent i){
        i.putExtra("capitulo_id", this.id);
        i.putExtra("capitulo_duracion", this.duracion);
        i.putExtra("capitulo_podcastid", this.podcastid);

        i.putExtra("capitulo_titulo", this.titulo);
        i.putExtra("capitulo_url", this.url);
        i.putExtra("capitulo_descripcion", this.descripcion);
        i.putExtra("capitulo_fecha", this.fecha);
        i.putExtra("capitulo_programaTitulo", this.programaTitulo);
    }


    public int getId() {
        return id;
    }

    public int getDuracion() {
        return duracion / 60;
    }

    public String getTitulo() {
        return titulo.replace("'", "");
    }

    public String getUrl() {
        return url;
    }

    public String getDescripcion() {
        return descripcion.replace("'", "");
    }

    public String getFecha() {
        return fecha;
    }

    public int getPodcastid() {
        return podcastid;
    }

    public String getProgramaTitulo() {
        return programaTitulo;
    }

    public String getImagenSmall(){
        return "http://tfgpodcast.esy.es/images/programas/small/"+this.podcastid+".jpg";
    }

    public String fileName(){
        return Constantes.path+id+".mp3";
    }

    public boolean estaDescargado(){
        Log.d("FILENAME", ""+this.fileName());
        File file = new File(this.fileName());
        Log.d("EXISTE", ""+file.exists());
        return file.exists();
    }

    public String getFechaHace(Resources res){
        Date date;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(fecha);
        } catch (ParseException e) {
            e.printStackTrace();
            return fecha;
        }

        long s = System.currentTimeMillis();
        s = s - date.getTime();
        s /= 1000;

        int temp;

        if (s < 60*60){

            temp = (int) s / (60);
            if (temp == 1)
                return String.format(res.getString(R.string.hace_minutos), 1);
            else
                return String.format(res.getString(R.string.hace_minutos), temp);
        }else if (s < 60*60*24){

            temp = (int) s / (60*60);
            if (temp == 1)
                return String.format(res.getString(R.string.hace_hora), 1);
            else
                return String.format(res.getString(R.string.hace_horas), temp);
        }else if (s < 60*60*24*30){

            temp = (int) s /  (60*60*24);
            if (temp == 1)
                return String.format(res.getString(R.string.hace_dia), 1);
            else
                return String.format(res.getString(R.string.hace_dias), temp);
        }else{

            temp = (int) s /  (60*60*24*30);
            if (temp == 1)
                return String.format(res.getString(R.string.hace_mes), 1);
            else
                return String.format(res.getString(R.string.hace_meses), temp);
        }
    }

}
