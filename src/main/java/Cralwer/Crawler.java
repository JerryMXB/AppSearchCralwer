package Cralwer;

import Bean.App;
import lombok.extern.log4j.Log4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by chaoqunhuang on 11/29/17.
 */
@Log4j
public class Crawler {
    static final char[] navs = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
            'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
    static final String nav = "https://itunes.apple.com/us/genre/ios-productivity/id6007?mt=8";

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
                        processPage(a.attr("href"));
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            log.error(ioe.getMessage());
            throw new RuntimeException(ioe);
        }
    }


    private void processPage(String url_link) {
        try {
            // Fetch app page
            Document doc = Jsoup.connect(url_link).get();
            //doc.html();
            App app = new App();

            // Fetch Name
            Elements name = doc.select("h1[itemprop=\"name\"]");
            app.setAppName(name.get(0).text());
            System.out.println(name.get(0).text());

            // Fetch description
            Elements functions = doc.select("p[itemprop=\"description\"]");

            // Extract functionality
            String description = functions.get(0).html();
            String[] splitLines = description.split("<br>");
            ArrayList<String> bullets = new ArrayList<>();

            for (String s : splitLines) {
                if (s.startsWith("•") || s.startsWith("-") || s.startsWith("*")) {
                    bullets.add(s);
                }
            }
            app.setFunctions(bullets.toArray(new String[bullets.size()]));
            bullets.forEach(s -> log.info(s));
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            log.error(ioe.getMessage());
            throw new RuntimeException(ioe);
        }
    }

}
