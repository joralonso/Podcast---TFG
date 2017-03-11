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
import android.widget.SeekBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import es.usal.podcast.IniciarActivity;
import es.usal.podcast.R;
import es.usal.podcast.SearchActivity;
import es.usal.podcast.SettingsActivity;
import es.usal.podcast.UsuarioActivity;
import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;

public class ReproductorActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView bnosound, bplay, bstop, bReplay, bForward;
    private SeekBar seek;
    private TextView titulo, subtitulo;
    private ImageView imagen;
    private Capitulo capitulo;
    private Boolean descargado;
    private ProgressBar pb;

    private Thread t;

    private Timer progressRefresher;
    private int playerStatus;
    private ReproductorService reproductorService;
    private RefrescarUI uiRefresher;
    private ServiceConnection playerServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            Log.d("ReproductorActivity", "Service");

            // Obtenemos el servicio

            final ReproductorService.PlayerBinder playerBinder = (ReproductorService.PlayerBinder)service;
            reproductorService = playerBinder.getService();

            if (reproductorService.getStatus() == ReproductorService.STOPED || reproductorService.getCapitulo() == null){

                // Si no hay ningún capítulo reproduciendose, reproducimos el capítulo

                playCapitulo();
            } else if (reproductorService.getCapitulo().getId() != capitulo.getId()){

                // Si ya hay algo reproduciendose, que no sea este capítulo, preguntamos

                AlertDialog.Builder builder = new AlertDialog.Builder(ReproductorActivity.this);
                builder.setMessage("¿Deseas remplazar el capítulo que se está escuchando?")
                        .setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                playCapitulo();
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                capitulo = reproductorService.getCapitulo();
                                Picasso.with(ReproductorActivity.this).load(capitulo.getImagenSmall()).into(imagen);
                                titulo.setText(capitulo.getProgramaTitulo());
                                subtitulo.setText(capitulo.getTitulo());
                            }
                        });
                builder.show();
            }

            seek.setMax(reproductorService.getDuration());
            uiRefresher = new RefrescarUI();
            (new Thread(uiRefresher)).start();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reproductor);

        capitulo = new Capitulo(this.getIntent().getExtras());
        descargado = capitulo.estaDescargado();
        getSupportActionBar().setHomeButtonEnabled(true);


        bnosound = (ImageView) findViewById(R.id.reproductor_nosound);
        bplay = (ImageView) findViewById(R.id.reproductor_play);
        bstop = (ImageView) findViewById(R.id.reproductor_stop);
        bReplay = (ImageView) findViewById(R.id.reproductor_replay);
        bForward = (ImageView) findViewById(R.id.reproductor_forward);
        imagen = (ImageView) findViewById(R.id.reproductor_imagen);
        titulo = (TextView) findViewById(R.id.reproductor_titulo);
        subtitulo = (TextView) findViewById(R.id.reproductor_subtitulo);
        seek = (SeekBar) findViewById(R.id.reproductor_seek);
        pb = (ProgressBar) findViewById(R.id.reproductor_progress);

        bnosound.setOnClickListener(this);
        bplay.setOnClickListener(this);
        bstop.setOnClickListener(this);
        bReplay.setOnClickListener(this);
        bForward.setOnClickListener(this);

        startService(new Intent(this, ReproductorService.class));

        if (descargado){
            bnosound.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete));
        }


        Picasso.with(this).load(capitulo.getImagenSmall()).into(imagen);
        titulo.setText(capitulo.getProgramaTitulo());
        subtitulo.setText(capitulo.getTitulo());

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (reproductorService != null)
                    reproductorService.goTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }
        });

        progressRefresher = new Timer();
        progressRefresher.schedule(new TimerTask() {

            @Override
            public void run() {
                if (playerStatus == ReproductorService.PLAYING) {
                    refreshProgress();
                }
            }
        }, 0, 500);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){

            case R.id.reproductor_play:

                if (reproductorService != null){
                    if (reproductorService.getCapitulo() == null)
                        reproductorService.setCapitulo(capitulo);

                    reproductorService.play();
                }


                break;

            case R.id.reproductor_nosound:

                if (descargado){
                    borrarAudio();
                }

                break;

            case R.id.reproductor_stop:

                reproductorService.stop();
                stopService(new Intent(this, ReproductorService.class));
                finish();
                break;

            case R.id.reproductor_forward:
                // Avanzamos 30 segundos
                if (reproductorService.getDuration() > reproductorService.getCurrentTrackProgress() + 30000)
                    reproductorService.goTo(seek.getProgress()+30);
                break;

            case R.id.reproductor_replay:
                // Retrocedemos 30 segundos
                if ( reproductorService.getCurrentTrackProgress() - 30000 > 0)
                    reproductorService.goTo(seek.getProgress()-30000);
                else
                    reproductorService.goTo(0);
                break;

        }

    }

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

    /**
     * Borra el audio que se está reproduciendo, en caso de que esté descargado
     */

    private void borrarAudio(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ReproductorActivity.this);
        builder.setMessage(R.string.borrar_capitulo);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                File f = new File(capitulo.fileName());
                f.delete();
                CapituloDAO dao = new CapituloDAOImpl(getBaseContext());
                dao.deleteDescarga(capitulo.getId());
                finish();
            }
        });builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();

    }



    private class RefrescarUI implements Runnable {

        private boolean done = false;

        public void done() {
            done = true;
        }

        @Override
        public void run() {

            while (!done) {
                synchronized (reproductorService) {
                    playerStatus = reproductorService.getStatus();
                    refreshProgress();
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
        if (progressRefresher != null){
            progressRefresher.cancel();
        }

        //getApplicationContext().unbindService(playerServiceConnection);
    }


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

    /**
     * Actualiza la progress bar con la posición actual del tiempo
     */
    private void refreshProgress() {

        final int progress = reproductorService.getCurrentTrackProgress();
        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                seek.setProgress(progress);
            }
        });
    }


    /**
     * Método que comprueba si se puede reproducir el capítulo, y en caso afirmativo empieza la reproducción
     */
    private void playCapitulo(){

        Log.d("ReproductorActivity", "playCapitulo()");

        pb.setVisibility(View.VISIBLE);
        bplay.setVisibility(View.GONE);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (capitulo.estaDescargado()){

            /* Si el capítulo está descargado, lo reproducimos */

            new Thread() {
                public void run() {
                    synchronized (reproductorService) {
                        reproductorService.newPlay(capitulo);
                    }
                    handler.sendEmptyMessage(0);
                }
            }.start();
        }else if (!Utils.verificaConexion(getApplicationContext())){

            /* Si no tenemos conexión a internet, mostramos aviso */

            AlertDialog.Builder builder = new AlertDialog.Builder(ReproductorActivity.this);
            builder.setMessage(R.string.nointernet);
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    finish();
                }
            });
            builder.show();

        }else if (!pref.getBoolean("pref_wifi", true) || Utils.conectadoPor(getApplicationContext()) == 2){

            /* Si estamos conectados por wifi o no tenemos esta opción marcada, reproducimos */

            new Thread() {
                public void run() {
                    synchronized (reproductorService) {
                        reproductorService.newPlay(capitulo);
                    }
                    handler.sendEmptyMessage(0);
                }
            }.start();
        } else {

            /* Si no, mostramos aviso de que no estamos conectados por wifi */

            AlertDialog.Builder builder = new AlertDialog.Builder(ReproductorActivity.this);
            builder.setMessage(R.string.nowifi);
            builder.setNegativeButton(R.string.abriropciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    }
                });
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
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
            seek.setMax(reproductorService.getDuration());
            setEscuchado();
        }
    };


    /**
     * Marcamos como escuchado el capítulo
     */
    public void setEscuchado(){
        new Thread(){
            public void run(){
                Log.d("ReproductorActivity", "setEscuchado");
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String token = settings.getString("token", "");
                new CapituloDAOImpl(getBaseContext()).addCapituloEscuchado(token, capitulo.getId());
            }
        }.start();
    }
}
