import java.io.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@SuppressWarnings("unchecked")

class EventManager {

    private Map<String, List<Function>> subscriptions;

    public EventManager() {
        this.subscriptions = new HashMap<>();
    }

    public void subscribe(String event_type, Function handler){
        if(!subscriptions.containsKey(event_type)) {
            subscriptions.put(event_type, new ArrayList<>());
        }
        subscriptions.get(event_type).add(handler);
    }

    public void publish(List<String> event){
        String event_type = event.get(0);
        subscriptions.get(event_type).forEach(h-> h.apply(event));
    }
}

class DataStorage15 {

    private EventManager event_manager;
    private List<String> data;

    private  boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z');
    }

    public DataStorage(EventManager event_manager) {
        this.event_manager = event_manager;
        this.event_manager.subscribe("load", this.load);
        this.event_manager.subscribe("start", this.produce_words);
    }

    private Function<List<String>, Void> load = (List<String> event) -> {
        String pathToFile = event.get(1);
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

    private Function<List<String>, Void> produce_words = (List<String> event) -> {
        
        for (String word : this.data) {
            List<String> data_str = new ArrayList<>();
            data_str.add("word");
            data_str.add(word);
            this.event_manager.publish(data_str);
        }
        List<String> data_str = new ArrayList<>();
        data_str.add("eof");
        data_str.add(null);
        this.event_manager.publish(data_str);
        return null;
    };
}


class StopWordFilter15 {
    private EventManager event_manager;
    private List<String> stop_words;

    public StopWordFilter(EventManager event_manager) {
        this.event_manager = event_manager;
        stop_words = new ArrayList<>();
        this.event_manager.subscribe("load", this.load);
        this.event_manager.subscribe("word", this.is_stop_word);
    }


    private Function<List<String>, Void> load = (List<String> event) -> {
        List<String> stop_words = new ArrayList<>();        
        try {
            Scanner scan = new Scanner(new File("../../stop_words.txt"));
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

    private Function<List<String>, Void> is_stop_word = (event) -> {
        String word = event.get(1);
        if(!(stop_words.stream().filter((stopWord)->stopWord.equals(word)).count() >= 1)){
            List<String> data_str = new ArrayList<>();
            data_str.add("valid_word");
            data_str.add(word);
            this.event_manager.publish(data_str);
        }
        return null;
    };
}

class WordFrequencyCounter15 {
    private EventManager event_manager;
    private Map<String, Integer> word_freqs;

    public WordFrequencyCounter(EventManager event_manager) {
        this.event_manager = event_manager;
        word_freqs = new HashMap<>();
        this.event_manager.subscribe("valid_word", this.increment_count);
        this.event_manager.subscribe("print", this.print_freqs);
    }

    private Function<List<String>, Void> increment_count = (List<String> event) -> {
        String word = event.get(1);
        if(word_freqs.containsKey(word)) {
            word_freqs.put(word, word_freqs.get(word)+1);
        }
        else {
            word_freqs.put(word, 1);    
        }
        return null;
    };

    private Function<List<String>, Void> print_freqs = (List<String> event) -> {
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
        return null;
    };
}


class WordFrequencyApplication15 {

    private EventManager event_manager;

    public WordFrequencyApplication(EventManager event_manager) {
        this.event_manager = event_manager;
        this.event_manager.subscribe("run", this.run);
        this.event_manager.subscribe("eof", this.stop);
    }

    private Function<List<String>, Void> run = (List<String> event) -> {
        String pathToFile = event.get(1);
        this.event_manager.publish(Stream.of("load", pathToFile).collect(Collectors.toList()));
        this.event_manager.publish(Stream.of("start", null).collect(Collectors.toList()));
        return null;
    };

    private Function<List<String>, Void> stop = (List<String> event) -> {
        this.event_manager.publish(Stream.of("print", null).collect(Collectors.toList()));
        return null;
    };
}

class WordsWithZ15 {
    private EventManager event_manager;
    private int counter = 0;

    public WordsWithZ(EventManager event_manager) {
        this.event_manager = event_manager;
        this.event_manager.subscribe("valid_word", this.increment_count);
        this.event_manager.subscribe("print", this.printWordsStartingWithZCount);
    }

    private Function<List<String>, Void> increment_count = (List<String> event) -> {
        String word = event.get(1);
        if(word.contains("z")){
            this.counter++;
        }
        return null;
    };

    private Function<List<String>, Void> printWordsStartingWithZCount = (List<String> event) -> {
        System.out.println(String.format("Number of non-stop words starting with z: %d", this.counter));
        return null;
    };
}

class Fifteen {
    public static void main(String[] args) {
        EventManager event_manager = new EventManager();
        new DataStorage(event_manager);
        new StopWordFilter(event_manager);
        new WordFrequencyCounter(event_manager);
        new WordFrequencyApplication(event_manager);
        new WordsWithZ(event_manager);
        event_manager.publish(Stream.of("run", args[0]).collect(Collectors.toList()));
    }
}