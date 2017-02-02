package nl.mprog.rens.vinylcountdown.ObjectClasses;

/**
 * Rens van der Veldt - 10766162
 * Minor Programmeren
 *
 * NewsItem.class
 *
 * This is a simple news item with title, content and author. These items are displayed in the main
 * activity and in a more detailed version when clicked in the newsactivity.
 */

public class NewsItem {

    // Declare properties.
    private String title;
    private String content;
    private String author;

    public void NewsItem(String title, String content, String author){

        // Assign local values.
        this.title = title;
        this.author = author;
        this.content = content;
    }

    // Getters and setters.
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



}
