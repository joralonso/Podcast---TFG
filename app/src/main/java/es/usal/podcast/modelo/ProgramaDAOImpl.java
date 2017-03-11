package es.usal.podcast.modelo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;

/**
 * Implementación del patrón DAO de Programa
 * @author Jorge Alonso Merchán
 */
public class ProgramaDAOImpl implements ProgramaDAO{


    public List<Programa> getBusqueda(String query){

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/buscar/"+query));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Programa> programas = new ArrayList<Programa>();
        for (int i = 0; i < json.length(); i++){
            try {
                programas.add(new Programa(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return programas;

    }

    public List<Programa> getRelacionados(int programaId){

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/"+programaId+"/relacionados"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Programa> programas = new ArrayList<Programa>();
        for (int i = 0; i < json.length(); i++){
            try {
                programas.add(new Programa(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return programas;

    }

    @Override
    public List<Programa> getDestacados() {
        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/destacados"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Programa> programas = new ArrayList<Programa>();
        for (int i = 0; i < json.length(); i++){
            try {
                programas.add(new Programa(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return programas;
    }

    @Override
    public Programa addPrograma(String rss) {
        JSONObject json;
        try {
            json  = new JSONObject(Utils.postStringFromUrl(Constantes.ENDPOINT+"programas/", "url="+rss));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Programa programa = new Programa(json);
        return programa;
    }

    public List<Programa> getSubscripciones(String token){

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/subscripciones/", token));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Programa> programas = new ArrayList<Programa>();
        for (int i = 0; i < json.length(); i++){
            try {
                programas.add(new Programa(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return programas;

    }

    public List<Programa> getUsuarioSubscripciones(int usuarioid){

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid+"/subscripciones"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Programa> programas = new ArrayList<Programa>();
        for (int i = 0; i < json.length(); i++){
            try {
                programas.add(new Programa(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return programas;

    }

    public int addSubscripcion(String token, int podcastid){
        String r = null;
        try {
            r = Utils.postStringFromUrl(Constantes.ENDPOINT+"programas/"+podcastid, "", token);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        try{
            return Integer.parseInt(r);
        }catch (NumberFormatException ex){
            return -3;
        }
    }

    public boolean estoySubscrito(String token, int programaId){
        String r = null;
        try {
            r = Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/"+programaId+"/estoySubscrito", token);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        try{
            return Boolean.parseBoolean(r);
        }catch (Exception ex){
            return false;
        }
    }

    public int deleteSubscripcion(String token, int programaId){

        String r = null;
        try {
            r = Utils.deleteStringFromUrl(Constantes.ENDPOINT+"programas/"+programaId,token);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        try{
            return Integer.parseInt(r);
        }catch (NumberFormatException ex){
            return -3;
        }
    }
}
