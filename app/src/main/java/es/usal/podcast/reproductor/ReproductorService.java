package es.usal.podcast.reproductor;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;
import es.usal.podcast.modelo.Radio;

/**
 * Servicio del reproductor
 * @autor Jorge Alonso Merchán
 */

public class ReproductorService extends Service {

    static public final int STOPED = -1, PAUSED = 0, PLAYING = 1;
    private MediaPlayer mediaPlayer;
    private Capitulo capitulo;
    private Radio radio;
    private int status;
    private boolean taken;
    private IBinder playerBinder;
    private BroadcastReceiver mIntentReceiver;
    private boolean mReceiverRegistered = false;


    public static final String CMD_NAME = "command";
    public static final String CMD_PAUSE = "pause";
    public static final String CMD_STOP = "stop";
    public static final String CMD_PLAY = "play";

    // Jellybean
    public static String SERVICE_CMD = "com.sec.android.app.music.musicservicecommand";
    public static String PAUSE_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.pause";
    public static String PLAY_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.play";
    public static String STOP_SERVICE_CMD = "com.sec.android.app.music.musicservicecommand.stop";

    @Override
    public void onCreate() {
        super.onCreate();
        setStatus(STOPED);
        playerBinder = new PlayerBinder();
        setupBroadcastReceiver();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return playerBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void take() {
        taken = true;
    }

    private void untake() {
        synchronized (this) {
            taken = false;
            notifyAll();
        }
    }

    private void setStatus(int s) {
        status = s;
    }

    public int getStatus() {
        return status;
    }

    public Capitulo getCapitulo(){
        return capitulo;
    }

    public Radio getRadio() {
        return radio;
    }

    public void setCapitulo(Capitulo _capitulo){
        this.capitulo = _capitulo;
    }

    public void setRadio(Radio radio) {
        this.radio = radio;
    }

    /**
     * Reproducimos radio
     * @param radio Radio que queremos reproducir
     */

    public void newPlayRadio(Radio radio) {

        Log.d("ReproductorService", "New Play");

        if (status > STOPED) {
            stop();
        }
        this.radio = radio;

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(radio.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mediaPlayer.start();

        mediaPlayer.setLooping(false);

        PlayerRadioNotification.notify(getApplicationContext(), radio);

        setStatus(PLAYING);

        untake();
    }


    /**
     * Reproducimos nuevo capítulo
     */
    public void newPlay(Capitulo capitulo) {

        Log.d("ReproductorService", "New Play");
        if (status > STOPED) {
            stop();
        }

        this.capitulo = capitulo;


        mediaPlayer = new MediaPlayer();
        try {
            if (capitulo.estaDescargado())
                mediaPlayer.setDataSource(capitulo.fileName());
            else
                mediaPlayer.setDataSource(capitulo.getUrl());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }


        /* Avanzamos hasta la última posición almacenada */
        CapituloDAO dao = new CapituloDAOImpl(getBaseContext());
        int posicion = dao.getTiempoEscuchado(capitulo.getId());
        mediaPlayer.seekTo(posicion);
        Log.d("ReproductorService.Goto", ""+posicion);

        mediaPlayer.start();

        mediaPlayer.setLooping(false);

        PlayerNotification.notify(getApplicationContext(), capitulo);

        setStatus(PLAYING);

        untake();
    }

    /**
     * Método que reproducirá de nuevo si está en pausa, pondrá en pausa si se está reproduciendo e iniciará una nueva reproducción si está parado
     */
    public void play() {

        Log.d("ReproductorService", "Play");

        switch (status) {
        case STOPED:
            newPlay(capitulo);
        break;
        case PLAYING:
            mediaPlayer.pause();
            if (capitulo != null)
                PlayerNotification.pause(getApplicationContext());
            else
                PlayerRadioNotification.pause(getApplicationContext());
            setStatus(PAUSED);
        break;
        case PAUSED:
            mediaPlayer.start();
            if (capitulo != null)
                PlayerNotification.play(getApplicationContext());
            else
                PlayerRadioNotification.play(getApplicationContext());
            setStatus(PLAYING);
        break;
        }
        untake();
    }

    /**
     * Método que reproducirá de nuevo si está en pausa, pondrá en pausa si se está reproduciendo e iniciará una nueva reproducción si está parado
     * Igual que play() pero para Radio
     */
    public void playRadio() {

        Log.d("ReproductorService", "Play");

        switch (status) {
            case STOPED:
                newPlayRadio(radio);
                break;
            case PLAYING:
                mediaPlayer.pause();
                if (capitulo != null)
                    PlayerNotification.play(getApplicationContext());
                else
                    PlayerRadioNotification.play(getApplicationContext());
                setStatus(PAUSED);
                break;
            case PAUSED:
                mediaPlayer.start();
                if (capitulo != null)
                    PlayerNotification.pause(getApplicationContext());
                else
                    PlayerRadioNotification.pause(getApplicationContext());
                setStatus(PLAYING);
                break;
        }
        untake();
    }

    /**
     * Pone en pause el capítulo que se está reproduciendo. Además guarda la posición de por donde se va reproduciendo el capítulo
     */

    public void pause() {
        if (capitulo != null){
            new CapituloDAOImpl(getApplicationContext()).updateTiempoEscuchado(getCapitulo().getId(), mediaPlayer.getCurrentPosition());
            PlayerNotification.pause(getApplicationContext());
        }else{
            PlayerRadioNotification.pause(getApplicationContext());
        }

        mediaPlayer.pause();
        setStatus(PAUSED);
        untake();
    }

    /**
     * Para del tod.o el capítulo o la radio en reproducción. Además guarda la posición de por donde se va reproduciendo el capítulo
     */
    public void stop() {

        Log.d("ReproductorService", "Stop");

        if (capitulo != null){
            new CapituloDAOImpl(getApplicationContext()).updateTiempoEscuchado(getCapitulo().getId(), mediaPlayer.getCurrentPosition());
            capitulo = null;
            PlayerNotification.cancel(getApplicationContext());
        } else {
            Log.d("ReproductorService", "Stop Radio");
            radio = null;
            PlayerRadioNotification.cancel(getApplicationContext());
        }
        mediaPlayer.stop();
        mediaPlayer.reset();
        setStatus(STOPED);
    }


    /**
     * Devuelve la posición actual de por donde va la reproducción
     * @return posición de la reproducción
     */

    public int getCurrentTrackProgress() {
        if (status > STOPED) {
            return mediaPlayer.getCurrentPosition();
        } else {
            return 0;
        }
    }

    public class PlayerBinder extends Binder {

        public ReproductorService getService() {
            return ReproductorService.this;
        }
    }


    public static String formatTrackDuration(int d) {
        String min = Integer.toString((d/1000)/60);
        String sec = Integer.toString((d/1000)%60);
        if (sec.length() == 1) sec = "0"+sec;
        return min+":"+sec;
    }

    /**
     * Devuelve la duración del archivo que se está reproduciendo
     * @return duración en milisegundos de la duración del archivo
     */

    public int getDuration(){
        if (mediaPlayer != null)
            return mediaPlayer.getDuration();
        else
            return 0;
    }

    /**
     * Mueve la posición actual de la reprodución a pos
     * @param pos Posición, en milisegundos, a la que queremos mover la reprodución
     */
    public void goTo(int pos){
        if (mediaPlayer != null)
            mediaPlayer.seekTo(pos);

    }

    /**
     * Inicializamos el BroadcastReceiver con las opciones que recibirá
     */

    private void setupBroadcastReceiver() {
        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                String cmd = intent.getStringExtra(CMD_NAME);
                Log.i("ReproductorService", "mIntentReceiver.onReceive " + action + " / " + cmd);

                if (PAUSE_SERVICE_CMD.equals(action) || (SERVICE_CMD.equals(action) && CMD_PAUSE.equals(cmd))) {
                    Log.d("MusicPlayer", "PLAY");
                    play();
                }else  if (PLAY_SERVICE_CMD.equals(action) || (SERVICE_CMD.equals(action) && CMD_PLAY.equals(cmd))) {
                    Log.d("MusicPlayer", "PAUSE");
                    pause();
                } else if (STOP_SERVICE_CMD.equals(action)|| (SERVICE_CMD.equals(action) && CMD_STOP.equals(cmd))){
                    Log.d("MusicPlayer", "STOP");
                    stop();
                }
            }
        };


        if (!mReceiverRegistered) {
            IntentFilter commandFilter = new IntentFilter();
            commandFilter.addAction(SERVICE_CMD);
            commandFilter.addAction(STOP_SERVICE_CMD);
            commandFilter.addAction(PAUSE_SERVICE_CMD);
            commandFilter.addAction(PLAY_SERVICE_CMD);
            getApplicationContext().registerReceiver(mIntentReceiver, commandFilter);
            mReceiverRegistered = true;
        }
    }


}
