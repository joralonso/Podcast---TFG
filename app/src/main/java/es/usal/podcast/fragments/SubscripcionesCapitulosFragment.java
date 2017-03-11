package es.usal.podcast.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.usal.podcast.R;
import es.usal.podcast.adapters.DescargasRecyclerAdapter;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAOImpl;

/**
 * Fragment uq emuestra los últimos capítulos de las subscripciones del usuario
 * @author Jorge Alonso Merchán
 */
public class SubscripcionesCapitulosFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Capitulo> capitulos;

    public SubscripcionesCapitulosFragment() {
        super();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_layout2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefresh);
        cargarCapitulos();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarCapitulos();
            }
        });
    }

    /**
     * Carga los últimos capítulos de las subscripciones del usuario
     */

    public void cargarCapitulos(){

        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);

        new Thread(){
            public void run(){
                CapituloDAOImpl capDAO = new CapituloDAOImpl(getContext());

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = settings.getString("token", "");

                capitulos = capDAO.getUltimosCapitulosSubscripciones(token);
                handler.sendEmptyMessage(0);

            }
        }.start();
    }

    /**
     * Handler que muestra los capítulos en el RecyclerView
     */

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {


            refreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);

            if (capitulos != null ){
                if (capitulos.size() == 0){
                    cargarUltimosCapitulos();
                }else{
                    DescargasRecyclerAdapter itemAdapter = new DescargasRecyclerAdapter(capitulos);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                    recyclerView.setAdapter(itemAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }
            }

        }

    };

    /**
     * Carga los últimos capítulos publicados (en caso de que no haya subscripcioens
     */
    public void cargarUltimosCapitulos(){

        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);

        new Thread(){
            public void run(){
                CapituloDAOImpl capDAO = new CapituloDAOImpl(getContext());
                capitulos = capDAO.getUltimosCapitulos();
                handlerUltimos.sendEmptyMessage(0);

            }
        }.start();
    }

    /**
     * Handler para mostrar los capítulos en el RecyclerView
     */

    private Handler handlerUltimos = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);

            if (capitulos != null ){
                DescargasRecyclerAdapter itemAdapter = new DescargasRecyclerAdapter(capitulos);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }

    };


}
