package es.usal.podcast.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.usal.podcast.ProgramaActivity;
import es.usal.podcast.R;
import es.usal.podcast.modelo.Programa;

/**
 * Adaptador de programas en un RecyclerView
 * @author Jorge Alonso Merchán
 */
public class ProgramaRecyclerAdapter extends RecyclerView.Adapter<ProgramaRecyclerAdapter.ViewHolder> {

    private List<Programa> mItems;

    public ProgramaRecyclerAdapter(List<Programa> items) {
        mItems = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_programas, parent, false));
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
        public ImageView imagen;
        public Programa item;
        public Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.programa_adapter_titulo);
            extra = (TextView) itemView.findViewById(R.id.programa_adapter_subtitle);
            imagen = (ImageView) itemView.findViewById(R.id.programa_adapter_cover);
            context = itemView.getContext();
        }

        public void setData(Programa item) {
            this.item = item;
            title.setText(item.getTitulo());
            extra.setText(item.getCategoria());
            Picasso.with(context).load(item.getImagenSmall()).into(imagen);
        }

        @Override
        public void onClick(View v) {

            Intent i = new Intent(context, ProgramaActivity.class);
            item.setIntent(i);
            context.startActivity(i);
        }
    }
}