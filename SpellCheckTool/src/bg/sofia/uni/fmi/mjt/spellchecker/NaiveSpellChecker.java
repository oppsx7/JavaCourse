package bg.sofia.uni.fmi.mjt.spellchecker;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class NaiveSpellChecker implements SpellChecker {
    private final HashMap<String, List<Pair<String, Integer>>> dictionary;
    private List<String> stopwords;
    private HashMap<String, Double> cosinusSimilarWords;

    public NaiveSpellChecker(Reader dictionaryReader, Reader stopwordsReader) {
        String dictWord;
        String stopWord;
        dictionary = new HashMap<>();
        stopwords = new ArrayList<>();
        cosinusSimilarWords = new HashMap<>();
        if (dictionaryReader == null || stopwordsReader == null) {
            throw new IllegalArgumentException("Readers cannot be null");
        }
        try (Scanner firstScanner = new Scanner(dictionaryReader);
             Scanner secondScanner = new Scanner(stopwordsReader)) {

            while (firstScanner.hasNextLine()
                    && (dictWord = firstScanner.nextLine()) != null) {
                dictWord = dictWord.trim();
                if (dictWord.length() > 1) {
                    dictWord = removeNonAlphanumeric(dictWord);
                    dictionary.put(dictWord.toLowerCase(), findVector(dictWord));
                }
            }

            while (secondScanner.hasNextLine()
                    && (stopWord = secondScanner.nextLine()) != null) {
                stopWord = stopWord.trim();
                stopwords.add(stopWord);
            }
        }

    }

    public static String removeNonAlphanumeric(String str) {
        if (str.isEmpty()) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);

        if (str.matches("^[^a-zA-Z0-9].*")) {
            while (str.matches("^[^a-zA-Z0-9].*")) {
                sb.deleteCharAt(0);
                str = sb.toString();
            }
            return sb.toString();
        } else if (str.matches("[a-zA-Z0-9]+[^a-zA-Z0-9]+$")) {
            while (str.matches("[a-zA-Z0-9]+[^a-zA-Z0-9]+$")) {
                sb.deleteCharAt(str.length() - 1);
                str = sb.toString();
            }
            return sb.toString();
        }

        return str;
    }

    public static List<String> ngrams(int n, String str) {
        List<String> ngrams = new ArrayList<>();
        if (!str.isEmpty()) {
            for (int i = 0; i < str.length() - n + 1; i++) {
                ngrams.add(str.substring(i, i + n).toLowerCase());
            }
        }
        return ngrams;
    }

    public int countWords(String[] lineWords) {
        int wordCounter = 0;
        for (String word : lineWords) {
            String tempWord = removeNonAlphanumeric(word);
            if (stopwords.contains(tempWord) || !tempWord.matches("[a-zA-Z0-9]+")) {
                continue;
            } else {
                wordCounter++;
            }
        }

        return wordCounter;
    }

    public double countGramOccurunces(List<Pair<String, Integer>> wordVector) {
        if (wordVector == null || wordVector.isEmpty()) {
            return 0;
        }
        double wordVectorSum = 0;
        for (Pair<String, Integer> pair : wordVector) {
            wordVectorSum += (pair.getSecond() ^ 2);
        }

        return wordVectorSum;
    }

    public int countCharacters(String line) {

        line = line.replaceAll("\\s+", "");

        return line.toCharArray().length;
    }

    public int countMistakes(String[] lineWords) {
        int mistakesCount = 0;
        for (String word : lineWords) {
            String tempWord = removeNonAlphanumeric(word);
            if (!dictionary.containsKey(tempWord.toLowerCase())) {
                mistakesCount++;
            }
        }

        return mistakesCount;
    }

    public List<Pair<String, Integer>> findVector(String word) {
        if (word.equals("")) {
            return new ArrayList<Pair<String, Integer>>();
        }
        List<String> twoGrams = ngrams(2, word);
        List<Pair<String, Integer>> wordVector = new ArrayList<>();
        for (String gram : twoGrams) {
            wordVector.add(new Pair<>(gram, word.split(gram, -1).length - 1));
        }
        return wordVector;
    }

    public List<Pair<String, Integer>> commonParts(List<Pair<String,
            Integer>> vector1, List<Pair<String, Integer>> vector2) {
        List<Pair<String, Integer>> tempVector = new ArrayList<>();
        if (vector1 == null || vector1.isEmpty() || vector2 == null || vector2.isEmpty()) {
            return tempVector;
        }
        for (Pair<String, Integer> pair : vector1) {
            if (vector2.contains(pair)) {
                tempVector.add(pair);
            }
        }

        return tempVector;
    }

    public void populateCosinusSimilarWords(List<Pair<String, Integer>> wordVector, Double wordLength) {
        if (wordVector == null) {
            throw new IllegalArgumentException("Invalid argument pass");
        } else if (wordVector.isEmpty()) {
            return;
        }
        for (String singleWord : dictionary.keySet()) {
            List<Pair<String, Integer>> vector = dictionary.get(singleWord);
            double multiplication = 0;
            List<Pair<String, Integer>> similarElements = commonParts(vector, wordVector);
            double length = countGramOccurunces(vector);
            for (Pair<String, Integer> pair : similarElements) {
                multiplication += (2 * pair.getSecond());
            }
            double cosSimilarity = multiplication / (length * wordLength);
            if (cosSimilarity > 0) {
                cosinusSimilarWords.put(singleWord, cosSimilarity);
            }
        }
    }

    public static HashMap<String, Double> sortByValue(HashMap<String, Double> hm) {
        if (hm == null) {
            throw new IllegalArgumentException("Argument cannot be null");
        } else if (hm.isEmpty()) {
            return new HashMap<>();
        }
        List<Map.Entry<String, Double>> list =
                new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        HashMap<String, Double> temp = new LinkedHashMap<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            temp.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return temp;
    }

    public InputStream createInputStream(List<String> source) throws IOException, NullPointerException {
        if (source == null) {
            throw new IllegalArgumentException("Invalid source file");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        for (String line : source) {
            baos.write(line.getBytes());
        }
        byte[] bytes = baos.toByteArray();
        return new ByteArrayInputStream(bytes);
    }


    @Override
    public void analyze(Reader textReader, Writer output, int suggestionsCount) {
        String line;
        List<String> linesRead = new ArrayList<>();
        Map<Pair<String, List<String>>, Integer> misspelledWords = new HashMap<>();
        if (textReader == null || output == null) {
            throw new IllegalArgumentException("Invalid textReader or output");
        }
        try (BufferedReader br = new BufferedReader(textReader);
             BufferedWriter bw = new BufferedWriter(output)) {
            int lineCounter = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                lineCounter++;
                String[] args = line.split(" ");
                for (String word : args) {
                    String tempWord = removeNonAlphanumeric(word);
                    if (!dictionary.containsKey(tempWord.toLowerCase())) {
                        misspelledWords.put(new Pair<>(word, findClosestWords(word, suggestionsCount)), lineCounter);
                    }
                }
                bw.write(line + "\n");
                linesRead.add(line.toLowerCase());
            }
            InputStream targetStream = createInputStream(linesRead);
            Metadata metadata = metadata(new InputStreamReader(targetStream));
            bw.write("===Metadata===\n");
            bw.write(metadata.characters()
                    + " characters, "
                    + metadata.words()
                    + " words, "
                    + metadata.mistakes() +
                    " spelling issue(s) found\n");
            bw.write("===Findings===\n");


            for (Pair<String, List<String>> pair : misspelledWords.keySet()) {
                bw.write("Line #"
                        + misspelledWords.get(pair)
                        + ", {"
                        + pair.getFirst() +
                        "} - Possible suggestions are {"
                        + pair.getSecond().toString().replaceAll("[\\[\\]]", "")
                        + "}\n");
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Metadata metadata(Reader textReader) {
        int characters = 0;
        int words = 0;
        int mistakes = 0;
        if (textReader == null) {
            throw new IllegalArgumentException("Reader cannot be null");
        }
        String line;
        try (BufferedReader reader = new BufferedReader(textReader)) {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] lineWords = line.split(" ");
                words += countWords(lineWords);
                mistakes += countMistakes(lineWords);
                characters += countCharacters(line);
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return new Metadata(characters, words, mistakes);
    }

    @Override
    public List<String> findClosestWords(String word, int n) {
        List<String> closestWords = new ArrayList<>();
        List<Pair<String, Integer>> wordVector = findVector(word);
        double wordVectorLength = Math.sqrt(countGramOccurunces(wordVector));
        int count = 0;
        cosinusSimilarWords.clear();
        populateCosinusSimilarWords(wordVector, wordVectorLength);

        HashMap<String, Double> sortedHashMap = sortByValue(cosinusSimilarWords);
        for (String closestWord : sortedHashMap.keySet()) {
            if (count == n) {
                break;
            }
            closestWords.add(closestWord);
            count++;
        }

        return closestWords;
    }
}
