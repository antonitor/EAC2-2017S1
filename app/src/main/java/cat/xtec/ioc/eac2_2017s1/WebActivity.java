package cat.xtec.ioc.eac2_2017s1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

        WebView webview = (WebView) this.findViewById(R.id.web_view);

        mIntent = getIntent();
        mTitle = mIntent.getStringExtra(getString(R.string.titol_extra));
        mLink = mIntent.getStringExtra(getString(R.string.enllac_extra));
        this.setTitle(mTitle);

        if (NetworkUtils.comprovaXarxa(this)) {
            webview.loadUrl(mLink);
        } else {
            String summary = buildHtmlNoticia();
            webview.loadData(summary, "text/html; charset=UTF-8", null);
        }
    }

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


}
