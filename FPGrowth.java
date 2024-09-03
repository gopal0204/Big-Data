import java.util.*;

class FPGrowth {
    static class Node {
        String item;
        int count;
        Node parent;
        List<Node> children;
        Node next;

        Node(String item) {
            this.item = item;
            this.count = 1;
            this.children = new ArrayList<>();
        }

        Node getChild(String item) {
            for (Node child : children) {
                if (child.item.equals(item)) {
                    return child;
                }
            }
            return null;
        }
    }

    public static void main(String[] args) {
        List<List<String>> transactions = Arrays.asList(
                Arrays.asList("a", "b"),
                Arrays.asList("b", "c", "d"),
                Arrays.asList("a", "c", "d", "e"),
                Arrays.asList("a", "d", "e"),
                Arrays.asList("a", "b", "c"),
                Arrays.asList("a", "b", "c", "d"),
                Arrays.asList("a"),
                Arrays.asList("a", "b", "c"));

        int minSupport = 2;
        FPGrowth fpGrowth = new FPGrowth();
        Node root = fpGrowth.constructFPTree(transactions, minSupport);
        fpGrowth.printFPTree(root, 0);
    }

    public Node constructFPTree(List<List<String>> transactions, int minSupport) {
        Map<String, Integer> frequencyMap = buildFrequencyMap(transactions);
        List<List<String>> sortedTransactions = sortTransactions(transactions, frequencyMap, minSupport);
        return buildFPTree(sortedTransactions);
    }

    private Map<String, Integer> buildFrequencyMap(List<List<String>> transactions) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        for (List<String> transaction : transactions) {
            for (String item : transaction) {
                frequencyMap.put(item, frequencyMap.getOrDefault(item, 0) + 1);
            }
        }
        return frequencyMap;
    }

    private List<List<String>> sortTransactions(List<List<String>> transactions, Map<String, Integer> frequencyMap,
            int minSupport) {
        List<List<String>> sortedTransactions = new ArrayList<>();
        for (List<String> transaction : transactions) {
            List<String> sortedTransaction = new ArrayList<>();
            for (String item : transaction) {
                if (frequencyMap.get(item) >= minSupport) {
                    sortedTransaction.add(item);
                }
            }
            sortedTransaction.sort((i1, i2) -> frequencyMap.get(i2) - frequencyMap.get(i1));
            sortedTransactions.add(sortedTransaction);
        }
        return sortedTransactions;
    }

    private Node buildFPTree(List<List<String>> transactions) {
        Node root = new Node(null);
        Map<String, Node> headerTable = new HashMap<>();

        for (List<String> transaction : transactions) {
            Node current = root;
            for (String item : transaction) {
                Node child = current.getChild(item);
                if (child == null) {
                    child = new Node(item);
                    current.children.add(child);
                    child.parent = current;

                    if (!headerTable.containsKey(item)) {
                        headerTable.put(item, child);
                    } else {
                        Node headerNode = headerTable.get(item);
                        while (headerNode.next != null) {
                            headerNode = headerNode.next;
                        }
                        headerNode.next = child;
                    }
                } else {
                    child.count++;
                }
                current = child;
            }
        }

        return root;
    }

    private void printFPTree(Node node, int level) {
        for (int i = 0; i < level; i++) {
            System.out.print("  ");
        }
        System.out.println(node.item == null ? "root" : node.item + " (" + node.count + ")");
        for (Node child : node.children) {
            printFPTree(child, level + 1);
        }
    }
}