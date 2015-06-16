/* 
 * The MIT License
 *
 * Copyright 2015 Bob Larsen.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.rwlarsen.metaformers;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * This Application counts the number of words in a specified file.
 *
 * @author Bob Larsen
 */
public class WordCounter {

    private static final Map<String, Integer> wordCounts = new HashMap<>();
    private static final Set<String> longestWords = new HashSet<>();
    private static final Set<String> shortestWords = new HashSet<>();
    private static int longestLength;
    private static int shortestLength;

    /**
     * The main method: Counts instances of words within a file. Since no
     * definition of "word" was given, I'm assuming that words are formed
     * entirely of English letters of both cases, digits, hyphens, and
     * apostrophes. Words which end in hyphens are stripped of the hyphen and
     * combined with the following word to account for words which have been
     * hyphenated onto different lines of the original file. Double-hyphens are
     * treated as word boundaries. Words are compared without regard for case.
     *
     * @param args the filename to be evaluated. If not present, filename is
     * read from console.
     */
    public static void main(String[] args) throws IOException {
        String fileName;
        if (args.length > 0) {
            fileName = args[0];
        } else {
            System.out.print("Enter filename: ");
            fileName = new BufferedReader(new InputStreamReader(System.in)).readLine();
        }
        Scanner scanner = new Scanner(new File(fileName)).useDelimiter("[^a-zA-z0-9']*\\-[^a-zA-z0-9']+|[^a-zA-z0-9\\-']+");
        if (scanner.hasNext()) { // in case an empty file is loaded
            String word = scanner.next().toLowerCase();
            if (word.charAt(word.length() - 1) == '-' && scanner.hasNext()) { // tests for trailing hyphen
                word = word.substring(0, word.length() - 1) + scanner.next().toLowerCase();
            }
            int count = 1;
            wordCounts.put(word, count);
            longestWords.add(word);
            shortestWords.add(word);
            shortestLength = longestLength = word.length(); // initialized static variables to sane state based on first word
            while (scanner.hasNext()) { // loops through remaining words
                word = scanner.next().toLowerCase();
                if (word.charAt(word.length() - 1) == '-' && scanner.hasNext()) { // tests for trailing hyphen
                    word = word.substring(0, word.length() - 1) + scanner.next().toLowerCase();
                }
                if (wordCounts.containsKey(word)) {
                    count = wordCounts.get(word) + 1;
                } else {
                    count = 1;
                }
                wordCounts.put(word, count);
                if (word.length() == longestLength) {
                    longestWords.add(word);
                } else if (word.length() > longestLength) {
                    longestWords.clear();
                    longestWords.add(word);
                    longestLength = word.length();
                }
                if (word.length() == shortestLength) {
                    shortestWords.add(word);
                } else if (word.length() < shortestLength) {
                    shortestWords.clear();
                    shortestWords.add(word);
                    shortestLength = word.length();
                }
            }
        }
        /*
         * Since no output mechanism was specified, I'm printing to the console.
         * Changing the terminal operation for each Stream will easily change
         * the output (to return a Collection, for example)
         */
        System.out.println("|number\t|word");
        System.out.println("|-------|-------------");
        wordCounts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue() - e1.getValue())
                .forEachOrdered((e) -> System.out.println("| " + e.getValue() + "\t| " + e.getKey()));
        
        System.out.println("Longest words:");
        longestWords.stream().forEach((s) -> System.out.print(s + " "));
        System.out.println("");
        
        System.out.println("Shortest words:");
        shortestWords.stream().forEach((s) -> System.out.print(s + " "));
        System.out.println("");
    }

}
