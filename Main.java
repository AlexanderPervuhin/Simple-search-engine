package search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        if (args[0].equals("--data")) {
            try {
                String[] data = readData(args[1]);
                InvertedIndex index = new InvertedIndex(data);
                index.indexWords();
                Scanner scanner = new Scanner(System.in);
                int command;
                do {
                    System.out.println("\n=== Menu ===\n"
                            + "1.Find a person\n"
                            + "2.Print All\n"
                            + "0.Exit\n");
                    String input = scanner.next();
                    try {
                        command = Integer.parseInt(input);
                        switch (command) {
                            case 1:
                                index.selectSearchStrategy();
                                break;
                            case 2:
                                printData(index.getIndexedData().getData());
                                break;
                            case 0:
                                System.out.println("Bye");
                                return;
                            default:
                                System.out.println("Incorrect option! Try again.");
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Wrong input - NumberFormatException");
                    }
                } while (true);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
    private static  void printData(String[] data)
    {System.out.println("=== List of people ===");
        for (String line : data) {
            System.out.println(line);
        }
    }

    private static String[] readData(String filePath) throws FileNotFoundException {
        File file=new File(filePath);
        StringBuilder builder=new StringBuilder();
        Scanner scanner=new Scanner(file);
        while (scanner.hasNext()) {
            builder.append( scanner.nextLine()).append("\n");
        }
        String[]linesFromFile=builder.toString().split("\n");
        scanner.close();
        return  linesFromFile;
    }
}

class InvertedIndex {

    private IndexedDataBundle indexedData;
    private MatchingStrategy strategy;

    InvertedIndex(String[] data) {
        this.indexedData = new IndexedDataBundle();
        this.indexedData.setData(data);
    }

    void indexWords() {
        String[] data = getData();
        Map<String, LinkedHashSet<Integer>> invertedIndex = getInvertedIndexMap();
        for (int i = 0; i < data.length; i++) {
            String[] words = data[i].toLowerCase().split(" ");
            for (String word :
                    words) {
                LinkedHashSet<Integer> indexes = new LinkedHashSet<>();
                indexes.add(i);
                if (invertedIndex.containsKey(word)) {
                    System.out.println(word + " ");
                    indexes.addAll(invertedIndex.get(word));
                }
                invertedIndex.put(word, indexes);
            }
        }
        setInvertedIndex(invertedIndex);
        System.out.println(getInvertedIndexMap());
    }

    void selectSearchStrategy() {
        System.out.println("Select a matching strategy: ALL, ANY, NONE");
        Scanner scanner = new Scanner(System.in);
        String Strategy = scanner.next().toUpperCase();
        switch (Strategy) {
            case "ALL":
                this.setStrategy(new MatchAll());
                break;
            case "ANY":
                this.setStrategy(new MatchAny());
                break;
            case "NONE":
                this.setStrategy(new MatchNone());
                break;
            default:
                System.out.println("Wrong Strategy");
                return;
        }
        search();
    }

    private void search() {
        String[] data=indexedData.getData();
        Map <String, LinkedHashSet<Integer>> invertedIndex=indexedData.getInvertedIndex();
        Scanner scanner = new Scanner(System.in);
        StringBuilder result = new StringBuilder();
        System.out.println("\nEnter a name or email to search all suitable people");
        String[] wordsToSearch = scanner.nextLine().toLowerCase().split(" ");
        strategy.setMatchedSet(data.length);
        for (String word:
                wordsToSearch) {
            if (invertedIndex.containsKey(word)) {
                strategy.match(invertedIndex.get(word));
            }
        }
        if(strategy.getMatchedSet().isEmpty()) {
            result.append("No matching people found.\n");
        }
        else {
            for (int index :
                    strategy.getMatchedSet()) {
                result.append(data[index]).append("\n");
            }
            result.insert(0, "\nFound People:\n");
        }
        System.out.println(result.toString());
    }

    private String[] getData() {
        return indexedData.getData();
    }
    IndexedDataBundle getIndexedData() {
        return indexedData;
    }
    private Map<String, LinkedHashSet<Integer>> getInvertedIndexMap() {
        return indexedData.getInvertedIndex();
    }


    private void setInvertedIndex(Map<String, LinkedHashSet<Integer>> invertedIndex) {
        this.indexedData.setInvertedIndex(invertedIndex);
    }

    private void setStrategy(MatchingStrategy strategy) {
        this.strategy = strategy;
    }
}

abstract class MatchingStrategy {
    LinkedHashSet<Integer> matchedSet;

    MatchingStrategy() {
        this.matchedSet = new LinkedHashSet<>();
    }
    void match(LinkedHashSet<Integer> setToMatch) {
    }

    void setMatchedSet(int size) {
        for (int i=0;i<size;i++) {
            matchedSet.add(i);
        }
        System.out.println(matchedSet);
    }

    LinkedHashSet<Integer> getMatchedSet() {
        return matchedSet;
    }
}

class MatchAll extends MatchingStrategy {
    @Override
    public void match(LinkedHashSet<Integer> setToMatch) {
        this.matchedSet.retainAll(setToMatch);
    }
}
class  MatchNone extends MatchingStrategy {
    @Override
    public void match( LinkedHashSet<Integer> setToMatch) {
        this.matchedSet.removeAll(setToMatch);
    }
}
class  MatchAny extends MatchingStrategy {
    void setMatchedSet(int size) {
        matchedSet.clear();
    }
    @Override
    public  void match( LinkedHashSet<Integer> setToMatch) {
        this.matchedSet.addAll(setToMatch);
    }
}
