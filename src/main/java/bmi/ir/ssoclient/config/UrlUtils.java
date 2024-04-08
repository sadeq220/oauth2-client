package bmi.ir.ssoclient.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UrlUtils {

    private static final Pattern ORIGIN_PATTERN = Pattern.compile("^https?://[^/]+(?=/|$)");//browser definition of origin(CORS)
    public static String getOriginPart(String url){
        Matcher matcher = ORIGIN_PATTERN.matcher(url);
        if (matcher.find()) {
            return matcher.group();
        } else {
            throw new RuntimeException("specified URL doesn't match for the origin pattern");
        }
    }
}
