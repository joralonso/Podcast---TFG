package es.usal.podcast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import es.usal.podcast.Utiles.Constantes;
import es.usal.podcast.Utiles.Utils;
import es.usal.podcast.fragments.DescargasFragment;
import es.usal.podcast.fragments.RadiosFragment;
import es.usal.podcast.fragments.SubscripcionesCapitulosFragment;
import es.usal.podcast.fragments.SubscripcionesProgramasFragment;
import es.usal.podcast.fragments.TimelineFragment;

/**
 * Actividad principal donde se muestran las pestañas principales: Subscripciones, Capítulos, Descargas, Timeline y Radios
 * @author Jorge Alonso Merchán
 */

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Si no hay internet, vamos directamente a la pestaña de DESCARGAS
        if (!Utils.verificaConexion(this)) {
            mViewPager.setCurrentItem(2);
            Snackbar.make(mViewPager, getResources().getString(R.string.sin_internet), Snackbar.LENGTH_SHORT).show();
        }

        // Si no tenemos permiso de escrituras, pedimos permisos. Esencial para las Descargas
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
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
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                break;

            case R.id.action_podcast:
                startActivity(new Intent(MainActivity.this, AddPodcastActivity.class));
                break;

            case R.id.action_cerrar_sesion:
                Utils.cerrarSesion(getBaseContext());
                startActivity(new Intent(getApplicationContext(), IniciarActivity.class));
                finish();
                break;

            case R.id.action_mi_perfil:
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

                Intent i = new Intent(MainActivity.this, UsuarioActivity.class);
                i.putExtra("usuarioid", settings.getInt(Constantes.USERID, -1));
                i.putExtra("nombre", settings.getString(Constantes.NOMBRE, ""));
                startActivity(i);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Fragment tab1 = new SubscripcionesProgramasFragment();
                    return tab1;
                case 1:
                    Fragment tab2 = new SubscripcionesCapitulosFragment();
                    return tab2;
                case 2:
                    Fragment tab3 = new DescargasFragment();
                    return tab3;
                case 3:
                    Fragment tab4 = new TimelineFragment();
                    return tab4;
                case 4:
                    Fragment tab5 = new RadiosFragment();
                    return tab5;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.tab_main).length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] temp = getResources().getStringArray(R.array.tab_main);
            if (position < temp.length)
                return temp[position];
            else
                return null;
        }
    }
}
