package bg.sofia.uni.fmi.mjt.stylechecker;

import org.junit.Before;
import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import static junit.framework.TestCase.assertEquals;

public class NaiveSpellCheckerTest {

    private StyleChecker checker;
    private Reader input;
    private Writer output;

    @Before
    public void startUp() {
        checker = new StyleChecker();


    }

    @Test
    public void testImportWildCards() {
        input = new StringReader("import java.util.*;");
        output = new StringWriter();
        checker.checkStyle(input, output);
        String actual = output.toString();

        assertEquals("// FIXME Wildcards are not allowed in import statements"
                + System.lineSeparator() + "import java.util.*;", actual.strip());
    }

    @Test
    public void testIFMoreThanOneStatementPerLine() {
        input = new StringReader("sayHello();sayHello();sayHello();");
        output = new StringWriter();
        checker.checkStyle(input, output);
        String actual = output.toString();
        assertEquals("// FIXME Only one statement per line is allowed" +
                System.lineSeparator() + "sayHello();sayHello();sayHello();", actual.strip());
    }

    @Test
    public void testIfPackageNameContainsUpperCaseLetters() {
        input = new StringReader("package bg.UNI.sofia.fmi.mjt;");
        output = new StringWriter();
        checker.checkStyle(input, output);
        String actual = output.toString();
        assertEquals("// FIXME Package name should not contain upper-case letters or underscores" +
                System.lineSeparator() + "package bg.UNI.sofia.fmi.mjt;", actual.strip());
    }

    @Test
    public void testIfPackageNameContainsUnderScores() {
        input = new StringReader("package bg.uni_sofia.fmi.mjt;");
        output = new StringWriter();
        checker.checkStyle(input, output);
        String actual = output.toString();
        assertEquals("// FIXME Package name should not contain upper-case letters or underscores" +
                System.lineSeparator() + "package bg.uni_sofia.fmi.mjt;", actual.strip());
    }

    @Test
    public void testIfOpeningBracketsAreOnSameLine() {
        input = new StringReader("{");
        output = new StringWriter();
        checker.checkStyle(input, output);
        String actual = output.toString();
        assertEquals("// FIXME Opening brackets should be placed on the same line as the declaration" +
                System.lineSeparator() + "{", actual.strip());
    }

    @Test
    public void testLengthOfLine() {
        input = new StringReader("String hello = \"Hey, it's Hannah, Hannah Baker. That's right. Don't adjust your... whatever device you're listening to this on. It's me, live and in stereo.\";");
        output = new StringWriter();
        checker.checkStyle(input, output);
        String actual = output.toString();
        assertEquals("// FIXME Length of line should not exceed 100 characters" +
                        System.lineSeparator() + "String hello = \"Hey, it's Hannah, Hannah Baker. That's right. Don't adjust your... whatever device you're listening to this on. It's me, live and in stereo.\";"
                , actual.strip());
    }

}
