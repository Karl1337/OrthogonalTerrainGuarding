package de.TerrainGuarding;
import sipura.graphs.SimpleGraph;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Reduction {


    /**
     * Method that reduces the bipartite visibility graph of an orthogonal terrain. Checks whether the neighborhood of
     * a reflex vertex is contained in the neighborhood of any other reflex vertex and deletes it (the reflex) from graph if true
     *
     * @param graph  bipartite visibility graph of orthogonal terrain
     * @param degCap min degree up from which the neighborhoods should be checked
     */
    static void reductionViaSubsetsRootWithDelCapped(SimpleGraph<Vertex> graph, int degCap) {

        int n = graph.getN(); //graphsize
        //if graph is empty terminate
        if (n == 0) {
            return;
        }

        //init hashmap that will mimic bucket-structure, bucket 'i' will be filled with vertices of degree i.
        HashMap<Integer, HashSet<Vertex>> map = new HashMap<>();

        int max = graph.maxDegree();

        if (max < degCap) {
            System.out.println("Min degree for subset-reduction is greater than max degree of graph itself");
            return;
        }
        if (degCap < 0) {
            degCap = 0;
        }

        //if graph has no edges terminate
        if (max == 0) {
            return;
        }

        //init sets for all keys from 0 to max degree
        for (int i = 0; i <= max; i++) {
            map.put(i, new HashSet<>());
        }

        //add reflex vertices to corresponding buckets
        for (Vertex v : graph.getV()) {
            if (!v.getvType().contains("C")) {
                map.get(graph.degreeOf(v)).add(v);
            }
        }

        Set<Vertex> root_set = new HashSet<>();
        int _newr;
        //only check for vertices with neighborhoods of size greater than degCap
        for (int j = max; j >= degCap; j--) {

            for (Vertex vertex : map.get(j)) {

                //skip irrelevant and convex vertices
                if (vertex.getvType().contains("C")) {
                    continue;
                }

                _newr = 0;

                if (graph.degreeOf(vertex) != 0) {
                    Vertex x = graph.neighbors(vertex).iterator().next(); //take one vertex from the set
                    for (Vertex u : graph.neighbors(vertex)) { //find mindegree vertex from set
                        if (graph.degreeOf(u) < graph.degreeOf(x)) {
                            x = u;
                        }
                    }
                    Set<Vertex> root_cand = new HashSet<>(graph.neighbors(x));
                    root_cand.add(x);
                    root_cand.remove(vertex);
                    root_cand.retainAll(root_set); //get intersection

                    for (Vertex sect : graph.neighbors(vertex)) {
                        //This is faster than creating a new set for the closed neighborhood although it is ugly
                        if (root_cand.contains(sect)) {
                            root_cand.retainAll(graph.neighbors(sect));
                            root_cand.add(sect);
                        } else {
                            root_cand.retainAll(graph.neighbors(sect));
                        }
                        if (root_cand.size() == 0) {
                            break;
                        }
                    }
                    _newr = root_cand.size();
                }
                if (_newr == 0) {
                    root_set.add(vertex);
                } else {
                    graph.removeVertex(vertex);
                }
            }
        }
    }



    /**
     * Method that reduces the bipartite visibility graph of an orthogonal terrain. Removes convex c, if N(c) is a
     * superset of N(c2) for any convex vertex c2.
     *
     * @param graph bipartite visibility graph of an orthogonal terrain
     */
    static void reductionDeleteConv(SimpleGraph<Vertex> graph) {
        int n = graph.getN(); //graphsize
        //if graph is empty terminate
        if (n == 0) {
            return;
        }

        HashSet<Vertex> convexes = new HashSet<>();
        for (Vertex v : graph.getV()) {
            if (v.getvType().contains("C")) {
                convexes.add(v);
            }
        }


        for (Vertex conv : convexes) {
            //skip reflex vertices
            if (!graph.contains(conv)) {
                continue;
            }
            boolean superset = false;
            for (Vertex otherConv : graph.getV()) {
                //skip the convex itself and reflex vertices
                if (otherConv.equals(conv) || !otherConv.getvType().contains("C")) {
                    continue;
                }
                if (graph.neighbors(conv).containsAll(graph.neighbors(otherConv))) {
                    superset = true;
                    break;
                }
            }
            if (superset) {
                graph.removeVertex(conv);
            } //remove conv if it is superset of any other
        }
    }


    /**
     * Method that reduces a bipartite visibility graph of a terrain. The methods reductionViaSubsetsRootWithDelCapped and
     * reductionIntersectCapped iteratively get called, with their appropriate parameters, as long as max steps is not reached and
     * the graph is at least reduced by a factor of 1-relaxParam in each iteration-step.
     *
     * @param graph           bipartite visibility graph of orthogonal terrain
     * @param maxSteps        max steps you allow for reduction
     * @param degCapIntersect max degree for reductionIntersectCapped
     * @param degCapSubsets   degree from which subsets should be build (method calculates maxdeg(graph)-degCapSubsets)
     * @param relaxParam      between 0 and 1, stops iteration if less than terrain-size * relaxParam vertices got removed in the last step
     * @return number of iteration steps, and hashset that contains guards chosen by reduction method
     */
    static AbstractMap.SimpleEntry<Integer, HashSet<Vertex>> reduceBipartiteGraphRelax(SimpleGraph<Vertex> graph, int maxSteps, int degCapIntersect, int degCapSubsets, double relaxParam, long timeOut) {
        //in case graph is empty return empty set
        if (graph.getN() == 0) {
            return new AbstractMap.SimpleEntry<>(0, new HashSet<Vertex>());
        }
        //in case max steps is set to negative, set it to vertex number. This way iteration will not
        //stop unless the other crit is reached
        if (maxSteps < 0) {
            maxSteps = graph.getN();
        }

        if (degCapIntersect < 0) {
            degCapIntersect = graph.maxDegree();
        }

        HashSet<Vertex> guards = new HashSet<>();
        int initialVertexCount = graph.getN();
        int initialEdgeCount = graph.getM();
        Instant start = Instant.now();
        int n_last = (int) (graph.getN() * (1 / relaxParam)) + 1; //helpvar that allows for iteration while something changes in the graph.
        int counter = 1;

        //set degcap
        if (degCapSubsets >= 0) {
            degCapSubsets = graph.maxDegree() - degCapSubsets;
        }

        //Call reduction while the reduction steps "do enough"
        while (graph.getN() < relaxParam * n_last && graph.getN() > 0 && counter <= maxSteps) {
            n_last = graph.getN();
            guards.addAll(reductionIntersectCapped(graph, degCapIntersect));
            reductionViaSubsetsRootWithDelCapped(graph, degCapSubsets);
            counter++;
            Instant end = Instant.now();
            if (Duration.between(start, end).getSeconds() >= timeOut) {
                System.out.println("TimeLimit reached, empty graph returned");
                return new AbstractMap.SimpleEntry<>(counter - 1, guards);
            }
        }

        System.out.println("Reduction called " + (counter - 1) + " times.");
        System.out.println("Dataset reduced from " + initialVertexCount + " to " + graph.getN() + " vertices.");
        System.out.println("Dataset reduced from " + initialEdgeCount + " to " + graph.getM() + " edges.");
        return new AbstractMap.SimpleEntry<>(counter - 1, guards);
    }


    //same as reduceBipartiteGraphRelax, without relaxParam
    static HashSet<Vertex> reduceBipartiteGraph(SimpleGraph<Vertex> graph, int maxSteps, int degCapIntersect, int degCapSubsets) {
        //in case graph is empty return empty set
        if (graph.getN() == 0) {
            return new HashSet<>();
        }
        //in case max steps is set to negative, set it to vertex number. This way iteration will not
        //stop unless the other crit is reached
        if (maxSteps < 0) {
            maxSteps = graph.getN();
        }

        HashSet<Vertex> guards = new HashSet<>();
        int initialVertexCount = graph.getN();
        int initialEdgeCount = graph.getM();
        int n_last = graph.getN() + 1; //helpvar that allows for iteration while something changes in the graph.
        int counter = 1;

        //Call reduction while something happens
        while (graph.getN() < n_last && graph.getN() > 0 && counter <= maxSteps) {
            n_last = graph.getN();
            guards.addAll(reductionIntersectCapped(graph, degCapIntersect));
            reductionViaSubsetsRootWithDelCapped(graph, degCapSubsets);
            counter++;
        }

        System.out.println("Reduction called " + (counter - 1) + " times.");
        System.out.println("Dataset reduced from " + initialVertexCount + " to " + graph.getN() + " vertices.");
        System.out.println("Dataset reduced from " + initialEdgeCount + " to " + graph.getM() + " edges.");
        return guards;
    }


    static AbstractMap.SimpleEntry<Integer, HashSet<Vertex>> reduceBipartiteGraphRelaxInclConv(SimpleGraph<Vertex> graph, int maxSteps, int degCapIntersect, int degCapSubsets, double relaxParam, long timeOut) {
        //in case graph is empty return empty set
        if (graph.getN() == 0) {
            return new AbstractMap.SimpleEntry<>(0, new HashSet<Vertex>());
        }
        //in case max steps is set to negative, set it to vertex number. This way iteration will not
        //stop unless the other crit is reached
        if (maxSteps < 0) {
            maxSteps = graph.getN();
        }

        if (degCapIntersect < 0) {
            degCapIntersect = graph.maxDegree();
        }

        HashSet<Vertex> guards = new HashSet<>();
        int initialVertexCount = graph.getN();
        int initialEdgeCount = graph.getM();
        Instant start = Instant.now();
        int n_last = (int) (graph.getN() * (1 / relaxParam)) + 1; //helpvar that allows for iteration while something changes in the graph.
        int counter = 1;

        //set degcap
        if (degCapSubsets >= 0) {
            degCapSubsets = graph.maxDegree() - degCapSubsets;
        }

        //Call reduction while the reduction steps "do enough"
        while (graph.getN() < relaxParam * n_last && graph.getN() > 0 && counter <= maxSteps) {
            n_last = graph.getN();
            guards.addAll(reductionIntersectCapped(graph, degCapIntersect));
            reductionViaSubsetsRootWithDelCapped(graph, degCapSubsets);
            reductionDeleteConv(graph);
            guards.addAll(reductionPrivateSmarter(graph));
            removeDegZeroVertices(graph); //remove zero Deg vertices
            counter++;
            Instant end = Instant.now();
            if (Duration.between(start, end).getSeconds() >= timeOut) {
                System.out.println("TimeLimit reached, empty graph returned");
                return new AbstractMap.SimpleEntry<>(counter - 1, guards);
            }
        }

        System.out.println("Reduction called " + (counter - 1) + " times.");
        System.out.println("Dataset reduced from " + initialVertexCount + " to " + graph.getN() + " vertices.");
        System.out.println("Dataset reduced from " + initialEdgeCount + " to " + graph.getM() + " edges.");
        return new AbstractMap.SimpleEntry<>(counter - 1, guards);
    }


    /** Method that removes vertices of degree zero in a graph
     *
     * @param graph input graph
     */
    static void removeDegZeroVertices(SimpleGraph<Vertex> graph) {
        HashSet<Vertex> toRemove = new HashSet<>();
        for (Vertex v : graph.getV()) {
            if (graph.degreeOf(v) == 0) {
                toRemove.add(v);
            }
        }
        for (Vertex v : toRemove) {
            graph.removeVertex(v);
        }
    }


    //straightforward implementation of the idea behind reductionPrivateSmarter, just for tests runtime very bad
    /*
    static HashSet<Vertex> reductionPrivateNeighborhood(SimpleGraph<Vertex> graph){

        //init guarding set
        HashSet<Vertex> guards = new HashSet<>();

        //read reflex vertices (blue set)
        HashSet<Vertex> reflVertices = new HashSet<>();
        for(Vertex v:graph.getV()){
            if (!v.getvType().contains("C")){
                reflVertices.add(v);
            }
        }

        //compute private neighborhoods and stop when criteria is reached
        for(Vertex refl: reflVertices){
            HashSet<Vertex> neighs = new HashSet<>(graph.neighbors(refl));
            System.out.println("started: " + refl.getID());
            for(Vertex conv: neighs){
                HashSet<Vertex> neighRed = new HashSet<>(graph.neighbors(conv));
                HashSet<Vertex> privNeigh = new HashSet<>();
                for(Vertex neigh:neighRed){
                    privNeigh.addAll(graph.neighbors(neigh));
                }
                if(graph.neighbors(refl).containsAll(privNeigh)){
                    guards.add(refl); //add reflex to guard in case private neighborhood has size at least one
                    graph.removeVertex(refl);
                    System.out.println("removed: " + refl.getID());
                    break;
                }
            }
        }
        return guards;
    }
     */


    /**
     * Method that reduces graph (and chooses guards) based on private neighborhoods of reflex vertices.
     *
     * @param graph bipartite visibility graph of orthogonal terrain
     * @return vertices chosen as guards by reduction method
     */
    static HashSet<Vertex> reductionPrivateSmarter(SimpleGraph<Vertex> graph) {
        //init guarding set
        HashSet<Vertex> guards = new HashSet<>();

        //read convex vertices (red set)
        HashSet<Vertex> convVertices = new HashSet<>();
        for (Vertex v : graph.getV()) {
            if (v.getvType().contains("C")) {
                convVertices.add(v);
            }
        }


        //compute all possible vertices that might end up in a private neighborhood
        for (Vertex conv : convVertices) {
            HashSet<Vertex> toRemove = new HashSet<>();
            if (graph.contains(conv)) {
                HashSet<Vertex> bigNeigh = new HashSet<>();
                for (Vertex refl : graph.neighbors(conv)) {
                    bigNeigh.addAll(graph.neighbors(refl)); //init big neighborhoods
                }
                for (Vertex refl : graph.neighbors(conv)) {
                    if (graph.neighbors(refl).containsAll(bigNeigh)) {
                        guards.add(refl);
                        toRemove.add(refl);
                        toRemove.addAll(bigNeigh); //remove bigNeigh, must be equal to neighborhood of refl so this is faster than reading v's neighborhood
                    }
                }
            }

            for (Vertex rem : toRemove) {
                graph.removeVertex(rem);
            }
            return guards;
        }
        return new HashSet<>();
    }


    //compute subgraph
    static SimpleGraph<Vertex> getSubgraph(SimpleGraph<Vertex> graph, Set<Vertex> vertices) {
        SimpleGraph<Vertex> subGraph = new SimpleGraph<>();
        for (Vertex reflex : vertices) {
            subGraph.addVertex(reflex);
            for (Vertex convex : graph.neighbors(reflex)) {
                subGraph.addVertex(convex);
                subGraph.addEdge(convex, reflex);
            }
        }
        return subGraph;
    }


    /**
     * Method that iterates over all convex vertices in the terrain and intersects
     * the neighborhoods of all reflex neighbors of the current convex vertex. The convex vertices in this intersection
     * are then removed from the graph (except for the current convex of iteration). Further, convex vertices of degree
     * 1 get removed and their only reflex neighbor chosen as guard.
     *
     * @param graph  bipartite visibility graph of orthogonal terrain
     * @param degCap max degree up to which the intersections shall be computed
     * @return set of guards chosen by method
     */
    static HashSet<Vertex> reductionIntersectCapped(SimpleGraph<Vertex> graph, int degCap) {

        if (graph.getN() == 0) {
            System.out.println("Graph has no vertices. emptyset will be returned");
            return new HashSet<>();
        }
        if (degCap == 0) {
            System.out.println("Please choose degCap > 0. emptyset will be returned");
            return new HashSet<>();
        }

        //init hashmap that will mimic bucket-structure, bucket i will be filled with vertices of degree i.
        HashMap<Integer, HashSet<Vertex>> map = new HashMap<>();
        int max = graph.maxDegree();

        HashSet<Vertex> guards = new HashSet<>();

        //graph has no edges or is entirely empty
        if (max == 0) {
            return guards;
        }

        //in case degCap is negative set to maxdeg
        if (degCap < 0) {
            degCap = max;
        }

        //in case degCap is higher than max degree, we can set it to maxdeg
        if (degCap > max) {
            degCap = max;
        }

        //init sets for all keys from 0 to max degree
        for (int i = 0; i <= degCap; i++) {
            map.put(i, new HashSet<>());
        }


        //add convex vertices to corresponding buckets
        for (Vertex v : graph.getV()) {
            if (v.getvType().contains("C") && graph.degreeOf(v) <= degCap) {
                map.get(graph.degreeOf(v)).add(v);
            }
        }


        //in case degCap is set to 1 we are done
        if (degCap == 1) {
            return guards;
        }


        //remove degree 1 convexes, set guards and remove all guarded convexes from graph
        HashSet<Vertex> convexes = new HashSet<>(map.get(1));
        for (Vertex v : convexes) {
            if (graph.contains(v)) {
                Vertex reflex = new ArrayList<>(graph.neighbors(v)).get(0); //ArrayList can only have 1 reflex in it!
                guards.add(reflex); //add the one reflex that sees the current convex to guards
                Set<Vertex> reflexNeighbours = new HashSet<>(graph.neighbors(reflex));
                //remove all now guarded convexes from graph and the hashmap
                for (Vertex convex : reflexNeighbours) {
                    //avoid trying to delete vertices from buckets that we never created
                    if (graph.degreeOf(convex) <= degCap) {
                        map.get(graph.degreeOf(convex)).remove(convex); //O(1) on average, worst case O(logN) with N bucketsize
                    }
                    graph.removeVertex(convex);
                }
            }
        }

        //init nextBucket, will contain set of next nonempty bucket in iteration
        HashSet<Vertex> nextBucket = new HashSet<>();

        for (int i = 2; i <= degCap; i++) {
            if (map.get(i).isEmpty()) continue; //skip empty buckets

            nextBucket.addAll(map.get(i));

            for (Vertex convex : nextBucket) {
                if (!graph.contains(convex)) continue; //skip already deleted convexes

                Set<Vertex> intersection = new HashSet<>();
                Set<Vertex> convexNeighbourhood = graph.neighbors(convex); //read reflex neighbors of current convex
                Iterator<Vertex> reflexNeighbours = convexNeighbourhood.iterator();

                //put neighborhood of first reflex vertex in intersection.
                if (reflexNeighbours.hasNext()) {
                    intersection.addAll(graph.neighbors(reflexNeighbours.next()));
                }
                /*It is important to note, that the while loop always runs at least once, since we start our iteration
                with degree 2 or higher buckets. Also since we only remove convex vertices, the degree of any convex
                can't change during this method (graph is bipartite), thus we always can build an intersection.
                 */
                //go through all reflexes that see the convex and intersect their neighborhoods (can stop when intersection is empty)
                while (reflexNeighbours.hasNext() && !intersection.isEmpty()) {
                    Vertex reflex = reflexNeighbours.next();
                    intersection.retainAll(graph.neighbors(reflex));
                }

                /*remove the convex of current iteration from the intersection. Its needed in the graph so that one
                of the reflexes MUST be chosen as a guard
                 */
                intersection.remove(convex);
                //System.out.println("Size intersect: " + intersection.size());

                //remove vertices in the intersection from the graph and the buckets
                for (Vertex toRemove : intersection) {
                    //avoid trying to delete vertices from buckets that we never created
                    if (graph.degreeOf(toRemove) <= degCap) {
                        map.get(graph.degreeOf(toRemove)).remove(toRemove);
                    }
                    graph.removeVertex(toRemove);
                }
            }
            nextBucket.clear(); //reset nextBucket
        }
        //In the very end, we can delete all reflex vertices with degree 0, this won't affect any neighborhoods!
        HashSet<Vertex> allVerts = new HashSet<>(graph.getV());
        for (Vertex v : allVerts) {
            if (graph.neighbors(v).size() == 0 && !v.getvType().contains("C")) {
                graph.removeVertex(v);
            }
        }
        return guards;
    }


    //test whether or not initial sorting improves the running time of the intersection method. It does not.
    static HashSet<Vertex> reductionIntersectCappedArrayListsSort(SimpleGraph<Vertex> graph, int degCap) {
        if (graph.getN() == 0) {
            System.out.println("Graph has no vertices. emptyset will be returned");
            return new HashSet<>();
        }
        if (degCap <= 0) {
            System.out.println("Please choose degCap >= 0. emptyset will be returned");
            return new HashSet<>();
        }

        //init hashmap that will mimic bucket-structure, bucket i will be filled with vertices of degree i.
        HashMap<Integer, ArrayList<Vertex>> map = new HashMap<>();
        int max = graph.maxDegree();

        HashSet<Vertex> guards = new HashSet<>();

        //graph has no edges or is entirely empty
        if (max == 0) {
            return guards;
        }

        //in case degCap is higher than max degree, we can set it to maxdeg
        if (degCap > max) {
            degCap = max;
        }

        //init sets for all keys from 0 to max degree
        for (int i = 0; i <= degCap; i++) {
            map.put(i, new ArrayList<>());
        }


        //add convex vertices to corresponding buckets
        for (Vertex v : graph.getV()) {
            if (v.getvType().contains("C") && graph.degreeOf(v) <= degCap) {
                map.get(graph.degreeOf(v)).add(v);
            }
        }


        //in case degCap is set to 1 we are done
        if (degCap == 1) {
            return guards;
        }


        //remove degree 1 convexes, set guards and remove all guarded convexes from graph
        ArrayList<Vertex> convexes = new ArrayList<>(map.get(1));
        for (Vertex v : convexes) {
            if (graph.contains(v)) {
                Vertex reflex = new ArrayList<>(graph.neighbors(v)).get(0); //ArrayList can only have 1 reflex in it!
                guards.add(reflex); //add the one reflex that sees the current convex to guards
                Set<Vertex> reflexNeighbours = new HashSet<>(graph.neighbors(reflex));
                //remove all now guarded convexes from graph and the hashmap
                for (Vertex convex : reflexNeighbours) {
                    //avoid trying to delete vertices from buckets that we never created
                    if (graph.degreeOf(convex) <= degCap) {
                        map.get(graph.degreeOf(convex)).remove(convex);
                    }
                    graph.removeVertex(convex);
                }
            }
        }

        //init nextBucket, will contain set of next nonempty bucket in iteration
        ArrayList<Vertex> nextBucket = new ArrayList<>();

        for (int i = 2; i <= degCap; i++) {
            if (map.get(i).isEmpty()) continue; //skip empty buckets

            nextBucket.addAll(map.get(i));

            //sort nextBucket by descending y values
            nextBucket.sort(Comparator.comparingDouble(Vertex::getY).reversed());

            for (Vertex convex : nextBucket) {
                if (!graph.contains(convex)) continue; //skip already deleted convexes

                Set<Vertex> intersection = new HashSet<>();
                Set<Vertex> convexNeighbourhood = graph.neighbors(convex); //read reflex neighbors of current convex
                Iterator<Vertex> reflexNeighbours = convexNeighbourhood.iterator();

                //put neighborhood of first reflex vertex in intersection.
                if (reflexNeighbours.hasNext()) {
                    intersection.addAll(graph.neighbors(reflexNeighbours.next()));
                }
                /*It is important to note, that the while loop ALWAYS runs at least once, since we start our iteration
                with degree 2 or higher buckets. Also since we only remove convex vertices, the degree of any convex
                can't change during this method (graph is bipartite), thus we always can build an intersection.
                 */
                //go through all reflexes that see the convex and intersect their neighborhoods (can stop when intersection is empty)
                while (reflexNeighbours.hasNext() && !intersection.isEmpty()) {
                    Vertex reflex = reflexNeighbours.next();
                    intersection.retainAll(graph.neighbors(reflex));
                }

                /*remove the convex of current iteration from the intersection. Its needed in the graph so that one
                of the reflexes MUST be chosen as a guard
                 */
                intersection.remove(convex);

                //remove vertices in the intersection from the graph and the buckets
                for (Vertex toRemove : intersection) {
                    //avoid trying to delete vertices from buckets that we never created
                    if (graph.degreeOf(toRemove) <= degCap) {
                        map.get(graph.degreeOf(toRemove)).remove(toRemove);
                    }
                    graph.removeVertex(toRemove);
                }
            }
            nextBucket.clear(); //reset nextBucket
        }
        //In the very end, we can delete all reflex vertices with degree 0, this won't affect any neighborhoods!
        HashSet<Vertex> allVerts = new HashSet<>(graph.getV());
        for (Vertex v : allVerts) {
            if (graph.neighbors(v).size() == 0 && !v.getvType().contains("C")) {
                graph.removeVertex(v);
            }
        }
        return guards;
    }


    /**
     * Method that computes the local minima of an orthogonal terrain. That is two convex vertices that are next to
     * each other in the ordering of the vertex set.
     *
     * @param terrain orthogonal terrain
     * @return set that contains all vertices that are part of a local minimum
     */
    static HashSet<Vertex> getLocalMins(ArrayList<Vertex> terrain) {
        HashSet<Vertex> mins = new HashSet<>();
        for (int i = 0; i < terrain.size() - 1; i++) {
            //a pair of consecutive vertices is local min iff both are convex vertices
            if (terrain.get(i).getvType().contains("C") && terrain.get(i + 1).getvType().contains("C")) {
                mins.add(terrain.get(i));
                mins.add(terrain.get(i + 1));
            }
        }
        return mins;
    }

}