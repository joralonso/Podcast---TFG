package es.usal.podcast.Utiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.usal.podcast.R;
import es.usal.podcast.modelo.UsuarioDAO;
import es.usal.podcast.modelo.UsuarioDAOImpl;

/**
 * Colección de métodos útiles
 * @author Jorge Alonso Merchán
 */
public class Utils {

    /**
     * Comprueba si estamos conectados a internet
     * @param ctx Contexto
     * @return true si estamos conectados internet, false si no lo estamos
     */
    public static boolean verificaConexion(Context ctx) {
        boolean bConectado = false;
        ConnectivityManager connec = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // No sólo wifi, también GPRS
        NetworkInfo[] redes = connec.getAllNetworkInfo();
        // este bucle debería no ser tan ñapa
        for (int i = 0; i < 2; i++) {
            // ¿Tenemos conexión? ponemos a true
            if (redes[i].getState() == NetworkInfo.State.CONNECTED) {
                bConectado = true;
            }
        }
        return bConectado;
    }


    /**
     * Dada una url devuelve un Bitmap
     * @param src URL de donde se encuentra la imagen
     * @return Bitmap
     */

    public static Bitmap getBitmapFromURL(String src) {
        Log.d("getBitmapFromURL", src);
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
            return null;
        }
    }

    public static void cerrarSesion(Context context){

        final SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = settings.edit();
        final String token = settings.getString(Constantes.TOKEN, "");
        new Thread(){
            public void run(){
                UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
                usuarioDAO.cerrarSesion(token);
            }
        }.start();


        editor.putInt(Constantes.USERID, -1);
        editor.putString(Constantes.NOMBRE, "");
        editor.putString(Constantes.CORREO, "");
        editor.putString(Constantes.TOKEN, "");
        editor.apply();
    }

    /**
     * Comprueba si estamos conectados a internet a tarvés de wifi o de red móvil
     * @param c
     * @return 1 si estamos conectados a través de red móvil, 2 si estamos conectados a traves de wifi y 0 si no estamos conectados
     */

    public static int conectadoPor(Context c){

        ConnectivityManager conMan = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State internet_movil = conMan.getNetworkInfo(0).getState();
        NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState();
        if (internet_movil == NetworkInfo.State.CONNECTED || internet_movil == NetworkInfo.State.CONNECTING) {
            return 1;
        } else if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
            return 2;
        } else {
            return 0;
        }

    }

    public static void avisoInternet(Context context){
        if (!verificaConexion(context)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getResources().getString(R.string.necesitas_internet_title));
            builder.setMessage(context.getResources().getString(R.string.necesitas_internet_text))
                    .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            builder.show();
        }
    }

    /*
    http://stackoverflow.com/questions/5980658/how-to-sha1-hash-a-string-in-android
     */

    /**
     * Dado una contraseña devolverá el hash (codificado en SHA-256) de la contraseña junto con un salt para aumentar la seguridad
     * @param text Contraseña a codificar
     * @return Contraseña codificada
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */

    public static String SHA256(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        text = "usal"+text;
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(text.getBytes("iso-8859-1"), 0, text.length());
        byte[] sha1hash = md.digest();

        StringBuilder buf = new StringBuilder();
        for (byte b : sha1hash) {
            int halfbyte = (b >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                buf.append((0 <= halfbyte) && (halfbyte <= 9) ? (char) ('0' + halfbyte) : (char) ('a' + (halfbyte - 10)));
                halfbyte = b & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    /**
     * Dado una URL devuelve un String del contenido de esa URL
     * @param _url URL a la que queremos acceder
     * @return String del contenido de la URL
     */

    public static String getStringFromUrl(String _url) throws IOException {
        String respuesta = "";
        URL url = new URL(_url);
        Log.d("getStringFromUrl", _url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();

        InputStream is = conn.getInputStream();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        respuesta = responseStrBuilder.toString();
        return respuesta;
    }

    /**
     * Dado una URL devuelve un String del contenido de esa URL con un token de autorización
     * @param _url URL a la que queremos acceder
     * @param token token de autorización
     * @return String del contenido de la URL
     */

    public static String getStringFromUrl(String _url, String token) throws IOException {
        String respuesta = "";
        URL url = new URL(_url);
        Log.d("getStringFromUrl", _url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty ("Authorization", token);
        conn.connect();

        InputStream is = conn.getInputStream();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        respuesta = responseStrBuilder.toString();
        return respuesta;
    }

    /**
     * Envia unos datos a una URL por medio de POST con un token de autorización
     * @param _url URL a la que queremos acceder
     * @param urlParameters parámetros que queremos enviar
     * @param token token de autorización
     * @return el String que devuelve la URL
     */

    public static String postStringFromUrl(String _url, String urlParameters, String token) throws IOException {
        String respuesta = "";
        byte[] postData       = urlParameters.getBytes();
        int    postDataLength = postData.length;
        URL url = new URL(_url);
        Log.d("postStringFromUrl2", _url);
        Log.d("TOKEN", token);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty ("Authorization", token);
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(postData);
        out.flush();
        out.close();

        InputStream is = conn.getInputStream();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        respuesta = responseStrBuilder.toString();

        Log.d("respuesta",respuesta);
        return respuesta;
    }


    /**
     * Envia unos datos a una URL por medio de POST
     * @param _url URL a la que queremos acceder
     * @param urlParameters parámetros que queremos enviar
     * @return el String que devuelve la URL
     */

    public static String postStringFromUrl(String _url, String urlParameters) throws IOException {
        String respuesta = "";
        byte[] postData       = urlParameters.getBytes();
        int    postDataLength = postData.length;
        URL url = new URL(_url);
        Log.d("postStringFromUrl1", _url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "POST" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(postData);
        out.flush();
        out.close();

        InputStream is = conn.getInputStream();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        respuesta = responseStrBuilder.toString();
        Log.d("respuesta",respuesta);
        return respuesta;
    }

    public static String deleteStringFromUrl(String _url, String token) throws IOException {
        String respuesta = "";
        byte[] postData       = "".getBytes();
        int    postDataLength = postData.length;
        URL url = new URL(_url);
        Log.d("deleteStringFromUrl", _url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty ("Authorization", token);
        conn.setDoOutput( true );
        conn.setInstanceFollowRedirects( false );
        conn.setRequestMethod( "DELETE" );
        conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty( "charset", "utf-8");
        conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
        conn.setUseCaches( false );
        OutputStream out = new BufferedOutputStream(conn.getOutputStream());
        out.write(postData);
        out.flush();
        out.close();

        InputStream is = conn.getInputStream();
        BufferedReader streamReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();

        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);

        respuesta = responseStrBuilder.toString();
        Log.d("respuesta",respuesta);
        return respuesta;
    }

}
