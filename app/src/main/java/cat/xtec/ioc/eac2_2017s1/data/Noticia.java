package cat.xtec.ioc.eac2_2017s1.data;

/**
 * Created by Toni on 12/10/2017.
 */

public class Noticia {
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
}
