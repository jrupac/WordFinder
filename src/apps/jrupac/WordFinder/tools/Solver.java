package apps.jrupac.WordFinder.tools;

import android.util.Pair;
import com.google.common.collect.HashMultimap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class Solver {
    private static boolean initialized = false;
    private static HashMultimap<String, Pair<String, Integer>> wordMapping;
    private static Map<Character, Integer> valueMap;
    private static final CountDownLatch latch = new CountDownLatch(1);

    public static void waitUntilReady() {
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for init completion: " + e.getMessage());
        }
    }

    public static synchronized void initContext(InputStream dictStream) {
        if (!initialized) {
            wordMapping = HashMultimap.create();
            valueMap = new HashMap<Character, Integer>();
            enumerateValueMap();
            enumerateWordMap(dictStream);
            initialized = true;
            latch.countDown();
        }
    }

    private static void enumerateWordMap(InputStream dictStream) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new InputStreamReader(dictStream));
            String word = reader.readLine();

            while (word != null) {
                wordMapping.put(getSorted(word), new Pair<String, Integer>(
                        word, getWordValue(word)));
                word = reader.readLine();
            }

        } catch (IOException e) {
            System.err.println("Failed to read file: " + e.getMessage());
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    System.err.println("Failed to close reader: " + e.getMessage());
                }
            }
        }
    }

    private static void enumerateValueMap() {
        valueMap.put('a', 1);
        valueMap.put('b', 3);
        valueMap.put('c', 3);
        valueMap.put('d', 2);
        valueMap.put('e', 1);
        valueMap.put('f', 4);
        valueMap.put('g', 2);
        valueMap.put('h', 4);
        valueMap.put('i', 1);
        valueMap.put('j', 8);
        valueMap.put('k', 5);
        valueMap.put('l', 1);
        valueMap.put('m', 3);
        valueMap.put('n', 1);
        valueMap.put('o', 1);
        valueMap.put('p', 3);
        valueMap.put('q', 10);
        valueMap.put('r', 1);
        valueMap.put('s', 1);
        valueMap.put('t', 1);
        valueMap.put('u', 1);
        valueMap.put('v', 4);
        valueMap.put('w', 4);
        valueMap.put('x', 8);
        valueMap.put('y', 4);
        valueMap.put('z', 10);
        valueMap.put(' ', 0);
    }

    private static String getSorted(String s) {
        char[] chars = s.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }

    private static int getWordValue(String s) {
        int value = 0;

        for (char c : s.toCharArray()) {
            value += valueMap.get(c);
        }

        return value;
    }

    public Solver() {
        if (!initialized) {
            throw new IllegalStateException("Must initialize context first.");
        }
    }

    private final class BY_VALUE implements Comparator<Pair<String, Integer>> {
        @Override
        public int compare(Pair<String, Integer> arg0,
                           Pair<String, Integer> arg1) {
            return -arg0.second.compareTo(arg1.second);
        }
    }

    public List<Pair<String, Integer>> solve(String query, String startsWith,
                                             String endsWith, String containsExact) {
        try {
            latch.await();
        } catch (InterruptedException e) {
            System.err.println("Interrupted while waiting for init completion: " + e.getMessage());
            return null;
        }

        Iterable<String> substrs = getAllSubstrings(query);
        Set<Pair<String, Integer>> result = new HashSet<Pair<String, Integer>>();
        Pattern pattern = Pattern.compile(buildRegex(startsWith, endsWith,
                                                     containsExact));

        for (String substr : substrs) {
            for (Pair<String, Integer> potentialResult : wordMapping
                    .get(substr)) {
                if (pattern.matcher(potentialResult.first).matches()) {
                    result.add(potentialResult);
                }
            }
        }

        List<Pair<String, Integer>> resultList = new ArrayList<Pair<String, Integer>>(
                result);
        Collections.sort(resultList, new BY_VALUE());

        return resultList;
    }

    private String buildRegex(String startsWith, String endsWith,
                              String containsExact) {
        String regex = "";

        if (startsWith != null) {
            regex += "^" + startsWith + ".*";
        }

        if (containsExact != null) {
            regex += containsExact.replaceAll("\\?", ".") + ".*";
        }

        if (endsWith != null) {
            regex += endsWith + "$";
        }

        return regex;
    }

    private Set<String> getAllSubstrings(String s) {
        if (s.length() == 0) {
            return new HashSet<String>();
        }

        Set<String> prev = getAllSubstrings(s.substring(1));
        Set<String> current = new HashSet<String>(prev);
        String first = s.substring(0, 1);

        if (first.equals("?")) {
            for (int i = (int) 'a'; i <= (int) 'z'; i++) {
                String curChar = String.valueOf((char) i);

                for (String p : prev) {
                    current.add(getSorted(curChar + p));
                }
                current.add(curChar);
            }
        } else {
            for (String p : prev) {
                current.add(getSorted(first + p));
            }
            current.add(first);
        }

        return current;
    }
}
