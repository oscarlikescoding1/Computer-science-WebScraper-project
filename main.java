import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Scraper scraper = new Scraper(); // Create a Scraper instance
        scraper.scrapeWebsite(); // Perform the scraping

        Menu menu = new Menu(scraper.getTopicsList()); // Pass the topics to the menu
        menu.displayMenu(); // Start the user interaction menu
    }
}
