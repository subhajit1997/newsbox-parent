package org.newsbox;

import com.google.gson.Gson;
import com.newsbox.commons.NewsAPIAggregatorConstants;
import com.newsbox.entity.Article;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FullSolrIndexer {
    public static final Logger logger = LoggerFactory.getLogger(FullSolrIndexer.class);
    public static SolrClient solrClient = null;
    public static int countIndexedDoc=1;
    public static void main(String[] args) {
        logger.info("Performing full indexing");
        logger.info("Indexing data to solr for URL: "+NewsAPIAggregatorConstants.SOLR_URL +" ,core:"+NewsAPIAggregatorConstants.CORE_NAME);
        try {
            solrClient = new HttpSolrClient.Builder(NewsAPIAggregatorConstants.SOLR_URL + NewsAPIAggregatorConstants.CORE_NAME).build();
            File directory = new File(NewsAPIAggregatorConstants.OUTPUT_FILE_DESTINATION);
            logger.info("Indexing all files at location: "+NewsAPIAggregatorConstants.OUTPUT_FILE_DESTINATION);
            File[] files = directory.listFiles();

            for (File file : files) {
                if (file.isFile()) {
                    String filePath = file.getAbsolutePath();
                    logger.info("File to index: " + filePath);
                    BufferedReader reader = new BufferedReader(new FileReader(filePath));
                    String line = null;
                    StringBuilder jsonContent = new StringBuilder();

                    while ((line = reader.readLine()) != null) {
                        jsonContent.append(line);
                    }
                    reader.close();

                    String jsonString = jsonContent.toString();
                    indexData(solrClient, jsonString);
                }
            }

            logger.info("Waiting for commit to complete");
            solrClient.commit();
            logger.info("Committed to Solr");
            System.out.println("Total Number of : "+countIndexedDoc+" Data indexed successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }

    }
    private static void indexData(SolrClient solrClient, String jsonString) throws IOException, SolrServerException {

        Article[] articles = new Gson().fromJson(jsonString, Article[].class);
        logger.info("Total Documents to be indexed for current file: "+articles.length);
        for (Article article : articles) {
            SolrInputDocument document = new SolrInputDocument();
            document.addField("id", article.getId());
            document.addField("title", article.getTitle());
            document.addField("description", article.getDescription());
            document.addField("url", article.getUrl());
            document.addField("publishedAt", article.getPublishedAt());
            document.addField("author", article.getAuthor());
            document.addField("category", article.getCategory());
            document.addField("imageURL", article.getUrlToImage());
            solrClient.add(document);
            logger.info("Indexed count : "+countIndexedDoc);
            countIndexedDoc++;
        }
    }
}
