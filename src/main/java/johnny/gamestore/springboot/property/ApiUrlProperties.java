package johnny.gamestore.springboot.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "site")
public class ApiUrlProperties {
    private String apiUrl;

    public String getApiUrl() {
        if (apiUrl == null || apiUrl.isEmpty()) {
            return apiUrl;
        }
        return apiUrl.replaceAll("\"", ""); // remove double quotes in case that complex path is specified with double quotes
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
}
