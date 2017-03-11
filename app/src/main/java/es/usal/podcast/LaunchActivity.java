package es.usal.podcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAOImpl;

/**
 * Primera actividad, comprobará si nuestro usuario es valido, si tenemos conexión a internet o si debemos registrarnos o iniciar sesión
 * @author Jorge Alonso Merchán
 */

public class LaunchActivity extends AppCompatActivity {

    private Usuario usuario;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        getSupportActionBar().hide();


        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int userid = settings.getInt(Constantes.USERID, -1);
        token = settings.getString(Constantes.TOKEN, "");

        if (userid > 0 && !token.equalsIgnoreCase("") ){
            if (Utils.verificaConexion(this)){
                // Si tenemos conexión a internet y tenemos guardada un token y un usuario, comprobamos que es valido
                iniciarSesion();
            }else{
                // Si tenemos un token y usuario guardado pero no tenemos conexión a internet, vamos a la pantalla principal
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }

        } else {
            // Si no tenemos guardado ningún usuario ni token, vamos a la actividad para iniciar Sesión o registrarnos
            startActivity(new Intent(this, IniciarActivity.class));
            finish();
        }

    }

    public void iniciarSesion(){

        new Thread(){
            public void run(){
                // Comprobamos el token. Si es correcto, devolverá un usuario (el nuestro)

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                usuario = new UsuarioDAOImpl().getUsuario(token);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (usuario == null){
                // TOKEN INCORRECTO

                startActivity(new Intent(getApplicationContext(), IniciarActivity.class));
                finish();
            }else{
                // TOKEN CORRECTO

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Constantes.USERID, usuario.getId());
                editor.putString(Constantes.NOMBRE, usuario.getNombre());
                editor.putString(Constantes.CORREO, usuario.getCorreo());
                editor.apply();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        }

    };
}
