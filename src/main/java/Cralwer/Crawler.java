package Cralwer;

import Bean.App;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by chaoqunhuang on 11/29/17.
 */
@Log4j
public class Crawler {
    static final char[] navs = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    static final String nav = "https://itunes.apple.com/us/genre/ios-productivity/id6007?mt=8";

    private final DynamoDBMapper mapper;

    public Crawler() {
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withRegion(Regions.US_EAST_1).withCredentials(new ProfileCredentialsProvider("AppCrawler")).build();
        this.mapper = new DynamoDBMapper(client);
    }

    public void getNavLinks() {
        for (char c : navs) {
            String pageNav = String.format("https://itunes.apple.com/us/genre/ios-productivity/id6007?mt=8&letter=%s", c);
            System.out.println(pageNav);
            getAppLinks(pageNav);
        }
    }

    /**
     * Get Apps links given a nav link with letter
     * @param pageNav Nav link with letter
     */
    private void getAppLinks(String pageNav) {
        // Fetch app page
        try {
            Document doc = Jsoup.connect(pageNav).get();

            // Get List of nav page with specified letter and page number
            Elements elements = doc.select("ul[class=\"list paginate\"]");
            Elements hrefs = elements.get(0).select("a");

            // Get The app links
            for (Element e : hrefs) {
                log.info(e.text() + e.attr("href"));

                // Ignore Next link
                if ("Next".equals(e.text())) {
                    Document pageOfApps = Jsoup.connect(e.attr("href")).get();
                    Elements appLinks = pageOfApps.select("div[id=\"selectedcontent\"]").get(0).select("a");
                    for (Element a : appLinks) {
                        log.info(a.attr("href") + " " + a.text());
                        try {
                            log.info("Sleep for 1 seconds");
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            log.error(ie);
                        }
                        processPage(a.attr("href"));
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            log.error(ioe.getMessage());
        }
    }


    private void processPage(String url_link) {
        try {
            // Fetch app page
            Document doc = Jsoup.connect(url_link).get();

            App app = new App();
            // Set Url
            app.setUrl(url_link);

            // Fetch Name
            Elements name = doc.select("h1[itemprop=\"name\"]");
            app.setAppName(name.get(0).text());
            System.out.println(name.get(0).text());

            // Fetch description
            Elements functions = doc.select("p[itemprop=\"description\"]");

            // Extract functionality
            String description = functions.get(0).html();
            String[] splitLines = description.split("<br>");
            Set<String> bullets = new HashSet<>();

            for (String s : splitLines) {
                if (s.startsWith("•") || s.startsWith("-") || s.startsWith("*")) {
                    bullets.add(s);
                }
            }

            // Fetch related apps
            Elements relatedAppsElements = doc.select("a[class=\"artwork-link\"]");
            Set<String> relatedApps = new HashSet<>();

            int index = 0;
            for (Element e : relatedAppsElements) {
                if (index > 4) {
                    break;
                }
                relatedApps.add(e.select("a[class=\"artwork-link\"]").get(0).attr("href"));
                index++;
            }

            app.setRelatedApps(relatedApps);

            // Fetch ratings
            double rating = Double.valueOf(doc.select("span[itemprop=\"ratingValue\"]").size() > 0 ?
                    doc.select("span[itemprop=\"ratingValue\"]").get(0).text() : "-1");
            app.setRating(rating);

            app.setFunctions(bullets);
            if (!app.getFunctions().isEmpty() && !app.getRelatedApps().isEmpty()) {
                log.info("Save app into dynamodb");
                mapper.save(app);
                try {
                    log.info("Sleep for 5 seconds");
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    log.error(e);
                }
            }
            bullets.forEach(s -> log.info(s));
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            log.error(ioe.getMessage());
        }
    }



}
