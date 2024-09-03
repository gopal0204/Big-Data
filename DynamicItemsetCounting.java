import java.util.*;

public class DynamicItemsetCounting {

    // Minimum support threshold
    private static final int MIN_SUPPORT = 3;
    // Block size
    private static final int BLOCK_SIZE = 3;

    public static void main(String[] args) {
        List<Set<String>> transactions = Arrays.asList(
                new HashSet<>(Arrays.asList("I1", "I2", "I5")),
                new HashSet<>(Arrays.asList("I2", "I4")),
                new HashSet<>(Arrays.asList("I2", "I3")),
                new HashSet<>(Arrays.asList("I1", "I2", "I4")),
                new HashSet<>(Arrays.asList("I1", "I3")),
                new HashSet<>(Arrays.asList("I2", "I3")),
                new HashSet<>(Arrays.asList("I1", "I3")),
                new HashSet<>(Arrays.asList("I1", "I2", "I3", "I5")),
                new HashSet<>(Arrays.asList("I1", "I2", "I3"))
        );

        Set<Set<String>> frequentItemsets = findFrequentItemsets(transactions, MIN_SUPPORT, BLOCK_SIZE);
        System.out.println("Frequent Itemsets: " + frequentItemsets);
    }

    public static Set<Set<String>> findFrequentItemsets(List<Set<String>> transactions, int minSupport, int blockSize) {
        Set<Set<String>> frequentItemsets = new HashSet<>();
        Map<Set<String>, Integer> itemsetCounts = new HashMap<>();

        // Process transactions in blocks
        int totalTransactions = transactions.size();
        for (int i = 0; i < totalTransactions; i += blockSize) {
            List<Set<String>> block = transactions.subList(i, Math.min(i + blockSize, totalTransactions));
            updateCounts(itemsetCounts, block);
            filterFrequentItemsets(itemsetCounts, minSupport, frequentItemsets);
        }

        return frequentItemsets;
    }

    private static void updateCounts(Map<Set<String>, Integer> itemsetCounts, List<Set<String>> block) {
        // Count itemsets in the current block
        Map<Set<String>, Integer> blockCounts = new HashMap<>();

        // For each transaction in the block
        for (Set<String> transaction : block) {
            List<Set<String>> candidates = generateCandidatesFromTransaction(transaction);
            for (Set<String> candidate : candidates) {
                blockCounts.put(candidate, blockCounts.getOrDefault(candidate, 0) + 1);
            }
        }

        // Merge block counts into global itemset counts
        for (Map.Entry<Set<String>, Integer> entry : blockCounts.entrySet()) {
            Set<String> itemset = entry.getKey();
            int count = entry.getValue();
            itemsetCounts.put(itemset, itemsetCounts.getOrDefault(itemset, 0) + count);
        }
    }

    private static List<Set<String>> generateCandidatesFromTransaction(Set<String> transaction) {
        List<Set<String>> candidates = new ArrayList<>();
        List<String> items = new ArrayList<>(transaction);

        // Generate itemsets of size 1 to 3 from the transaction
        for (int size = 1; size <= 3; size++) {
            generateCombinations(items, size, 0, new HashSet<>(), candidates);
        }
        return candidates;
    }

    private static void generateCombinations(List<String> items, int size, int start, Set<String> current, List<Set<String>> candidates) {
        if (current.size() == size) {
            candidates.add(new HashSet<>(current));
            return;
        }
        for (int i = start; i < items.size(); i++) {
            current.add(items.get(i));
            generateCombinations(items, size, i + 1, current, candidates);
            current.remove(items.get(i));
        }
    }

    private static void filterFrequentItemsets(Map<Set<String>, Integer> itemsetCounts, int minSupport, Set<Set<String>> frequentItemsets) {
        for (Map.Entry<Set<String>, Integer> entry : itemsetCounts.entrySet()) {
            if (entry.getValue() >= minSupport) {
                frequentItemsets.add(entry.getKey());
            }
        }
    }
}
