package cat.xtec.ioc.eac2_2017s1.data;

import android.provider.BaseColumns;

/**
 * Created by Toni on 08/10/2017.
 */

public class Contracte {

    public static final class Noticies implements BaseColumns {
        public static final String NOM_TAULA = "noticies";
        public static final String TITOL = "titol";
        public static final String AUTOR = "autor";
        public static final String DESCRIPCIO = "descripcio";
        public static final String DATA_PUBLICACIO = "data";
        public static final String CATEGORIA = "categoria";
        public static final String ENLLAC = "enllac";
        public static final String THUMBNAIL = "thumbnail";
    }
}
