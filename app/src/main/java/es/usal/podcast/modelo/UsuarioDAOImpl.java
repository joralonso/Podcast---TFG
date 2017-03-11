package es.usal.podcast.modelo;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;

/**
 * Implementación del patrón DAO de Usuario
 * @author Jorge Alonso Merchán
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    public UsuarioDAOImpl(){
        super();
    }


    @Override
    public Usuario getUsuario(String token) {
        String s = null;
        try {
            s = Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/token/"+token);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        Log.d("UsuarioDAO/getUsuario", s);
        try {
            JSONObject j = new JSONObject(s);
            Usuario u = new Usuario(j);
            if (u.getId() < 0)
                return null;
            else
                return u;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Usuario> getSubscriptores(int programaid){

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/"+programaid+"/subscriptores"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Usuario> usuarios = new ArrayList<Usuario>();
        for (int i = 0; i < json.length(); i++){
            try {
                usuarios.add(new Usuario(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return usuarios;

    }


    public Usuario getUsuario(int usuarioid){
        String temp = null;
        try {
            temp = Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        JSONObject json = null;
        String nombre, correo;
        try {
            json = new JSONObject(temp);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        try {
            nombre = json.getString("nombre");
        } catch (JSONException e) {
            e.printStackTrace();
            nombre = "";
        }

        try {
            correo = json.getString("correo");
        } catch (JSONException e) {
            e.printStackTrace();
            correo = "";
        }

        return new Usuario(nombre, correo ,usuarioid);

    }

    public Usuario updateUsuario(Usuario usuario, String token){
        try {
            usuario = new Usuario(Utils.postStringFromUrl(Constantes.ENDPOINT+"usuarios/miperfil", "nombre="+usuario.getNombre()+"&correo=&"+usuario.getCorreo()+"password="+usuario.getPassword(), token));
            return usuario;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Usuario updateUsuarioPasswordNuevo(Usuario usuario, String password, String token){
        try {
            usuario = new Usuario(Utils.postStringFromUrl(Constantes.ENDPOINT+"usuarios/miperfil", "nombre="+usuario.getNombre()+"&correo=&"+usuario.getCorreo()+"password="+usuario.getPassword()+"&passwordNuevo="+password, token));
            return usuario;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public int registrar(String nombre, String correo, String password){
        try {
            return Integer.parseInt(Utils.postStringFromUrl(Constantes.ENDPOINT+"usuarios/", "nombre="+nombre+"&password="+password+"&correo="+correo));
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (NumberFormatException e){
            e.printStackTrace();
            return -1;
        }

    }

    public String nuevoUsuarioAnonimo(){
        try {
            return Utils.postStringFromUrl(Constantes.ENDPOINT+"usuarios/anonimo", "");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public String login (String correo, String password){
        try {
            return Utils.postStringFromUrl(Constantes.ENDPOINT+"usuarios/token", "correo="+correo+"&password="+password);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int login(Context context, String correo, String password){
        String s = null;
        try {
            s = Utils.postStringFromUrl(Constantes.ENDPOINT+"usuarios/token", "correo="+correo+"&password="+password);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

        try {
            JSONObject j = new JSONObject(s);
            String token = j.getString("token");
            int userid = j.getInt("userid");
            if (token != null){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constantes.TOKEN, token);
                editor.putInt(Constantes.USERID, userid);
                editor.apply();
                return userid;
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return -1;
        }
        return -1;

    }

    @Override
    public int addSeguir(String token, int usuarioid) {
        String r = null;
        try {
            r = Utils.postStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid, "", token);
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

    @Override
    public boolean leSigo(String token, int usuarioid) {
        String r = null;
        try {
            r = Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid+"/lesigo", token);
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

    @Override
    public int deleteSeguir(String token, int usuarioid) {
        String r = null;
        try {
            r = Utils.deleteStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid, token);
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


    public List<Usuario> getSeguidores(int usuarioid){
        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid+"/seguidores"));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        List<Usuario> usuarios = new ArrayList<Usuario>();
        for (int i = 0; i < json.length(); i++){
            try {
                usuarios.add(new Usuario(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return usuarios;
    }

    public List<Usuario> getSeguidos(int usuarioid){
        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid+"/seguidos"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Usuario> usuarios = new ArrayList<Usuario>();
        for (int i = 0; i < json.length(); i++){
            try {
                usuarios.add(new Usuario(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return usuarios;
    }

    @Override
    public List<Usuario> getBusqueda(String text) {
        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/buscar/"+text));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Usuario> usuarios = new ArrayList<Usuario>();
        for (int i = 0; i < json.length(); i++){
            try {
                usuarios.add(new Usuario(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return usuarios;
    }

    public void cerrarSesion(String token){
        try {
            Utils.deleteStringFromUrl(Constantes.ENDPOINT+"usuarios/token/"+token, token);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
