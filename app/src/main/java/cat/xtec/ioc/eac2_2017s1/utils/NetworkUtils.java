package cat.xtec.ioc.eac2_2017s1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cat.xtec.ioc.eac2_2017s1.MainActivity;

import static cat.xtec.ioc.eac2_2017s1.MainActivity.LOG_TAG;

/**
 * Created by Toni on 08/10/2017.
 */

public class NetworkUtils {

    public static InputStream getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        int resposta = -1;
        Log.d(LOG_TAG, "HTTPURLCONNECTION");
        try {
            resposta = urlConnection.getResponseCode();
            Log.d(LOG_TAG, "HTTP Response Code: " + resposta);
            if (resposta == HttpURLConnection.HTTP_OK) {
                return urlConnection.getInputStream();
            } else {
                return null;
            }
        } catch (IOException ioe) {
            Log.d(LOG_TAG, "Impossible obtenir inputStream.");
            return null;
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

    public static boolean comprovaXarxa(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(context,"No hi ha connexió!!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public static void closeInputStream (InputStream in) {
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
