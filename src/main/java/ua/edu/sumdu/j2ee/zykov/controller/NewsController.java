package ua.edu.sumdu.j2ee.zykov.controller;

import org.json.JSONArray;
import org.json.XML;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

@RestController
public class NewsController {
    private final static Logger logger = LoggerFactory.getLogger(NewsController.class);
    private final List<NewsService> newsServices;
    private final ServletContext servletContext;
    @Value("${thread.count}")
    private int countThread;

    public NewsController(List<NewsService> newsServices, ServletContext servletContext) {
        this.newsServices = newsServices;
        this.servletContext = servletContext;
    }

    @RequestMapping(path = "/", method = RequestMethod.GET)
    public ResponseEntity<?> welcome() {
        return ResponseEntity.ok("Welcome to news portal!!!");
    }

    @RequestMapping(path = "/news/doc", method = RequestMethod.GET)
    public void getDocument(HttpServletResponse response, @RequestParam(name = "country") String[] countries,
                            @RequestParam(name = "category") String[] categories) {
        News news;
        XWPFDocument document;
        StringBuilder filename = new StringBuilder("news_");
        ExecutorService executorService = Executors.newFixedThreadPool(countThread);
        CompletionService<News> completionService = new ExecutorCompletionService<>(executorService);
        List<News> newsList = new ArrayList<>();
        for (NewsService newsService : newsServices) {
            for (String tempCountry : countries) {
                for (String tempCategory : categories) {
                    Future<News> submit = completionService.submit(() -> newsService.getNews(tempCountry, tempCategory, tempCountry + tempCategory));
                    try {
                        filename.append(tempCategory).append("_");
                        news = submit.get();
                        newsList.add(news);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted thread get news for country {} and category {} - {}", tempCountry, tempCategory, e.getMessage());
                    } catch (ExecutionException e) {
                        logger.error("Execution thread get news for country {} and category {} - {}", tempCountry, tempCategory, e.getMessage());
                    }
                }
                filename.append(tempCountry).append("_");
            }
            document = newsService.getDocument(newsList);
            filename.deleteCharAt(filename.lastIndexOf("_"));
            filename.append(".docx");
            MediaType mediaType = MediaTypeUtils.getMediaTypeForFileName(this.servletContext, filename.toString());
            response.setContentType(mediaType.getType());
            response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename.toString());
            BufferedOutputStream outStream = null;
            try {
                outStream = new BufferedOutputStream(response.getOutputStream());
                document.write(outStream);
                outStream.flush();
                logger.info("Successfully write stream for file name {}", filename.toString());
            } catch (IOException e) {
                logger.error("Failed write stream for file name {} - {}", filename.toString(), e.getMessage());
            } finally {
                try {
                    if (outStream != null) {
                        outStream.close();
                    }
                } catch (IOException e) {
                    logger.error("Failed close stream for file name {} - {}", filename.toString(), e.getMessage());
                }
            }
        }
    }

    @RequestMapping(path = "/news", method = RequestMethod.GET, produces = { MediaType.APPLICATION_XML_VALUE }, params = "format=xml")
    public ResponseEntity<?> getNewsXml(@RequestParam(name = "country") String[] countries,
                                     @RequestParam(name = "category") String[] categories) {
        return getResponseEntity(countries, categories);
    }

    @RequestMapping(path = "/news", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE }, params = "format=json")
    public ResponseEntity<?> getNewsJson(@RequestParam(name = "country") String[] countries,
                                     @RequestParam(name = "category") String[] categories) {
        return getResponseEntity(countries, categories);
    }

    private ResponseEntity<?> getResponseEntity(@RequestParam(name = "country") String[] countries, @RequestParam(name = "category") String[] categories) {
        ExecutorService executorService = Executors.newFixedThreadPool(countThread);
        CompletionService<News> completionService = new ExecutorCompletionService<>(executorService);
        List<News> newsList = new ArrayList<>();
        for (NewsService newsService : newsServices) {
            for (String tempCountry : countries) {
                for (String tempCategory : categories) {
                    Future<News> submit = completionService.submit(() -> newsService.getNews(tempCountry, tempCategory, tempCountry + tempCategory));
                    try {
                        newsList.add(submit.get());
                    } catch (InterruptedException e) {
                        logger.error("Interrupted thread get news for country {} and category {} - {}", tempCountry, tempCategory, e.getMessage());
                    } catch (ExecutionException e) {
                        logger.error("Execution thread get news for country {} and category {} - {}", tempCountry, tempCategory, e.getMessage());
                    }
                }
            }
        }
        return Optional.of(newsList).map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NO_CONTENT));
    }
}
