package es.usal.podcast;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import es.usal.podcast.Utiles.DownloadFileAsync;
import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;
import es.usal.podcast.reproductor.ReproductorActivity;

/**
 * Cuadro de Dialogo que muestra la ficha de un capítulo, así como opciones para descargar y escuchar el capítulo
 * @author Jorge Alonso Merchán
 */
public class CapituloDialog extends Dialog {

    public Activity c;
    public Button bplay, bdownload;
    public Capitulo capitulo;

    public CapituloDialog(Context context) {
        super(context);
    }

    public CapituloDialog(Context context, int themeResId, Capitulo _capitulo) {
        super(context, themeResId);
        capitulo = _capitulo;
    }

    public CapituloDialog(Activity a, Context context, int themeResId, Capitulo _capitulo) {
        super(context, themeResId);
        capitulo = _capitulo;
        c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);

        TextView text_titulo = (TextView) findViewById(R.id.dialogo_titulo);
        TextView text_subtitulo = (TextView) findViewById(R.id.dialogo_subtitulo);
        TextView text_fecha = (TextView) findViewById(R.id.dialogo_fecha);

        bplay = (Button) findViewById(R.id.dialogo_play);
        bdownload = (Button) findViewById(R.id.dialogo_download);

        if(capitulo.estaDescargado()) {
            bdownload.setCompoundDrawablesWithIntrinsicBounds(null, getContext().getResources().getDrawable(R.drawable.ic_delete), null, null);
            bdownload.setText("Borrar");
        }

        text_titulo.setText(capitulo.getTitulo());
        text_subtitulo.setText(capitulo.getDescripcion());
        text_fecha.setText(capitulo.getFecha());



        bdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {

                    if (capitulo.estaDescargado()) {
                        borrarCapitulo();
                    } else {
                        descargarCapitulo();
                    }
                } else {

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        //if (ActivityCompat.shouldShowRequestPermissionRationale(c.getParent(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        //    ActivityCompat.requestPermissions(getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISOS_WRITE);
                        //} else {
                        //    ActivityCompat.requestPermissions(c.getParent(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISOS_WRITE);
                        //}

                        //ActivityCompat.requestPermissions(getOwnerActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);


                    } else {

                        if (capitulo.estaDescargado()) {
                            borrarCapitulo();
                        } else {
                            descargarCapitulo();
                        }

                    }
                }
                cancel();
            }
        });

        bplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reproducirCapitulo();

            }
        });


    }


    /**
     * Reproduce el capítulo
     */

    private void reproducirCapitulo(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (pref.getBoolean("reproductor_externo", false)){

            // Lanzamos reproductor externo si así está puesto en las opciones

            if (capitulo.estaDescargado()){
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File(capitulo.fileName())), "audio/*");
                getContext().startActivity(intent);
            }else{
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(capitulo.getUrl()), "audio/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }

            dismiss();
        } else{

            // Si no, lanzamos el reproductor de la app

            Intent i = new Intent(getContext(), ReproductorActivity.class);
            capitulo.setIntent(i);
            i.putExtra("play", true);
            getContext().startActivity(i);
            dismiss();
        }
    }


    /**
     * Descargará el capítulo
     */

    private void descargarCapitulo(){
        Toast.makeText(getContext(), "Descargando...", Toast.LENGTH_SHORT).show();
        new DownloadFileAsync(getContext(), capitulo).execute(capitulo.getUrl());
    }

    /**
     * Borrará el capítulo, tanto el fichero del móvil como en la base de datos
     */

    private void borrarCapitulo(){
        File f = new File(capitulo.fileName());
        f.delete();
        CapituloDAO dao = new CapituloDAOImpl(getContext());
        dao.deleteDescarga(capitulo.getId());
        Toast.makeText(getContext(), "Borrado", Toast.LENGTH_SHORT).show();
    }

}
