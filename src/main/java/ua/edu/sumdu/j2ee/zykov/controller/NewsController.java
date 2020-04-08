package ua.edu.sumdu.j2ee.zykov.controller;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.sumdu.j2ee.zykov.model.News;
import ua.edu.sumdu.j2ee.zykov.service.NewsService;
import ua.edu.sumdu.j2ee.zykov.util.MediaTypeUtils;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.concurrent.*;

@RestController
public class NewsController {
    private static Logger logger = LoggerFactory.getLogger(NewsController.class);
    private final List<NewsService> newsServices;
    private final ServletContext servletContext;

    public NewsController(List<NewsService> newsServices, ServletContext servletContext) {
        this.newsServices = newsServices;
        this.servletContext = servletContext;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> welcome() {
        return ResponseEntity.ok("Welcome to news portal!!!");
    }

    @RequestMapping(path = "/news", method = RequestMethod.GET)
    public void getInfoNews(HttpServletResponse response, @RequestParam(name = "country") String country,
                            @RequestParam(name = "category") String category) throws IOException {
        News news = null;
        XWPFDocument document = null;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<News> completionService = new ExecutorCompletionService<>(executorService);

        for (NewsService newsService : newsServices) {
            Future<News> submit = completionService.submit(() -> newsService.getNews(country, category, country + category));
            try {
                news = submit.get();
                document = newsService.getDocument(news);
            } catch (InterruptedException e) {
                logger.error("Interrupted thread - " + e.getMessage());
            } catch (ExecutionException e) {
                logger.error("Execution thread - " + e.getMessage());
            }
            if (news != null) {
                String filename = "news_" + category + ".docx";
                MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, filename);
                response.setContentType(mediaType.getType());
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
                BufferedOutputStream outStream = new BufferedOutputStream(response.getOutputStream());
                document.write(outStream);
                outStream.flush();
                outStream.close();
            }
        }
    }
}
