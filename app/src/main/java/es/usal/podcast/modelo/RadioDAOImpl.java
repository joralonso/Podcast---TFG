package es.usal.podcast.modelo;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;

/**
 * Implementación del patrón DAO de Radio
 * @author Jorge Alonso Merchán
 */
public class RadioDAOImpl implements RadioDAO {

    public List<Radio> getRadios(){

        JSONArray json;
        try {
           json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"radios/"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Radio> radios = new ArrayList<Radio>();
        for (int i = 0; i < json.length(); i++){
            try {
                radios.add(new Radio(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return radios;
    }

    @Override
    public List<Radio> getBusqueda(String text) {
        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"radios/buscar/"+text));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Radio> radios = new ArrayList<Radio>();
        for (int i = 0; i < json.length(); i++){
            try {
                radios.add(new Radio(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return radios;
    }
}
