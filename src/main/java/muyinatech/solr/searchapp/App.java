package muyinatech.solr.searchapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.utils.CollectionUtils;
import spark.utils.StringUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static spark.Spark.get;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final AppProperties APP_PROPERTIES;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper(); // thread-safe

    static {
        try {
            APP_PROPERTIES = new AppProperties();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        get("/search", (req, res) -> executeSearch(req.queryParams("term")));
    }

    private static String executeSearch(String searchTerm) throws IOException, SolrServerException {

        try (SolrClient solr = new HttpSolrClient(APP_PROPERTIES.getSolrURL())) {

            SolrQuery query = getSolrQuery(searchTerm);

            LOGGER.info("Query string: {}", query.toString());

            QueryResponse queryResponse = solr.query(query);

            SolrDocumentList documents = queryResponse.getResults();
            Map<String, Map<String, List<String>>> highlighting = queryResponse.getHighlighting();

            List<Result> resultList = new ArrayList<>();
            for (SolrDocument document : documents) {
                String code = (String) document.get("code");
                String id = (String) document.get("id");
                Map<String, List<String>> highlightMap = highlighting.get(id);
                List<String> friendlyDescription = highlightMap.get("friendlyDescription");

                if (!CollectionUtils.isEmpty(friendlyDescription)) {
                    Result result = new Result();
                    result.setCode(code);
                    result.setHighlightedText(friendlyDescription.get(0));
                    resultList.add(result);
                }
            }

            StringWriter stringWriter = new StringWriter();
            OBJECT_MAPPER.writeValue(stringWriter, new Results(resultList));
            return stringWriter.toString();
        }
    }

    private static SolrQuery getSolrQuery(String searchTerm) {
        SolrQuery query = new SolrQuery();

        query.setHighlight(true);
        query.setHighlightSimplePre("<span>");
        query.setHighlightSimplePost("</span>");

        StringBuilder queryValue = new StringBuilder(searchTerm);
        queryValue.append(" OR ");
        queryValue.append("\"") ;
        queryValue.append(searchTerm);
        queryValue.append("\"");
        queryValue.append(APP_PROPERTIES.getPhraseSlop());
        queryValue.append(APP_PROPERTIES.getPhraseBoost());

        query.set("q", queryValue.toString());

        if (StringUtils.isNotEmpty(APP_PROPERTIES.getField())) {
            query.set("fl", APP_PROPERTIES.getField());
        }

        if (StringUtils.isNotEmpty(APP_PROPERTIES.getQueryField())) {
            query.set("defType", "edismax");
            query.set("qf", APP_PROPERTIES.getQueryField());
        }

        if (StringUtils.isNotEmpty(APP_PROPERTIES.getBoostFunction())) {
            query.set("bf", APP_PROPERTIES.getBoostFunction());
        }

        query.set("wt", "json");

        if (StringUtils.isNotEmpty(APP_PROPERTIES.getRows())) {
            query.set("rows", APP_PROPERTIES.getRows());
        }

        if (StringUtils.isNotEmpty(APP_PROPERTIES.getHighlightField())) {
            query.set("hl.fl", APP_PROPERTIES.getHighlightField());
        }

        query.set("hl.preserveMulti", "true");
        query.set("hl.fragsize", "0");
        query.set("hl.maxAnalyzedChars", "10240000");
        query.set("hl.highlightMultiTerm", "true");

        return query;
    }

}
