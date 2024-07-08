package main.view.settings;

public class LanguageItem {
    private String language;
    private String country;
    private String name;

    public LanguageItem(String language, String country, String name) {
        this.name = name;
        this.country = country;
        this.language = language;
    }



    public String getLanguage() {
        return this.language;
    }

    public String getCountry() {
        return this.country;
    }
    
    public String getName() {
        return this.name;
    }


}