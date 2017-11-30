import Cralwer.Crawler;
import org.apache.log4j.BasicConfigurator;

/**
 * Created by chaoqunhuang on 11/29/17.
 */
public class Main {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Crawler crawler = new Crawler();
        //crawler.processPage("https://itunes.apple.com/us/app/gmail-email-by-google/id422689480?mt=8");
        crawler.getNavLinks();
    }
}
