package com.shukhrat.wbpvp.ui.news;

public class NewsModel {
    private String news_title, news_description, news_text, news_image, news_date;

    public NewsModel() {
    }

    public NewsModel(String news_title, String news_description, String news_text, String news_image, String news_date) {
        this.news_title = news_title;
        this.news_description = news_description;
        this.news_text = news_text;
        this.news_image = news_image;
        this.news_date = news_date;
    }

    public String getNews_title() {
        return news_title;
    }

    public void setNews_title(String news_title) {
        this.news_title = news_title;
    }

    public String getNews_description() {
        return news_description;
    }

    public void setNews_description(String news_description) {
        this.news_description = news_description;
    }

    public String getNews_text() {
        return news_text;
    }

    public void setNews_text(String news_text) {
        this.news_text = news_text;
    }

    public String getNews_image() {
        return news_image;
    }

    public void setNews_image(String news_image) {
        this.news_image = news_image;
    }

    public String getNews_date() {
        return news_date;
    }

    public void setNews_date(String news_date) {
        this.news_date = news_date;
    }
}
