import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
@SuppressWarnings("unchecked")

class WordFrequencyFramework {
    private List<Function<String, Void>> load_event_handlers = new ArrayList<>();
    private List<FunctionWithoutArgument> dowork_event_handlers = new ArrayList<>();
    private List<FunctionWithoutArgument> end_event_handlers = new ArrayList<>();


    public void register_for_load_event(Function handler){
        this.load_event_handlers.add(handler);
    }

    public void register_dowork_event_handlers(FunctionWithoutArgument handler){
        this.dowork_event_handlers.add(handler);
    }

    public void register_end_event_handlers(FunctionWithoutArgument handler){
        this.end_event_handlers.add(handler);
    }

    public void run(String pathToFile){
        load_event_handlers.forEach(h -> h.apply(pathToFile));
        dowork_event_handlers.forEach(h -> h.apply());
        end_event_handlers.forEach(h -> h.apply());
    }
}


class DataStorage {

    private List<String> data;
    private StopWordFilter stop_word_filter = null;
    private List<Function<String, Void>>word_event_handlers = new ArrayList<>();
    
    private  boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z');
    }
    
    public DataStorage(WordFrequencyFramework wffapp, StopWordFilter stop_word_filter) {
        this.stop_word_filter = stop_word_filter;
        wffapp.register_for_load_event(this.load);
        wffapp.register_dowork_event_handlers(this.produce_words);
    }

    private Function<String, Void> load = (pathToFile) -> {
        List<String> words = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(pathToFile))) {
            StringBuilder word = new StringBuilder();
            int currentChar;
            while ((currentChar = br.read()) != -1){
                char s;
                s = (char) currentChar;
                if(Character.isLetter(s)){
                    s = Character.toLowerCase(s);
                    word.append(s);
                }
                else{
                    if(word.length()>=2) {
                        words.add(word.toString());
                    }
                    word.setLength(0);
                }
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.data = words;
        return null;
    };

    private FunctionWithoutArgument produce_words = () -> {
        for (String w : this.data) {
            if(!this.stop_word_filter.is_stop_word(w)){
                word_event_handlers.forEach(h -> h.apply(w));
            }
        }
    };

    public void register_for_word_event(Function handler){
        word_event_handlers.add(handler);
    }
}

class StopWordFilter {
    private List<String> stop_words = new ArrayList<>();

    Boolean is_stop_word(String word) {
        for (String t : stop_words){
            if(t.equals(word)) {
                return true;
            }
        }
        return false;
    }

    public StopWordFilter(WordFrequencyFramework wffapp) {
        wffapp.register_for_load_event(this.load);
    }

    private Function<String, Void> load = (String ignore) -> {
        List<String> stop_words = new ArrayList<>();        
        try {
            Scanner scan = new Scanner(new File("../stop_words.txt"));
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(",");
                while (lineScanner.hasNext()) {
                    // Get each split data from the Scanner object and print the value.
                    String part = lineScanner.next();
                    stop_words.add(part);
                }
            }

        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.stop_words.addAll(stop_words);
        return null;
    };
}

interface FunctionWithoutArgument {
    void apply();
}

class WordFrequencyCounter {
    private Map<String, Integer> word_freqs = new HashMap<>();

    public WordFrequencyCounter(WordFrequencyFramework wffapp, DataStorage data_storage) {
        data_storage.register_for_word_event(this.increment_count);
        wffapp.register_end_event_handlers(this.print_freqs);
    }

    private Function<String, Void> increment_count = (String word) -> {
        word_freqs.put(word, word_freqs.getOrDefault(word, 0)+1);
        return null;
    };

    private FunctionWithoutArgument print_freqs = () -> {
        int count = 0;
        Map<String, Integer> sorted_word_freqs = word_freqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        
        for (Map.Entry < String, Integer > word: sorted_word_freqs.entrySet()) {
            System.out.println(word.getKey() + "  -  " + word.getValue());
            count++;
            if(count==25) {
                break;
            }
        }
    };
}

class WordsWithZ {
    private int count = 0;

    public WordsWithZ(WordFrequencyFramework wffapp, DataStorage data_storage) {
        data_storage.register_for_word_event(this.increment_count);
        wffapp.register_end_event_handlers(this.printWordsWithZ);
    }

    private Function<String, Void> increment_count = (String word) -> {
        if(word.contains("z")){
            this.count++;
        }
        return null;
    };

    private FunctionWithoutArgument printWordsWithZ = () -> {
        System.out.println("Number of non-stop words containing z: " + this.count);
    };
}

class Fourteen {
    public static void main(String[] args) {
        WordFrequencyFramework wfapp = new WordFrequencyFramework();
        StopWordFilter stop_word_filter = new StopWordFilter(wfapp);
        DataStorage data_storage = new DataStorage(wfapp, stop_word_filter);
        new WordFrequencyCounter(wfapp, data_storage);
        new WordsWithZ(wfapp, data_storage);
        wfapp.run(args[0]);
    }
}