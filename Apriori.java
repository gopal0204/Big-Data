
import java.util.*;

public class Apriori {
    
    // Method to find frequent 1-itemsets
    public static List<Set<String>> findFrequent1Itemsets(List<Set<String>> transactions, int minSup) {
        Map<String, Integer> itemCountMap = new HashMap<>();
        for (Set<String> transaction : transactions) {
            for (String item : transaction) {
                itemCountMap.put(item, itemCountMap.getOrDefault(item, 0) + 1);
            }
        }
        
        List<Set<String>> L1 = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : itemCountMap.entrySet()) {
            if (entry.getValue() >= minSup) {
                Set<String> itemset = new HashSet<>();
                itemset.add(entry.getKey());
                L1.add(itemset);
            }
        }
        return L1;
    }

    // Method to generate candidates
    public static List<Set<String>> aprioriGen(List<Set<String>> LkMinus1) {
        List<Set<String>> Ck = new ArrayList<>();
        int size = LkMinus1.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Set<String> l1 = LkMinus1.get(i);
                Set<String> l2 = LkMinus1.get(j);

                List<String> l1List = new ArrayList<>(l1);
                List<String> l2List = new ArrayList<>(l2);

                if (canJoin(l1List, l2List)) {
                    Set<String> candidate = new HashSet<>(l1);
                    candidate.addAll(l2);

                    if (!hasInfrequentSubset(candidate, LkMinus1)) {
                        Ck.add(candidate);
                    }
                }
            }
        }
        return Ck;
    }

    // Method to check if two itemsets can be joined
    public static boolean canJoin(List<String> l1, List<String> l2) {
        int length = l1.size();
        for (int i = 0; i < length - 1; i++) {
            if (!l1.get(i).equals(l2.get(i))) {
                return false;
            }
        }
        return l1.get(length - 1).compareTo(l2.get(length - 1)) < 0;
    }

    // Method to check if a candidate has any infrequent subset
    public static boolean hasInfrequentSubset(Set<String> candidate, List<Set<String>> LkMinus1) {
        for (String item : candidate) {
            Set<String> subset = new HashSet<>(candidate);
            subset.remove(item);

            if (!LkMinus1.contains(subset)) {
                return true;
            }
        }
        return false;
    }

    // Method to find all frequent itemsets
    public static List<Set<String>> apriori(List<Set<String>> transactions, int minSup) {
        List<Set<String>> L = new ArrayList<>();

        List<Set<String>> Lk = findFrequent1Itemsets(transactions, minSup);
        L.addAll(Lk);

        int k = 2;
        while (!Lk.isEmpty()) {
            List<Set<String>> Ck = aprioriGen(Lk);

            Map<Set<String>, Integer> countMap = new HashMap<>();
            for (Set<String> transaction : transactions) {
                for (Set<String> candidate : Ck) {
                    if (transaction.containsAll(candidate)) {
                        countMap.put(candidate, countMap.getOrDefault(candidate, 0) + 1);
                    }
                }
            }

            Lk = new ArrayList<>();
            for (Map.Entry<Set<String>, Integer> entry : countMap.entrySet()) {
                if (entry.getValue() >= minSup) {
                    Lk.add(entry.getKey());
                }
            }

            L.addAll(Lk);
            k++;
        }

        return L;
    }

    public static void main(String[] args) {
        List<Set<String>> transactions = new ArrayList<>();
        transactions.add(new HashSet<>(Arrays.asList("A", "B", "C")));
        transactions.add(new HashSet<>(Arrays.asList("A", "B")));
        transactions.add(new HashSet<>(Arrays.asList("A", "C")));
        transactions.add(new HashSet<>(Arrays.asList("B", "C")));
        transactions.add(new HashSet<>(Arrays.asList("A", "B", "C")));
        transactions.add(new HashSet<>(Arrays.asList("A", "B", "D")));

        int minSup = 2;

        List<Set<String>> frequentItemsets = apriori(transactions, minSup);

        System.out.println("Frequent Itemsets:");
        for (Set<String> itemset : frequentItemsets) {
            System.out.println(itemset);
        }
    }
}