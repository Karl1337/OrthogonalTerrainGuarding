package de.TerrainGuarding;

import gurobi.*;
import sipura.graphs.SimpleGraph;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class CallbackOTG extends GRBCallback {
    private SimpleGraph<Vertex> visGraph;
    //private FileWriter logfile;
    private HashMap<Vertex, GRBVar > vertexVars;

    private ArrayList<Vertex> terrainList;
    private  HashSet<Vertex> reflexVisited;
    private  HashSet<Vertex> convexVertices;
    double probAdd;
    double relationConvNonGuard;
    int visConvCount;
    int calls;

    public CallbackOTG(SimpleGraph<Vertex> visGraph, HashMap<Vertex, GRBVar> vertexVars, ArrayList<Vertex> terrainList,
                       double probAdd, HashSet<Vertex> reflexVisited, HashSet<Vertex> convexVertices,
                       double relationConvNonGuard, int visConvCount, int calls){
        this.visGraph = visGraph;
        this.vertexVars = vertexVars;
        this.terrainList = terrainList;
        this.probAdd = probAdd;
        this.reflexVisited = reflexVisited;
        this.convexVertices = convexVertices;
        this.relationConvNonGuard = relationConvNonGuard;
        this.visConvCount = visConvCount;
        this.calls = calls;
    }
    protected void callback(){
        try{
            if(where==GRB.Callback.MIPSOL){
                //count calls
                calls ++;
                //init subset (will include all vertices of guards that have not been visited before)
                HashSet<Vertex> subset = new HashSet<>();
                //retrieve guards (only check the reflex positions)
                HashSet<Vertex> guards = new HashSet<>();
                for(Vertex refl: vertexVars.keySet()){
                    if(!refl.getvType().contains("C") && this.getSolution(vertexVars.get(refl)) > 0.99){
                        guards.add(refl);
                        //in case the reflex was not updated in visGraph already, add him to visited and to subset
                        if(!reflexVisited.contains(refl)) {
                            reflexVisited.add(refl);
                            subset.add(refl);
                        }
                    }
                }
                //update visGraph by computing all edges of the guards, skipping the ones that were visited already
                VisibilityMethods.visSubGraph(visGraph, terrainList, subset);

                //compute set of unguarded convex vertices
                HashSet<Vertex> notGuarded = CheckGuarding.notGuardedSet(visGraph, guards, convexVertices);

                //in case the terrain is not fully guarded already
                if(!notGuarded.isEmpty()){
                    //randomly choose not guarded vertices whose edges get added to visGraph
                    HashSet<Vertex> toAdd = new HashSet<>();
                    //in case less than relationConvNonGuard(%) of the convex vertices are uncovered, we can add all of them at
                    //once to avoid many callbacks
                    if((double) notGuarded.size()/convexVertices.size() < relationConvNonGuard ){
                        toAdd.addAll(notGuarded);
                    }else {
                        for (Vertex conv : notGuarded) {
                            if (Math.random() < probAdd) {
                                toAdd.add(conv);
                            }
                        }
                        //in case notGuarded is not empty, but toAdd is due to bad luck, add all vertices in notGuarded to toAdd
                        if (toAdd.isEmpty()) {
                            toAdd.addAll(notGuarded);
                        }
                    }

                    //update visGraph by adding edges of those convex vertices
                    VisibilityMethods.visSubGraph(visGraph, terrainList, toAdd);

                    //update convexVisited counter
                    visConvCount = visConvCount + toAdd.size();

                    //now add LazyConstraints based on their new neighborhoods
                    for(Vertex conv: toAdd){
                        GRBLinExpr lazy = new GRBLinExpr();
                        for(Vertex refl: visGraph.neighbors(conv)){
                            lazy.addTerm(1.0, vertexVars.get(refl));
                        }
                        //neighbors must add to at least one
                        this.addLazy(lazy, GRB.GREATER_EQUAL, 1.0);
                    }
                }
            }
        }catch(GRBException err){
            System.out.println("Error during Callback: " + err.getMessage());
        }
    }
}
