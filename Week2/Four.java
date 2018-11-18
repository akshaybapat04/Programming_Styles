
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Four {
    private static String fileName;
    private static HashSet<String> stopwords = new HashSet<>();
    private static List<String>words = new ArrayList<>();
    private static HashMap<String, Integer> count = new HashMap<>();
    private static List<Pair> keys = new ArrayList<>();

    private static void readStopWords() throws IOException{
        File file = new File("../stop_words.txt");
        Scanner scan = new Scanner(file);
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
    }

    private static void readFile() throws IOException{
        File file1 = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file1));
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
                words.add(word.toString());
                word.setLength(0);
            }
        }
    }

    private static void calculateFreq() {
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
    }

    private static void SortFreqTable(){
        for (Map.Entry<String, Integer> stringIntegerEntry : count.entrySet()) {
            keys.add(new Pair(stringIntegerEntry.getKey(), stringIntegerEntry.getValue()));
        }
        Collections.sort(keys, Comparator.reverseOrder());
    }

    private static void printOut(){
        for (int i=0; i<25;i++) {
            Pair key = keys.get(i);
            System.out.println(key.key + "  -  " + key.val);
        }
    }
    
    public static void main(String[] args) throws IOException {
        fileName = args[0];
        readStopWords();
        readFile();
        calculateFreq();
        SortFreqTable();
        printOut();
    }
}