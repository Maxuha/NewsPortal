package ua.edu.sumdu.j2ee.zykov.model;

public class NewsApi extends News {
    public NewsApi(String status, Integer totalResults, Article[] articles) {
        super(status, totalResults, articles);
    }
}
