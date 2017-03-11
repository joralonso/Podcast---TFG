package es.usal.podcast.adapters;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.usal.podcast.CapituloDialog;
import es.usal.podcast.R;
import es.usal.podcast.modelo.Timeline;

/**
 * Adaptador de Timeline en un RecyclerView
 * @author Jorge Alonso Merchán
 */
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private List<Timeline> mItems;

    public TimelineAdapter(List<Timeline> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_timeline, parent, false));
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
        public TextView subtitulo, extra, usuario;
        public Timeline item;
        public ImageView imagen;
        public Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.timeline_titulo);
            subtitulo = (TextView) itemView.findViewById(R.id.timeline_subtitulo);
            extra = (TextView) itemView.findViewById(R.id.timeline_extra);
            usuario = (TextView) itemView.findViewById(R.id.timeline_usuario);
            imagen = (ImageView) itemView.findViewById(R.id.timeline_cover);
            context = itemView.getContext();
        }

        public void setData(Timeline item) {
            this.item = item;
            usuario.setText(item.getTitulo(context.getResources()));
            title.setText(item.getCapitulo().getTitulo());
            subtitulo.setText(item.getCapitulo().getFechaHace(context.getResources()));
            extra.setText(String.format(context.getResources().getString(R.string.duracion), item.getCapitulo().getDuracion()));
            Picasso.with(context).load(item.getCapitulo().getImagenSmall()).into(imagen);
        }

        @Override
        public void onClick(View v) {
            Dialog d = new CapituloDialog(context, R.style.DialogStyle, item.getCapitulo());
            d.getWindow().setGravity(Gravity.BOTTOM);
            d.setCancelable(true);
            d.show();
        }
    }
}