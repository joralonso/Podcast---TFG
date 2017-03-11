package es.usal.podcast.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import es.usal.podcast.R;
import es.usal.podcast.UsuarioActivity;
import es.usal.podcast.modelo.Usuario;

/**
 * Adaptador de una lista de Usuarios en un RecyclerView
 * @author Jorge Alonso Merchán
 */
public class UsuarioRecyclerAdapter extends RecyclerView.Adapter<UsuarioRecyclerAdapter.ViewHolder> {

    private List<Usuario> mItems;

    public UsuarioRecyclerAdapter(List<Usuario> items) {
        mItems = items;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_usuarios, parent, false));
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
        public ImageView imagen;
        public Usuario item;
        public Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = (TextView) itemView.findViewById(R.id.usuarios_adapter_titulo);
            imagen = (ImageView) itemView.findViewById(R.id.usuarios_adatpter_cpver);
            context = itemView.getContext();
        }

        public void setData(Usuario item) {
            this.item = item;
            title.setText(item.getNombre());
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, UsuarioActivity.class);
            i.putExtra("usuarioid", item.getId());
            i.putExtra("nombre", item.getNombre());
            context.startActivity(i);
        }
    }
}