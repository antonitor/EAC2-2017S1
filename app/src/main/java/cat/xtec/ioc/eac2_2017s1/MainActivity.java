package cat.xtec.ioc.eac2_2017s1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import cat.xtec.ioc.eac2_2017s1.data.AjudaBD;
import cat.xtec.ioc.eac2_2017s1.data.Contracte.Noticies;
import cat.xtec.ioc.eac2_2017s1.utils.MarcaXmlParser;
import cat.xtec.ioc.eac2_2017s1.utils.NetworkUtils;
import cat.xtec.ioc.eac2_2017s1.utils.NoticiesListAdapter;
import cat.xtec.ioc.eac2_2017s1.utils.MarcaXmlParser.Noticia;

import static cat.xtec.ioc.eac2_2017s1.utils.NetworkUtils.comprovaXarxa;

public class MainActivity extends AppCompatActivity {

    private final String MARCA_URL = "http://estaticos.marca.com/rss/portada.xml";

    private NoticiesListAdapter mAdapter;
    AjudaBD mAjudaBD;
    SQLiteDatabase mDB;
    private RecyclerView mNoticiesRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private ArrayList<Noticia> mLlistaNoticies;
    private Context mContext;
    public final static String LOG_TAG = "TESTING -------->>>>>  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNoticiesRecyclerView = (RecyclerView) findViewById(R.id.noticies_recycler_view);
        mNoticiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mContext = this;


        mAjudaBD = new AjudaBD(this);
        mAdapter = new NoticiesListAdapter(this);
        mNoticiesRecyclerView.setAdapter(mAdapter);
        loadNoticies();
    }

    private void loadNoticies() {
        showNoticiesView();

        if (comprovaXarxa(this)) {
            new FetchNoticiesTask().execute(MARCA_URL);
        } else {
            mLlistaNoticies = loadLlistaNoticiesFromDB();
            mAdapter.setNoticiesList(mLlistaNoticies);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                Toast.makeText(this,"SEARCH!",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_refresh:
                loadNoticies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public class FetchNoticiesTask extends AsyncTask<String, Void, ArrayList<Noticia>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Noticia> doInBackground(String... params) {

            if (params.length==0) {
                return null;
            }

            URL url = NetworkUtils.buildURl(params[0]);
            InputStream inputStream = null;
            try {
                inputStream = NetworkUtils.getResponseFromHttpUrl(url);
            } catch (IOException ioe){
                Toast.makeText(mContext, "Error de connexió", Toast.LENGTH_SHORT).show();
                return null;
            }

            try {
                ArrayList<Noticia> llista = (ArrayList<Noticia>) new MarcaXmlParser().analitza(inputStream);
                mLlistaNoticies = llista;
                storeNoticesOnDB(llista);
                Log.d(LOG_TAG, "Exit en doInBackgroud. Noticies list size: " + llista.size());
                return llista;
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error doInBackground");
                e.printStackTrace();
                return null;
            } finally {
                NetworkUtils.closeInputStream(inputStream);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Noticia> noticies) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (noticies != null) {
                showNoticiesView();
                mAdapter.setNoticiesList(noticies);
            } else {
                showErrorMessage();
            }
        }
    }


    private ArrayList<Noticia> loadLlistaNoticiesFromDB() {
        mDB = mAjudaBD.getReadableDatabase();
        Cursor cursor = mDB.query(
                Noticies.NOM_TAULA,
                null,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<Noticia> llista = new ArrayList<Noticia>();
        if (cursor.moveToFirst()) {
             do {
                String title = cursor.getString(cursor.getColumnIndex(Noticies.TITOL));
                String author = cursor.getString(cursor.getColumnIndex(Noticies.TITOL));
                String link = cursor.getString(cursor.getColumnIndex(Noticies.TITOL));
                String desc = cursor.getString(cursor.getColumnIndex(Noticies.TITOL));
                String date = cursor.getString(cursor.getColumnIndex(Noticies.TITOL));
                String category = cursor.getString(cursor.getColumnIndex(Noticies.TITOL));
                String thumb = cursor.getString(cursor.getColumnIndex(Noticies.THUMBNAIL));
                llista.add(new Noticia(title, author, link, desc, date, category, thumb));
            }while (cursor.moveToNext());
        }
        mDB.close();
        return llista;
    }

    public void storeNoticesOnDB( ArrayList<Noticia> noticies){
        cleanNoticiesTable();
        for (Noticia noticia : noticies) {
            ContentValues cv = new ContentValues();
            cv.put(Noticies.TITOL, noticia.titol);
            cv.put(Noticies.AUTOR, noticia.autor);
            cv.put(Noticies.DESCRIPCIO, noticia.descripcio);
            cv.put(Noticies.DATA_PUBLICACIO, noticia.data);
            cv.put(Noticies.CATEGORIA, noticia.categoria);
            cv.put(Noticies.ENLLAC, noticia.enllac);
            cv.put(Noticies.THUMBNAIL, noticia.thumbnail);

            mDB = mAjudaBD.getWritableDatabase();
            mDB.insert(Noticies.NOM_TAULA, null, cv);
            mDB.close();
        }
    }

    private void cleanNoticiesTable() {
        mDB = mAjudaBD.getWritableDatabase();
        mDB.delete(Noticies.NOM_TAULA,null,null);
        mDB.close();
    }

    private void showErrorMessage() {
        mNoticiesRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showNoticiesView() {
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        mNoticiesRecyclerView.setVisibility(View.VISIBLE);
    }

}
