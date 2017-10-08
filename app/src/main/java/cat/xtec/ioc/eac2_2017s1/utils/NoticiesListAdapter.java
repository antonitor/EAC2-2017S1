package cat.xtec.ioc.eac2_2017s1.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cat.xtec.ioc.eac2_2017s1.R;
import cat.xtec.ioc.eac2_2017s1.data.Contracte.Noticies;
/**
 * Created by Toni on 08/10/2017.
 */

public class NoticiesListAdapter extends RecyclerView.Adapter<NoticiesListAdapter.NoticiaHolder>{

    private Cursor mCursor;
    private Context mContext;
    public NoticiesListAdapter(Context context, Cursor cursor) {
        this.mContext = context;
        this.mCursor = cursor;
    }

    @Override
    public NoticiaHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.noticia_item, parent, false);
        return new NoticiaHolder(view);
    }

    @Override
    public void onBindViewHolder(NoticiaHolder holder, int position) {

        if (!mCursor.moveToPosition(position)) {
            return;
        }

        String thumnail = mCursor.getString(mCursor.getColumnIndex(Noticies.THUMBNAIL));
        String titol = mCursor.getString(mCursor.getColumnIndex(Noticies.TITOL));
        long id = mCursor.getLong(mCursor.getColumnIndex(Noticies._ID));

        holder.thumbnailImageView.setImageURI(Uri.parse(thumnail));
        holder.titolNoticiaTextView.setText(titol);
        holder.itemView.setTag(id);

    }

    public void swapCursor(Cursor newCursor) {
        if (mCursor != null) mCursor.close();
        mCursor = newCursor;
        if (newCursor != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
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

