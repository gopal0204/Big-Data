import java.util.*;

public class ImprovedApriori {

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
    // Hash-based technique for generating frequent itemsets
    private static List<Set<String>> hashBasedFrequentItemsets(List<Set<String>> transactions, int minSup, int k) {
        Map<Set<String>, Integer> candidateCounts = new HashMap<>();
        Map<Integer, List<Set<String>>> hashTable = new HashMap<>();
        int hashSize = 1000; // Size of hash table
        
        // Generate candidate itemsets of size k
        for (Set<String> transaction : transactions) {
            List<Set<String>> candidates = generateCandidates(transaction, k);
            for (Set<String> candidate : candidates) {
                int hashCode = candidate.hashCode() % hashSize;
                hashTable.computeIfAbsent(hashCode, x -> new ArrayList<>()).add(candidate);
            }
        }
        
        // Count candidate itemsets
        for (List<Set<String>> bucket : hashTable.values()) {
            for (Set<String> candidate : bucket) {
                int count = (int) transactions.stream().filter(transaction -> transaction.containsAll(candidate)).count();
                if (count >= minSup) {
                    candidateCounts.put(candidate, count);
                }
            }
        }
        
        return new ArrayList<>(candidateCounts.keySet());
    }

    // Generate candidates of size k from a transaction
    private static List<Set<String>> generateCandidates(Set<String> transaction, int k) {
        List<String> items = new ArrayList<>(transaction);
        List<Set<String>> candidates = new ArrayList<>();
        generateCandidates(items, new HashSet<>(), 0, k, candidates);
        return candidates;
    }

    // Recursive method to generate candidate itemsets
    private static void generateCandidates(List<String> items, Set<String> current, int start, int k, List<Set<String>> candidates) {
        if (current.size() == k) {
            candidates.add(new HashSet<>(current));
            return;
        }
        for (int i = start; i < items.size(); i++) {
            current.add(items.get(i));
            generateCandidates(items, current, i + 1, k, candidates);
            current.remove(items.get(i));
        }
    }

    // Partitioning technique for finding frequent itemsets
    private static List<Set<String>> partitioning(List<Set<String>> transactions, int minSup, int numPartitions) {
        List<Set<String>> globalFrequentItemsets = new ArrayList<>();
        List<List<Set<String>>> partitions = partition(transactions, numPartitions);
        
        Set<Set<String>> candidateItemsets = new HashSet<>();
        for (List<Set<String>> partition : partitions) {
            List<Set<String>> localFrequentItemsets = apriori(partition, minSup);
            candidateItemsets.addAll(localFrequentItemsets);
        }
        
        Map<Set<String>, Integer> globalCountMap = new HashMap<>();
        for (Set<String> candidate : candidateItemsets) {
            int count = (int) transactions.stream().filter(transaction -> transaction.containsAll(candidate)).count();
            if (count >= minSup) {
                globalFrequentItemsets.add(candidate);
            }
        }
        
        return globalFrequentItemsets;
    }

    // Split transactions into partitions
    private static List<List<Set<String>>> partition(List<Set<String>> transactions, int numPartitions) {
        List<List<Set<String>>> partitions = new ArrayList<>();
        int partitionSize = (int) Math.ceil((double) transactions.size() / numPartitions);
        
        for (int i = 0; i < numPartitions; i++) {
            int start = i * partitionSize;
            int end = Math.min(start + partitionSize, transactions.size());
            List<Set<String>> partition = transactions.subList(start, end);
            partitions.add(new ArrayList<>(partition));
        }
        
        return partitions;
    }

    // Dynamic itemset counting technique
    private static List<Set<String>> dynamicItemsetCounting(List<Set<String>> transactions, int minSup) {
        List<Set<String>> frequentItemsets = new ArrayList<>();
        Map<Set<String>, Integer> countMap = new HashMap<>();
        int blockSize = 2; // Number of transactions to process per block
        
        for (int start = 0; start < transactions.size(); start += blockSize) {
            int end = Math.min(start + blockSize, transactions.size());
            List<Set<String>> block = transactions.subList(start, end);
            
            for (Set<String> transaction : block) {
                List<Set<String>> candidates = generateCandidates(transaction, 2);
                for (Set<String> candidate : candidates) {
                    countMap.put(candidate, countMap.getOrDefault(candidate, 0) + 1);
                }
            }
            
            // Check and collect frequent itemsets
            for (Map.Entry<Set<String>, Integer> entry : countMap.entrySet()) {
                if (entry.getValue() >= minSup) {
                    frequentItemsets.add(entry.getKey());
                }
            }
        }
        
        return frequentItemsets;
    }

    // Main method to demonstrate the improved Apriori
    public static void main(String[] args) {
        List<Set<String>> transactions = new ArrayList<>();
        transactions.add(new HashSet<>(Arrays.asList("A", "B", "C")));
        transactions.add(new HashSet<>(Arrays.asList("A", "B")));
        transactions.add(new HashSet<>(Arrays.asList("A", "C")));
        transactions.add(new HashSet<>(Arrays.asList("B", "C")));
        transactions.add(new HashSet<>(Arrays.asList("A", "B", "C")));
        transactions.add(new HashSet<>(Arrays.asList("A", "B", "D")));

        int minSup = 2;
        int numPartitions = 2;

        System.out.println("Hash-Based Frequent Itemsets:");
        List<Set<String>> hashBasedFrequentItemsets = hashBasedFrequentItemsets(transactions, minSup, 2);
        for (Set<String> itemset : hashBasedFrequentItemsets) {
            System.out.println(itemset);
        }

        System.out.println("Partitioning Frequent Itemsets:");
        List<Set<String>> partitioningFrequentItemsets = partitioning(transactions, minSup, numPartitions);
        for (Set<String> itemset : partitioningFrequentItemsets) {
            System.out.println(itemset);
        }

        System.out.println("Dynamic Itemset Counting Frequent Itemsets:");
        List<Set<String>> dynamicItemsetCountingFrequentItemsets = dynamicItemsetCounting(transactions, minSup);
        for (Set<String> itemset : dynamicItemsetCountingFrequentItemsets) {
            System.out.println(itemset);
        }
    }
}
