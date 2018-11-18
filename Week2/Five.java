import java.io.*;
import java.util.*;

public class Five {
    private static HashSet<String> readStopWords() throws IOException{
        File file = new File("../stop_words.txt");
        Scanner scan = new Scanner(file);
        HashSet<String> stopwords = new HashSet<>();
        while(scan.hasNextLine()) {
            String line = scan.nextLine();
            Scanner lineScanner = new Scanner(line);
            lineScanner.useDelimiter(",");
            while (lineScanner.hasNext()) {
                // Get each split data from the Scanner object and print the value.
                String part = lineScanner.next();
                stopwords.add(part);
            }
        }
        return stopwords;
    }

    private static List<String> readFile(String[] args) throws IOException{
        File file1 = new File(args[0]);
        BufferedReader br = new BufferedReader(new FileReader(file1));
        StringBuilder word = new StringBuilder();
        int currentChar;
        List<String>words = new ArrayList<>();
        while ((currentChar = br.read()) != -1){
            char s;
            s = (char) currentChar;
            if(Character.isLetter(s)){
                s = Character.toLowerCase(s);
                word.append(s);
            }
            else{
                words.add(word.toString());
                word.setLength(0);
            }
        }
        return words;
    }

    private static HashMap<String, Integer> calculateFreq(List<String>words, HashSet<String>stopwords) {
        HashMap<String, Integer> count = new HashMap<>();
        for (String word:words) {
            if(word.length()>=2) {
                if (!stopwords.contains(word)) {
                    if (count.containsKey(word)) {
                        count.put(word, count.get(word) + 1);
                    } else {
                        count.put(word, 1);
                    }
                }
            }
        }
        return count;
    }

    private static List<Pair> SortFreqTable(HashMap<String, Integer> count){
        List<Pair> keys = new ArrayList<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : count.entrySet()) {
            keys.add(new Pair(stringIntegerEntry.getKey(), stringIntegerEntry.getValue()));
        }
        Collections.sort(keys, Comparator.reverseOrder());
        return keys;
    }

    private static void printOut(List<Pair> keys){
        for (int i=0; i<25;i++) {
            Pair key = keys.get(i);
            System.out.println(key.key + "  -  " + key.val);
        }
    }
    public static void main(String[] args) throws IOException {
        HashSet<String> stopwords = readStopWords();
        List<String>words= readFile(args);
        HashMap<String, Integer> FreqTable = calculateFreq(words, stopwords);
        List<Pair> SortedKeys = SortFreqTable(FreqTable);
        printOut(SortedKeys);
    }
}


class Pair implements Comparable<Pair>{
    String key;
    int val;

    public Pair(String key, int val){
        this.key = key;
        this.val = val;
    }

    public int compareTo(Pair p){
        return this.val - p.val;
    }
}