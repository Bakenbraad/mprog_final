package nl.mprog.rens.vinylcountdown;

/**
 * Created by Rens on 23/01/2017.
 */

public class NewsItem {

    public void NewsItem(){

    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    String title;
    String content;
    String author;


}
