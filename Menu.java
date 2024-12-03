import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import okhttp3.*;
//Importing everything



//This is for how the actual information si going to be displayed
public class Menu {
    //Like the scraper class, we also initialize a 2d arraylist
    private ArrayList<ArrayList<String>> topicsList;
    //Initializing and giving the api keys to use later
    private static final String NEWSAPI_KEY = "...";
    private static final String OPENAI_API_KEY = "...";
    private static final OkHttpClient client = new OkHttpClient();
    //When calling this class, it initializes the topic list
    public Menu(ArrayList<ArrayList<String>> topicsList) {
        this.topicsList = topicsList;
    }

    //This is for displaying the information
    public void displayMenu() {
        // Initializes a scanner
        Scanner scanner = new Scanner(System.in);
        while(true) {

            System.out.println("\nAvailable topics and subtopics:");
            int topicIndex = 1;
            //Looping through the topics and subtopics and showing the user the available topics and subtopics
            for (ArrayList<String> topic : topicsList) {
                System.out.println("[" + topicIndex + "] " + topic.get(0));
                for (int i = 1; i < topic.size(); i++) {
                    System.out.println("    (" + i + ") " + topic.get(i));
                }
                topicIndex++;
            }

            //Checks for user input
            System.out.print("\nEnter the number of the topic you're interested in: ");
            int chosenTopicIndex = scanner.nextInt() - 1;
            //Feeds back to the computer what index is picked

            //Makes sure user topic input is valid
            if (chosenTopicIndex >= 0 && chosenTopicIndex < topicsList.size()) {
                ArrayList<String> chosenTopic = topicsList.get(chosenTopicIndex);

                System.out.println("\nYou selected: " + chosenTopic.get(0));
                System.out.print("Enter the number of the subtopic you're interested in: ");
                int chosenSubtopicIndex = scanner.nextInt();
                //Makes sure user subtopic input is valid
                if (chosenSubtopicIndex > 0 && chosenSubtopicIndex < chosenTopic.size()) {
                    String chosenSubtopic = chosenTopic.get(chosenSubtopicIndex);
                    // This splits our string in two parts whenever it sees a colon
                    String[] subtopicParts = chosenSubtopic.split(": ");
                    // This removes all the unnecessary spaces
                    String subtopicUrl = subtopicParts[1].trim();

                    System.out.println("\nYou selected subtopic: " + subtopicParts[0]);
                    System.out.println("Connecting to: " + subtopicUrl);

                    //Connecting the url to get more information.
                    try {
                        SubtopicScraper subtopicScraper = new SubtopicScraper();
                        ArrayList<String> articles = subtopicScraper.scrapeSubtopic(subtopicUrl);

                        //Displaying the articles found
                        System.out.println("\nArticles found:");
                        for (String article : articles) {
                            System.out.println(article + "\n");
                        }

                        //What I want to do here is to be able to allow the user to input again to 1)Save article(s) 2)Find article comparison 3)Choose another topic +subtopic or 4)Exit
                        Scanner scanner2 = new Scanner(System.in);

                        boolean continueChoosing = true;
                        while(continueChoosing) {
                            System.out.print("\nWould you like so 1)Save article(s)     2)Find similar articles     3)Choose another topic/subtopic      4)Exit?        5) Summarize article(s)     ");
                            int chosenNewInput = scanner2.nextInt();
                            // For the saving, it is going to ask for article number(s) then save the information into html
                            if (chosenNewInput == 1) {
                                System.out.println("Saving...");
                                saveArticles(articles);
                            }
                            // Trying to connect to API to search for other news website with similar topic
                            else if (chosenNewInput == 2) {
                                System.out.println("Finding similar articles...");
                                String keywords = extractKeywordsFromArticle(articles.get(0)); // Analyze the first article as an example
                                findSimilarArticles(keywords);
                            }
                            // Goes back to the topic selector
                            else if (chosenNewInput == 3) {
                                System.out.println("Choosing...");
                                continueChoosing = false;
                            }
                            // Exits the code
                            else if (chosenNewInput == 4) {
                                System.out.println("Exiting...");
                                return;
                            }

                            else if (chosenNewInput == 5) {
                                System.out.println("Summarizing an article...");
                                summarizeArticle(articles.get(0));
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to connect to the selected URL.");
                    }
                } else {
                    System.out.println("Invalid subtopic selection.");
                }
            } else {
                System.out.println("Invalid topic selection.");
            }
        }
    }

    //This is for saving articles to our HTML page
    private void saveArticles(ArrayList<String> articles){
        String[] input;

        // The actual saving part
        System.out.print("Please give me the number of article(s) you are interested in (if multiple, separated by comma):");
        Scanner scanner3 = new Scanner(System.in);
        input = scanner3.nextLine().split(",");
        //Creating a new arrayList specifically for the selected articles with the information presented above
        ArrayList<String> selectedArticles = new ArrayList<>();

        //Looping through the selected articles
        for (String s : input) {
            int articleIndex = Integer.parseInt(s.trim()) - 1;
            //Adding the selected articles into the list
            if (articleIndex >= 0 && articleIndex < articles.size()) {
                selectedArticles.add(articles.get(articleIndex));
            } else {
                System.out.println("Article number " + (articleIndex + 1) + " is out of range.");
            }
        }

        //Writing the file
        File file = new File("articles.html");


        // UH the issue is still persisting, I legit tried everything I could so imma just leave it as is...
        //https://stackoverflow.com/questions/1001540/how-to-write-a-utf-8-file-with-java
        // OK so we originally used file writer but we encountered a UTF-8 encoding error, so I had to do some research in how to fix it
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8))) {
            // The FileOutputStream writes and the file specifies where it is written and true makes it so that it appends new articles instead of creating a new file everytime we run the code
            // OutputStreamWriter apparently acts as a translator and converts the text we want to write into format understandable for the computer, we also specify we want to use UTF-8 encoding so our simple webpage can handle special characters like apostrophes
            // BufferedWriter is like using a basket when shopping so that it waits for a chunk of text, then writes it all together at once onto the "articles.html file"
            // I didn't really understand this part done on stack overflow so I had to do some research
            if (!file.exists()) { // If the file doesn't exist, create a basic HTML structure
                writer.write("<html><head><meta charset=\"UTF-8\"></head><body>\n");
            }

            // Append new articles, <p> is the paragraph tag so it ends with </p> as well,\n is new line, we use break in HTML instead of \n in coding or TXT files so we have to convert it to a break
            for (String article : selectedArticles) {
                writer.write("<p>" + article.replace("\n", "<br>") + "</p><hr>\n");
            }

            writer.write("</body></html>\n"); // Close HTML structure if necessary
            writer.close(); // Closing the file
            System.out.println("Articles successfully saved to 'articles.html'!");
        } catch (IOException e) {
            System.out.println("An error occurred while saving the articles.");
            e.printStackTrace();
        }
    }


    // OK so because I am a brokie and I didn't have a lot of time to try to adjust the code another API which would take like another one or two hours to set let alone run and debug, so I know the code should all be (theoretically) functional with only the actions using OPENAI API having problems
    // If I had like another day or two, I would've used cloudmersive (https://www.cloudmersive.com/nlp-api), it gives me the option to use natural language processing and perform textual analysis, it seems rather interesting and could be integrated if I improve on this project outside of school context.
    private void findSimilarArticles(String keywords) throws IOException {
        //Initializing NEWSAPI key (this is a special function by this API, it is in the documentation)
        //https://newsapi.org/docs/get-started#search
        String urlString = "https://newsapi.org/v2/everything?q=" + keywords + "&apiKey=" + NEWSAPI_KEY;

        // Creating a new url and establishes an http connection then specifies the http request method as "GET" which is also in the documentation
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        //Reads the response from the API and links together each line into the "response" string
        Scanner scanner = new Scanner(connection.getInputStream());
        String response = "";
        while (scanner.hasNext()) {
            response += scanner.nextLine();
        }

        // Converts the raw JSON response into a usable JSON (for myself: JSON that is sorta like a 2d array but is more organized and simple to access)
        JSONObject jsonResponse = new JSONObject(response);
        JSONArray articlesArray = jsonResponse.getJSONArray("articles");

        // Gives us similar articles: iterates through each article and retrieves information like getting the json object at location i, getting the title of said article, its corresponding url and then printing out it all
        System.out.println("\nSimilar Articles from Different News Sources:");
        int maxArticles = Math.min(8, articlesArray.length());

        for (int i = 0; i < maxArticles; i++) {
            JSONObject article = articlesArray.getJSONObject(i);
            String title = article.getString("title");
            String urlToArticle = article.getString("url");
            System.out.println(title + " - " + urlToArticle);
        }

        if (articlesArray.length() > 8) {
            System.out.println("\nNote: Only the top 8 similar articles are displayed.");
        }
    }

    // I found this part quite complicated, I tried researching but I did have to use a little bit of GPT to combine all the information I learned
    // Extracting the keywords to pass onto the findSimilarArticle function as we want to find similar articles
    // I was also contemplating using NLP instead but I found it too time consuming and complicated for the time frame
    //https://www.javatpoint.com/java-json-example
    private String extractKeywordsFromArticle(String articleContent) throws IOException {
        // Creates a new json object the second
        String jsonBody = new JSONObject()
                //".put" specifies which model we want to use
                .put("model", "gpt-3.5-turbo")
                //adds a key called messages with a value that is an array
                .put("messages", new JSONArray()
                        //creates an array to store conversation message
                        .put(new JSONObject()
                                // Indicates that the message is from the user
                                .put("role", "user")
                                // This is the content sent to the API and it tells it "hey please extract key words for me"
                                .put("content", "Extract key topics or keywords from the following article: " + articleContent)
                        )
                ).toString(); // Then converts "messages" to string
        // Specifies data type being sent is in JSON format
        //https://swagger.io/docs/specification/v3_0/describing-request-body/describing-request-body/#:~:text=requestBody%20consists%20of%20the%20content,bodies%20are%20optional%20by%20default.
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(jsonBody, mediaType);

        // Request is a function of the class: okhttp3 and it makes HTTPs request
        //https://www.baeldung.com/guide-to-okhttp
        Request request = new Request.Builder()
                //Specifies API endpoint (for myself: "API endpoints are the specific digital location where requests or API calls for information are sent by one program to retrieve the digital resource that exists there. Endpoints specify where APIs can access resources and help guarantee the proper functioning of the incorporated software"-tech target)
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                //https://stackoverflow.com/questions/22034144/what-does-it-mean-http-request-body
                .addHeader("Content-Type", "application/json")
                // Specifies the file format
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                // Authenticates request with API Key
                .build();

        //Sends HTTPs request and waits for response then it converts the http response to a string
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        //Used for debugging, gives raw response
        System.out.println("Raw Response Body: " + responseBody);


        // Extract keywords from response
        JSONObject jsonResponse = new JSONObject(responseBody);

// Check if the response contains an "error" field, this was just for testing why I was getting error messages, nothing of particular spectacle
        if (jsonResponse.has("error")) {
            String errorMessage = jsonResponse.getJSONObject("error").getString("message");
            throw new IOException("API Error: " + errorMessage);
        }

// Extract keywords from the "choices" array if no error
        JSONArray choices = jsonResponse.getJSONArray("choices");
        String keywords = choices.getJSONObject(0).getJSONObject("message").getString("content");

        System.out.println("Extracted Keywords: " + keywords);
        return keywords;
    }


    // I basically just copied the extractKeywordFromArticle function
    private void summarizeArticle(String articleContent) throws IOException {
        String jsonBody = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("messages", new JSONArray()
                        .put(new JSONObject()
                                .put("role", "user")
                                .put("content", "Summarize the following article, give some implied effects and give key statistical figures (if inside the article): " + articleContent)
                        )
                ).toString(); // Then converts "messages" to string

        MediaType mediaType1 = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(jsonBody, mediaType1);

        // Request is a function of the class: okhttp3 and it makes HTTPs request
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + OPENAI_API_KEY)
                .build();

        //Sends HTTPs request and waits for response then it converts the http response to a string
        Response response = client.newCall(request).execute();
        String responseBody = response.body().string();
        //Used for debugging, gives raw response
        System.out.println("Raw Response Body: " + responseBody);


        // Extract keywords from response
        JSONObject jsonResponse = new JSONObject(responseBody);

// Check if the response contains an "error" field, this was just for testing why I was getting error messages, nothing of particular spectacle
        if (jsonResponse.has("error")) {
            String errorMessage = jsonResponse.getJSONObject("error").getString("message");
            throw new IOException("API Error: " + errorMessage);
        }

// Extract keywords from the "choices" array if no error
        JSONArray choices = jsonResponse.getJSONArray("choices");
        String summary = choices.getJSONObject(0).getJSONObject("message").getString("content");

        System.out.println("Summarization: " + summary);
    }
}
