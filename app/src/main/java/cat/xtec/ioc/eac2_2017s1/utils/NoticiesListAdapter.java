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

    private ArrayList<Noticia> mNoticiesList;
    private Context mContext;
    public NoticiesListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public NoticiaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.noticia_item, parent, false);
        return new NoticiaHolder(view);
    }

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

    public void setNoticiesList(ArrayList<Noticia> novesNoticies) {
        mNoticiesList = novesNoticies;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mNoticiesList) return 0;
        return mNoticiesList.size();
    }

    class NoticiaHolder extends RecyclerView.ViewHolder{

        ImageView thumbnailImageView;
        TextView titolNoticiaTextView;

        public NoticiaHolder(View itemView) {
            super(itemView);
            thumbnailImageView = (ImageView) itemView.findViewById(R.id.thumbnail_image_view);
            titolNoticiaTextView = (TextView) itemView.findViewById(R.id.titol_noticia_text_view);
        }
    }
}

