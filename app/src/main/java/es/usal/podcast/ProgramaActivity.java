package es.usal.podcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.fragments.CapitulosFragment;
import es.usal.podcast.fragments.CapitulosMasEscuchadosFragment;
import es.usal.podcast.fragments.RelacionadosFragment;
import es.usal.podcast.fragments.SubscriptoresAlProgramaFragment;
import es.usal.podcast.modelo.Programa;
import es.usal.podcast.modelo.ProgramaDAOImpl;

/**
 * Actividad que muestra un programa junto con sus pestañas: Últimos capítulos, capítulos más escuchados, relacionados, subscripciones
 * @author Jorge Alonso Merchán
 */

public class ProgramaActivity extends AppCompatActivity {

    private Programa programa;
    //private TextView titulo, subtitulo;
    //private ImageView imagen;
    private FloatingActionButton fab;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private boolean subscrito = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programa);

        programa = new Programa(this.getIntent().getExtras());

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(programa.getTitulo());

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final  TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.htab_collapse_toolbar);
        collapsingToolbarLayout.setTitleEnabled(false);
        final ImageView imagen = (ImageView) findViewById(R.id.htab_header);

        Picasso.with(this).load(programa.getImageHeader()).into(imagen, new Callback() {
            @Override
            public void onSuccess() {
                Palette.from(((BitmapDrawable)imagen.getDrawable()).getBitmap()).generate(new Palette.PaletteAsyncListener() {
                    @SuppressWarnings("ResourceType")
                    @Override
                    public void onGenerated(Palette palette) {

                        int vibrantColor = palette.getVibrantColor(R.color.colorPrimary);
                        int vibrantDarkColor = palette.getDarkVibrantColor(R.color.colorPrimaryDark);
                        collapsingToolbarLayout.setContentScrimColor(getColorWithAplha(vibrantColor, 0.4f));
                        collapsingToolbarLayout.setStatusBarScrimColor(getColorWithAplha(vibrantDarkColor, 0.4f));

                        Palette.Swatch swatch = palette.getVibrantSwatch();
                        if (swatch != null)
                            collapsingToolbarLayout.setCollapsedTitleTextColor(swatch.getTitleTextColor());

                        tabLayout.setBackgroundColor( getColorWithAplha(palette.getDarkMutedColor(R.color.colorPrimary), 0.4f));


                    }
                });
            }

            @Override
            public void onError() {

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribirse();
            }
        });

        estoySubscrito();

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

    public void subscribirse(){
        new Thread(){
            public void run(){
                ProgramaDAOImpl dao = new ProgramaDAOImpl();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String token = settings.getString("token", "");
                if (subscrito){
                    int i = dao.deleteSubscripcion(token, programa.getId());
                    if (i > 0)
                        subscrito = false;
                    else
                        subscrito = true;
                } else{
                    int i = dao.addSubscripcion(token, programa.getId());
                    if (i > 0)
                        subscrito = true;
                    else
                        subscrito = false;
                }

                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    public void estoySubscrito(){
        new Thread(){
            public void run(){
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String token = settings.getString("token", "");
                subscrito = new ProgramaDAOImpl().estoySubscrito(token, programa.getId());
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (subscrito){
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_sub));
            }else{
                fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_no_sub));
            }
        }

    };



    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment frag = null;

            switch (position) {
                case 0:
                    frag = CapitulosFragment.newInstance(programa.getId());
                    break;
                case 1:
                    frag = CapitulosMasEscuchadosFragment.newInstance(programa.getId());
                    break;
                case 2:
                    frag = RelacionadosFragment.newInstance(programa.getId());
                    break;
                case 3:
                    frag = SubscriptoresAlProgramaFragment.newInstance(programa.getId());
                    break;
                default:
                    frag = CapitulosFragment.newInstance(programa.getId());
                    return null;
            }
            return frag;
        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.tab_programas).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] temp = getResources().getStringArray(R.array.tab_programas);
            if (position < temp.length)
                return temp[position];
            else
                return null;
        }
    }

    /* Source: http://stackoverflow.com/questions/36078861/palette-library-how-to-add-transparency-to-palette-swatch-color */

    /**
     * Devuelve un color con un % de transparencia
     * @param color
     * @param ratio
     * @return
     */

    private int getColorWithAplha(int color, float ratio)
    {
        int transColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        transColor = Color.argb(alpha, r, g, b);
        return transColor ;
    }

}
