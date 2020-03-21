package ua.edu.sumdu.j2ee.zykov.service;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import ua.edu.sumdu.j2ee.zykov.model.News;
import ua.edu.sumdu.j2ee.zykov.util.JsonParser;
import ua.edu.sumdu.j2ee.zykov.util.Network;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class NewsApiService implements NewsService {
    @Override
    public News[] getNews(String country, String category) {
        Map<String, String> headers = new HashMap<>();
        headers.put("country", country);
        headers.put("category", category);
        News[] news = null;
        try {
            news = JsonParser.getNewsArrayFromJson(Network.getResponse("http://newsapi.org/v2/top-headlines", headers));
        } catch (IOException e) {

        }
        return news;
    }

    @Override
    public XWPFDocument getDocument(News[] news, String categoryName) {
        int size = 128;
        XWPFDocument document = new XWPFDocument();

        for (News tempNews : news) {
            XWPFParagraph title = document.createParagraph();
            title.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun titleRun = title.createRun();
            titleRun.setText(tempNews.getTitle());
            titleRun.setColor("000000");
            titleRun.setBold(true);
            titleRun.setFontFamily("Times New Roman");
            titleRun.setFontSize(15);

            XWPFParagraph image = document.createParagraph();
            image.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun imageRun = image.createRun();
            imageRun.setTextPosition(20);

            InputStream in = null;
            try {
                in = Network.getImageInputStream(tempNews.getUrlToImage());
            } catch (IOException e) {

            }
            BufferedImage bufferedImage = null;
            try {
                bufferedImage = ImageIO.read(in);
            } catch (IOException e) {
            }  finally {
                try {
                    in.close();
                } catch (IOException e) {
                    System.out.println("Error close stream.");
                }
            }

            try {
                in = Network.getImageInputStream(tempNews.getUrlToImage());
            } catch (IOException e) {

            }
            assert bufferedImage != null;
            try {
                imageRun.addPicture(in, XWPFDocument.PICTURE_TYPE_PNG, "out.png", Units.toEMU(size /
                        (float)bufferedImage.getHeight() * bufferedImage.getWidth()), Units.toEMU(size));
            } catch (InvalidFormatException e) {
            } catch (IOException e) {
            }

            XWPFParagraph description = document.createParagraph();
            description.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun descriptionRun = description.createRun();
            descriptionRun.setText(tempNews.getDescription());
            descriptionRun.setColor("000000");
            descriptionRun.setBold(true);
            descriptionRun.setFontFamily("Times New Roman");
            descriptionRun.setFontSize(14);

            XWPFParagraph url = document.createParagraph();
            url.setAlignment(ParagraphAlignment.LEFT);
            XWPFRun urlRun = url.createRun();
            urlRun.setText(tempNews.getUrl());
            urlRun.setColor("009933");
            urlRun.setBold(true);
            urlRun.setFontFamily("Times New Roman");
            urlRun.setFontSize(12);

            XWPFParagraph author = document.createParagraph();
            author.setAlignment(ParagraphAlignment.RIGHT);
            XWPFRun authorRun = author.createRun();
            authorRun.setText(tempNews.getAuthor());
            authorRun.setColor("000000");
            authorRun.setBold(true);
            authorRun.setFontFamily("Times New Roman");
            authorRun.setFontSize(14);
            authorRun.setTextPosition(30);
        }
        return document;
    }
}
