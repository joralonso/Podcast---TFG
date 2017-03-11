package es.usal.podcast.modelo;

import android.content.res.Resources;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.usal.podcast.R;

/**
 * Clase auxiliar que une a un usuario con un capítulo y una fecha, representando cuando un usuario ha escuchado un capítulo
 * @author Jorge Alonso Merchán
 */
public class Timeline {

    private Capitulo capitulo;
    private Usuario usuario;
    private String fecha;

    public Timeline(JSONObject json){

        capitulo = new Capitulo (json);

        try {
            usuario = new Usuario(json.getString("nombre_usuario"));
        } catch (JSONException e) {
            usuario = new Usuario("");
            e.printStackTrace();
        }

        try {
            fecha = json.getString("fecha_escuchado");
        } catch (JSONException e) {
            fecha = "";
            e.printStackTrace();
        }

    }

    public String getTitulo(Resources res){
        return String.format(this.usuario.getNombre()+" "+this.getFechaHace(res));
    }

    public Capitulo getCapitulo() {
        return capitulo;
    }

    public String getNombre() {
        return usuario.getNombre();
    }

    public String getFecha() {
        return fecha;
    }

    /**
     * Devuelve la fecha en formato "Hace X minutos"
     * @param res Resources para obtener el string
     * @return String con la fecha en formato "Hace X minutos"
     */

    private String getFechaHace(Resources res){
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
