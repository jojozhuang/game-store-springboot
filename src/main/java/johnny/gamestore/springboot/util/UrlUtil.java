package johnny.gamestore.springboot.util;

import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class UrlUtil {
    public static String getBaseEnvLinkURL() {
        String baseEnvLinkURL = null;
        HttpServletRequest currentRequest = 
           ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        baseEnvLinkURL = "http://" + currentRequest.getLocalName();
        if(currentRequest.getLocalPort() != 80) {
            baseEnvLinkURL += ":" + currentRequest.getLocalPort();
        }
        if(StringUtils.hasText(currentRequest.getContextPath())) {
            baseEnvLinkURL += currentRequest.getContextPath();
        }
        
        return baseEnvLinkURL;
    }
}
