package cat.xtec.ioc.eac2_2017s1;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

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

public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<Noticia>>{

    private NoticiesListAdapter mAdapter;
    private SQLiteDatabase mBD;
    private RecyclerView noticiesRecyclerView;
    private static final int NOTICIES_LOADER_ID = 0;
    public final static String LOG_TAG = "TESTING -------->>>>>  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        noticiesRecyclerView = (RecyclerView) findViewById(R.id.noticies_recycler_view);
        noticiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        AjudaBD ajudaBD = new AjudaBD(this);
        mBD = ajudaBD.getReadableDatabase();

        //fillWithFakeData();
        if (comprovaXarxa(this)) {
            LoaderCallbacks<ArrayList<Noticia>> callback = MainActivity.this;
            getSupportLoaderManager().initLoader(NOTICIES_LOADER_ID, null, callback);
        } else {
            fillRecyclerFromSQLite(getNoticies());
        }

    }


    private void fillRecyclerFromSQLite (Cursor cursor) {
        mAdapter = new NoticiesListAdapter(this, cursor);
        noticiesRecyclerView.setAdapter(mAdapter);
    }

    private Cursor getNoticies() {
        Log.d(LOG_TAG, "Recollint noticies de la BD");
        return mBD.query(
                Noticies.NOM_TAULA,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
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
                Toast.makeText(this,"REFRESH!",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private long fillWithFakeData() {
        ContentValues cv = new ContentValues();
        cv.put(Noticies.TITOL, "Noticia Falsa per omplir el recycler");
        cv.put(Noticies.AUTOR, "Pep");
        cv.put(Noticies.DESCRIPCIO, "Tan sols és una noticia falsa");
        cv.put(Noticies.DATA_PUBLICACIO, "08-10-2017");
        cv.put(Noticies.CATEGORIA, "Noticies Falses");
        cv.put(Noticies.ENLLAC, "www.noticia-falsa.fal");
        cv.put(Noticies.THUMBNAIL, "");
        mBD.insert(Noticies.NOM_TAULA,null,cv);
        mBD.insert(Noticies.NOM_TAULA,null,cv);
        return mBD.insert(Noticies.NOM_TAULA,null,cv);
    }

    @Override
    public Loader<ArrayList<Noticia>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Noticia>>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
            }

            @Override
            public ArrayList<Noticia> loadInBackground() {
                URL url = NetworkUtils.buildURl(NetworkUtils.sUrl);
                Log.d(LOG_TAG, NetworkUtils.sUrl);
                InputStream in;
                try {
                    in = NetworkUtils.getResponseFromHttpUrl(url);
                } catch (IOException ioe){
                    Log.d(LOG_TAG, "Error al realitzar la connexió.");
                    in = null;
                }

                try {
                    return (ArrayList<Noticia>) new MarcaXmlParser().analitza(in);
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                    Log.d(LOG_TAG, "Error de contingut XML.");
                    return null;
                } catch (IOException e) {
                    Log.d(LOG_TAG, "Error d'inputStream al XMLParser.");
                    e.printStackTrace();
                    return null;
                } finally {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Noticia>> loader, ArrayList<Noticia> data) {
        if (data != null) {
            Log.d(LOG_TAG, "Array empty: " + data.isEmpty());
            for (Noticia noticia : data) {
                Log.d(LOG_TAG, noticia.titol);
                ContentValues cv = new ContentValues();
                cv.put(Noticies.TITOL, noticia.titol);
                cv.put(Noticies.AUTOR, noticia.autor);
                cv.put(Noticies.DESCRIPCIO, noticia.descripcio);
                cv.put(Noticies.DATA_PUBLICACIO, noticia.data);
                cv.put(Noticies.CATEGORIA, "");
                cv.put(Noticies.ENLLAC, "");
                cv.put(Noticies.THUMBNAIL, "");
                mBD.insert(Noticies.NOM_TAULA, null, cv);
            }

        }
        fillRecyclerFromSQLite(getNoticies());
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Noticia>> loader) {

    }
}
