package ua.edu.sumdu.j2ee.zykov.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import ua.edu.sumdu.j2ee.zykov.model.News;
import ua.edu.sumdu.j2ee.zykov.model.NewsApi;

public class JsonParser {
    @Value("${data.image.url}")
    private static String urlToNoImage;

    public static News[] getNewsArrayFromJson(String json) {
        News[] news = null;
        JSONObject parse = null;
        try {
            parse = new JSONObject(json);
            String status = parse.getString("status");
            int totalPages = Integer.parseInt(parse.getString("totalResults"));
            if ("ok".equals(status)) {
                news = new NewsApi[totalPages];
                JSONArray array = parse.getJSONArray("articles");
                JSONObject object;
                if (array.length() != 0) {
                    for (int i = 0; i < news.length; i++) {
                        object = (JSONObject) array.get(i);
                        news[i] = new NewsApi(object.getString("title"),
                                object.isNull("description") ? "Описания нету" : object.getString("description"),
                                object.isNull("author") ? "Неизвестный источник" : object.getString("author"),
                                object.getString("url"),
                                object.isNull("urlToImage") ? urlToNoImage : object.getString("urlToImage"));
                    }
                }
            }
        } catch (JSONException e) {

        }
        return news;
    }
}
