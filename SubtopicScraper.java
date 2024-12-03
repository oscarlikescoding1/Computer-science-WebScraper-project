import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import java.nio.charset.StandardCharsets;
//Subtopic scraper for us to find out more about it
public class SubtopicScraper {

    public ArrayList<String> scrapeSubtopic(String url) throws IOException {
        ArrayList<String> articles = new ArrayList<>();

        // Connect to the subtopic URL
        Document document = Jsoup.connect(url).get();
        document.outputSettings().charset(StandardCharsets.UTF_8);

        // Finding the css thingy for stuff related to articles
        Elements articleElements = document.select("div.css-0.exn4l0u0");

        //I wanted to exclude the discover more section so I mad ethis
        Elements discoverMoreSection = document.select("div.css-mqgbir.ekf19570 h2.ds-section-headline.ds-section-headline--rule-emphasised a.ds-section-headline-link");
        articleElements.removeAll(discoverMoreSection);

        // Removing discover more links
        for (Element discoverMoreLink : discoverMoreSection) {
            // Remove any articles related to the "Discover More" section by matching URLs
            String discoverMoreUrl = discoverMoreLink.attr("href");

            // Loop through articles and exclude any related to discover More (I couldn't find any solutions for this part so I had to use a little ChatGPT)
            articleElements.removeIf(article -> {
                String articleUrl = article.select("h3 a").attr("href");
                return articleUrl.contains(discoverMoreUrl);
            });
        }

        int i= 1;
        //This finds the title, title text, subtext/summary and the url for the user to learn even more
        for (Element article : articleElements) {
            Elements title = article.select("h3 a");
            String titleText = article.select("h3 a").text();
            String summary = article.select("p").text();
            String subtopicUrl = "https://www.economist.com" + title.attr("href");

            //Appending all the elements to a 2d list
            if (!title.isEmpty()) {
                articles.add("("+ i + ")" +"Title: " + titleText + "\nSummary: " + summary + "\nURL: " + subtopicUrl);
                i++;
            }

            //To ensure that even if it is empty, no error occurs
            if (articleElements.isEmpty()) {
                articles.add("No articles found. The page structure might have changed.");
                return articles;
            }

        }
        return articles;
    }
}
