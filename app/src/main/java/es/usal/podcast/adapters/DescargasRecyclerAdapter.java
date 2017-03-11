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
import es.usal.podcast.modelo.Capitulo;

/**
 * Adaptador para capítulos descargados (además de la información de un capítulo, muestra el logo del programa)
 * @author Jorge Alonso Merchán
 */
public class DescargasRecyclerAdapter extends RecyclerView.Adapter<DescargasRecyclerAdapter.ViewHolder> {

    private List<Capitulo> mItems;

    public DescargasRecyclerAdapter(List<Capitulo> items) {
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
        public TextView extra, extra2;
        public Capitulo item;
        public ImageView imagen;
        public Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.programa_adapter_titulo);
            extra = (TextView) itemView.findViewById(R.id.programa_adapter_subtitle);
            extra2 = (TextView) itemView.findViewById(R.id.programa_adapter_extra2);
            imagen = (ImageView) itemView.findViewById(R.id.programa_adapter_cover);
            context = itemView.getContext();
        }

        public void setData(Capitulo item) {
            this.item = item;
            title.setText(item.getTitulo());
            extra.setText(item.getFechaHace(context.getResources()));
            extra2.setText(String.format(context.getResources().getString(R.string.duracion), item.getDuracion()));
            Picasso.with(context).load(item.getImagenSmall()).into(imagen);
        }

        @Override
        public void onClick(View v) {
            Dialog d = new CapituloDialog(context, R.style.DialogStyle, item);
            d.getWindow().setGravity(Gravity.BOTTOM);
            d.setCancelable(true);
            d.show();
        }
    }
}