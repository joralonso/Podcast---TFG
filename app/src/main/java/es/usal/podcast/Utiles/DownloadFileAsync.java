package es.usal.podcast.Utiles;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import es.usal.podcast.MainActivity;
import es.usal.podcast.R;
import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;

/**
 * Gestiona las descargas de capítulos en el móvil
 * @author Jorge Alonso Merchán
 */
public class DownloadFileAsync extends AsyncTask<String, String, String> {

    NotificationManager nm;
    private NotificationCompat.Builder builder;
    private final String NOTIFICATION_TAG = "Descarga";
    private int prog  = 0;
    private Context context;
    private Capitulo capitulo;
    private int lenghtOfFile = 0;

    public DownloadFileAsync(Context _context,  Capitulo c){
        this.context = _context;
        this.capitulo = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d("DownloadFileAsync", "Descargando...");

        Log.d("PATH", Constantes.path);
        File folder = new File(Constantes.path);
        if (!folder.exists()) {
           folder.mkdir();
        }

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setContentTitle("Descargando...")
                .setContentText("Descargando "+capitulo.getTitulo())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(100, 0, false)
                .setAutoCancel(true);

        nm = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            nm.notify(NOTIFICATION_TAG, 0, builder.build());
        } else {
            nm.notify(NOTIFICATION_TAG.hashCode(), builder.build());
        }
    }

    @Override
    protected String doInBackground(String... aurl) {
        int count;

        try {
            URL url = new URL(aurl[0]);
            URLConnection conexion = url.openConnection();
            conexion.connect();

            lenghtOfFile = conexion.getContentLength();
            Log.d("ANDRO_ASYNC", "Lenght of file: " + lenghtOfFile);

            InputStream input = new BufferedInputStream(url.openStream());
            OutputStream output = new FileOutputStream( capitulo.fileName());

            conexion.setReadTimeout(10000);

            byte data[] = new byte[1024];

            long total = 0;

            while ((count = input.read(data)) != -1) {
                total += count;
                publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                output.write(data, 0, count);

            }

            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {}
        return null;

    }
    protected void onProgressUpdate(String... progress) {
        prog++;
        if (prog == 100) {
            prog = 0;
            builder.setProgress(100, Integer.parseInt(progress[0]), false);
            // Displays the progress bar for the first time.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.notify(NOTIFICATION_TAG, 0, builder.build());
            } else {
                nm.notify(NOTIFICATION_TAG.hashCode(), builder.build());
            }
        }
    }

    @Override
    protected void onPostExecute(String unused) {
        //dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
        Log.d("DownloadFileAsync", "Descargado ");

        if (lenghtOfFile == new File(capitulo.fileName()).length()) {

            CapituloDAO dao = new CapituloDAOImpl(context);
            dao.addDescarga(capitulo);

            builder.setContentText("Download complete")
                    // Removes the progress bar
                    .setProgress(0, 0, false);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.cancel(NOTIFICATION_TAG, 0);
            } else {
                nm.cancel(NOTIFICATION_TAG.hashCode());
            }

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("position", 1);

            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle("Descarga Completa")
                    .setContentText("Se ha descargado completamente")
                    .setSmallIcon(R.drawable.ic_stat_ok)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.notify(NOTIFICATION_TAG, 0, builder.build());
            } else {
                nm.notify(NOTIFICATION_TAG.hashCode(), builder.build());
            }
        }else {
            new File(capitulo.fileName()).delete();

            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("position", 1);

            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(android.R.drawable.stat_sys_download)
                    .setContentTitle("Error en la descarga")
                    .setContentText("Se ha producido un error al descargar el fichero")
                    .setSmallIcon(R.drawable.ic_report_problem)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(
                            PendingIntent.getActivity(
                                    context,
                                    0,
                                    intent,
                                    PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                nm.notify(NOTIFICATION_TAG, 0, builder.build());
            } else {
                nm.notify(NOTIFICATION_TAG.hashCode(), builder.build());
            }
        }


    }
}
