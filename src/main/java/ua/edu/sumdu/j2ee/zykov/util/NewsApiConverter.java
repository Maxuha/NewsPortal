package ua.edu.sumdu.j2ee.zykov.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import ua.edu.sumdu.j2ee.zykov.model.News;
import ua.edu.sumdu.j2ee.zykov.model.NewsApi;

@Component
public class NewsApiConverter implements Converter<String, NewsApi> {
    @Value("${data.image.url}")
    private static String urlToNoImage;

    @Override
    public NewsApi convert(String s) {
        NewsApi news = null;
        JSONObject parse;
        try {
            parse = new JSONObject(s);
            String status = parse.getString("status");
            int totalPages = parse.getInt("totalResults");
            if ("ok".equals(status)) {
                JSONArray array = parse.getJSONArray("articles");
                News.Article[] articles = new News.Article[array.length()];
                JSONObject object;
                for (int i = 0; i < articles.length; i++) {
                    object = (JSONObject) array.get(i);
                    articles[i] = new NewsApi.Article(object.getString("title"),
                            object.isNull("description") ? "Описания нету" : object.getString("description"),
                            object.isNull("author") ? "Неизвестный источник" : object.getString("author"),
                            object.getString("url"),
                            object.isNull("urlToImage") ? urlToNoImage : object.getString("urlToImage"));
                }
                news = new NewsApi(status, totalPages, articles);
            } else {

            }
        } catch (JSONException e) {

        }

        return news;
    }
}
