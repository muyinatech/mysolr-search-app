package mysolr.searchapp;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Results {

    @JsonProperty("results")
    private List<Result> results = new ArrayList<>();

    public List<Result> getResultList() {
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
