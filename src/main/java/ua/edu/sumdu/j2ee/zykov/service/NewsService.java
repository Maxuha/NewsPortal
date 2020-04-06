package ua.edu.sumdu.j2ee.zykov.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import ua.edu.sumdu.j2ee.zykov.model.News;

public interface NewsService {
    News getNews(String country, String category, String countryCategoryKey);
    News getNewsAndPut(String country, String category, String countryCategoryKey);
    XWPFDocument getDocument(News news);
}
