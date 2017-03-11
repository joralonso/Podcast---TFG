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
import es.usal.podcast.adapters.TimelineAdapter;
import es.usal.podcast.modelo.CapituloDAO;
import es.usal.podcast.modelo.CapituloDAOImpl;
import es.usal.podcast.modelo.Timeline;

/**
 * Fragment que mostrará el timeline (la actividad de los otros usuarios)
 * @author Jorge Alonso Merchán
 */
public class TimelineFragment extends Fragment {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private List<Timeline> timelines;
    private CardView cardView;

    public TimelineFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeRefresh);
        cardView = (CardView) getView().findViewById(R.id.card_view);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cargarTimeline();
            }
        });

    }

    public void onResume(){
        super.onResume();
        cargarTimeline();
    }


    /**
     * Carga el Timeline del usuario
     */
    private void cargarTimeline(){

        refreshLayout.setRefreshing(true);
        recyclerView.setVisibility(View.GONE);
        cardView.setVisibility(View.GONE);

        new Thread(){
            public void run(){

                CapituloDAO dao = new CapituloDAOImpl(getContext());

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getContext());
                String token = settings.getString("token", "");

                timelines = dao.getTimeline(token);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * Handler que muestra el timeline en el RecyclerView
     */
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {

            refreshLayout.setRefreshing(false);

            if (timelines != null ){
                recyclerView.setVisibility(View.VISIBLE);
                cardView.setVisibility(View.GONE);
                TimelineAdapter itemAdapter = new TimelineAdapter(timelines);
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.addItemDecoration(new DividerItemDecoration(getContext()));
                recyclerView.setAdapter(itemAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }else{
                cardView.setVisibility(View.VISIBLE);
            }
        }

    };
}
