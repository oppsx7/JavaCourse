package bg.sofia.uni.fmi.mjt.stylechecker;

import java.io.*;
import java.util.Scanner;

/**
 * Checks adherence to Java style guidelines.
 */
public class StyleChecker {

    public static final String FIX_WILDCARD_IMPORT = "// FIXME Wildcards are not allowed in import statements\n";
    public static final String FIX_IF_MORE_THAN_ONE_STATEMENT_ON_LINE = "// FIXME Only one statement per line is allowed\n";
    public static final String FIX_LINE_LENGTH = "// FIXME Length of line should not exceed 100 characters\n";
    public static final String FIX_OPENING_BRACKETS_ON_WRONG_PLACE = "// FIXME Opening brackets should be placed on the same line as the declaration\n";
    public static final String FIX_PACKAGE_NAME_UNDERSCORE_UPPERCASE_LETTER = "// FIXME Package name should not contain upper-case letters or underscores\n";
    public static final String UNDER_SCORE = "_";
    public static final String IMPORT = "import";
    public static final String PACKAGE = "package";

    public StyleChecker() {

    }


    /**
     * For each line from the given {@code source} performs code style checks
     * and writes to the {@code output}
     * 1. a FIXME comment line for each style violation in the input line, if any
     * 2. the input line itself.
     *
     * @param source
     * @param output
     */
    public void checkStyle(Reader source, Writer output) {

        String line;

        try (Scanner scanner = new Scanner(source);
             BufferedWriter bw = new BufferedWriter(output)) {

            while (scanner.hasNextLine() && (line = scanner.nextLine()) != null) {
                line = line.trim();
                if (line.split(" ")[0].equals(IMPORT) &&
                        line.indexOf('*') != -1) {
                    fixWildCardImports(line, bw);
                }

                String[] args = line.split(";", 2);
                if (args.length > 1 && !args[1].equals("")) {
                    fixIfMoreThanOneStatementOnLine(line, bw);
                }

                if (line.startsWith("{")) {
                    fixIfOpeningBracketsAreOnWrongPlace(line, bw);
                }

                if (line.length() > 100) {
                    fixLineLength(line, bw);
                }

                if (line.split(" ")[0].equals(PACKAGE)) {
                    fixIfPackageNameContainsUpperLettersOrUnderScores(line, bw);
                }
                    bw.write(line + "\n");

            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void fixWildCardImports(String line, BufferedWriter bw) throws IOException {
        bw.write(FIX_WILDCARD_IMPORT);
    }

    public void fixIfMoreThanOneStatementOnLine(String line, BufferedWriter bw) throws IOException {
        bw.write(FIX_IF_MORE_THAN_ONE_STATEMENT_ON_LINE);

    }

    public void fixLineLength(String line, BufferedWriter bw) throws IOException {

        bw.write(FIX_LINE_LENGTH);


    }

    public void fixIfOpeningBracketsAreOnWrongPlace(String line, BufferedWriter bw) throws IOException {
        bw.write(FIX_OPENING_BRACKETS_ON_WRONG_PLACE);

    }

    public void fixIfPackageNameContainsUpperLettersOrUnderScores(String line, BufferedWriter bw) throws IOException {

        if (line.contains(UNDER_SCORE)) {
            bw.write(FIX_PACKAGE_NAME_UNDERSCORE_UPPERCASE_LETTER);

        }
        for (int i = 0; i < line.length(); i++) {
            if (Character.isUpperCase(line.charAt(i))) {
                bw.write(FIX_PACKAGE_NAME_UNDERSCORE_UPPERCASE_LETTER);
                return;
            }
        }
    }


}