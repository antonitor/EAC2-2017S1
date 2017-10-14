package cat.xtec.ioc.eac2_2017s1;

import android.support.v7.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;

import cat.xtec.ioc.eac2_2017s1.utils.NetworkUtils;

public class WebActivity extends AppCompatActivity {

    private Intent mIntent;
    private String mTitle;
    private String mLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        //Funcionalitat afegida que no es demana al enunciat del EAC2
        //Mostra el botó per tornar a MainActivity
        ActionBar ab =  getSupportActionBar();
        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);

        //Recollim el widged WebView en una variable membre
        WebView webview = (WebView) this.findViewById(R.id.web_view);

        //Recollim el Intent que ha llençat aquesta Activity i en recollim els Extras xtitol i xenllac
        mIntent = getIntent();
        mTitle = mIntent.getStringExtra(getString(R.string.titol_extra));
        mLink = mIntent.getStringExtra(getString(R.string.enllac_extra));
        //Afegim el títol a l'Activity
        this.setTitle(mTitle);

        //Si tenim xarxa carreguem la pàgina web del enllaç, si no construim un String amb format
        //html amb els camps emmagatzemats que componen la noticia
        if (NetworkUtils.comprovaXarxa(this)) {
            webview.loadUrl(mLink);
        } else {
            String summary = buildHtmlNoticia();
            webview.loadData(summary, "text/html; charset=UTF-8", null);
        }
    }

    /**
     * A partir dels atributs del objexte Noticia pasats com Extras a aquest Activity construïm
     * un String amb formàt html per tal de mostrar el titol, descripció, autor, categories i data
     * de la noticia
     * @return String en forma de html
     */
    public String buildHtmlNoticia() {
        String descripcio = mIntent.getStringExtra(getString(R.string.descripcio_extra));
        String autor = mIntent.getStringExtra(getString(R.string.autor_extra));
        String categoria = mIntent.getStringExtra(getString(R.string.categoria_extra));
        String data = mIntent.getStringExtra(getString(R.string.data_extra));

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("<html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta "
                + "http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">");
        sBuilder.append("<body>");
        sBuilder.append("<h3>"+ mTitle +"</h3>");
        sBuilder.append("<hr />");
        sBuilder.append("<p>"+ descripcio +"</p>");
        sBuilder.append("<hr />");
        sBuilder.append("<p  align=\"right\"><i>"+ autor +"</i></p>");
        sBuilder.append("<hr />");
        sBuilder.append("<p><b>Categories: </b>"+ categoria +"</p>");
        sBuilder.append("<p>"+ data +"</p>");
        sBuilder.append("</html></body>");
        return sBuilder.toString();
    }

    /**
     * Funcionalitat afegida que no es demana al enunciat del EAC2
     *
     * En premer el botó enrere que es mostra al costat del titol al ActionBar es torna
     * a MainActivity i es tanca aquesta.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}
