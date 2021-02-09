package bg.sofia.uni.fmi.mjt.spellchecker;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

public class NaiveSpellCheckerTest {

    private NaiveSpellChecker spellChecker;
    private Reader text;
    private Writer resultOutput;

    @Before
    public void startUp() throws IOException {
        Path dictPath = Paths.get("src/dictionary.txt");
        Path stopWordsPath = Paths.get("src/stopwords.txt");
        Path outputPath = Paths.get("src/testOutput.txt");
        spellChecker = new NaiveSpellChecker(new FileReader(String.valueOf(dictPath.toAbsolutePath())),
                new FileReader(String.valueOf(stopWordsPath.toAbsolutePath())));
        Path path = Paths.get("src/test.txt");
        text = new FileReader(String.valueOf(path.toAbsolutePath()));
        resultOutput = new FileWriter(String.valueOf(outputPath.toAbsolutePath()));

    }

    @Test
    public void metadataCharactersTest() {
        Metadata metadata = spellChecker.metadata(text);
        assertEquals(26, metadata.characters());
    }

    @Test
    public void metadataWordsTest() {
        Metadata metadata = spellChecker.metadata(text);
        assertEquals(6, metadata.words());
    }

    @Test
    public void metadataMistakesTest() {
        Metadata metadata = spellChecker.metadata(text);
        assertEquals(2, metadata.mistakes());
    }

    @Test
    public void findClosestWordsTest() {
        List<String> closestWords1 = spellChecker.findClosestWords("god", 2);
        assertEquals(2, closestWords1.size());
        List<String> closestWords2 = spellChecker.findClosestWords("dyanite", 3);
        assertEquals(3, closestWords2.size());
        List<String> checkList = new ArrayList<>();
        checkList.add("ate");
        checkList.add("eddy");
        checkList.add("dynamite");
        assertTrue(closestWords2.containsAll(checkList));
    }

    @Test(expected = IllegalArgumentException.class)
    public void analyzeWithNullTest() {
        BufferedReader br = null;
        BufferedWriter bw = null;
        spellChecker.analyze(br, bw, 0);
    }

    @Test
    public void removeNonAlphanumericTest() {
        String word = ".me";
        assertEquals("me", spellChecker.removeNonAlphanumeric(word));
        String word2 = "buddy";
        assertEquals("buddy", spellChecker.removeNonAlphanumeric(word2));
        String word3 = "enemy!";
        assertEquals("enemy", spellChecker.removeNonAlphanumeric(word3));
        String word4 = "enemy!!";
        assertEquals("enemy", spellChecker.removeNonAlphanumeric(word4));
        String word5 = "";
        assertEquals("", spellChecker.removeNonAlphanumeric(word5));
    }

    @Test
    public void ngramsTest() {
        List<String> ngrams = new ArrayList<>();
        ngrams.add("he");
        ngrams.add("el");
        assertEquals(ngrams, NaiveSpellChecker.ngrams(2, "Hel"));

        assertTrue(NaiveSpellChecker.ngrams(2, "").isEmpty());

    }

    @Test
    public void countWordsTest() {
        String[] lineWords = {"one", "two", "about", "one"};
        assertEquals(3, spellChecker.countWords(lineWords));
    }

    @Test
    public void checkCountGramOccurrencesTest() {
        assertEquals(0.0, spellChecker.countGramOccurunces(null));
        assertEquals(0.0, spellChecker.countGramOccurunces(new ArrayList<Pair<String, Integer>>()));

    }

    @Test
    public void countCharactersTest() {
        String line = "asdgvfbh !r  a\nx";
        assertEquals(12, spellChecker.countCharacters(line));
    }

    @Test
    public void countMistakesTest() {
        String[] lineWords = {"Eddy", "dyno", "myte"};
        assertEquals(2, spellChecker.countMistakes(lineWords));
    }

    @Test
    public void findVectorTest() {
        assertTrue(spellChecker.findVector("").isEmpty());
    }

    @Test
    public void commonPartsTest() {
        List<Pair<String, Integer>> vector1 = new ArrayList<>();
        List<Pair<String, Integer>> vector2 = new ArrayList<>();
        vector1.add(new Pair<>("he", 2));
        vector2.add(new Pair<>("he", 1));
        assertFalse(spellChecker.commonParts(vector1, vector2).size() > 0);
        assertTrue(spellChecker.commonParts(null, vector2).isEmpty());
        assertTrue(spellChecker.commonParts(vector1, null).isEmpty());
        assertTrue(spellChecker.commonParts(new ArrayList<Pair<String, Integer>>(),
                new ArrayList<Pair<String, Integer>>()).isEmpty());
    }

    @Test
    public void sortByValueTest() {
        Map<String, Double> hm = new HashMap<>();
        hm.put("Vox", 0.22);
        hm.put("Gosho", 0.32);
        hm.put("Peso", 0.92);
        Map<String, Double> result = spellChecker.sortByValue((HashMap<String, Double>) hm);
        Map.Entry<String, Double> entry = result.entrySet().iterator().next();
        assertEquals(hm.get("Peso"), entry.getValue());
        assertTrue(spellChecker.sortByValue(new HashMap<>()).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void sortByValueWithNullTest() {
        spellChecker.sortByValue(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createInputStreamForNullSourceTest() throws IOException {
        spellChecker.createInputStream(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkConstructorForNullTest() {
        SpellChecker naiveSpellChecker = new NaiveSpellChecker(null, null);
        naiveSpellChecker.metadata(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void populateCosinusSimilarWordsWithNullTest() {
        spellChecker.populateCosinusSimilarWords(null, 2.0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void metadataWithNullTest() {
        spellChecker.metadata(null);
    }

    @Test
    public void analyzeTest() throws IOException {
        spellChecker.analyze(text, resultOutput, 2);
        Path firstReaderPath = Paths.get("src/testOutput.txt");
        Path secondReaderPath = Paths.get("src/output.txt");
        BufferedReader reader1 = new BufferedReader(new FileReader(String.valueOf(firstReaderPath.toAbsolutePath())));
        BufferedReader reader2 = new BufferedReader(new FileReader(String.valueOf(secondReaderPath.toAbsolutePath())));
        boolean areEqual = true;
        String line1 = reader1.readLine();
        String line2 = reader2.readLine();
        while (line1 != null || line2 != null) {
            if (line1 == null || line2 == null) {
                areEqual = false;
                break;
            } else if (!line1.equalsIgnoreCase(line2)) {
                areEqual = false;
                break;
            }
            line1 = reader1.readLine();
            line2 = reader2.readLine();
        }
        reader1.close();
        reader2.close();
        assertTrue(areEqual);
    }

    @Test
    public void recordMetadataTest() {
        Metadata metadata = new Metadata(1, 2, 3);
        assertEquals(1, metadata.characters());
        assertEquals(2, metadata.words());
        assertEquals(3, metadata.mistakes());
    }

}
