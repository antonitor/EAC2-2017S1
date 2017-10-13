package cat.xtec.ioc.eac2_2017s1.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import cat.xtec.ioc.eac2_2017s1.R;
import cat.xtec.ioc.eac2_2017s1.data.Noticia;

/**
 * Created by Toni on 08/10/2017.
 */
public class NoticiesListAdapter extends RecyclerView.Adapter<NoticiesListAdapter.NoticiaHolder>{

    //Aquesta és la variable que referencia el dataSet que mostrará el RecyclerView
    private ArrayList<Noticia> mNoticiesList;
    private Context mContext;
    private final NoticiesListAdapterOnClickHandler mClickHandler;

    /**
     * Interfície que implementará l'Activity que mostra el RecyclerView per tal de forçar
     * l'implementació del mètode onClick que reb per paràmetre la noticia del item clicat
     */
    public interface NoticiesListAdapterOnClickHandler {
        void onClick(Noticia noticia);
    }

    /**
     * El constructor d'aquest RecyvlerView.Adapter reb el context de l'activitat que conté
     * el RecyclerView i una instància de la interfície NoticiesListAdapterOnClickHandler implementat
     * en la primera.
     *
     * @param context context de l'activitat que conté el RecyclerView
     * @param mClickHandler instància de la interfície NoticiesListAdapterOnClickHandler implementam
     * en a l'Activity que conté el RecyclerView
     */
    public NoticiesListAdapter(Context context, NoticiesListAdapterOnClickHandler mClickHandler) {
        this.mContext = context;
        this.mClickHandler = mClickHandler;
    }

    /**
     * En crear el NoticiesHolder s'infla amb el layout noticia_item
     */
    @Override
    public NoticiaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.noticia_item, parent, false);
        return new NoticiaHolder(view);
    }

    /**
     * En afegir el NoticiesHolder al nostre RecyclerView hi afegim el thumbnail i el title
     * @param holder el NoticiaHolder que s'afegeix al recycler
     * @param position posició del NoticiaHolder que correspón amb la posició a la nostra llista
     *                 de noticies.
     */
    @Override
    public void onBindViewHolder(NoticiaHolder holder, int position) {

        String thumnail = mNoticiesList.get(position).getThumbnail();
        String titol = mNoticiesList.get(position).getTitol();

        if (Drawable.createFromPath(thumnail)!=null){
            holder.thumbnailImageView.setImageDrawable(Drawable.createFromPath(thumnail));
        } else {
            holder.thumbnailImageView.setImageResource(R.drawable.nofound);
        }

        holder.titolNoticiaTextView.setText(titol);
    }

    /**
     * Reescriu la variable membre que conté el nostre dataSet i en notifica els canvis al adaptador
     * per tal de repercutir-los al RecyclerView.
     * @param novesNoticies nova llista de Noticies
     */
    public void setNoticiesList(ArrayList<Noticia> novesNoticies) {
        mNoticiesList = novesNoticies;
        this.notifyDataSetChanged();
    }

    /**
     * Torna la mida del dataSet que omple el RecyclerView, en cas de ser null torna 0
     * @return mida de la llsita
     */
    @Override
    public int getItemCount() {
        if (null == mNoticiesList) return 0;
        return mNoticiesList.size();
    }

    /**
     * Clase que hereta de RecyclerView.ViewHolder i que conté un ImageView y un TextView.
     * Incorpora els listeners OnClickListener que torna la Noticia corresponent al ítem i
     * OOnLongClickListener que torna la possició del ítem al RecycleRView
     */
    class NoticiaHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

        ImageView thumbnailImageView;
        TextView titolNoticiaTextView;

        public NoticiaHolder(View itemView) {
            super(itemView);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_image_view);
            titolNoticiaTextView = (TextView) itemView.findViewById(R.id.titol_noticia_text_view);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        /**
         * Sobreescriptura del mètode OnClick de l'interfíce OnClickListener que envía la Noticia
         * que correspon al item on s'ha fet click per tal de disposar d'aquesta noticia al
         * Activity on implementem el RecyclerView
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Noticia noticia = mNoticiesList.get(adapterPosition);
            mClickHandler.onClick(noticia);
        }

        /**
         * Sobreescriptura del mètode onLongClick de la interficie OnLongClickListener que esborra
         * la Noticia corresponent a la posició on s'ha fet un click llarg tant del dataSet com
         * del RecyclerView.
         */
        @Override
        public boolean onLongClick(View view) {
            int adapterPosition = getAdapterPosition();
            mNoticiesList.remove(adapterPosition);
            notifyItemRemoved(adapterPosition);
            return true;
        }
    }
}

