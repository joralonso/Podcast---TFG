package es.usal.podcast.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import es.usal.podcast.CapituloDialog;
import es.usal.podcast.R;
import es.usal.podcast.modelo.Capitulo;

/**
 * Adaptador de una lista de capítulos en un RecyclerView
 * @author Jorge Alonso Merchán *
 */
public class CapituloRecyclerAdapter extends RecyclerView.Adapter<CapituloRecyclerAdapter.ViewHolder> {

    private List<Capitulo> mItems;

    public CapituloRecyclerAdapter(List<Capitulo> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_capitulos, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        public TextView title;
        public TextView extra;
        public TextView extra2;
        public Capitulo item;
        public Context context;
        private CapituloDialog d;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.capituloTitle);
            extra = (TextView) itemView.findViewById(R.id.capituloExtra);
            extra2 = (TextView) itemView.findViewById(R.id.capituloExtra2);
            context = itemView.getContext();
        }

        public void setData(Capitulo item) {
            this.item = item;
            title.setText(item.getTitulo());
            extra.setText(item.getFechaHace(context.getResources()));
            extra2.setText(String.format(context.getResources().getString(R.string.duracion), item.getDuracion()));

        }

        @Override
        public void onClick(View v) {
            d = new CapituloDialog(context, R.style.DialogStyle, item);
            d.getWindow().setGravity(Gravity.BOTTOM);
            d.setCancelable(true);
            d.show();
        }



    }
}