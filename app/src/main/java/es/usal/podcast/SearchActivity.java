package es.usal.podcast;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAOImpl;
import es.usal.podcast.modelo.Programa;
import es.usal.podcast.modelo.ProgramaDAOImpl;
import es.usal.podcast.modelo.Radio;
import es.usal.podcast.modelo.RadioDAOImpl;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAOImpl;
import es.usal.podcast.adapters.CapituloRecyclerAdapter;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.adapters.ProgramaRecyclerAdapter;
import es.usal.podcast.adapters.RadioRecyclerAdapter;
import es.usal.podcast.adapters.UsuarioRecyclerAdapter;

/**
 * Actividad que permite buscar programas, capítulos, radios y usuarios. Se accede a ella desde cualquier actividad, pulsando en el botón de buscar del ActionBar
 * @author Jorge Alonso Merchán
 */

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    private RecyclerView recyclerView;
    private RelativeLayout progress;
    private List<Programa> programas;
    private List<Capitulo> capitulos;
    private List<Radio> radios;
    private List<Usuario> usuarios;
    private MenuItem searchItem;
    private String query;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewBusqueda);
        progress = (RelativeLayout) findViewById(R.id.progress);
        spinner = (Spinner) findViewById(R.id.spinner);

        spinner.setAdapter(ArrayAdapter.createFromResource( this, R.array.busqueda , android.R.layout.simple_spinner_dropdown_item));

        progress.setVisibility(View.GONE);

        query = this.getIntent().getExtras().getString("query", "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        searchItem = menu.findItem(R.id.search);
        searchItem.setTitle(query);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        if (!query.equalsIgnoreCase("")) {
            Log.d("SearchActivity", "Query="+query);
            MenuItemCompat.expandActionView(searchItem);
            searchView.setQuery(query, true);
            buscar(query);
        }
        searchView.setOnQueryTextListener(SearchActivity.this);


        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        buscar(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void buscar(final String query){

        Log.d("SearchActivity", "Empieza la busquedad de "+query);

        progress.setVisibility(View.VISIBLE);

        new Thread(){
            public void run(){

                int position = spinner.getSelectedItemPosition();
                switch (position){

                    case 0:
                        programas = new ProgramaDAOImpl().getBusqueda(query);
                        handlerProgramas.sendEmptyMessage(0);
                        break;

                    case 1:
                        capitulos = new CapituloDAOImpl(getApplicationContext()).getBusqueda(query);
                        handlerCapitulos.sendEmptyMessage(0);
                        break;

                    case 2:
                        usuarios = new UsuarioDAOImpl().getBusqueda(query);
                        handlerUsuarios.sendEmptyMessage(0);
                        break;

                    case 3:
                        radios = new RadioDAOImpl().getBusqueda(query);
                        handlerRadios.sendEmptyMessage(0);
                        break;

                }
            }
        }.start();
    }

    private Handler handlerProgramas = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            progress.setVisibility(View.GONE);

            if (programas != null ){

                ProgramaRecyclerAdapter itemAdapter = new ProgramaRecyclerAdapter(programas);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            } else {
                recyclerView.setAdapter(null);
                Toast.makeText(getBaseContext(), "No hay resultados a la busqueda", Toast.LENGTH_SHORT).show();
            }
        }

    };



    private Handler handlerCapitulos = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            progress.setVisibility(View.GONE);

            if (capitulos != null ){
                CapituloRecyclerAdapter itemAdapter = new CapituloRecyclerAdapter(capitulos);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            } else {
                recyclerView.setAdapter(null);
                Toast.makeText(getBaseContext(), "No hay resultados a la busqueda", Toast.LENGTH_SHORT).show();
            }

        }

    };


    private Handler handlerRadios = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            progress.setVisibility(View.GONE);

            if (radios != null ){
                RadioRecyclerAdapter itemAdapter = new RadioRecyclerAdapter(radios);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            } else {
                recyclerView.setAdapter(null);
                Toast.makeText(getBaseContext(), "No hay resultados a la busqueda", Toast.LENGTH_SHORT).show();
            }

        }

    };


    private Handler handlerUsuarios = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            progress.setVisibility(View.GONE);
            if (usuarios != null ){
                UsuarioRecyclerAdapter itemAdapter = new UsuarioRecyclerAdapter(usuarios);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getBaseContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            } else {
                recyclerView.setAdapter(null);
                Toast.makeText(getBaseContext(), "No hay resultados a la busqueda", Toast.LENGTH_SHORT).show();
            }
        }
    };
}
