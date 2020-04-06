package ua.edu.sumdu.j2ee.zykov.controller;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.*;

@RestController
public class NewsController {
    private static Logger logger = LoggerFactory.getLogger(NewsController.class);
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
        News news = null;
        XWPFDocument document;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<News> completionService = new ExecutorCompletionService<>(executorService);

        for (NewsService newsService : newsServices) {
            Future<News> submit = completionService.submit(() -> newsService.getNews(country, category, country + category));
            try {
                news = submit.get();
                document = newsService.getDocument(news);
                try (FileOutputStream outputStream = new FileOutputStream("news_" + category + ".docx")) {
                    document.write(outputStream);
                } catch (FileNotFoundException e) {
                    logger.error("File not found - " + e.getMessage());
                } catch (IOException e) {
                    logger.error("Failed save document - " + e.getMessage());
                } catch (NullPointerException e) {
                    logger.info("No news");
                }
            } catch (InterruptedException e) {
                logger.error("Interrupted thread - " + e.getMessage());
            } catch (ExecutionException e) {
                logger.error("Execution thread - " + e.getMessage());
            }
            if (news != null) {
                return ResponseEntity.ok(news);
            }
        }
        return ResponseEntity.ok("Empty");
    }
}
