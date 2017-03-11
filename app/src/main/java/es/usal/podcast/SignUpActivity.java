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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAOImpl;

/**
 * Actividad para registrarse en el sistema
 * @author Jorge Alonso Merchán
 */

public class  SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText name, email, password, password2;
    private Button registrarse;
    private RelativeLayout progress;

    private int respuesta = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().hide();

        name = (EditText) findViewById(R.id.signup_name);
        email = (EditText) findViewById(R.id.signup_email);
        password = (EditText) findViewById(R.id.signup_password);
        password2 = (EditText) findViewById(R.id.signup_password2);
        progress = (RelativeLayout) findViewById(R.id.progress);

        registrarse = (Button) findViewById(R.id.signup_button);

        registrarse.setOnClickListener(this);

        Utils.verificaConexion(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.signup_button:

                if (!Utils.verificaConexion(getApplicationContext())){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle(getApplicationContext().getResources().getString(R.string.necesitas_internet_title));
                    builder.setMessage(getApplicationContext().getResources().getString(R.string.necesitas_internet_text))
                            .setPositiveButton(getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });
                    builder.show();
                }else if (name.getText().toString().equalsIgnoreCase(""))
                    name.setError(getResources().getString(R.string.error_field_required));
                else  if (email.getText().toString().equalsIgnoreCase(""))
                    email.setError(getResources().getString(R.string.error_field_required));
                else if (password.getText().toString().equalsIgnoreCase(""))
                    password.setError(getResources().getString(R.string.error_field_required));
                else if (password2.getText().toString().equalsIgnoreCase(""))
                    password2.setError(getResources().getString(R.string.error_field_required));
                else if (!password.getText().toString().equalsIgnoreCase(password2.getText().toString()))
                    password.setError(getResources().getString(R.string.error_pasword_not_equals));
                else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
                    email.setError(getResources().getString(R.string.error_invalid_email));
                else if (password.getText().toString().length() < 5)
                    password.setError(getResources().getString(R.string.error_invalid_password));
                else{
                    progress.setVisibility(View.VISIBLE);
                    new Thread(){
                        public void run(){

                            try{
                                String pass = Utils.SHA256(password.getText().toString());
                                UsuarioDAOImpl dao = new UsuarioDAOImpl();

                                respuesta = dao.registrar(name.getText().toString(), email.getText().toString(), pass);
                            } catch (NumberFormatException ex){
                                ex.printStackTrace();
                                respuesta = -2;
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                                respuesta = -2;
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                respuesta = -2;
                            }

                            handler.sendEmptyMessage(0);

                            }
                    }.start();
                }

                break;

        }

    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
           if (respuesta > 1){
               //startActivity(new Intent(getApplicationContext(), MainActivity.class));
               login();
           }else if (respuesta == -1){
               progress.setVisibility(View.GONE);
               AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
               builder.setMessage("Ya hay otro usuario con ese correo. Prueba a poner otro");
               builder.show();
           }else if (respuesta == -2){
               progress.setVisibility(View.GONE);
               AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
               builder.setMessage("Ha sido imposible conectar con la base de datos");
               builder.show();
           }

        }

    };


    public void login(){

        new Thread(){
            public void run(){

                String pass = "";
                try {
                    pass = Utils.SHA256(password.getText().toString());
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                UsuarioDAOImpl dao = new UsuarioDAOImpl();
                String temp = dao.login(email.getText().toString(), pass);
                if (temp.equalsIgnoreCase("")){
                    respuesta = -1;
                }else{
                    try {
                        JSONObject j = new JSONObject(temp);
                        String token = j.getString("token");
                        Usuario usuario = new Usuario(j);
                        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(Constantes.TOKEN, token);
                        editor.putInt(Constantes.USERID, usuario.getId());
                        editor.putString(Constantes.NOMBRE, usuario.getNombre());
                        editor.putString(Constantes.CORREO, usuario.getCorreo());
                        editor.apply();
                        respuesta = 1;
                    } catch (JSONException e) {
                        respuesta = -1;
                        e.printStackTrace();
                    }
                }
                handlerLogin.sendEmptyMessage(0);
            }
        }.start();
    }


    private Handler handlerLogin = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (respuesta > 0){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Toast.makeText(getApplicationContext(), "Bienvenido "+settings.getInt(Constantes.USERID, -1), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else{
                progress.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
            }

        }

    };
}
