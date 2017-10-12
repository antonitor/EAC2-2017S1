package cat.xtec.ioc.eac2_2017s1.utils;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import cat.xtec.ioc.eac2_2017s1.data.Noticia;

/**
 * Created by Admin on 05/10/2017.
 */

public class MarcaXmlParser {

    // No fem servir namespaces
    private static final String ns = null;

    //Aquesta classe representa una entrada de noticia del RSS Feed

    public List<Noticia> analitza(InputStream in) throws XmlPullParserException, IOException {
        try {
            //Obtenim analitzador
            XmlPullParser parser = Xml.newPullParser();

            //No fem servir namespaces
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);

            //Especifica l'entrada de l'analitzador
            parser.setInput(in, null);

            //Obtenim la primera etiqueta
            parser.nextTag();

            //Retornem la llista de noticies
            return llegirNoticies(parser);
        } finally {
            in.close();
        }
    }

    //Llegeix una llista de noticies de Marca a partir del parser i retorna una llista d'Entrades
    private List<Noticia> llegirNoticies(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Noticia> llistaEntrades = new ArrayList<Noticia>();

        //Comprova si l'event actual és del tipus esperat (START_TAG) i del nom "rss"
        parser.require(XmlPullParser.START_TAG, ns, "rss");
        parser.nextTag();
        parser.require(XmlPullParser.START_TAG, ns, "channel");

        //Mentre que no arribem al final d'etiqueta
        while (parser.next() != XmlPullParser.END_TAG) {
            //Ignorem tots els events que no siguin un començament d'etiqueta
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                //Saltem al seguent event
                continue;
            }
            //Obtenim el nom de l'etiqueta
            String name = parser.getName();
            // Si aquesta etiqueta és una entrada de noticia
            if (name.equals("item")) {
                //Afegim l'entrada a la llista
                llistaEntrades.add(llegirEntrada(parser));
            } else {
                //Si és una altra cosa la saltem
                saltar(parser);
            }
        }
        return llistaEntrades;
    }

    //Analitza el contingut d'una entrada. Si troba un ttol, resum o enllaç, crida els mètodes de lectura
    //propis per processar-los. Si no, ignora l'etiqueta.
    private Noticia llegirEntrada(XmlPullParser parser) throws XmlPullParserException, IOException {
        String titol = null;
        String enllac = null;
        String autor = null;
        String descripcio = null;
        String data = null;
        String categoria = "";
        String thumbnail = null;

        //L'etiqueta actual ha de ser "item"
        parser.require(XmlPullParser.START_TAG, ns, "item");

        //Mentre que no acabe l'etiqueta de "entry"
        while (parser.next() != XmlPullParser.END_TAG) {
            //Ignora fins que no trobem un començament d'etiqueta
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            //Obtenim el nom de l'etiqueta
            String etiqueta = parser.getName();
            switch (etiqueta) {
                case "title":
                    titol = llegirTitol(parser);
                    break;
                case "link":
                    enllac = llegirEnllac(parser);
                    break;
                case "dc:creator":
                    autor = llegirAutor(parser);
                    break;
                case "media:description":
                    descripcio = llegirDescripcio(parser).replaceAll("\\<[^>]*>","");
                    break;
                case "pubDate":
                    data = llegirData(parser);
                    break;
                case "category":
                    if (categoria.length()>0) {
                        categoria = llegirCategoria(parser) + ", " + categoria;
                    } else {
                        categoria = llegirCategoria(parser);
                    }
                    break;
                case "media:thumbnail":
                    thumbnail = llegirThumbnail(parser);
                    break;
                default:
                    saltar(parser);
            }
        }

        //Creem una nova entrada amb aquestes dades i la retornem
        if (categoria == null) categoria = "";
        return new Noticia(titol, autor, enllac, descripcio, data, categoria, thumbnail);
    }

    //Aquesta funció serveix per saltar-se una etiqueta i les seves subetiquetes aniuades.
    private void saltar(XmlPullParser parser) throws XmlPullParserException, IOException {
        //Si no és un començament d'etiqueta: ERROR
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;

        //Comprova que ha passat per tantes etiquetes de començament com acabament d'etiqueta

        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    //Cada vegada que es tanca una etiqueta resta 1
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    //Cada vegada que s'obre una etiqueta augmenta 1
                    depth++;
                    break;
            }
        }
    }

    //Llegeix el títol de una notcia del feed i el retorna com String
    private String llegirTitol(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "title"
        parser.require(XmlPullParser.START_TAG, ns, "title");

        //Llegeix
        String titol = llegeixText(parser);

        //Fi d'etiqueta
        parser.require(XmlPullParser.END_TAG, ns, "title");
        if (titol!=null) {
            return titol;
        }else{
            return "";
        }
    }

    //Llegeix l'enllaç de una notícia del feed i el retorna com String
    private String llegirEnllac(XmlPullParser parser) throws IOException, XmlPullParserException {
        String enllac = "";

        //L'etiqueta actual ha de ser "link"
        parser.require(XmlPullParser.START_TAG, ns, "link");

        //Llegeix
        enllac = llegeixText(parser);

        //Fi d'etiqueta
        parser.require(XmlPullParser.END_TAG, ns, "link");

        if (enllac!=null) {
            return enllac;
        }else{
            return "";
        }
    }

    //Llegeix l'autor de una notícia del feed i el retorna com String
    private String llegirAutor(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "summary"
        parser.require(XmlPullParser.START_TAG, ns, "dc:creator");

        String autor = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "dc:creator");
        if (autor!=null) {
            return autor;
        }else{
            return "";
        }
    }

    //Llegeix la descripció de una notícia del feed i el retorna com String
    private String llegirDescripcio(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "summary"
        String descripcio = "";
        parser.require(XmlPullParser.START_TAG, ns, "media:description");

        descripcio = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "media:description");

        return descripcio;
    }

    //Llegeix la data de publicació de una notícia del feed i el retorna com String
    private String llegirData(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "summary"
        String pubdate = "";

        parser.require(XmlPullParser.START_TAG, ns, "pubDate");

        pubdate = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "pubDate");

        return pubdate;
    }

    //Llegeix la categoría de una notícia del feed i el retorna com String
    private String llegirCategoria(XmlPullParser parser) throws IOException, XmlPullParserException {
        //L'etiqueta actual ha de ser "summary"
        String category = "";
        parser.require(XmlPullParser.START_TAG, ns, "category");

        category = llegeixText(parser);

        parser.require(XmlPullParser.END_TAG, ns, "category");
        return category;
    }

    //Llegeix l'enllaç al thumbnail de l'imatge de una notícia del feed i el retorna com String
    private String llegirThumbnail(XmlPullParser parser) throws IOException, XmlPullParserException {
        String thumbnail = "";
        //L'etiqueta actual ha de ser "thumbnail"
        parser.require(XmlPullParser.START_TAG, ns, "media:thumbnail");
        thumbnail = parser.getAttributeValue(null, "url");
        parser.nextTag();
        return thumbnail;
    }



    //Extrau el valor de text per les etiquetes
    private String llegeixText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultat = "";
        if (parser.next() == XmlPullParser.TEXT) {
            resultat = parser.getText();
            parser.nextTag();
        }
        return resultat;
    }
}
