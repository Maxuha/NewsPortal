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
    private final static Logger logger = LoggerFactory.getLogger(NewsController.class);
    private final List<NewsService> newsServices;
    private final ServletContext servletContext;
    private String[] countries;
    private String[] categories;

    public NewsController(List<NewsService> newsServices, ServletContext servletContext) {
        this.newsServices = newsServices;
        this.servletContext = servletContext;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> welcome() {
        return ResponseEntity.ok("Welcome to news portal!!!");
    }

    @RequestMapping(path = "/news/doc", method = RequestMethod.GET)
    public void getDocument(HttpServletResponse response, @RequestParam(name = "country") String country,
                            @RequestParam(name = "category") String category) {
        News news = null;
        XWPFDocument document;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<News> completionService = new ExecutorCompletionService<>(executorService);
        countries = country.split(",");
        categories = category.split(",");

        for (NewsService newsService : newsServices) {
            for (String tempCountry : countries) {
                for (String tempCategory : categories) {
                    Future<News> submit = completionService.submit(() -> newsService.getNews(tempCountry, tempCategory, tempCountry + tempCategory));
                    try {
                        news = submit.get();
                    } catch (InterruptedException e) {
                        logger.error("Interrupted thread get news for country {} and category {} - {}", country, category, e.getMessage());
                    } catch (ExecutionException e) {
                        logger.error("Execution thread get news for country {} and category {} - {}", country, category, e.getMessage());
                    }
                    if (news != null) {
                        document = newsService.getDocument(news);
                        String filename = "news_" + category + ".docx";
                        MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, filename);
                        response.setContentType(mediaType.getType());
                        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
                        BufferedOutputStream outStream = null;
                        try {
                            outStream = new BufferedOutputStream(response.getOutputStream());
                            document.write(outStream);
                            outStream.flush();
                            logger.info("Successfully write stream for file name {}", filename);
                        } catch (IOException e) {
                            logger.error("Failed write stream for file name {} - {}", filename, e.getMessage());
                        } finally {
                            try {
                                if (outStream != null) {
                                    outStream.close();
                                }
                            } catch (IOException e) {
                                logger.error("Failed close stream for file name {} - {}", filename, e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    @RequestMapping(path = "/news/json", method = RequestMethod.GET)
    public ResponseEntity<?> getJson(@RequestParam(name = "country") String country,
                                     @RequestParam(name = "category") String category) {
        String[] countries = country.split(",");
        String[] categories = category.split(",");
        StringBuilder json = new StringBuilder("");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<String> completionService = new ExecutorCompletionService<>(executorService);

        for (NewsService newsService : newsServices) {
            for (String tempCountry : countries) {
                for (String tempCategory : categories) {
                    Future<String> submit = completionService.submit(() -> newsService.getJson(tempCountry, tempCategory, tempCountry + tempCategory));
                    try {
                        json.append(submit.get());
                    } catch (InterruptedException e) {
                        logger.error("Interrupted thread get news for country {} and category {} - {}", country, category, e.getMessage());
                    } catch (ExecutionException e) {
                        logger.error("Execution thread get news for country {} and category {} - {}", country, category, e.getMessage());
                    }
                }
            }
        }

        return ResponseEntity.ok(json.toString());
    }
}
