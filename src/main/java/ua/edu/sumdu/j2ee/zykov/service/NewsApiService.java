package ua.edu.sumdu.j2ee.zykov.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import ua.edu.sumdu.j2ee.zykov.model.Article;
import ua.edu.sumdu.j2ee.zykov.model.News;
import ua.edu.sumdu.j2ee.zykov.util.NewsApiConverter;
import ua.edu.sumdu.j2ee.zykov.util.Network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NewsApiService implements NewsService {
    private static final Logger logger = LoggerFactory.getLogger(NewsApiService.class);
    @Value("${data.news.token}")
    private String token;
    private final NewsApiConverter newsApiConverter;

    public NewsApiService(NewsApiConverter newsApiConverter) {
        this.newsApiConverter = newsApiConverter;
    }

    @Cacheable(cacheNames = "news", key = "#countryCategoryKey")
    @Override
    public News getNews(String country, String category, String countryCategoryKey) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("country", country);
        parameters.put("category", category);
        News news = null;
        try {
            news = newsApiConverter.convert(Network.getResponse("http://newsapi.org/v2/top-headlines", token, parameters));
            logger.info("News successfully received from a remote server newsapi.org");
        } catch (IOException e) {
            logger.error("Failed to receive news a remote server newsapi.org - " + e.getMessage());
        }
        return news;
    }

    @CachePut(cacheNames = "news", key = "#countryCategoryKey")
    @Override
    public News getNewsAndPut(String country, String category, String countryCategoryKey) {
        return getNews(country, category, countryCategoryKey);
    }

    @Override
    public XWPFDocument getDocument(List<News> newsList) {
        int size = 128;
        XWPFDocument document = new XWPFDocument();
        for (News news : newsList) {
            Article[] articles = news.getArticles();
            for (Article article : articles) {
                XWPFParagraph title = document.createParagraph();
                title.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun titleRun = title.createRun();
                titleRun.setText(article.getTitle());
                titleRun.setColor("000000");
                titleRun.setBold(true);
                titleRun.setFontFamily("Times New Roman");
                titleRun.setFontSize(15);

                XWPFParagraph image = document.createParagraph();
                image.setAlignment(ParagraphAlignment.CENTER);
                XWPFRun imageRun = image.createRun();
                imageRun.setTextPosition(20);

                InputStream in = null;
                BufferedImage bufferedImage;
                try {
                    in = Network.getImageInputStream(article.getUrlToImage());
                    bufferedImage = ImageIO.read(in);
                    in = Network.getImageInputStream(article.getUrlToImage());
                    imageRun.addPicture(in, XWPFDocument.PICTURE_TYPE_PNG, "out.png", Units.toEMU(size /
                            (float) bufferedImage.getHeight() * bufferedImage.getWidth()), Units.toEMU(size));
                } catch (NullPointerException e) {
                    logger.error("No found image {} - {}", article.getUrlToImage(), e.getMessage());
                } catch (IllegalArgumentException e) {
                    logger.error("Illegal argument for image {} - {}", article.getUrlToImage(), e.getMessage());
                } catch (IllegalStateException e) {
                    logger.error("Illegal state for image {} - {}", article.getUrlToImage(), e.getMessage());
                } catch (IOException e) {
                    logger.error("Image not load {} - {}", article.getUrlToImage(), e.getMessage());
                } catch (InvalidFormatException e) {
                    logger.error("Invalid format picture {} - {}", article.getUrlToImage(), e.getMessage());
                } finally {
                    try {
                        if (in != null) {
                            in.close();
                        }
                    } catch (IOException e) {
                        logger.warn("Error close stream for load image {} - {}", article.getUrlToImage(), e.getMessage());
                    }
                }

                XWPFParagraph description = document.createParagraph();
                description.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun descriptionRun = description.createRun();
                descriptionRun.setText(article.getDescription());
                descriptionRun.setColor("000000");
                descriptionRun.setBold(false);
                descriptionRun.setFontFamily("Times New Roman");
                descriptionRun.setFontSize(14);

                XWPFParagraph url = document.createParagraph();
                url.setAlignment(ParagraphAlignment.LEFT);
                XWPFRun urlRun = url.createRun();
                urlRun.setText(article.getUrl());
                urlRun.setColor("009933");
                urlRun.setBold(true);
                urlRun.setFontFamily("Times New Roman");
                urlRun.setFontSize(12);

                XWPFParagraph author = document.createParagraph();
                author.setAlignment(ParagraphAlignment.RIGHT);
                XWPFRun authorRun = author.createRun();
                authorRun.setText(article.getAuthor());
                authorRun.setColor("000000");
                authorRun.setBold(true);
                authorRun.setFontFamily("Times New Roman");
                authorRun.setFontSize(14);
                authorRun.setTextPosition(30);
                logger.info("Added news {} to document", article.getUrl());
            }
        }
        return document;
    }
}
