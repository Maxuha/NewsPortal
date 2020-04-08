package ua.edu.sumdu.j2ee.zykov.model;

import java.util.Arrays;

public abstract class News {
    private String status;
    private Integer totalResults;
    private Article[] articles;

    public News(String status, Integer totalResults, Article[] articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public Article[] getArticles() {
        return articles;
    }

    public void setArticles(Article[] articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return "News{" +
                "status='" + status + '\'' +
                ", totalResults=" + totalResults +
                ", articles=" + Arrays.toString(articles) +
                '}';
    }
}
