import java.util.*;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

class DataStorageManager{
    private List <String> words = new ArrayList<>();
    private String data;

    public List <String> dispatch( String[] message) throws Exception{
        if(message[0].equals("init")) return init(message[1]);
        else if(message[0].equals("words")) return words();
        else throw new Exception("Message not understood" + message[0]);
    }

    public List<String> init(String pathToFile){
        try (InputStream in = new FileInputStream(pathToFile);
             Reader reader = new InputStreamReader(in);
             Reader buffer = new BufferedReader(reader)) {
            int r;
            StringBuilder sb = new StringBuilder();
            while ((r = buffer.read()) != -1) {
                char ch = (char) r;
                ch = Character.toLowerCase(ch);
                if(isLetter(ch)){
                    sb.append(ch);
                } else {
                    String word = sb.toString();
                    if(word.length() >= 2)
                        words.add(word);
                    sb = new StringBuilder();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return words;
    }

    public List <String> words() {
        return words;
    }

    private static boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z');
    }
}

class StopWordManager{
    private List <String> stop_words = new ArrayList<>();
    private String data;
    public boolean dispatch( String[] message) throws Exception{
        if(message[0].equals("init")) return init();
        else if(message[0].equals("is_stop_word")) return is_stop_word(message[1]);
        else throw new Exception("Message not understood" + message[0]);
    }

    public boolean init() throws Exception{
        try {
            Scanner sc = new Scanner(new File("../stop_words.txt"));
            while (sc.hasNextLine()) {
                String line = sc.nextLine().toLowerCase();
                String[] words = line.split(",");
                Collections.addAll(stop_words, words);
            }
        }
        catch(FileNotFoundException e) {
            e.getStackTrace();
        }
        return true;
    }

    public boolean is_stop_word(String word) {
        return stop_words.contains(word);
    }
}

class WordFrequencyManager {
    private Map <String, Integer> word_freqs = new HashMap<>();

    public Map <String, Integer> dispatch(String [] message) throws Exception {
        if(message[0].equals("increment_count")) return incrementCount(message[1]);
        else if(message[0].equals("sorted")) return sorted();
        else throw new Exception("Message not understood" + message[0]);
    }

    public Map <String, Integer> incrementCount(String word) {
        if(word_freqs.get(word) == null) {
            word_freqs.put(word, 1);
        }
        else {
            word_freqs.put(word, word_freqs.get(word)+1);
        }
        return word_freqs;
    }

    public Map <String, Integer> sorted() {
        return word_freqs.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }
}

class WordFrequencyController {
    private DataStorageManager storage_manager;
    private StopWordManager stop_word_manager;
    private WordFrequencyManager word_freq_manager;
    private Map <String, Integer> word_freqs;

    public void dispatch(String [] message) throws Exception {
        if(message[0].equals("init")) init(message[1]);
        else if(message[0].equals("run")) run();
        else throw new Exception("Message not understood" + message[0]);
    }

    public void init(String path_to_file) throws Exception{
        storage_manager = new DataStorageManager();
        stop_word_manager = new StopWordManager();
        word_freq_manager = new WordFrequencyManager();
        String[] init_storage = {"init", path_to_file};
        storage_manager.dispatch(init_storage);
        String[] init_stop_words = {"init"};
        stop_word_manager.dispatch(init_stop_words);
    }


    public void run() throws Exception {

        String[] words_run = {"words"};
        for (String word: storage_manager.dispatch(words_run)) {
            String[] stop_words_run = {"is_stop_word", word};
            if (!stop_word_manager.dispatch(stop_words_run)) {
                String[] counter_run = {"increment_count", word};
                word_freq_manager.dispatch(counter_run);
            }
        }

        String[] sort_run = {"sorted"};
        word_freqs = word_freq_manager.dispatch(sort_run);

        int count = 0;
        for (Map.Entry < String, Integer > word: word_freqs.entrySet()) {
            System.out.println(word.getKey() + "  -  " + word.getValue());
            count++;
            if(count==25) {
                break;
            }
        }
    }
}

public class Eleven {

    public static void main(String[] args) throws Exception {
        WordFrequencyController wfcontroller = new WordFrequencyController();
        String[] main_init = {"init", args[0]};
        wfcontroller.dispatch(main_init);
        String[] main_run = {"run"};
        wfcontroller.dispatch(main_run);
    }
}