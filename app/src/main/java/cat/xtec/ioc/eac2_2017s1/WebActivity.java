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
        mTitle = mIntent.getStringExtra("titol");
        mLink = mIntent.getStringExtra("enllac");
        this.setTitle(mTitle);

        if (NetworkUtils.comprovaXarxa(this)) {
            webview.loadUrl(mLink);
        } else {
            String summary = buildHtmlNoticia();
            webview.loadData(summary, "text/html", null);
        }
    }

    public String buildHtmlNoticia() {
        String descripcio = mIntent.getStringExtra("descripcio");
        String autor = mIntent.getStringExtra("autor");
        String categoria = mIntent.getStringExtra("categoria");
        String data = mIntent.getStringExtra("data");

        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append("<html><body>");
        sBuilder.append("<h1>"+ mTitle +"</h1>");
        sBuilder.append("<p>"+ descripcio +"</p>");
        sBuilder.append("<p>"+ autor +"</p>");
        sBuilder.append("<p><b>Categoria: </b>"+ categoria +"</p>");
        sBuilder.append("<p>"+ data +"</p>");
        sBuilder.append("</html></body>");
        return sBuilder.toString();
    }


}
