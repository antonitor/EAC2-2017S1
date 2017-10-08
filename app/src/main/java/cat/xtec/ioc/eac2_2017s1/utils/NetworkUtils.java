package cat.xtec.ioc.eac2_2017s1.utils;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static cat.xtec.ioc.eac2_2017s1.MainActivity.LOG_TAG;

/**
 * Created by Toni on 08/10/2017.
 */

public class NetworkUtils {

    public static final String sUrl = "http://estaticos.marca.com/rss/portada.xml";

    public static InputStream getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Log.d(LOG_TAG, "HTTPURLCONNECTION");
        try {
            return urlConnection.getInputStream();
        } catch (IOException ioe) {
            Log.d(LOG_TAG, "Impossible obtenir inputStream.");
            return null;
        } finally {
            urlConnection.disconnect();
        }
    }

    public static URL buildURl(String url) {

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            Log.d(LOG_TAG, "Impossible construïr la URL");
            return null;
        }
    }

}
