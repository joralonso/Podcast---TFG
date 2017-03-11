package es.usal.podcast;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.Toast;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAOImpl;

/**
 * Actividad de opciones
 * @author Jorge Alonso Merchán
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

        // Botón de borrar todas las descargas
        Preference button = (Preference)findPreference("borrar_descargas");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                CapituloDAO capituloDAO = new CapituloDAOImpl(SettingsActivity.this);
                capituloDAO.deleteDescargas();
                Toast.makeText(SettingsActivity.this,"Descargas borradas", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        Preference button_actualiza = (Preference)findPreference("actualizar_datos");
        button_actualiza.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                startActivity(new Intent(SettingsActivity.this, UpdateUsuario.class));
                return true;
            }
        });

        setupActionBar();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        guardarDatos();


    }

    private void guardarDatos(){
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final String token = settings.getString(Constantes.TOKEN, "");
        final int usuarioid = settings.getInt(Constantes.USERID, 0);
        String nombre = settings.getString(Constantes.NOMBRE, "");
        String correo = settings.getString(Constantes.CORREO, "");

        final Usuario u = new Usuario(nombre, correo, usuarioid);
        new Thread(){
            public void run(){
                if (usuarioid > 0)
                    new UsuarioDAOImpl().updateUsuario(u, token);
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){

            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
