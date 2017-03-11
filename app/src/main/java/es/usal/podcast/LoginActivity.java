package es.usal.podcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
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
 * Actividad para Iniciar Sesión
 * @author Jorge Alonso Merchán
 */
public class LoginActivity extends AppCompatActivity{

    private EditText mEmailView;
    private EditText mPasswordView;
    private RelativeLayout progress;
    private View mLoginFormView;
    int respuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        progress = (RelativeLayout) findViewById(R.id.progress);
        Log.d("INICIAR SESION", "INICIAR SESION");

        Utils.verificaConexion(this);

        Button iniciarSesionButton = (Button) findViewById(R.id.iniciar_sesion_button);
        iniciarSesionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("INICIAR SESION", "INICIAR SESION");
                login();

            }
        });
    }

    public void login(){

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailView.getText()).matches())
            mEmailView.setError(getResources().getString(R.string.error_invalid_email));
        else if (mPasswordView.getText().toString().length() < 5)
            mPasswordView.setError(getResources().getString(R.string.error_invalid_password));
        else {

            progress.setVisibility(View.VISIBLE);
            new Thread() {
                public void run() {
                    String pass = "";
                    try {
                        pass = Utils.SHA256(mPasswordView.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    UsuarioDAOImpl dao = new UsuarioDAOImpl();
                    String temp = dao.login(mEmailView.getText().toString(), pass);
                    Log.d("LoginActivity", temp);
                    if (temp.equalsIgnoreCase("")) {
                        respuesta = -1;
                    } else {
                        try {
                            JSONObject j = new JSONObject(temp);
                            String token = j.getString("token");
                            Log.d("TOKEN", token);
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
                    handler.sendEmptyMessage(0);
                }
            }.start();
        }
    }


    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (respuesta > 0){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Toast.makeText(LoginActivity.this, "Bienvenido "+settings.getInt(Constantes.USERID, -1), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }else{
                progress.setVisibility(View.GONE);
                Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }

        }

    };

}

