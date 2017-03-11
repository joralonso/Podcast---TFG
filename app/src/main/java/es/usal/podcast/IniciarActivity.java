package es.usal.podcast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAOImpl;

/**
 * Actividad que te permite acceder a la pantalla para iniciar sesión, para registrate como usuario o para utilizar la aplicación como un usuario anónimo
 * @author Jorge Alonso Merchán
 */

public class IniciarActivity extends AppCompatActivity implements View.OnClickListener{


    int respuesta;
    Usuario usuario;
    String token;
    private RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar);
        getSupportActionBar().hide();

        ((Button) findViewById(R.id.iniciar_sesion_button)).setOnClickListener(this);
        ((Button) findViewById(R.id.registrar_button)).setOnClickListener(this);
        ((Button) findViewById(R.id.no_registrar_button)).setOnClickListener(this);
        progress = (RelativeLayout) findViewById(R.id.progress);

    }

    @Override
    public void onClick(View v) {

        int id = v.getId();

        switch (id){

            case R.id.iniciar_sesion_button:
                startActivity(new Intent(IniciarActivity.this, LoginActivity.class));
                break;

            case R.id.registrar_button:
                startActivity(new Intent(IniciarActivity.this, SignUpActivity.class));
                break;

            case R.id.no_registrar_button:
                nuevoUsuarioAnonimo();
                break;

        }

    }


    public void nuevoUsuarioAnonimo(){

        progress.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                UsuarioDAOImpl dao = new UsuarioDAOImpl();
                token = dao.nuevoUsuarioAnonimo();
                usuario = new UsuarioDAOImpl().getUsuario(token);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            progress.setVisibility(View.GONE);
            if (usuario == null){
                // TOKEN INCORRECTO

                AlertDialog.Builder builder = new AlertDialog.Builder(IniciarActivity.this);
                builder.setTitle(getResources().getString(R.string.error_iniciar_title));
                builder.setMessage(getResources().getString(R.string.error_iniciar_text))
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();

            }else{

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt(Constantes.USERID, usuario.getId());
                editor.putString(Constantes.TOKEN, token);
                editor.putString(Constantes.NOMBRE, usuario.getNombre());
                editor.putString(Constantes.CORREO, usuario.getCorreo());
                editor.apply();

                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }

        }

    };

}
