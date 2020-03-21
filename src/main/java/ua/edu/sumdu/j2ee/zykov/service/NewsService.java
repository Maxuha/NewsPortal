package ua.edu.sumdu.j2ee.zykov.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import ua.edu.sumdu.j2ee.zykov.model.News;

public interface NewsService {
    News getNews(String country, String category);
    XWPFDocument getDocument(News news);
}
