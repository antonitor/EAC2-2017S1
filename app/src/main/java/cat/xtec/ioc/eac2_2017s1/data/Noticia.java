package cat.xtec.ioc.eac2_2017s1.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Toni on 12/10/2017.
 *
 * Aquesta classe implementa la interficie Parcelable per tal de poder guardar
 * l'ArrayList de Noticies al Bundle onSavedInstanceState per tal de no haver de
 * descarregar d'inernet o carregar de la base de dades cada cop que es pausa MainActivity.
 * Per exemple, quan es rota la pantalla o quan es prem el botó home i es torna a obrir l'App
 */
public class Noticia implements Parcelable{
        private String titol;          //Títol de la noticia
        private String enllac;         //Enllaç a la noticia completa
        private String autor;          //Autor de la notícia
        private String descripcio;     //Descripció de la noticia
        private String data;           //Data de publicació de la noticia
        private String categoria;      //Categoria de la noticia
        private String thumbnail;      //Enllaç al thumbnail de la imatge


        public Noticia(String title, String author, String link, String desc, String date, String cat, String thumb) {
            this.setTitol(title);
            this.setAutor(author);
            this.setEnllac(link);
            this.setDescripcio(desc);
            this.setData(date);
            this.setCategoria(cat);
            this.setThumbnail(thumb);
        }

    protected Noticia(Parcel in) {
        titol = in.readString();
        enllac = in.readString();
        autor = in.readString();
        descripcio = in.readString();
        data = in.readString();
        categoria = in.readString();
        thumbnail = in.readString();
    }

    public static final Creator<Noticia> CREATOR = new Creator<Noticia>() {
        @Override
        public Noticia createFromParcel(Parcel in) {
            return new Noticia(in);
        }

        @Override
        public Noticia[] newArray(int size) {
            return new Noticia[size];
        }
    };

    public String getTitol() {
        return titol;
    }

    public String getEnllac() {
        return enllac;
    }

    public String getAutor() {
        return autor;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public String getData() {
        return data;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setTitol(String titol) {
        this.titol = titol;
    }

    public void setEnllac(String enllac) {
        this.enllac = enllac;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titol);
        parcel.writeString(enllac);
        parcel.writeString(autor);
        parcel.writeString(descripcio);
        parcel.writeString(data);
        parcel.writeString(categoria);
        parcel.writeString(thumbnail);
    }
}
