package ua.edu.sumdu.j2ee.zykov.controller;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.sumdu.j2ee.zykov.model.News;
import ua.edu.sumdu.j2ee.zykov.service.NewsService;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@RestController
public class NewsController {
    private final List<NewsService> newsServices;

    public NewsController(List<NewsService> newsServices) {
        this.newsServices = newsServices;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> welcome() {
        return ResponseEntity.ok("Welcome to news portal!!!");
    }

    @RequestMapping(path = "/news", method = RequestMethod.GET)
    public ResponseEntity<?> getInfoNews(@RequestParam(name = "country") String country,
                                         @RequestParam(name = "category") String category) {
        News[] news;
        XWPFDocument document;
        for (NewsService newsService : newsServices) {
            news = newsService.getNews(country, category);
            document = newsService.getDocument(news);

            try (FileOutputStream outputStream = new FileOutputStream("news_" + category + ".docx")) {
                document.write(outputStream);
            } catch (FileNotFoundException e) {
            } catch (IOException e) {
            }
        }
        return ResponseEntity.ok("Empty");
    }
}
