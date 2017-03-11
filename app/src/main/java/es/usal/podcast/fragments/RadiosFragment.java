package es.usal.podcast.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.usal.podcast.R;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.adapters.RadioRecyclerAdapter;
import es.usal.podcast.modelo.Radio;
import es.usal.podcast.modelo.RadioDAO;
import es.usal.podcast.modelo.RadioDAOImpl;

/**
 * Fragment que muestra la listas de radios disponibles para escuchar
 * @author Jorge Alonso Merchán
 */
public class RadiosFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Radio> radios;

    public RadiosFragment() {
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
        cargarRadios();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarRadios();
            }
        });
    }


    /**
     * Carga las radios
     */
    private void cargarRadios(){

        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);

        new Thread(){
            public void run(){

                RadioDAO radioDAO = new RadioDAOImpl();
                radios = radioDAO.getRadios();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * Handler que muestra las radios en el RecyclerView
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);

            if (radios != null ){
                RadioRecyclerAdapter itemAdapter = new RadioRecyclerAdapter(radios);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

        }

    };

}
