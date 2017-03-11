package es.usal.podcast;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAO;
import es.usal.podcast.modelo.UsuarioDAOImpl;

public class UpdateUsuario extends AppCompatActivity {

    private EditText nombre, correo, password, password2;
    private Button button_actualizar;
    private RelativeLayout progress;
    private Usuario usuario;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_usuario);

        nombre = (EditText) findViewById(R.id.update_name);
        correo = (EditText) findViewById(R.id.update_email);
        password = (EditText) findViewById(R.id.update_password);
        password2 = (EditText) findViewById(R.id.update_password2);
        button_actualizar = (Button) findViewById(R.id.update_button);
        progress = (RelativeLayout) findViewById(R.id.progress);

        cargarUsuario();


        button_actualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(UpdateUsuario.this);
                alertDialog.setTitle("PASSWORD");
                alertDialog.setMessage("Enter Password");

                final EditText input = new EditText(UpdateUsuario.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    if (!input.getText().toString().equalsIgnoreCase(""))
                                        usuario.setPassword(Utils.SHA256(input.getText().toString()));
                                    else
                                        usuario.setPassword("");
                                    actualizarUsuario();
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        });

    }

    private void cargarUsuario(){
        progress.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
                usuario = usuarioDAO.getUsuario(settings.getString(Constantes.TOKEN, ""));
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
                nombre.setText(usuario.getNombre());
                correo.setText(usuario.getCorreo());
                progress.setVisibility(View.GONE);
            }
        }

    };

    // TODO: revisar
    private void actualizarUsuario(){
        progress.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
                if (!nombre.getText().toString().equalsIgnoreCase("")){
                    usuario.setNombre(nombre.getText().toString());
                }
                if (!correo.getText().toString().equalsIgnoreCase("")){
                    usuario.setCorreo(correo.getText().toString());
                }
                if (!password.getText().toString().equalsIgnoreCase(""))
                    if(password2.getText().toString().equalsIgnoreCase(password.getText().toString()))
                        usuario = usuarioDAO.updateUsuarioPasswordNuevo(usuario, password.getText().toString(), settings.getString(Constantes.TOKEN,""));
                    else
                        usuario = usuarioDAO.updateUsuario(usuario,settings.getString(Constantes.TOKEN, ""));
                else
                   usuario = usuarioDAO.updateUsuario(usuario,settings.getString(Constantes.TOKEN, ""));
                handler2.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler2 = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (usuario == null){
                // TOKEN INCORRECTO
                Toast.makeText(UpdateUsuario.this, "No se han podido guardar los datos, compruebe que la contraseña es correcta", Toast.LENGTH_LONG).show();
                progress.setVisibility(View.GONE);
            }else{
                // TOKEN CORRECTO
                progress.setVisibility(View.GONE);
                finish();
            }
        }

    };
}
