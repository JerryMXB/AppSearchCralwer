import Cralwer.Crawler;

/**
 * Created by chaoqunhuang on 11/29/17.
 */
public class Main {
    public static void main(String[] args) {
        Crawler crawler = new Crawler();
        //crawler.processPage("https://itunes.apple.com/us/app/gmail-email-by-google/id422689480?mt=8");
        crawler.getNavLinks();
    }
}
