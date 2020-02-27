package search;


import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class IndexedDataBundle {
    private String[] data;
    private Map<String, LinkedHashSet<Integer>> invertedIndex;

    public IndexedDataBundle() {
        this.invertedIndex=new HashMap<>();
    }

    String[] getData() {
        return data;
    }

    void setData(String[] data) {
        this.data = data;
    }

    Map<String, LinkedHashSet<Integer>> getInvertedIndex() {
        return invertedIndex;
    }

    void setInvertedIndex(Map<String, LinkedHashSet<Integer>> invertedIndex) {
        this.invertedIndex = invertedIndex;
    }


}