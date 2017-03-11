package es.usal.podcast.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import es.usal.podcast.R;
import es.usal.podcast.adapters.DividerItemDecoration;
import es.usal.podcast.adapters.UsuarioRecyclerAdapter;
import es.usal.podcast.modelo.Usuario;
import es.usal.podcast.modelo.UsuarioDAO;
import es.usal.podcast.modelo.UsuarioDAOImpl;

/**
 * Fragment que mostrará los usuarios subscritos a un programa determinado
 */
public class SubscriptoresAlProgramaFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Usuario> usuarios;
    private int programaId;

    /**
     * Crea una instancia del fragment con el id del programa
     * @param programaId id del programa que queremos ver los subscriptores
     * @return nueva instancia del fragment
     */

    public static SubscriptoresAlProgramaFragment newInstance(int programaId) {
        SubscriptoresAlProgramaFragment fragment = new SubscriptoresAlProgramaFragment();
        Bundle args = new Bundle();
        args.putInt("programaId", programaId);
        fragment.setArguments(args);
        return fragment;
    }

    public SubscriptoresAlProgramaFragment() {
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

        Log.d(getClass().getName(), "ProgramaID "+programaId);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefresh);
        cargarSubscriptores();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarSubscriptores();
            }
        });
    }


    /**
     * Carga los subscriptores del programa
     */
    private void cargarSubscriptores(){

        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);

        new Thread(){
            public void run(){

                UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
                usuarios = usuarioDAO.getSubscriptores(programaId);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * Handler para mostrar los subscriptores en el RecyclerView
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);

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
