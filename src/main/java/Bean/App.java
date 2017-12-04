package Bean;


import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.util.Set;

/**
 * Created by chaoqunhuang on 11/29/17.
 */
@DynamoDBTable(tableName="App")
public class App {
    String appName;
    Set<String> functions;
    Set<String> relatedApps;
    String url;
    double rating;

    @DynamoDBHashKey(attributeName = "appUrl")
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    @DynamoDBAttribute(attributeName = "appName")
    public String getAppName() {
        return appName;
    }
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @DynamoDBAttribute(attributeName = "functions")
    public Set<String> getFunctions() { return functions; }
    public void setFunctions(Set<String> functions) {
        this.functions = functions;
    }

    @DynamoDBAttribute(attributeName = "relatedApps")
    public Set<String> getRelatedApps() { return relatedApps; }
    public void setRelatedApps(Set<String> relatedApps) { this.relatedApps = relatedApps; }

    @DynamoDBAttribute(attributeName = "rating")
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

}
