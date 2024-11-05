import dsa.DiGraph;
import dsa.SeparateChainingHashST;
import dsa.Set;
import stdlib.In;
import stdlib.StdOut;

public class WordNet {
    SeparateChainingHashST<String, Set<Integer>> st;
    SeparateChainingHashST<Integer, String> rst;
    ShortestCommonAncestor sca;


    // Constructs a WordNet object given the names of the input (synset and hypernym) files.
    public WordNet(String synsets, String hypernyms) {
        // Throws the appropriate error message if synsets is null
        if (synsets == null) {
            throw new NullPointerException("synsets is null");
        }
        // Throws the appropriate error message if hypernyms is null
        if (hypernyms == null) {
            throw new NullPointerException("hypernyms is null");
        }

        // Initializes st and rst to empty SeparateChainingHashSTs
        this.st = new SeparateChainingHashST<String, Set<Integer>>();
        this.rst = new SeparateChainingHashST<Integer, String>();

        // Creates an In for the synsets file and reads the lines from it to synsetsLines
        In synsetsIn = new In(synsets);
        String[] synsetsLines = synsetsIn.readAllLines();

        // Initializes the number of entries to zero
        int numberOfEntries = 0;

        // For every line is synsetsLines...
        for (String line : synsetsLines) {
            // Splits the line into its components
            String[] lineInSynsets = line.split(",");

            // For each noun in the line...
            for (String noun : lineInSynsets[1].split("\\s")) {
                // Creates an entry for the noun if it does not currently exist
                if (!st.contains(noun)) {
                    st.put(noun, new Set<Integer>());
                }
                // Adds the number corresponding to the current noun to the set in the symbol table
                st.get(noun).add(Integer.parseInt(lineInSynsets[0]));
            }

            // Puts the correct values into rst
            rst.put(Integer.parseInt(lineInSynsets[0]), lineInSynsets[1]);

            // Increments the number of entries
            numberOfEntries++;
        }

        // Creates a new digraph with the same number of vertexes as there are entries
        DiGraph G = new DiGraph(numberOfEntries);

        // Creates an in from the hypernyms file and reads the lines from it to hypernymsLines
        In hypernymsIn = new In(hypernyms);
        String[] hypernymsLines = hypernymsIn.readAllLines();

        // For each line from hypernymsLines...
        for (String line : hypernymsLines) {

            // Splits the line into its components
            String[] lineInHypernyms = line.split(",");

            // Sets the source to the first component
            int source = Integer.parseInt(lineInHypernyms[0]);

            // For each string in the rest of the line...
            for (String stringVersionOfTheVertex : lineInHypernyms) {
                // Translates the string to an int
                int intVersionOfTheVertex = Integer.parseInt(stringVersionOfTheVertex);

                // Skips the source
                if (intVersionOfTheVertex == source) {continue;}

                // Adds an edge from the source to the current vertex
                G.addEdge(source, intVersionOfTheVertex);
            }
        }

        // Stores the shortest common ancestor in sca
        this.sca = new ShortestCommonAncestor(G);
    }

    // Returns all WordNet nouns.
    public Iterable<String> nouns() {
        return this.st.keys();
    }

    // Returns true if the given word is a WordNet noun, and false otherwise.
    public boolean isNoun(String word) {
        // Throws the appropriate error message if word is null
        if (word == null) {
            throw new NullPointerException("word is null");
        }
        return st.contains(word);
    }

    // Returns a synset that is a shortest common ancestor of noun1 and noun2.
    public String sca(String noun1, String noun2) {
        // Throws the appropriate error message if noun1 is null
        if (noun1 == null) {
            throw new NullPointerException("noun1 is null");
        }
        // Throws the appropriate error message if noun2 is null
        if (noun2 == null) {
            throw new NullPointerException("noun2 is null");
        }
        // Throws the appropriate error message if noun1 is not a noun in the wordNet
        if (!this.isNoun(noun1)) {
            throw new IllegalArgumentException("noun1 is not a noun");
        }
        // Throws the appropriate error message if noun2 is not a noun in the wordNet
        if (!this.isNoun(noun2)) {
            throw new IllegalArgumentException("noun2 is not a noun");
        }
        // Returns the vertex that is the shortest common ancestor for both noun one and two
        int commonAncestor = sca.ancestor(st.get(noun1), st.get(noun2));

        // Returns the corresponding noun
        return rst.get(commonAncestor);
    }

    // Returns the length of the shortest ancestral path between noun1 and noun2.
    public int distance(String noun1, String noun2) {
        // Throws the appropriate error message if noun1 is null
        if (noun1 == null) {
            throw new NullPointerException("noun1 is null");
        }
        // Throws the appropriate error message if noun2 is null
        if (noun2 == null) {
            throw new NullPointerException("noun2 is null");
        }
        // Throws the appropriate error message if noun1 is not a noun in the wordNet
        if (!this.isNoun(noun1)) {
            throw new IllegalArgumentException("noun1 is not a noun");
        }
        // Throws the appropriate error message if noun2 is not a noun in the wordNet
        if (!this.isNoun(noun2)) {
            throw new IllegalArgumentException("noun2 is not a noun");
        }
        return sca.length(st.get(noun1), st.get(noun2));
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        String word1 = args[2];
        String word2 = args[3];
        int nouns = 0;
        for (String noun : wordnet.nouns()) {
            nouns++;
        }
        StdOut.printf("# of nouns = %d\n", nouns);
        StdOut.printf("isNoun(%s)? %s\n", word1, wordnet.isNoun(word1));
        StdOut.printf("isNoun(%s)? %s\n", word2, wordnet.isNoun(word2));
        StdOut.printf("isNoun(%s %s)? %s\n", word1, word2, wordnet.isNoun(word1 + " " + word2));
        StdOut.printf("sca(%s, %s) = %s\n", word1, word2, wordnet.sca(word1, word2));
        StdOut.printf("distance(%s, %s) = %s\n", word1, word2, wordnet.distance(word1, word2));
    }
}
