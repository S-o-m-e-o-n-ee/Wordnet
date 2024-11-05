import dsa.DiGraph;
import dsa.LinkedQueue;
import dsa.SeparateChainingHashST;
import stdlib.In;
import stdlib.StdIn;
import stdlib.StdOut;

public class ShortestCommonAncestor {
    private DiGraph G;

    // Constructs a ShortestCommonAncestor object given a rooted DAG.
    public ShortestCommonAncestor(DiGraph G) {
        // Throws the appropriate error if G is null
        if (G == null) {
            throw new NullPointerException("G is null");
        }
        this.G = G;
    }

    // Returns length of the shortest ancestral path between vertices v and w.
    public int length(int v, int w) {
        // Throws the appropriate error if v is not within accepted values
        if (v < 0 || v >= G.V()) {
            throw new IndexOutOfBoundsException("v is invalid");
        }
        // Throws the appropriate error if w is not within accepted values
        if (w < 0 || w >= G.V()) {
            throw new IndexOutOfBoundsException("w is invalid");
        }
        // Finds the closest common ancestor of v and w
        int ancestor = this.ancestor(v, w);
        // Returns the sum of the distance from v to the ancestor and the distance from w to the ancestor
        return distFrom(v).get(ancestor) + distFrom(w).get(ancestor);
    }

    // Returns a shortest common ancestor of vertices v and w.
    public int ancestor(int v, int w) {
        // Throws the appropriate error if v is not within accepted values
        if (v < 0 || v >= G.V()) {
            throw new IndexOutOfBoundsException("v is invalid");
        }
        // Throws the appropriate error if w is not within accepted values
        if (w < 0 || w >= G.V()) {
            throw new IndexOutOfBoundsException("w is invalid");
        }

        // Gathers the ancestors of v and w
        SeparateChainingHashST<Integer, Integer> ancestorsV = distFrom(v);
        SeparateChainingHashST<Integer, Integer> ancestorsW = distFrom(w);

        // Initializes the smallest distance found so far and the corresponding vertex
        int smallestDistance = Integer.MAX_VALUE;
        int commonAncestor = -1;

        // Iterates over all the possible ancestors of v
        for (int possibleAncestor : ancestorsV.keys()) {
            // If the possible ancestor is also a possible ancestor of w...
            if (ancestorsW.contains(possibleAncestor)) {
                // Calculates the distance between v and w with respect to the possible ancestor
                int distance = ancestorsV.get(possibleAncestor) + ancestorsW.get(possibleAncestor);
                // If the current distance is smaller than the smallest distance encountered so far...
                if (distance < smallestDistance) {
                    // Sets the smallest distance so far to the current distance
                    smallestDistance = distance;
                    // Sets the best ancestor candidate to the current possible ancestor
                    commonAncestor = possibleAncestor;
                }
            }
        }

        // Returns the best ancestor candidate found
        return commonAncestor;
    }

    // Returns length of the shortest ancestral path of vertex subsets A and B.
    public int length(Iterable<Integer> A, Iterable<Integer> B) {
        // Throws the appropriate error if A is null
        if (A == null) {
            throw new NullPointerException("A is null");
        }
        // Throws the appropriate error if B is null
        if (B == null) {
            throw new NullPointerException("B is null");
        }
        // Throws the appropriate error if A is empty
        if (!A.iterator().hasNext()) {
            throw new IllegalArgumentException("A is empty");
        }
        // Throws the appropriate error if B is empty
        if (!B.iterator().hasNext()) {
            throw new IllegalArgumentException("B is empty");
        }

        // Gets a triad with the best ancestor, the first vertex from the subset and the second vertex from teh subset, in that order
        int[] triad = triad(A, B);

        // Returns the distance from the first vertex to the second vertex through the best ancestor
        return distFrom(triad[1]).get(triad[0]) + distFrom(triad[2]).get(triad[0]);
    }

    // Returns a shortest common ancestor of vertex subsets A and B.
    public int ancestor(Iterable<Integer> A, Iterable<Integer> B) {
        // Throws the appropriate error if A is null
        if (A == null) {
            throw new NullPointerException("A is null");
        }
        // Throws the appropriate error if B is null
        if (B == null) {
            throw new NullPointerException("B is null");
        }
        // Throws the appropriate error if A is empty
        if (!A.iterator().hasNext()) {
            throw new IllegalArgumentException("A is empty");
        }
        // Throws the appropriate error if B is empty
        if (!B.iterator().hasNext()) {
            throw new IllegalArgumentException("B is empty");
        }

        // Gets a triad with the best ancestor, the first vertex from the subset and the second vertex from teh subset, in that order
        int[] triad = triad(A, B);

        // Returns the best ancestor
        return triad[0];
    }

    // Returns a map of vertices reachable from v and their respective shortest distances from v.
    private SeparateChainingHashST<Integer, Integer> distFrom(int v) {
        // Creates an empty symbol table to store the distances
        SeparateChainingHashST<Integer, Integer> st = new SeparateChainingHashST<Integer, Integer>();

        // Puts the source with the value zero, as the distance from the source to itself is zero
        st.put(v, 0);

        // Creates an empty queue to store the vertexes as they are traversed
        LinkedQueue<Integer> traversalQueue = new LinkedQueue<Integer>();

        // Enqueues v to the traversal queue
        traversalQueue.enqueue(v);

        // While the traversal queue is not empty...
        while (!traversalQueue.isEmpty()) {
            // Sets the current vertex to the item that is dequeued from the traversal queue
            int CurrentVertex = traversalQueue.dequeue();

            // For every vertex adjacent to the current vertex...
            for (int adjacentVertex : G.adj(CurrentVertex)) {
                // So long as the adjacent vertex is not already on the symbol table
                if (!st.contains(adjacentVertex)) {
                    // Adds the adjacent vertex to the symbol table and enqueues it to the traversal queue
                    st.put(adjacentVertex, st.get(CurrentVertex) + 1);
                    traversalQueue.enqueue(adjacentVertex);
                }
            }
        }

        // Returns the symbol table
        return st;
    }

    // Returns an array consisting of a shortest common ancestor a of vertex subsets A and B,
    // vertex v from A, and vertex w from B such that the path v-a-w is the shortest ancestral
    // path of A and B.
    private int[] triad(Iterable<Integer> A, Iterable<Integer> B) {
        // Initializes the triad to an empty array of three integers
        int[] triad = new int[3];

        // Initializes the smallest distance found
        int smallestDistance = Integer.MAX_VALUE;

        // For all the vertexes in A...
        for (int vertex1 : A) {
            // For all the vertexes in B...
            for (int vertex2 : B) {
                // Calculates the distance between the two vertexes
                int distance = length(vertex1, vertex2);
                // If the current distance is less than the smallest distance found so far...
                if (distance < smallestDistance) {
                    // Sets the smallest distance so far to the current distance
                    smallestDistance = distance;
                    // Sets the triad to the ancestor of the two current vertex and the two current vertexes themselves, in that order
                    triad = new int[]{ancestor(vertex1, vertex2), vertex1, vertex2};
                }
            }
        }

        // Returns the best triad found so far
        return triad;
    }

    // Unit tests the data type. [DO NOT EDIT]
    public static void main(String[] args) {
        In in = new In(args[0]);
        DiGraph G = new DiGraph(in);
        in.close();
        ShortestCommonAncestor sca = new ShortestCommonAncestor(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sca.length(v, w);
            int ancestor = sca.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
