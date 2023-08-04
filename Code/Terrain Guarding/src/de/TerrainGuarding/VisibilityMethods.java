package de.TerrainGuarding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import kotlin.Pair;
import sipura.alg.GraphProperty;
import sipura.graphs.SimpleGraph;

public class VisibilityMethods {

    /** Method that computes the full non-bipartite visibility graph of a non-orthogonal terrain.
     *
     * @param terrain terrain to compute visibility graph for
     * @return full visibility graph of terrain
     */
    static SimpleGraph<Vertex> fullVisgraph(ArrayList<Vertex> terrain, long timeOut) {

        //start timer
        Instant start = Instant.now();

        //initialize the visibility graph
        SimpleGraph<Vertex> visGraph = new SimpleGraph<>();
        for (Vertex v : terrain) {
            visGraph.addVertex(v);
        }

        //Go through all vertices of terrain and check whether they are visible
        for (int i = 0; i < terrain.size() - 1; i++) {
            double x1 = terrain.get(i).getX();
            double y1 = terrain.get(i).getY();

            //compute gradient of line of sight with direct neighbor
            //division by 0 is taken into account and works as intended here
            double m_last = ((terrain.get(i + 1).getY() - y1) / (terrain.get(i + 1).getX() - x1));
            for (int j = i + 1; j < terrain.size(); j++) {
                double x2 = terrain.get(j).getX();
                double y2 = terrain.get(j).getY();
                double m = ((y2 - y1) / (x2 - x1));
                //in case the gradient is greater than before we can add an edge in visGraph
                if (m >= m_last) {
                    visGraph.addEdge(terrain.get(i), terrain.get(j));
                    m_last = m;
                }
            }

            Instant end = Instant.now();
            if(Duration.between(start, end).getSeconds()>=timeOut ){
                System.out.println("TimeLimit reached, empty graph returned");
                return new SimpleGraph<>();
            }
        }
        return visGraph;
    }



    /** Simpler method to compute the bipartite visibility graph for an orthogonal terrain
     *
     * @param terrain orthogonal terrain
     * @return bipartite visibility graph of terrain
     */
    static SimpleGraph<Vertex> bipartiteVisGraphOrth(ArrayList<Vertex> terrain, long timeOut) {

        //start timer
        Instant start = Instant.now();

        //initialize the visibility graph
        SimpleGraph<Vertex> visGraph = new SimpleGraph<>();
        for (Vertex v : terrain) {
            visGraph.addVertex(v);
        }

        //Go through all vertices of terrain and check whether they are visible
        for (int i = 0; i < terrain.size() - 1; i++) {
            double x1 = terrain.get(i).getX();
            double y1 = terrain.get(i).getY();

            //since we sweep from left to right, we can skip right convex vertices and only add the reflex above it
            if (terrain.get(i).getvType().contains("RC")) {
                visGraph.addEdge(terrain.get(i), terrain.get(i + 1));
                continue;
            }

            //compute gradient of line of sight with direct neighbor
            //note that division by 0 is taken into account and works as intended here
            double m_last = ((terrain.get(i + 1).getY() - y1) / (terrain.get(i + 1).getX() - x1));
            for (int j = i + 1; j < terrain.size(); j++) {
                double x2 = terrain.get(j).getX();
                double y2 = terrain.get(j).getY();
                double m = ((y2 - y1) / (x2 - x1));
                //in case the gradient is greater than before we can add an edge in visGraph
                if (m >= m_last) {
                    if (terrain.get(i).getvType().contains("C") && !terrain.get(j).getvType().contains("C")) {
                        visGraph.addEdge(terrain.get(i), terrain.get(j));
                    }
                    if (!terrain.get(i).getvType().contains("C") && terrain.get(j).getvType().contains("C")) {
                        visGraph.addEdge(terrain.get(i), terrain.get(j));
                    }
                    m_last = m;
                }
            }

            Instant end = Instant.now();
            if(Duration.between(start, end).getSeconds()>=timeOut ){
                System.out.println("TimeLimit reached, empty graph returned");
                return new SimpleGraph<>();
            }
        }

        return visGraph;
    }



    /** Method that adds visibility based edges to an input graph only for those vertices in the input vertex set.
     * Used to generate partial visibility graphs of only orthogonal terrains and stepwise expanding them.
     *
     * @param graph visibility graph in which to add edges
     * @param terrainList ArrayList representation of orthogonal terrain (sorted by x values ascending)
     * @param vertexSet subset of vertices of the terrain for which to generate visibility information
     */
    static void visSubGraph(SimpleGraph<Vertex> graph, ArrayList<Vertex> terrainList, HashSet<Vertex> vertexSet) {
        if (!terrainList.containsAll(vertexSet)) {
            System.out.println("The vertex set provided is not a subset of the terrain. No edges added.");
            return;
        }
        if (!graph.getV().containsAll(terrainList)) {
            System.out.println("The terrainList provided does not match the vertices of the graph. No edges added.");
            return;
        }
        if(!TerrainBuild.isOrthogonal(terrainList)){
            System.out.println("The terrainList provided is non-orthogonal.");
            return;
        }
        if(!TerrainBuild.isFreeOfCollinearVertices(terrainList)){
            System.out.println("The terrainList provided is not free of subsequent collinear vertices.");
            return;
        }

        for (Vertex v : vertexSet) {
            double x1 = v.getX();
            double y1 = v.getY();
            int index_v = terrainList.indexOf(v);
            //left convex vertices can only see to the right plus one above them
            if (v.getvType().equals("LC")) {
                //add the reflex above v
                if (index_v > 0) {
                    graph.addEdge(v, terrainList.get(index_v - 1));
                }

                if (index_v + 1 < terrainList.size()) {
                    double m_last = ((terrainList.get(index_v + 1).getY() - y1) / (terrainList.get(index_v + 1).getX() - x1));
                    //go through all vertices right of the reflex
                    for (int j = index_v + 1; j < terrainList.size(); j++) {
                        double x2 = terrainList.get(j).getX();
                        double y2 = terrainList.get(j).getY();
                        double m = ((y2 - y1) / (x2 - x1));
                        //in case the gradient is greater than before we can add an edge in visGraph
                        if (m >= m_last) {
                            //only add edges between reflex and convex vertices
                            if (!terrainList.get(j).getvType().contains("C")) {
                                graph.addEdge(v, terrainList.get(j));
                            }
                            m_last = m;
                        }
                    }
                }
            } else if (v.getvType().equals("RC")) {
                //add reflex above v
                if (index_v < terrainList.size() - 1) {
                    graph.addEdge(v, terrainList.get(index_v + 1));
                }

                //check all vertices to left for visibility
                if (index_v - 1 >= 0) {
                    double m_last = ((terrainList.get(index_v - 1).getY() - y1) / (terrainList.get(index_v - 1).getX() - x1));
                    //go through all vertices left of the reflex
                    for (int j = index_v - 1; j >= 0; j--) {
                        double x2 = terrainList.get(j).getX();
                        double y2 = terrainList.get(j).getY();
                        double m = ((y2 - y1) / (x2 - x1));
                        //in case the gradient is less than before we can add an edge in visGraph
                        if (m <= m_last) {
                            if (!terrainList.get(j).getvType().contains("C")) {
                                graph.addEdge(v, terrainList.get(j));
                            }
                            m_last = m;
                        }
                    }
                }
            } else {
                if (index_v + 1 < terrainList.size()) {
                    double m_last = ((terrainList.get(index_v + 1).getY() - y1) / (terrainList.get(index_v + 1).getX() - x1));
                    //go through all vertices right of the reflex
                    for (int j = index_v + 1; j < terrainList.size(); j++) {
                        double x2 = terrainList.get(j).getX();
                        double y2 = terrainList.get(j).getY();
                        double m = ((y2 - y1) / (x2 - x1));
                        //in case the gradient is greater than before we can add an edge in visGraph
                        if (m >= m_last) {
                            //only add edges between reflex and convex vertices
                            if (terrainList.get(j).getvType().contains("C")) {
                                graph.addEdge(v, terrainList.get(j));
                            }
                            m_last = m;
                        }
                    }
                }
                if (index_v - 1 >= 0) {
                    double m_last = ((y1 - terrainList.get(index_v - 1).getY()) / (x1 - terrainList.get(index_v - 1).getX()));
                    //go through all vertices left of the reflex
                    for (int j = index_v - 1; j >= 0; j--) {
                        double x2 = terrainList.get(j).getX();
                        double y2 = terrainList.get(j).getY();
                        double m = ((y1 - y2) / (x1 - x2));
                        //in case the gradient is less than before we can add an edge in visGraph
                        if (m <= m_last) {
                            //only add edges between reflex and convex vertices
                            if (terrainList.get(j).getvType().contains("C")) {
                                graph.addEdge(v, terrainList.get(j));
                            }
                            m_last = m;
                        }
                    }
                }
            }
        }
    }


    //The following methods combined yield a slightly better algorithm for computing the bipartite visibility graph
    //(only used for testing purpose, not for experiments)

    /**
     * Method that sweeps through a previously split ArrayList representing an orthogonal terrain from left to right
     * and adds edges in the graph belonging to that ArrayList whenever a left convex vertex sees a reflex vertex.
     *
     * @param list_conv sublist of the ArrayList representing the orth. terrain that contains all left convex vertices
     * @param list_refl sublist of the ArrayList representing the orth. terrain that contains all left reflex vertices
     * @param main_list ArrayList representing the orth. terrain
     * @param graph     graph belonging to the main_list
     */
    private static void sweepLtoR(ArrayList<Vertex> list_conv, ArrayList<Vertex> list_refl, ArrayList<Vertex> main_list, SimpleGraph<Vertex> graph) {
        int k_itStart = 0; //this is a helpvar that stores from which point on the iteration is relevant
        for (int i = 0; i < list_conv.size(); i++) {
            double m = 0;
            double m_last = 0; //stores slope of the sightline from last iterationstep
            double x_conv = list_conv.get(i).getX(); //stores x value of convex
            double y_conv = list_conv.get(i).getY(); //stores y value of convex
            int ID_conv = list_conv.get(i).getID(); //stores ID of convex
            for (int k = k_itStart; k < list_refl.size(); k++) {
                double x_refl = list_refl.get(k).getX(); //stores x value of reflex
                double y_refl = list_refl.get(k).getY(); //stores y value of reflex
                //we only need to look at reflexes to the right of the current convex
                if (x_conv < x_refl) {
                    m = (y_refl - y_conv) / (x_refl - x_conv);
                    //if the slope of the sightline is equal or greater than the one from last iterationstep
                    if (m >= m_last) {
                        m_last = m; // set m_last for next iteration step
                        graph.addEdge(list_conv.get(i), list_refl.get(k)); //add edge to graph
                        //if m = m_last = 0, the reflex that's being added has same y value. If the next neighbor of said reflex
                        //is a reflex (of opposite type) it can be added to the neighborhood of conv as well!
                        if (m == 0) {
                            //if neighbor is also reflex (actually of opposite type) note that main_list.get(...ID-1) never
                            //runs out of bound, since the last Vertex in terrain is always either a RR or RC or LC, so not contained
                            //in list_refl (list of LR)
                            if (main_list.get(list_refl.get(k).getID() + 1).getvType().substring(1).equals("R")) {
                                graph.addEdge(list_conv.get(i), main_list.get(list_refl.get(k).getID() + 1)); //add opposite reflex as well
                            }
                        }
                    }
                } else {
                    //if the reflex is to the left of the current convex, it certainly is also to the left of the next
                    //convex (since they are sorted) so we can set k_itStart to the current Reflex and save iteration steps
                    //the further to the right we are with i
                    k_itStart = k; //gets overwritten as long as the first reflex is to the right of the current convex
                }
                //now all that's left to do is to add edges to the direct neighbors of current conv
                //if we're not looking at the first vertex in the terrain, add reflex directly above convex
                if (ID_conv > 0) {
                    graph.addEdge(list_conv.get(i), main_list.get(ID_conv - 1)); //add reflex directly above conv
                }
                //check if current LC is last vertex of terrain, if not add neighbor to the right if it's a reflex
                if (ID_conv < main_list.size() - 1) {
                    if (main_list.get(ID_conv + 1).getvType().substring(1).equals("R")) {
                        graph.addEdge(list_conv.get(i), main_list.get(ID_conv + 1)); //add vertex directly to the right of conv if it is reflex
                    }
                }
            }
        }
    }



    /**
     * Method that sweeps through a previously split ArrayList representing an orthogonal terrain from right to left
     * and adds edges in the graph belonging to that ArrayList whenever a left convex vertex sees a reflex vertex.
     *
     * @param list_conv sublist of the ArrayList representing the orth. terrain that contains all right convex vertices
     * @param list_refl sublist of the ArrayList representing the orth. terrain that contains all right reflex vertices
     * @param main_list ArrayList representing the orth. terrain
     * @param graph     graph belonging to the main_list
     */
    private static void sweepRtoL(ArrayList<Vertex> list_conv, ArrayList<Vertex> list_refl, ArrayList<Vertex> main_list, SimpleGraph<Vertex> graph) {
        int k_itStart = list_refl.size() - 1; //this is a helpvar that stores from which point on the iteration is relevant
        for (int i = list_conv.size() - 1; i >= 0; i--) {
            double m = 0;
            double m_last = 0; //stores slope of the sightline from last iterationstep
            double x_conv = list_conv.get(i).getX(); //stores x value of right convex
            double y_conv = list_conv.get(i).getY(); //stores y value of right convex
            int ID_conv = list_conv.get(i).getID(); //stores ID of right convex
            for (int k = k_itStart; k >= 0; k--) {
                double x_refl = list_refl.get(k).getX(); //stores x value of right reflex
                double y_refl = list_refl.get(k).getY(); //stores y value of right reflex
                //we only need to look at reflexes to the left of the current convex
                if (x_conv > x_refl) {
                    m = (y_refl - y_conv) / (x_refl - x_conv);
                    //if the slope of the sightline is equal or less than the one from last iterationstep
                    if (m <= m_last) {
                        m_last = m; // set m_last for next iteration step
                        graph.addEdge(list_conv.get(i), list_refl.get(k)); //add edge to graph
                        //if m = m_last = 0, the reflex that's being added has same y value. If the next neighbor of said reflex
                        //is a reflex (of opposite type) it can be added to the neighborhood of conv as well!
                        if (m == 0) {
                            //if neighbor is also reflex (actually of opposite type). note that main_list.get(...ID-1) never
                            //runs out of bound, since the first Vertex in terrain is always either a LR or LC or RC, so not contained
                            //in list_refl (list of RR)
                            if (main_list.get(list_refl.get(k).getID() - 1).getvType().substring(1).equals("R")) {
                                graph.addEdge(list_conv.get(i), main_list.get(list_refl.get(k).getID() - 1)); //add opposite reflex as well
                            }
                        }
                    }
                } else {
                    //if the reflex is to the right of the current convex, it certainly is also to the right of the next
                    //convex (since they are sorted) so we can set k_itStart to the current Reflex and save iteration steps
                    //the further to the left we are with i
                    k_itStart = k; //gets overwritten as long as the first reflex is to the left of the current convex
                }
                //now all that's left to do is to add edges to the direct neighbors of current conv
                //if we're not looking at the last vertex in the terrain, add reflex directly above convex
                if (ID_conv < main_list.size() - 1) {
                    graph.addEdge(list_conv.get(i), main_list.get(ID_conv + 1)); //add reflex directly above conv
                }
                //check if current RC is first vertex of terrain, if not add neighbor to the left if it's a reflex
                if (ID_conv > 0) {
                    if (main_list.get(ID_conv - 1).getvType().substring(1).equals("R")) {
                        graph.addEdge(list_conv.get(i), main_list.get(ID_conv - 1)); //add vertex directly to the left of conv if it is reflex
                    }
                }
            }
        }
    }


    /**
     * Method that takes as input a ArrayList representing an orthogonal terrain and returns a graph containing the vertices
     * stored in the ArrayList as well as edges between all convex and reflex vertices that see each other. Uses somewhat
     * advanced methods compared to naive sweep.
     *
     * @param terrain ArrayList representing an orthogonal terrain
     * @return graph containing all vertices stored in @terrain as well as edges between all convex and reflex vertices
     * that see each other, empty graph if input terrain does not match criteria
     */
    static SimpleGraph<Vertex> advancedVisibilityGraphRB(ArrayList<Vertex> terrain) {
        if(!TerrainBuild.isOrthogonal(terrain) || TerrainBuild.isFreeOfCollinearVertices(terrain)){
            System.out.println("Advanced red-blue graph not created. Terrain either non orthogonal or contains consecutive collinear vertices");
            return new SimpleGraph<>();
        }

        //initialize the visibility graph
        SimpleGraph<Vertex> visGraph = new SimpleGraph<>();
        for (Vertex v : terrain) {
            visGraph.addVertex(v);
        }
        ArrayList<Vertex> list_LR = new ArrayList<>();
        ArrayList<Vertex> list_LC = new ArrayList<>();
        ArrayList<Vertex> list_RR = new ArrayList<>();
        ArrayList<Vertex> list_RC = new ArrayList<>();
        //The following filter method maintains the ordering of the terrain!
        terrain.stream().filter(v -> v.getvType().equals("LR")).forEach(list_LR::add);
        terrain.stream().filter(v -> v.getvType().equals("LC")).forEach(list_LC::add);
        terrain.stream().filter(v -> v.getvType().equals("RR")).forEach(list_RR::add);
        terrain.stream().filter(v -> v.getvType().equals("RC")).forEach(list_RC::add);

        //Sweep terrain in both directions and add edges to the graph according to visibility between vertices
        sweepLtoR(list_LC, list_LR, terrain, visGraph);
        sweepRtoL(list_RC, list_RR, terrain, visGraph);

        return visGraph;
    }



    //method that checks if a graph is bipartite
    static boolean isBipartite(SimpleGraph<Vertex> graph) {
        return GraphProperty.INSTANCE.isBipartite(graph);
    }

    //unused method that stores entire visibility graphs to .txt file
    static void visGraphtotxt(String filePath, SimpleGraph<Vertex> graph) {

        try {

            FileWriter visGraph = new FileWriter(filePath);

            Iterator<Pair<Vertex, Vertex>> edges = graph.edgeIterator();
            while (edges.hasNext()) {
                Pair<Vertex, Vertex> currEdge = edges.next();
                visGraph.write(currEdge.getFirst().getID() + " " + currEdge.getFirst().getX() + " " +
                        currEdge.getFirst().getY() + " " + currEdge.getFirst().getvType() + " " +
                        currEdge.getSecond().getID() + " " + currEdge.getSecond().getX() + " " +
                        currEdge.getSecond().getY() + " " + currEdge.getSecond().getvType() + "\n");
            }
            visGraph.close();
        } catch (IOException err) {
            System.out.println("The following Error occurred when writing the file: " + err);
        }


    }


    //unused method that reads entire visibility graph from .txt file
    static SimpleGraph<Vertex> readVisGraphtxt(String filePath) throws IOException{

        //init visGraph
        SimpleGraph<Vertex> visGraph = new SimpleGraph<>();

        //read in file
        File graphFile = new File(filePath);

        Scanner graphFileScan = new Scanner(graphFile);
        HashSet<Integer> IDset = new HashSet<>();
        HashMap<Integer, Vertex> vertices = new HashMap<>();

        //go through file line by line and add vertices to graph
        while (graphFileScan.hasNextLine()) {
            String[] vert = graphFileScan.nextLine().split(" ");
            Vertex v1 = new Vertex(Integer.parseInt(vert[0]) ,Double.parseDouble(vert[1]), Double.parseDouble(vert[2]), vert[3]);
            Vertex v2 = new Vertex(Integer.parseInt(vert[4]) ,Double.parseDouble(vert[5]), Double.parseDouble(vert[6]), vert[7]);
            if(!IDset.contains(v1.getID())){
                vertices.put(v1.getID(), v1);
                IDset.add(v1.getID());
            }else{
                v1 = vertices.get(v1.getID());
            }
            if(!IDset.contains(v2.getID())){
                vertices.put(v2.getID(), v2);
                IDset.add(v2.getID());
            }else{
                v2 = vertices.get(v2.getID());
            }

            if(!visGraph.contains(v1)){
                visGraph.addVertex(v1);
            }
            if(!visGraph.contains(v2)){
                visGraph.addVertex(v2);
            }
            visGraph.addEdge(v1, v2);

        }
        graphFileScan.close();

        return visGraph;
    }


}