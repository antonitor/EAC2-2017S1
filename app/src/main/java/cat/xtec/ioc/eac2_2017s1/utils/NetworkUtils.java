package cat.xtec.ioc.eac2_2017s1.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    public static void downloadImageToCache(String urlString, String path_imatge){
        URL urlImatge = buildURl(urlString);
        InputStream inputStream = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) urlImatge.openConnection();
            int totalImatge= connection.getContentLength();
            InputStream inputstream = (InputStream) urlImatge.getContent();
            byte[] bufferImatge = new byte[1024];

            OutputStream outputstream = new FileOutputStream(path_imatge);

            int descarregat = 0;
            int count;

            // Mentre hi hagi informació que llegir
            while ((count = inputstream.read(bufferImatge)) != -1) {
                // Acumulem tot el que ha llegit
                descarregat += count;
                // Guardem al disc el que hem descarregat
                outputstream.write(bufferImatge, 0, count);
            }
            inputstream.close();
            outputstream.close();
        } catch (IOException exception) {
            Log.d(LOG_TAG, "Alguna cosa no ha anat bé!");
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
