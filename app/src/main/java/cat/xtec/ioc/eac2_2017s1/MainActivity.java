package cat.xtec.ioc.eac2_2017s1;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
import cat.xtec.ioc.eac2_2017s1.data.Noticia;

import static cat.xtec.ioc.eac2_2017s1.utils.NetworkUtils.comprovaXarxa;

/**
 * L'Activity principal implementa la interfície NoticiesListAdapterOnClickHandler per tal obligar a l'implementació del mètode onClick
 * que rep un objecte tipus Noticia corresponent al item clickat.
 * Implementa també OnQueryTextListener que obliga a l'implementació dels mètodes que actualitzen el recyclerView així com s'insereix
 * text al quadre de búsqueda SearchView.
 */
public class MainActivity extends AppCompatActivity implements NoticiesListAdapter.NoticiesListAdapterOnClickHandler, SearchView.OnQueryTextListener {

    private final String MARCA_URL = "http://estaticos.marca.com/rss/portada.xml";

    private String mCacheDir;
    private NoticiesListAdapter mAdapter;
    private AjudaBD mAjudaBD;
    private SQLiteDatabase mDB;
    private RecyclerView mNoticiesRecyclerView;
    private ProgressBar mLoadingIndicator;
    private TextView mErrorMessageDisplay;
    private ArrayList<Noticia> mLlistaNoticies;
    private Context mContext;
    private boolean isAppStarting = true;
    public final static String LOG_TAG = "TESTING -------->>>>>  ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Obtenim una variable amb la referencia al RecyclerView del layout content_view
        mNoticiesRecyclerView = (RecyclerView) findViewById(R.id.noticies_recycler_view);
        //Si sabem que les dimensions del RecyclerView no han de canviar en millorem el rendiemnt
        //amb aquest mètode.
        mNoticiesRecyclerView.setHasFixedSize(true);
        mNoticiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        //En cas d'error al carregar les dades al RecyclerView mostrarem aquest missatge d'error
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);
        mContext = this;
        //Ruta al directori caché d'aquesta aplicació
        mCacheDir  = this.getCacheDir().toString();

        //Instància de la nostra classe que hereta de SQLiteOpenHelper
        mAjudaBD = new AjudaBD(this);
        //L'adapter pel recyclerView que agafa com a paràmetre el Context i el Listener
        mAdapter = new NoticiesListAdapter(this, this);
        mNoticiesRecyclerView.setAdapter(mAdapter);

        loadNoticies();
    }

    /**
     * Aquest mètode comprova si tenim conexió, sí és així llença la tasca que descarrega el xml
     * d'internet, el pasa pel XMLPullParser i en retorna un ArrayList amb les Noticies.
     *
     * Si no hi ha internet i és el primer cop que engeguem l'aplicació, prova de carregar les
     * noticies de la base de dades SQLite, pro si l'aplicació ja ha carregat noticies abans
     * tan sols mostra un missatge advertint que no hi ha connexió i no fa res mes. Tal i com
     * demana l'enunciat del EAC.
     */
    private void loadNoticies() {
        showNoticiesView();
        if (comprovaXarxa(this)) {
            isAppStarting = false;
            new DownloadNoticiesTask().execute(MARCA_URL);
        } else if (isAppStarting) {
            isAppStarting = false;
            Toast.makeText(this,"Carrega offline de noticies.", Toast.LENGTH_LONG).show();
            mLlistaNoticies = loadLlistaNoticiesFromDB();
            mAdapter.setNoticiesList(mLlistaNoticies);
        } else {
            Toast.makeText(this,"No hi ha connexió!!", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Carrega el menú al ActionBar i hi afegeix un SearchView amb un listener per tal de
     * d'actualitzar el RecyclerView així com s'insereix text
     *
     * @param menu menú a inflar
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    /**
     * Quan es sel·lecciona l'opció del menú de refrescar truca el mètode loadNoticies()
     *
     * @param item el item premut
     * @return true si s'ha premut l'ítem action_refresh
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_refresh:
                loadNoticies();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Mètode implementat de l'interfície NoticiesListAdapterOnClickHandler que s'encarrega
     * de llençar l'Activity WebActivity amb tots els components que forment una noticia.
     *
     * @param noticia objecte Noticia corresponent al ítem clickat al recyclerView
     */
    @Override
    public void onClick(Noticia noticia) {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra("titol", noticia.getTitol());
        intent.putExtra("descripcio", noticia.getDescripcio());
        intent.putExtra("categoria", noticia.getCategoria());
        intent.putExtra("autor", noticia.getAutor());
        intent.putExtra("data", noticia.getData());
        intent.putExtra("enllac", noticia.getEnllac());
        startActivity(intent);
    }

    /**
     * Mètode implementat degut a l'implementació de l'interfície OnQueryTextListener en aquesta
     * classe. Truca el mètode onQueryTextChange passant el parametre query amb la el text inerit
     * al SearchView.
     *
     * @param query text inserit al SearchView
     * @return
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return onQueryTextChange(query);
    }

    /**
     * Mètode implementat degut a l'implementació de l'interfície OnQueryTextListener en aquesta
     * classe.
     *
     * Crea una nova llista de noticies, l'omple amb aquelles que contenen el text inserit al
     * SearchView i la pasa al mètode setNoticiesList() que actualitza el contingut del RecyclerView
     *
     * @param newText text inserit al SearchView
     * @return true
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        newText = newText.toLowerCase();
        ArrayList<Noticia> llistaNoticiesFiltrada = new ArrayList<Noticia>();
        for (Noticia noticia : mLlistaNoticies) {
            if (noticia.getTitol().toLowerCase().contains(newText)) {
                llistaNoticiesFiltrada.add(noticia);
            }
        }
        mAdapter.setNoticiesList(llistaNoticiesFiltrada);
        return true;
    }

    /**
     * Clase que hereta d'AsyncTasc. Reb un String com a paràmetre i tornarà un ArrayList un cop
     * s'ha dut a terme l
     */
    private class DownloadNoticiesTask extends AsyncTask<String, Void, ArrayList<Noticia>> {

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
                mLlistaNoticies= (ArrayList<Noticia>) new MarcaXmlParser().analitza(inputStream);
                downloadImagesToCache();
                storeNoticesOnDB(mLlistaNoticies);
                Log.d(LOG_TAG, "Exit doInBackgroud. Noticies list size: " + mLlistaNoticies.size());
                return mLlistaNoticies;
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
                String author = cursor.getString(cursor.getColumnIndex(Noticies.AUTOR));
                String link = cursor.getString(cursor.getColumnIndex(Noticies.ENLLAC));
                String desc = cursor.getString(cursor.getColumnIndex(Noticies.DESCRIPCIO));
                String date = cursor.getString(cursor.getColumnIndex(Noticies.DATA_PUBLICACIO));
                String category = cursor.getString(cursor.getColumnIndex(Noticies.CATEGORIA));
                String thumb = cursor.getString(cursor.getColumnIndex(Noticies.THUMBNAIL));
                llista.add(new Noticia(title, author, link, desc, date, category, thumb));
            }while (cursor.moveToNext());
        }
        mDB.close();
        return llista;
    }

    private void storeNoticesOnDB( ArrayList<Noticia> noticies){
        cleanNoticiesTable();
        for (Noticia noticia : noticies) {
            ContentValues cv = new ContentValues();
            cv.put(Noticies.TITOL, noticia.getTitol());
            cv.put(Noticies.AUTOR, noticia.getAutor());
            cv.put(Noticies.DESCRIPCIO, noticia.getDescripcio());
            cv.put(Noticies.DATA_PUBLICACIO, noticia.getData());
            cv.put(Noticies.CATEGORIA, noticia.getCategoria());
            cv.put(Noticies.ENLLAC, noticia.getEnllac());
            cv.put(Noticies.THUMBNAIL, noticia.getThumbnail());

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

    private void downloadImagesToCache() {
        int count = 1;
        for (Noticia noticia : mLlistaNoticies) {
            String path_imatge = mCacheDir + "imatge"+count;  //UUID.randomUUID().toString().replace("-","");
            NetworkUtils.downloadImageToCache(noticia.getThumbnail(), path_imatge);
            noticia.setThumbnail(path_imatge);
            count++;
        }
    }

}
