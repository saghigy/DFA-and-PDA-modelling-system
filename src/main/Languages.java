package main;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Languages
 */
public class Languages  {

    public static String language;
    public static String region;
    public static ResourceBundle resource;

    public static String msg(String key) {
        return resource.getString(key);
    }

    public static void setLanguageAndRegion(String langueString , String regionString) {
        language = langueString;
        region = regionString;
        Locale currentLocale = new Locale(language, region);  
        resource = ResourceBundle.getBundle("languages.lang",currentLocale);
    }
    
}