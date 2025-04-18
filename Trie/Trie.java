import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Trie {

    private TrieNode root = new TrieNode();
    private boolean terminal;

    /**
     * Inserts a string into the trie and returns the last node that was
     * inserted.
     *
     * @param str The string to insert into the trie
     * @param data	The data associated with the string
     * @return The last node that was inserted into the trie
     */
    public TrieNode insert(String str, TrieData data) {
        // hint you can use str.toCharArray() to get the char[] of characters
        char[] Characters = str.toCharArray();
        TrieNode nodeToInsert = root;

        for (char index : Characters){
            if (nodeToInsert.getChild(index) == null) {
                TrieNode newNode = new TrieNode();
                nodeToInsert.addChild(index, newNode);
            }
            nodeToInsert = nodeToInsert.getChild(index);
        }
        nodeToInsert.addData(data);
        nodeToInsert.setTerminal(true);
        return nodeToInsert;
    }

    /**
     * Search for a particular prefix in the trie, and return the final node in
     * the path from root to the end of the string, i.e. the node corresponding
     * to the final character. getNode() differs from get() in that getNode()
     * searches for any prefix starting from the root, and returns the node
     * corresponding to the final character of the prefix, whereas get() will
     * search for a whole word only and will return null if it finds the pattern
     * in the trie, but not as a whole word.  A "whole word" is a path in the
     * trie that has an ending node that is a terminal node.
     *
     * @param str The string to search for
     * @return the final node in the path from root to the end of the prefix, or
     * null if prefix is not found
     */
    public TrieNode getNode(String str) {
        // hint you can use str.toCharArray() to get the char[] of characters
        TrieNode insertNode = root;
        for (char characters : str.toCharArray()) {
            insertNode = insertNode.getChild(characters);
            if (insertNode == null) {
                return null;
            }
        }
        return insertNode;
    }

    /**
     * Searches for a word in the trie, and returns the final node in the search
     * sequence from the root, i.e. the node corresponding to the final
     * character in the word.
     *
     * getNode() differs from get() in that getNode() searches for any prefix
     * starting from the root, and returns the node corresponding to the final
     * character of the prefix, whereas get() will search for a whole word only
     * and will return null if it finds the pattern in the trie, but not as a
     * whole word. A "whole word" is a path in the
     * trie that has an ending node that is a terminal node.
     *
     * @param str The word to search for
     * @return The node corresponding to the final character in the word, or
     * null if word is not found
     */
    public TrieNode get(String str) {
        // hint you can use str.toCharArray() to get the char[] of characters

        // hint use getNode to find the end node and then check to see if it is
        // not null and a terminal

        TrieNode node = getNode(str);
        if (node != null && node.isTerminal()) {
            return node;
        }
        return null;
    }

    /**
     * Retrieve from the trie an alphabetically sorted list of all words
     * beginning with a particular prefix.
     *
     * @param prefix The prefix with which all words start.
     * @return The list of words beginning with the prefix, or an empty list if
     * the prefix was not found.
     */
    public List<String> getAlphabeticalListWithPrefix(String prefix) {
        List<String> listOfValidWords = new ArrayList<>();
        TrieNode node = getNode(prefix);

        if (node == null) {
            return listOfValidWords;
        }

        validWordCollector(node, new StringBuilder(prefix), listOfValidWords);
        Collections.sort(listOfValidWords);

        return listOfValidWords;
    }

    /**
     * NOTE: TO BE IMPLEMENTED IN ASSIGNMENT 1 Finds the most frequently
     * occurring word represented in the trie (according to the dictionary file)
     * that begins with the provided prefix.
     *
     * @param prefix The prefix to search for
     * @return The most frequent word that starts with prefix
     */
    public String getMostFrequentWordWithPrefix(String prefix) {
        TrieNode startNode = getNode(prefix);
        if (startNode == null) return null;

        String greatestFreq = null;
        int biggestFreq = 0;

        // Use DFS with a stack of pairs
        Stack<Pair> stack = new Stack<>();
        stack.push(new Pair(startNode, new StringBuilder(prefix)));

        while (!stack.isEmpty()) {
            Pair pair = stack.pop();
            TrieNode node = pair.node;
            StringBuilder word = pair.word;
            if (node.isTerminal()) {
                TrieData data = node.getData();
                if (data != null && data.getFrequency() > biggestFreq) {
                    biggestFreq = data.getFrequency();
                    greatestFreq = word.toString();
                }
            }
            node.getChildren().forEach((ch, childNode) -> {
                stack.push(new Pair(childNode, new StringBuilder(word).append(ch)));
            });
        }
        return greatestFreq;
    }

    private class Pair {
        TrieNode node;
        StringBuilder word;

        Pair(TrieNode node, StringBuilder word) {
            this.node = node;
            this.word = word;
        }
    }


    /**
     * NOTE: TO BE IMPLEMENTED IN ASSIGNMENT 1 Reads in a dictionary from file
     * and places all words into the trie.
     *
     * @param fileName the file to read from
     * @return the trie containing all the words
     */
    public static Trie readInDictionary(String fileName) {
        // Initialising a new trie (which is functionally our dictionary)
        Trie newDictionary = new Trie();

        try {
            Scanner fileScanner = new Scanner(new File(fileName));
            while (fileScanner.hasNextLine()) {
                String[] parts = fileScanner.nextLine().split(" ");
                if (parts.length >= 3) {
                    try {
                        newDictionary.insert(parts[1], new TrieData(Integer.parseInt(parts[2])));
                    } catch (NumberFormatException ignored) {
                        // Skip invalid frequency silently
                    }
                }
            }

            fileScanner.close();
        } catch (FileNotFoundException exception) {
            System.out.println("Trying to open: " + new File(fileName).getAbsolutePath());
            System.err.println("File not found: " + fileName);
        }

        return newDictionary;
    }


    private void validWordCollector(TrieNode node, StringBuilder word, List<String> currentListOfWords){
        if (node.isTerminal()){
            currentListOfWords.add(word.toString());
        }

        for(Map.Entry<Character, TrieNode> entry : node.getChildren().entrySet()){
            char childCharacter = entry.getKey();
            TrieNode childNode = entry.getValue();

            word.append(childCharacter);
            validWordCollector(childNode, word, currentListOfWords);

            word.deleteCharAt(word.length() - 1);
        }
    }

}
