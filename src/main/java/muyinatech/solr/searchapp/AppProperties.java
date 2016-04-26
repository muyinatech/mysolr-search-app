package muyinatech.solr.searchapp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppProperties {

    private final String solrURL;
    private final String highlightField;
    private final String queryField;
    private final String rows;
    private final String phraseBoost;
    private final String field;
    private final String phraseSlop;
    private final String boostFunction;

    public AppProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(System.getProperty("app.properties")));
        solrURL = properties.getProperty("solr.url");
        highlightField = properties.getProperty("solr.hl.fl");
        queryField = properties.getProperty("solr.qf");
        phraseSlop = properties.getProperty("solr.ps");
        rows = properties.getProperty("solr.rows");
        phraseBoost = properties.getProperty("solr.pb");
        field = properties.getProperty("solr.fl");
        boostFunction = properties.getProperty("solr.bf");
    }

    public String getSolrURL() {
        return solrURL;
    }

    public String getHighlightField() {
        return highlightField;
    }

    public String getQueryField() {
        return queryField;
    }

    public String getRows() {
        return rows;
    }

    public String getPhraseBoost() {
        return phraseBoost;
    }

    public String getField() {
        return field;
    }

    public String getPhraseSlop() {
        return phraseSlop;
    }

    public String getBoostFunction() {
        return boostFunction;
    }
}
