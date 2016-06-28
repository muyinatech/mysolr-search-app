package mysolr.searchapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Results {

    @JsonProperty("results")
    private final List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    @SuppressWarnings("unused")
    private Results() {
        this(null);
    }

    public Results(List<Result> results) {
        this.results = results;
    }
}
