//This is just the file where I wrote everything then I decided to sort it out
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

public class WebsiteScrapperEconomist {

    public static <List> void main(String[] args) throws IOException {
        // Connecting the link to jSoup
        String url = "https://www.economist.com/";
        Document document = Jsoup.connect(url).get();

        //List to store topics and subtopics
        ArrayList<ArrayList<String>> topicsList = new ArrayList<>();

        // Select all list items in navigation menu
        Elements sections = document.select("ul.link-groups > li");


        // For formatting
        System.out.println("================");
        System.out.println("Web scraper:");

        // Iterate through each section
        for (Element section : sections) {
            // Extract the header title
            String headTitle = section.select("h2.ds-navigation-list__header").text();
            if (headTitle.isEmpty()) {
                System.out.println("No header found for this section.");
                continue;
            }
            //Exclude opinion and current topics
            if (headTitle.equalsIgnoreCase("Opinion") ||
                    headTitle.equalsIgnoreCase("Current topics") ||
                    headTitle.equalsIgnoreCase("Opinion Current topics") ||
                    headTitle.equalsIgnoreCase("more")) {
                System.out.println("Skipping section: " + headTitle);
                continue; // Skip to the next iteration
            }
            // Extract subtopics within this section
            Elements subtopics = section.select("ul> li > a");
            ArrayList<String> subtopicList = new ArrayList<>();

            //Loops through and add topics
            for (Element subtopic : subtopics) {
                String subtopicText = subtopic.text();
                String subtopicUrl = subtopic.attr("href");
                subtopicUrl = "https://www.economist.com" + subtopicUrl;

                if (!subtopicText.isEmpty()) {
                    subtopicList.add(subtopicText + ": " + subtopicUrl);
                }
            }
            if (!subtopicList.isEmpty()) {
                ArrayList<String> topicEntry = new ArrayList<>();
                topicEntry.add(headTitle); // Add the main topic
                topicEntry.addAll(subtopicList); // Add the subtopics
                topicsList.add(topicEntry);
            }
        }

        //Displays information scraped from website
        System.out.println("\nAvailable topics and subtopics:");
        int topicIndex = 1;
        for (ArrayList<String> topic : topicsList) {
            System.out.println("[" + topicIndex + "] " + topic.get(0)); // Main topic
            for (int i = 1; i < topic.size(); i++) {
                System.out.println("    (" + i + ") " + topic.get(i)); // Subtopic title and URL
            }
            topicIndex++;
        }
        //Asks for topic interest
        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter the number of the topic you're interested in: ");
        int chosenTopicIndex = scanner.nextInt() - 1;

        //Makes sure topic inputted is valid
        if (chosenTopicIndex >= 0 && chosenTopicIndex < topicsList.size()) {
            ArrayList<String> chosenTopic = topicsList.get(chosenTopicIndex);

            //Asks for subtopic interest
            System.out.println("\nYou selected: " + chosenTopic.get(0));
            System.out.print("Enter the number of the subtopic you're interested in: ");
            int chosenSubtopicIndex = scanner.nextInt();

            //Makes sure subtopic inputted is valid
            if (chosenSubtopicIndex > 0 && chosenSubtopicIndex < chosenTopic.size()) {
                String chosenSubtopic = chosenTopic.get(chosenSubtopicIndex);
                String[] subtopicParts = chosenSubtopic.split(": "); // Extract the URL
                String subtopicUrl = subtopicParts[1].trim();

                System.out.println("\nYou selected subtopic: " + subtopicParts[0]);
                System.out.println("Connecting to: " + subtopicUrl);

                // Connect to the selected subtopic's URL
                Document selectedDocument = Jsoup.connect(subtopicUrl).get();
                System.out.println("\nPage title: " + selectedDocument.title());
            } else {
                System.out.println("Invalid subtopic selection.");
            }
        } else {
            System.out.println("Invalid topic selection.");
        }
    }
}
