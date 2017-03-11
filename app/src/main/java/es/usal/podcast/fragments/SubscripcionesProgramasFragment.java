package es.usal.podcast.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.usal.podcast.R;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.adapters.ProgramaRecyclerAdapter;
import es.usal.podcast.modelo.Programa;
import es.usal.podcast.modelo.ProgramaDAO;
import es.usal.podcast.modelo.ProgramaDAOImpl;

/**
 * Fragment que mostrará la lista de programas a los que está subscrito el usuario
 * @author Jorge Alonso Merchán
 */
public class SubscripcionesProgramasFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Programa> programas;
    private CardView cardView;

    public SubscripcionesProgramasFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_subscripciones, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);


        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefresh);
        cardView = (CardView) getView().findViewById(R.id.card_view);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardView.setVisibility(View.GONE);
            }
        });
        cargarSubscripciones();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarSubscripciones();
            }
        });

    }


    /**
     * Carga las subscripciones
     */
    private void cargarSubscripciones(){

        cardView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        refreshLayout.setRefreshing(true);

        new Thread(){
            public void run(){

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = settings.getString("token", "");
                programas = new ProgramaDAOImpl().getSubscripciones(token);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * Handler para mostrar las subscripciones en el RecyclerView
     */

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);

            if (programas != null ){

                if (programas.size() > 0){

                    cardView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);

                    ProgramaRecyclerAdapter itemAdapter = new ProgramaRecyclerAdapter(programas);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                    recyclerView.setAdapter(itemAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                }else{
                    cardView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                    cargarDestacados();
                }

            }
        }

    };

    /**
     * Carga los podcast destacados (si no hay subscripciones)
     */

    private void cargarDestacados(){

        refreshLayout.setRefreshing(true);

        new Thread(){
            public void run(){

                ProgramaDAO programaDAO = new ProgramaDAOImpl();
                programas = programaDAO.getDestacados();
                handlerDestacados.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * Handler para mostrar los destacados en el RecyclerView
     */
    private Handler handlerDestacados = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);

            if (programas != null ){

                cardView.setVisibility(View.VISIBLE);

                ProgramaRecyclerAdapter itemAdapter = new ProgramaRecyclerAdapter(programas);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            }
        }

    };

}
