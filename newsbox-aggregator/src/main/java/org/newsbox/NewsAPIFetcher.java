package org.newsbox;

import com.google.gson.*;
import com.newsbox.commons.NewsAPIAggregatorConstants;
import com.newsbox.entity.Article;
import com.newsbox.entity.NewsResponse;
import okhttp3.HttpUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Main class to fetch and aggregate data from newsAPI
 */

public class NewsAPIFetcher {
    static String startDate = null;
    static String endDate = null;
    static String qParameter = null;

    public static final Logger logger = LoggerFactory.getLogger(NewsAPIFetcher.class);
    public static void main(String[] args) {

        if (args.length==3){
            startDate = args[0];
            endDate = args[1];
            qParameter = args[2];
        }
        for (String arg : args) {
            logger.info("Argument: " + arg);
        }
        logger.info("Starting to fetch data from: "+ NewsAPIAggregatorConstants.NEWS_API_URL);
        gatherData();
        logger.info("Completed");

    }
    public static void gatherData(){
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if(startDate==null){
            startDate = yesterday.format(formatter);
        }
        if(endDate==null){
            endDate = today.format(formatter);
        }
        if(qParameter == null){
            qParameter = NewsAPIAggregatorConstants.NEWS_API_Q;
        }


        logger.info("Gather data for parameter : "+qParameter+" from : "+startDate+" to today: "+endDate);
        HttpUrl.Builder urlBuilder = new HttpUrl.Builder()
                .scheme("https")
                .host(NewsAPIAggregatorConstants.NEWS_API_URL)
                .addPathSegment(NewsAPIAggregatorConstants.NEWS_API_VERSION)
                .addPathSegment(NewsAPIAggregatorConstants.NEWS_API_HANDLER);
        urlBuilder.addQueryParameter("q", qParameter)
                .addQueryParameter("sortBy", NewsAPIAggregatorConstants.NEWS_API_SORTBY)
                .addQueryParameter("apiKey", NewsAPIAggregatorConstants.API_KEY)
                .addQueryParameter("language", NewsAPIAggregatorConstants.NEWS_API_LANGUAGE)
                .addQueryParameter("from",startDate)
                .addQueryParameter("to",  endDate);
        String urlString = urlBuilder.build().toString();
        logger.info("Successfully fetched response from NewsAPI");
        logger.info("URL:"+ urlString);
        String response = HttpResponse.okHttpResponse(urlString);
        if(response!=null){
            String fileWriterName = NewsAPIAggregatorConstants.OUTPUT_FILE_DESTINATION+endDate+NewsAPIAggregatorConstants.OUTPUT_FILE_PATH;
            writeJsonToFile(fileWriterName,response);
        }else {
            logger.info("No response data found");
        }
    }
    public static void writeJsonToFile(String fileDestination, String response){
        try {
            DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            NewsResponse newsResponse = gson.fromJson(response, NewsResponse.class);
            int random=0;
            for (Article article : newsResponse.getArticles()) {
                LocalDateTime currentTimestamp = LocalDateTime.now();
                article.setId(currentTimestamp.format(localDateTimeFormatter)+random);
                article.setLang("en");
                article.setCategory(qParameter);
                random++;
            }
            FileWriter fileWriter = new FileWriter(fileDestination);
            gson.toJson(newsResponse.getArticles(), fileWriter);
            fileWriter.close();
            logger.info("Articles extracted and written to:  "+fileDestination);
        } catch (IOException e) {
            logger.info("Error while writing to file",e);
        }
    }
}
