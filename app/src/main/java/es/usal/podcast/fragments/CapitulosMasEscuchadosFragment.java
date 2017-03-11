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
import es.usal.podcast.adapters.CapituloRecyclerAdapter;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.modelo.Capitulo;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;

/**
 * Fragment que muestra la lista de capítulos de un programa determinado
 * @author Jorge Alonso Merchán
 */
public class CapitulosMasEscuchadosFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Capitulo> capitulos;
    private int programaId;

    /**
     * Crea una instancia del fragment con el id del programa
     * @param programaId id del programa que queremos ver los capítulos
     * @return nueva instancia del fragment
     */

    public static CapitulosMasEscuchadosFragment newInstance(int programaId) {
        CapitulosMasEscuchadosFragment fragment = new CapitulosMasEscuchadosFragment();
        Bundle args = new Bundle();
        args.putInt("programaId", programaId);
        fragment.setArguments(args);
        return fragment;
    }

    public CapitulosMasEscuchadosFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        programaId = getArguments().getInt("programaId");
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
        cargarCapitulosMasEscuchados();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarCapitulosMasEscuchados();
            }
        });

    }

    /**
     * Carga los capítulos más escuchados del programa
     */

    public void cargarCapitulosMasEscuchados(){

        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);

        new Thread(){
            public void run(){
                CapituloDAO capDAO = new CapituloDAOImpl(getContext());
                capitulos = capDAO.getCapitulosMasEscuchados(programaId);
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

            if (capitulos != null ){
                CapituloRecyclerAdapter itemAdapter = new CapituloRecyclerAdapter(capitulos);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

        }

    };

}
