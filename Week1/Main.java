import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
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

        File file1 = new File(args[0]);
        HashMap<String, Integer> count = new HashMap<String, Integer>();
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
                if(word.length()>=2) {
                    if (!stopwords.contains(word.toString())) {
                        if (count.containsKey(word.toString())) {
                            count.put(word.toString(), count.get(word.toString()) + 1);
                        } else {
                            count.put(word.toString(), 1);
                        }
                    }
                }
                word.setLength(0);
            }
        }

        List<Pair> keys = new ArrayList<>();

        for (Map.Entry<String, Integer> stringIntegerEntry : count.entrySet()) {
            keys.add(new Pair(stringIntegerEntry.getKey(), stringIntegerEntry.getValue()));
        }
        Collections.sort(keys, Comparator.reverseOrder());
        for (int i=0; i<25;i++) {
            Pair key = keys.get(i);
            System.out.println(key.key + "  -  " + key.val);
        }
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