package es.usal.podcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.List;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.adapters.CapituloRecyclerAdapter;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.adapters.ProgramaRecyclerAdapter;
import es.usal.podcast.adapters.UsuarioRecyclerAdapter;
import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAOImpl;
import es.usal.podcast.modelo.Programa;
import es.usal.podcast.modelo.ProgramaDAOImpl;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAOImpl;

/**
 * Actividad que muestra un usuario junto sus subscripciones y sus seguidores
 * @author Jorge Alonso Merchán
 */

public class UsuarioActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private int usuarioid;
    private String nombre;

    private boolean lesigo = false;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario);

        usuarioid = this.getIntent().getIntExtra("usuarioid", 0);
        nombre = this.getIntent().getStringExtra("nombre");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), usuarioid);


        getSupportActionBar().setTitle(nombre);
        getSupportActionBar().setHomeButtonEnabled(true);

        Log.d("UsuarioActivity", "Userid: "+usuarioid);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab_usuario);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seguir();
            }
        });


        if (usuarioid == PreferenceManager.getDefaultSharedPreferences(this).getInt(Constantes.USERID, -1)){
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams)    fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            p.width = 0;
            p.height = 0;
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);

        } else{
            leSigo();
        }


    }

    public void leSigo(){
        new Thread(){
            public void run(){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String token = settings.getString("token", "");
                lesigo = new UsuarioDAOImpl().leSigo(token, usuarioid);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (lesigo){
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_sub));
            }else{
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_sub));
            }
        }

    };

    public void seguir(){
        new Thread(){
            public void run(){
                UsuarioDAOImpl dao = new UsuarioDAOImpl();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String token = settings.getString("token", "");
                if (lesigo){
                    int i = dao.deleteSeguir(token, usuarioid);
                    if (i > 0)
                        lesigo = false;
                    else
                        lesigo = true;
                } else{
                    int i = dao.addSeguir(token, usuarioid);
                    if (i > 0)
                        lesigo = true;
                    else
                        lesigo = false;
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView recyclerView;
        private RelativeLayout progress;
        private List<Programa> programas;
        private List<Capitulo> capitulos;
        private List<Usuario> usuarios;
        private int usuarioid;

        public PlaceholderFragment() {
        }

        public void setUsuarioid(int usuarioid) {
            this.usuarioid = usuarioid;
        }

        public static PlaceholderFragment newInstance(int sectionNumber, int _usuarioid) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setUsuarioid(_usuarioid);
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_recicler, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
            progress = (RelativeLayout) rootView.findViewById(R.id.progress);

            switch (getArguments().getInt(ARG_SECTION_NUMBER)){
                case 0:
                    cargarSubscripciones();
                    break;
                case 1:
                    cargarEscuchados();
                    break;
                case 2:
                    cargarSeguidores();
                    break;
                case 3:
                    cargarSeguidos();
                    break;
            }
            return rootView;
        }

        private void cargarSubscripciones(){
            new Thread(){
                public void run(){

                    Log.d("Fragment", "usuarioid: "+usuarioid);

                    programas = new ProgramaDAOImpl().getUsuarioSubscripciones(usuarioid);
                    handlerSubscripciones.sendEmptyMessage(0);
                }
            }.start();
        }

        private Handler handlerSubscripciones = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progress.setVisibility(View.GONE);
                if (programas != null ){

                    ProgramaRecyclerAdapter itemAdapter = new ProgramaRecyclerAdapter(programas);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                    recyclerView.setAdapter(itemAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        };


        private void cargarEscuchados(){
            new Thread(){
                public void run(){
                    capitulos = new CapituloDAOImpl(getContext()).getCapitulosEscuchados(usuarioid);
                    handlerEscuchados.sendEmptyMessage(0);
                }
            }.start();
        }

        private Handler handlerEscuchados = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progress.setVisibility(View.GONE);
                if (capitulos != null ){
                    CapituloRecyclerAdapter itemAdapter = new CapituloRecyclerAdapter(capitulos);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                    recyclerView.setAdapter(itemAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        };


        private void cargarSeguidores(){
            new Thread(){
                public void run(){
                    usuarios = new UsuarioDAOImpl().getSeguidores(usuarioid);
                    handlerSeguidores.sendEmptyMessage(0);
                }
            }.start();
        }



        private void cargarSeguidos(){
            new Thread(){
                public void run(){
                    usuarios = new UsuarioDAOImpl().getSeguidos(usuarioid);
                    handlerSeguidores.sendEmptyMessage(0);
                }
            }.start();
        }

        private Handler handlerSeguidores = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                progress.setVisibility(View.GONE);
                if (usuarios != null ){
                    UsuarioRecyclerAdapter itemAdapter = new UsuarioRecyclerAdapter(usuarios);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                    recyclerView.setAdapter(itemAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }
        };
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int usuarioid;

        public SectionsPagerAdapter(FragmentManager fm, int usuarioid) {
            super(fm);
            this.usuarioid = usuarioid;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position, usuarioid);
        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.tab_usuario).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] temp = getResources().getStringArray(R.array.tab_usuario);
            if (position < temp.length)
                return temp[position];
            else
                return null;
        }
    }
}
