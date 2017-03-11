package es.usal.podcast.modelo;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.DBHelper;
import es.usal.podcast.Utiles.Utils;

/**
 * Implementación del patrón DAO de Capitulo
 * @author Jorge Alonso Merchán
 */

public class CapituloDAOImpl implements CapituloDAO {

    /**
     * Context para iniciar la base de datos SQLite
     */
    private Context context;

    /**
     * Constructor de CapituloDAOImp
     * @param context Context del activity donde se lanza
     */
    public CapituloDAOImpl(Context context) {
        this.context = context;
    }


    /**
     *
     * Obtiene los capítulos de un solo programa
     * @param programaId El id del programa del que se quieren los capítulos
     * @return Lista de capítulos del programa
     */
    @Override
    public List<Capitulo> getCapitulos(int programaId){

        JSONArray json;
        try {
           json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/"+programaId+"/capitulos/ultimos"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        List<Capitulo> capitulos = new ArrayList<Capitulo>();
        for (int i = 0; i < json.length(); i++){
            try {
                capitulos.add(new Capitulo(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return capitulos;

    }

    // TODO: cambiar esta api
    /**
     * Muestra los últimos capítulos escuchados por un usuario
     * @param usuarioid El id del usuario del que se desean consultar los capítulos
     * @return Lista de los últimos capítulos escuchados
     */
    @Override
    public List<Capitulo> getCapitulosEscuchados(int usuarioid){

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/"+usuarioid+"/escuchados"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Capitulo> capitulos = new ArrayList<Capitulo>();
        for (int i = 0; i < json.length(); i++){
            try {
                capitulos.add(new Capitulo(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return capitulos;

    }

    @Override
    public List<Capitulo> getCapitulosMasEscuchados(int programaId) {
        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/"+programaId+"/capitulos/masescuchados"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Capitulo> capitulos = new ArrayList<Capitulo>();
        for (int i = 0; i < json.length(); i++){
            try {
                capitulos.add(new Capitulo(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return capitulos;
    }

    /**
     * Devuelve los últimos capítulos publicados
     * @return Lista de los últimos capítulos
     */
    @Override
    public List<Capitulo> getUltimosCapitulos(){

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/capitulos/ultimos"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Capitulo> capitulos = new ArrayList<Capitulo>();
        for (int i = 0; i < json.length(); i++){
            try {
                capitulos.add(new Capitulo(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return capitulos;

    }

    /**
     * Devuelve los últimos capítulos de las subscripciones del usuario de la app
     * @param token Access token del usuario
     * @return Lista de los últimos capítulos de las subscripciones del usuario
     */
    @Override
    public List<Capitulo> getUltimosCapitulosSubscripciones(String token){

        //SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        //String token = settings.getString("token", "");

        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/capitulos/subscripciones", token));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Capitulo> capitulos = new ArrayList<Capitulo>();
        for (int i = 0; i < json.length(); i++){
            try {
                capitulos.add(new Capitulo(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return capitulos;

    }


    // TODO: cambiar api
    /**
     * Devuelve los últimos capítulos escuchados por los usuarios que sigue el usuario de la app
     * @param token Access token del usuario
     * @return Lista de usuarios y capítulos
     */
    @Override
    public List<Timeline> getTimeline(String token) {
        JSONArray json;
        try {
            String s =  Utils.getStringFromUrl(Constantes.ENDPOINT+"usuarios/timeline", token);
            Log.d("UsuarioDAOImpl", ""+s);
            json  = new JSONArray(s);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Timeline> timelines = new ArrayList<Timeline>();
        for (int i = 0; i < json.length(); i++){
            try {
                timelines.add(new Timeline(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return timelines;
    }

    /**
     * Devuelve los capítulos descargados en el móvil, almacenados en la base de datos local de la aplicación
     * @return Lista de capítulos descargados en el móvil
     */

    @Override
    public List<Capitulo> getDescargas() {

        DBHelper db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(" SELECT capituloid, duracion, podcastid, titulo, url, descripcion, fecha, programaTitulo FROM descargas ", null);
        List<Capitulo> capitulos = new ArrayList<Capitulo>();

        if (c.moveToFirst()) {
            do {
                capitulos.add(new Capitulo(c.getInt(0), c.getInt(1)*60, c.getInt(2), c.getString(3), c.getString(4), c.getString(5), c.getString(6), c.getString(7)));
            } while(c.moveToNext());
        }
        sqLiteDatabase.close();
        return capitulos;
    }


    /**
     * Busca capítulos con un titulo similar al texto proporcionado
     * @param text String de lo que se quiere buscar
     * @return Capítulos con un titulo similar al texto
     */
    @Override
    public List<Capitulo> getBusqueda(String text) {
        JSONArray json;
        try {
            json  = new JSONArray(Utils.getStringFromUrl(Constantes.ENDPOINT+"programas/capitulos/buscar/"+text));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Capitulo> capitulos = new ArrayList<Capitulo>();
        for (int i = 0; i < json.length(); i++){
            try {
                capitulos.add(new Capitulo(json.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return capitulos;
    }

    /**
     * Marca como escuchado un capítulo
     * @param token Access token del usuario
     * @param capituloid La id del capítulo que se quiere marcar como escuchado
     * @return
     */
    @Override
    public int addCapituloEscuchado(String token, int capituloid){

        try{
            String temp = Utils.postStringFromUrl(Constantes.ENDPOINT+"programas/capitulos/"+capituloid+"/escuchado", "", token);
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;

    }

    /**
     * Actualiza la posición por la que se va escuchando un capítulo
     * @param capituloid La id del capítulo del que se quiere guardar la posición
     * @param posicion Posición por donde se va escuchando el capítulo
     * @return
     */

    @Override
    public int updateTiempoEscuchado(int capituloid, int posicion){

        DBHelper db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();

        String sql = "INSERT OR REPLACE INTO escuchando VALUES ("+capituloid+", "+posicion+");";

        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 1;
        
    }

    /**
     * Guarda un capítulo en la tabla de Descargas
     * @param capitulo Capítulo que se desea guardar
     * @return
     */

    @Override
    public int addDescarga(Capitulo capitulo) {

        DBHelper db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        String sql = "INSERT INTO descargas (capituloid, podcastnombre, podcastid , titulo , descripcion , url , duracion ,  fecha, programaTitulo) VALUES ("+
                capitulo.getId()+", '"+capitulo.getProgramaTitulo()+"',  '"+capitulo.getPodcastid()+"',  '"+capitulo.getTitulo()+"',  '"+capitulo.getDescripcion()+"',  '"+capitulo.getUrl()+"',  "+capitulo.getDuracion()+",  '"+capitulo.getFecha()+"', '"+capitulo.getProgramaTitulo()+"')";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 1;
    }

    /**
     * Borra un capítulo de la tabla de Descargas
     * @param capituloid La id del capítulo que se desea borrar
     * @return
     */
    @Override
    public int deleteDescarga(int capituloid) {

        DBHelper db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        String sql = "DELETE FROM descargas WHERE capituloid = "+capituloid;
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();
        return 0;
    }

    /**
     * Borra todas las descargas de la base de datos y del dispostivo
     */
    @Override
    public void deleteDescargas() {

        DBHelper db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getWritableDatabase();
        String sql = "DELETE FROM descargas ";
        sqLiteDatabase.execSQL(sql);
        sqLiteDatabase.close();

        // Borramos todos los archivos
        File dir = new File(Environment.getExternalStorageDirectory()+Constantes.path);
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }

        return;
    }

    /**
     * Obtiene la última posición guardada de por donde se está escuchando un capítulo
     * @param capituloid LA id del capítulo del que se quiere consultar la última posicion
     * @return La posición guardada
     */

    @Override
    public int getTiempoEscuchado(int capituloid){

        int posicion = 0;

        DBHelper db = new DBHelper(context);
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();

        Cursor c = sqLiteDatabase.rawQuery("SELECT posicion FROM escuchando WHERE capituloid = "+capituloid+" LIMIT 1; ", null);

        if (c.moveToFirst()) {
            do {
                posicion = c.getInt(0);
            } while(c.moveToNext());
        }

        sqLiteDatabase.close();
        return posicion;
    }

}
