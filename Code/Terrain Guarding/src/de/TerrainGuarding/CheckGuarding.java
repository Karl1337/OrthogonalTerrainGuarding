package de.TerrainGuarding;

import sipura.graphs.SimpleGraph;

import java.util.HashSet;

public class CheckGuarding {


    /**
     * Method that checks for a given bipartite visibility graph whether or not all its convex vertices (and thus all vertices)
     * are guarded by the reflex vertices in guards.
     *
     * @param terrain bipartite visibility graph of orthogonal terrain
     * @param guards  the guards for which one wants to check whether or not they guard the entire terrain
     * @return true if the entire terrain is guarded by the vertices in guards, false if not
     */
    static boolean isGuardedRB(SimpleGraph<Vertex> terrain, HashSet<Vertex> guards) {
        //init Hashsets that store convex vertices to be guarded and that are guarded by guards (Hashsets contain no duplicates)
        HashSet<Vertex> convexTerrain = new HashSet<>();
        HashSet<Vertex> convexGuarded = new HashSet<>();
        //add all Convex vertices in the terrain
        for (Vertex c : terrain.getV()) {
            if (c.getvType().contains("C")) {
                convexTerrain.add(c);
            }
        }
        //add all convex vertices to the set that are guarded (duplicates get ignored automatically by Hashset)
        for (Vertex g : guards) {
            if(terrain.contains(g)) {
                convexGuarded.addAll(terrain.neighbors(g));
            }
        }
        return convexGuarded.containsAll(convexTerrain);
    }


    /**
     * Method that checks for a full visibility graph whether or not all its vertices are guarded by guards
     *
     * @param terrain general visibility graph of terrain
     * @param guards  the guards for which one wants to check whether or not they guard the entire terrain
     * @return true if the entire terrain is guarded, false if not
     */
    static boolean isGuardedFull(SimpleGraph<Vertex> terrain, HashSet<Vertex> guards) {
        //init Hashsets containing vertices that are guarded by guards (Hashsets contain no duplicates)
        HashSet<Vertex> vertexGuarded = new HashSet<>();

        //add vertices to the set that are guarded (duplicates get ignored automatically by Hashset)
        for (Vertex g : guards) {
            vertexGuarded.addAll(terrain.neighbors(g));
        }
        //add the guards themselves since they see themselves
        vertexGuarded.addAll(guards);

        return vertexGuarded.containsAll(terrain.getV());
    }


    /**
     * Method that computes the convex vertices that are not guarded by a given set of guards in the input terrain
     *
     * @param terrain        bipartite (possibly partial) visibility graph of the terrain
     * @param guards         set of guards (only reflex vertices)
     * @param convexVertices set of all convex vertices of the terrain
     * @return set of unguarded convex vertices
     */
    static HashSet<Vertex> notGuardedSet(SimpleGraph<Vertex> terrain, HashSet<Vertex> guards, HashSet<Vertex> convexVertices) {

        //init Hashsets that store convex vertices to be guarded and that are guarded by guards (Hashsets contain no duplicates)
        HashSet<Vertex> convexGuarded = new HashSet<>();


        //add all convex vertices to the set that are guarded (duplicates get ignored automatically by Hashset)
        for (Vertex g : guards) {
            //safety measure
            if(g.getvType().contains("C")){
                continue;
            }
            convexGuarded.addAll(terrain.neighbors(g));
        }
        //System.out.println("currently there are " + convexGuarded.size() + " convex vertices out of " + convexVertices.size() + " convex vertices guarded.");

        //init helpSet
        HashSet<Vertex> helpSet = new HashSet<>(convexVertices);

        //return unguarded convex vertices
        if (convexGuarded.containsAll(convexVertices)) {
            return new HashSet<>();
        } else {
            helpSet.removeAll(convexGuarded);
            return helpSet;
        }
    }
}
