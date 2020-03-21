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

    public static class Article {
        private String title;
        private String description;
        private String author;
        private String url;
        private String urlToImage;

        public Article(String title, String description, String author, String url, String urlToImage) {
            this.title = title;
            this.description = description;
            this.author = author;
            this.url = url;
            this.urlToImage = urlToImage;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public void setUrlToImage(String urlToImage) {
            this.urlToImage = urlToImage;
        }
    }
}
