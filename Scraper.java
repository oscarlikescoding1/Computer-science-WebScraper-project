import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

// This is the class that does most of the scraping work
public class Scraper {
    // Initialize the topic list which is a 2d list
    private ArrayList<ArrayList<String>> topicsList;

    //When calling the class, it initializes the topic list
    public Scraper() {
        topicsList = new ArrayList<>();
    }

    // Main function of the scraper
    public void scrapeWebsite() throws IOException {
        // Connect to the website
        String url = "https://www.economist.com/";
        Document document = Jsoup.connect(url).get();

        // Select all list items in the navigation menu (This took a while lol)
        Elements sections = document.select("ul.link-groups > li");

        // Iterate through each section
        for (Element section : sections) {
            String headTitle = section.select("h2.ds-navigation-list__header").text();
            // I wanted to exclude these three topics and one really weird one
            if (headTitle.isEmpty() ||
                    headTitle.equalsIgnoreCase("Opinion") ||
                    headTitle.equalsIgnoreCase("Current topics") ||
                    headTitle.equalsIgnoreCase("Opinion Current topics") ||
                    headTitle.equalsIgnoreCase("More")) {
                continue;
            }

            // Extracts the subtopics from the html file
            Elements subtopics = section.select("ul > li > a");
            ArrayList<String> subtopicList = new ArrayList<>();

            // Loops through the different topics and outputs the respective url
            for (Element subtopic : subtopics) {
                String subtopicText = subtopic.text();
                String subtopicUrl = "https://www.economist.com" + subtopic.attr("href");
                //Outputting the message
                if (!subtopicText.isEmpty()) {
                    subtopicList.add(subtopicText + ": " + subtopicUrl);
                }
            }

            // Create a new ArrayList to hold a single main topic and its subtopics and respective url.
            if (!subtopicList.isEmpty()) {
                ArrayList<String> topicEntry = new ArrayList<>();
                topicEntry.add(headTitle);
                topicEntry.addAll(subtopicList);
                topicsList.add(topicEntry);
            }
        }
    }

    // Function that returns the topics --> subtopics --> url
    public ArrayList<ArrayList<String>> getTopicsList() {
        return topicsList;
    }
}
