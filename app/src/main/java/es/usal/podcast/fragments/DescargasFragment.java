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
import es.usal.podcast.adapters.DescargasRecyclerAdapter;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;

/**
 * Fragment que muestar los capítulos descargados
 * @author Jorge Alonso Merchán
 */
public class DescargasFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Capitulo> capitulos;

    public DescargasFragment() {
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

        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarDescargas();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        cargarDescargas();
    }

    /**
     * Carga las descargas
     */

    private void cargarDescargas(){

        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);

        new Thread(){
            public void run(){

                CapituloDAO capitulosDAO = new CapituloDAOImpl(getContext());
                capitulos = capitulosDAO.getDescargas();
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * Handler para mostrar los capítulos descargados en el RecyclerView
     */

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);

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
