package es.usal.podcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import es.usal.podcast.modelo.Programa;
import es.usal.podcast.modelo.ProgramaDAO;
import es.usal.podcast.modelo.ProgramaDAOImpl;

/**
 * Activity para añadir nuevos podcast a través de una URL
 * @author Jorge Alonso Merchán
 */

public class AddPodcastActivity extends AppCompatActivity {

    private EditText editText;
    private RelativeLayout progress;
    private Programa programa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_podcast);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText = (EditText) findViewById(R.id.editText);
        progress = (RelativeLayout) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);

        ((Button) findViewById(R.id.add_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPodcast();
            }
        });
    }

    private void addPodcast(){
        progress.setVisibility(View.VISIBLE);
        new Thread(){
            public void run(){
                ProgramaDAO programaDAO = new ProgramaDAOImpl();
                programa = programaDAO.addPrograma(editText.getText().toString());
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            if (programa != null ){
                Intent i = new Intent(AddPodcastActivity.this, ProgramaActivity.class);
                programa.setIntent(i);
                startActivity(i);
                finish();
            }else{
                progress.setVisibility(View.GONE);
                Toast.makeText(AddPodcastActivity.this, R.string.add_programa_error, Toast.LENGTH_LONG).show();
            }
        }

    };

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
