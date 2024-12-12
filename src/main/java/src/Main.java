package src;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Scanner;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;


public class Main {

    public static void main(String[] args) {
        try {
            int numHashFunctions = 100;

            Scanner in = new Scanner(System.in);

            System.out.print("Enter text1: ");
            String first_text = in.nextLine();

            System.out.print("Enter text2 ");
            String second_text = in.nextLine();

            System.out.print("Enter text3 ");
            String third_text = in.nextLine();

            // Канонизация
            List<String> first_canonization = computeCanonical(first_text);
            List<String> second_canonization = computeCanonical(second_text);
            List<String> third_canonization = computeCanonical(third_text);

            System.out.println("Текст1 после канонизации: " + String.join(" ", first_canonization));
            System.out.println("Текст2 после канонизации: " + String.join(" ", second_canonization));
            System.out.println("Текст3 после канонизации: " + String.join(" ", third_canonization));

            // Шингл
            List<String> first_shingle = computeShingle(first_canonization, 3);
            Set<String> u_first_shingle = new HashSet<>(first_shingle);

            List<String> second_shingle = computeShingle(second_canonization, 3);
            Set<String> u_second_shingle = new HashSet<>(second_shingle);

            List<String> third_shingle = computeShingle(third_canonization, 3);
            Set<String> u_third_shingle = new HashSet<>(third_shingle);

            // Хеш
            int[] hash1 = computeHash(u_first_shingle, numHashFunctions);
            int[] hash2 = computeHash(u_second_shingle, numHashFunctions);
            int[] hash3 = computeHash(u_third_shingle, numHashFunctions);

            double comparison12 = checkHashes(hash1, hash2);
            double comparison13 = checkHashes(hash1, hash3);
            double comparison23 = checkHashes(hash2, hash3);


            System.out.println();
            System.out.println("Схожесть текста 1 и 2: " + comparison12);
            System.out.println("Схожесть текста 1 и 3: " + comparison13);
            System.out.println("Схожесть текста 2 и 3: " + comparison23);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    /**
     - All letters are lowercase.
     - All non-letters/numbers have been deleted.
     - Eliminated unnecessary gaps.
     - The line is divided into separate words.
     */
    public static List<String> computeCanonical (String text) {
        String lower_cased = text.toLowerCase();
        String only_letters = lower_cased.replaceAll("[^a-zа-я0-9]", " ");
        String[] split = only_letters.split(" ");
        return Arrays.stream(split).toList();
    }

    /**
     calculation of fixed-size substrings that are formed from consecutive list items.
     */
    public static List<String> computeShingle(List<String> data, int size) {
        List<String> shingles = new ArrayList<>();

        for (int i = 0; i <= data.size() - size; i++) {
            StringBuilder shingle = new StringBuilder();

            for (int j = 0; j < size; j++) {
                shingle.append(data.get(i + j));
            }

            shingles.add(shingle.toString());
        }
        return shingles;
    }

    /**
     - Returns an array of int[] signatures with length num Hash Functions.
     - Each cell of the signature array minimizes the hash values for the corresponding hash function.
     */
    public static int[] computeHash(Set<String> shingles, int numHashFunctions) {
        int[] signatures = new int[numHashFunctions];
        Arrays.fill(signatures, Integer.MAX_VALUE);

        for (String shingle : shingles) {
            for (int i = 0; i < numHashFunctions; i++) {
                try {
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update((i + shingle).getBytes(StandardCharsets.UTF_8));
                    int hash = Arrays.hashCode(md.digest());
                    signatures[i] = Math.min(signatures[i], hash);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return signatures;
    }

    /**
     - Calculates how many elements in two arrays match at the same indexes.
     - Returns the match coefficient as a value between 0 and 1:
     */
    public static double checkHashes(int[] first, int[] second) {
        if (first.length != second.length)
            throw new IllegalArgumentException("Error with signatures length");

        int matches = 0;
        for (int i = 0; i < first.length; i++) {
            if (first[i] == second[i]) {
                matches++;
            }
        }
        return (double) matches / first.length;
    }
}