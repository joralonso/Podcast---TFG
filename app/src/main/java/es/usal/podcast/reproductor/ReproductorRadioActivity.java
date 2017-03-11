package es.usal.podcast.reproductor;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import es.usal.podcast.IniciarActivity;
import es.usal.podcast.R;
import es.usal.podcast.SearchActivity;
import es.usal.podcast.SettingsActivity;
import es.usal.podcast.UsuarioActivity;
import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.modelo.Radio;

public class ReproductorRadioActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView bplay, bstop;
    private TextView titulo;
    private ImageView imagen;
    private Radio radio;
    private ProgressBar pb;

    private int playerStatus;
    private ReproductorService reproductorService;
    private UiRefresher uiRefresher;
    private ServiceConnection playerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            Log.d("ReproductorActivity", "Service");
            final ReproductorService.PlayerBinder playerBinder = (ReproductorService.PlayerBinder)service;
            reproductorService = playerBinder.getService();
            playRadio();
            uiRefresher = new UiRefresher();
            (new Thread(uiRefresher)).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor_radio);

        radio = new Radio(this.getIntent().getExtras());
        startService(new Intent(this, ReproductorService.class));

        bplay = (ImageView) findViewById(R.id.reproductor_play);
        bstop = (ImageView) findViewById(R.id.reproductor_stop);
        imagen = (ImageView) findViewById(R.id.reproductor_imagen);
        titulo = (TextView) findViewById(R.id.reproductor_titulo);
        pb = (ProgressBar) findViewById(R.id.reproductor_progress);

        bplay.setOnClickListener(this);
        bstop.setOnClickListener(this);

        Picasso.with(this).load(radio.getImagenSmall()).into(imagen);
        titulo.setText(radio.getTitulo());

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.reproductor_play:

                if (reproductorService != null){
                    if (reproductorService.getRadio() == null)
                        reproductorService.setRadio(radio);

                    reproductorService.playRadio();
                }


                break;

            case R.id.reproductor_nosound:


                break;

            case R.id.reproductor_stop:

                reproductorService.stop();
                finish();
                break;

            case R.id.reproductor_forward:

                break;

            case R.id.reproductor_replay:

                break;

        }

    }




    private void mostrarDialogoNoInternet(){

        Toast.makeText(this, "No hay internet", Toast.LENGTH_SHORT).show();

    }

    public void setEscuchado(){

    }



    private class UiRefresher implements Runnable {

        private boolean done = false;

        public void done() {
            done = true;
        }

        @Override
        public void run() {

            while (!done) {
                synchronized (reproductorService) {
                    playerStatus = reproductorService.getStatus();
                    refreshButtons();
                    reproductorService.take();
                    try {
                        reproductorService.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("START", "START");
        Intent playerServiceIntent = new Intent(this, ReproductorService.class);
        getApplicationContext().bindService(playerServiceIntent, playerServiceConnection, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (reproductorService != null) {
            synchronized (reproductorService) {
                reproductorService.notifyAll();
                uiRefresher.done();
            }
        }
        getApplicationContext().unbindService(playerServiceConnection);
    }


    /**
     * Actualiza el botón de play según se esté reproduciendo o no algo
     */
    private void refreshButtons() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                switch (playerStatus) {
                    case ReproductorService.PLAYING:
                        bplay.setImageResource(R.drawable.ic_pause);
                        break;
                    default:
                        bplay.setImageResource(R.drawable.ic_play);
                        break;
                }

            }
        });
    }


    private void playRadio(){
        pb.setVisibility(View.VISIBLE);
        bplay.setVisibility(View.GONE);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (!pref.getBoolean("pref_wifi", true) || Utils.conectadoPor(getApplicationContext()) == 2) {

            if (reproductorService.getRadio() == null || reproductorService.getRadio().getId() != radio.getId()) {

                new Thread() {
                    public void run() {
                        synchronized (reproductorService) {
                            reproductorService.newPlayRadio(radio);
                        }
                        handler.sendEmptyMessage(0);
                    }
                }.start();

            } else {

                handler.sendEmptyMessage(0);
            }

        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(ReproductorRadioActivity.this);

            if (Utils.conectadoPor(getApplicationContext()) == 0) {
                builder.setMessage(R.string.nointernet);
            }else {
                builder.setMessage(R.string.nowifi);
                builder.setNegativeButton(R.string.abriropciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    }
                });
            }

            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.show();
        }

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            pb.setVisibility(View.GONE);
            bplay.setVisibility(View.VISIBLE);

            setEscuchado();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                MenuItemCompat.collapseActionView(searchItem);
                Intent i = new Intent(getApplicationContext(), SearchActivity.class);
                i.putExtra("query", query);
                startActivity(i);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){

            case R.id.action_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;

            case R.id.action_cerrar_sesion:
                Utils.cerrarSesion(getBaseContext());
                startActivity(new Intent(getApplicationContext(), IniciarActivity.class));
                finish();
                break;

            case R.id.action_mi_perfil:
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                Intent i = new Intent(getApplicationContext(), UsuarioActivity.class);
                i.putExtra("usuarioid", settings.getInt(Constantes.USERID, -1));
                i.putExtra("nombre", settings.getString(Constantes.NOMBRE, ""));
                startActivity(i);
                break;

            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

}
