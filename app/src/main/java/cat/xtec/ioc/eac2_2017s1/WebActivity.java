package cat.xtec.ioc.eac2_2017s1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import cat.xtec.ioc.eac2_2017s1.utils.NetworkUtils;

public class WebActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WebView webview = new WebView(this);
        setContentView(webview);

        Intent intentThatStartedThisActivity = getIntent();
        String title = intentThatStartedThisActivity.getStringExtra("title");
        this.setTitle(title);

        if (NetworkUtils.comprovaXarxa(this)) {
            String url = intentThatStartedThisActivity.getStringExtra("url");
            webview.loadUrl(url);
        }
    }
}
