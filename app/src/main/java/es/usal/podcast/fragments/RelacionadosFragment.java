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
import es.usal.podcast.adapters.ProgramaRecyclerAdapter;
import es.usal.podcast.modelo.Programa;
import es.usal.podcast.modelo.ProgramaDAO;
import es.usal.podcast.modelo.ProgramaDAOImpl;

/**
 * Fragment que muestra una lista de programas relacionados con un programa
 * Created by Jorge on 11/4/16.
 */
public class RelacionadosFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Programa> programas;
    private int programaId = 0;

    public RelacionadosFragment() {
        super();
    }

    /**
     * Crea una instancia del fragment con el id del programa
     * @param programaId id del programa del que queremos ver programas relacionados
     * @return nueva instancia del fragment
     */

    public static RelacionadosFragment newInstance(int programaId) {
        RelacionadosFragment fragment = new RelacionadosFragment();
        Bundle args = new Bundle();
        args.putInt("programaId", programaId);
        fragment.setArguments(args);
        return fragment;
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
        cargarRelacionados();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarRelacionados();
            }
        });
    }


    /**
     * Carga los programas relacionados al progarma
     */
    private void cargarRelacionados(){

        recyclerView.setVisibility(View.GONE);
        refreshLayout.setRefreshing(true);

        new Thread(){
            public void run(){
                ProgramaDAO programaDAO = new ProgramaDAOImpl();
                programas = programaDAO.getRelacionados(programaId);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * Handler para mostrar los programas en en RecyclerView
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);
            recyclerView.setVisibility(View.VISIBLE);

            if (programas != null ){

                ProgramaRecyclerAdapter itemAdapter = new ProgramaRecyclerAdapter(programas);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        }

    };

}
