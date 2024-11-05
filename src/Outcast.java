import stdlib.StdIn;
import stdlib.StdOut;

public class Outcast {
    WordNet wordNet;

    // Constructs an Outcast object given the WordNet semantic lexicon.
    public Outcast(WordNet wordnet) {
        this.wordNet = wordnet;
    }

    // Returns the outcast noun from nouns.
    public String outcast(String[] nouns) {
        // Initializes the values for the largest distance encountered so far and the corresponding noun
        int largestDistance = 0;
        String outcast = "";

        // Iterates over all the nouns in nouns
        for (String noun1 : nouns) {
            // Initializes the distance corresponding to the current noun to zero
            int distance = 0;

            // Iterates over all the nouns in nouns again
            for (String noun2 : nouns) {
                // Adds the distance from the first noun to the second noun to the current distance
                distance += wordNet.distance(noun1, noun2);
            }

            // If the current distance is greater than the largest distance found so far...
            if (distance > largestDistance) {
                // Sets the largest distance to the current distance
                largestDistance = distance;

                // Sets the outcast to the current noun
                outcast = noun1;
            }
        }

        // Returns the outcast, i.e. the noun with the greatest distance
        return outcast;
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        String[] nouns = StdIn.readAllStrings();
        String outcastNoun = outcast.outcast(nouns);
        for (String noun : nouns) {
            StdOut.print(noun.equals(outcastNoun) ? "*" + noun + "* " : noun + " ");
        }
        StdOut.println();
    }
}
