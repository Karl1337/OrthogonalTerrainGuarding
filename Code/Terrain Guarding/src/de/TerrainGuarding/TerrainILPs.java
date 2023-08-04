package de.TerrainGuarding;

import gurobi.*;
import sipura.graphs.SimpleGraph;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


public class TerrainILPs {

    /** Method that solves discrete orthogonal terrain guarding for an orthogonal terrain with gurobi by solving red-blue dominating set in its
     * bipartite visibility graph
     *
     * @param graph bipartite visibility graph of orthogonal terrain
     * @return set of guards that guard the whole terrain
     */
    static AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> SolveTerrainGurobiRB(SimpleGraph<Vertex> graph) {

        try {
            //Create empty Gurobi environment, setup logfile name and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "test1.log");
            env.start();
            Instant start = Instant.now(); //start timer

            //Create an empty model
            GRBModel model = new GRBModel(env);
            HashMap<Vertex, GRBVar> vars = new HashMap<>();
            //GRBVar[] vars = new GRBVar[graph.getN()];
            //add all vertices to the model and set up a variable Array
            for (Vertex v : graph.getV()) {
                if(!v.getvType().contains("C")) {
                    vars.put(v, model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + v.getID()));
                }
            }

            GRBLinExpr exprReflNeighs;
            GRBLinExpr exprObj = new GRBLinExpr();

            for (Vertex v : graph.getV()) {
                //if the current vertex is a reflex add it to objective expression
                if (!v.getvType().contains("C")) {
                    exprObj.addTerm(1.0, vars.get(v));
                } else {
                    //if it is a convex put all its reflex neighbors in a constraint
                    exprReflNeighs = new GRBLinExpr();
                    for (Vertex refl : graph.neighbors(v)) {
                        exprReflNeighs.addTerm(1.0, vars.get(refl));
                    }
                    model.addConstr(exprReflNeighs, GRB.GREATER_EQUAL, 1.0, "c_" + v.getID());
                }
            }
            //set objective function
            model.setObjective(exprObj, GRB.MINIMIZE);


            //disable printing log info to console
            model.set(GRB.IntParam.LogToConsole, 0);

            //set the allowed gap small enough to guarantee exact solution
            model.set(GRB.DoubleParam.MIPGap, 1 / (double) graph.getN());

            //set thread count to 1
            env.set(GRB.IntParam.Threads, 1);

            //update model
            model.update();


            //run optimization
            model.optimize();
            Instant end = Instant.now(); //stop timer


            //Store the selected guards into HashSet
            HashSet<Vertex> guards = new HashSet<>();
            for (Vertex v : vars.keySet()) {
                String varName = "x" + v.getID();
                if (model.getVarByName(varName).get(GRB.DoubleAttr.X) >= 0.99) {
                    guards.add(v);
                }
            }

            model.dispose();
            env.dispose();

            Duration runTime = Duration.between(start, end);
            System.out.println("time for optimization: " + runTime);

            return new AbstractMap.SimpleEntry<>(guards, runTime);

        } catch (GRBException exc) {
            System.out.println(exc.getErrorCode() + " : " + exc.getMessage());
            System.out.println("Error in Gurobi");
        }
        return new AbstractMap.SimpleEntry<>(new HashSet<>(), Duration.ZERO);
    }


    /** Method that solves discrete terrain guarding for a terrain with gurobi by solving dominating set in its visibility graph
     *
     * @param graph visibility graph of terrain
     * @return set of guards that guard the whole terrain
     */
    static AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> SolveTerrainGurobiFullVis(SimpleGraph<Vertex> graph) {

        try {
            //Create empty Gurobi environment, setup logfile name and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "test1.log");
            env.start();
            Instant start = Instant.now(); //start timer

            //Create an empty model
            GRBModel model = new GRBModel(env);
            HashMap<Integer, GRBVar> vars = new HashMap<>();
            //GRBVar[] vars = new GRBVar[graph.getN()];
            //add all vertices to the model and set up a variable Array
            for (Vertex v : graph.getV()) {
                vars.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + v.getID()));
            }

            GRBLinExpr exprNeighs;
            GRBLinExpr exprObj = new GRBLinExpr();

            for (Vertex v : graph.getV()) {
                //add vertex to objective expression
                exprObj.addTerm(1.0, vars.get(v.getID()));
                //put all neighbors in a constraint
                exprNeighs = new GRBLinExpr();
                exprNeighs.addTerm(1.0, vars.get(v.getID()));
                for (Vertex vert : graph.neighbors(v)) {
                    exprNeighs.addTerm(1.0, vars.get(vert.getID()));
                }
                model.addConstr(exprNeighs, GRB.GREATER_EQUAL, 1.0, "c_" + v.getID());
            }
            //set objective function
            model.setObjective(exprObj, GRB.MINIMIZE);


            //disable printing log info to console
            model.set(GRB.IntParam.LogToConsole, 0);

            //set the allowed gap small enough
            model.set(GRB.DoubleParam.MIPGap, 1 / (double) graph.getN());

            //set thread count to 1
            env.set(GRB.IntParam.Threads, 1);

            //update model
            model.update();


            //run optimization
            model.optimize();
            Instant end = Instant.now(); //end timer



            //Store the selected guards into HashSet
            HashSet<Vertex> guards = new HashSet<>();
            for (Vertex v : graph.getV()) {
                String varName = "x" + v.getID();
                if (model.getVarByName(varName).get(GRB.DoubleAttr.X) >= 0.99) {
                    guards.add(v);
                }
            }

            model.dispose();
            env.dispose();

            Duration runTime = Duration.between(start, end);
            System.out.println("time for optimization: " + runTime);

            return new AbstractMap.SimpleEntry<>(guards, runTime);

        } catch (GRBException exc) {
            System.out.println(exc.getErrorCode() + " : " + exc.getMessage());
            System.out.println("FEHLER");
        }
        return new AbstractMap.SimpleEntry<>(new HashSet<>(), Duration.ZERO);
    }


    /** Method that solves discrete orthogonal terrain guarding for an orthogonal terrain with gurobi by solving dominating set in its
     * full visibility graph.
     *
     * @param graph full visibility graph of orthogonal terrain
     * @return set of guards that guard the whole terrain
     */
    static AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> SolveTerrainGurobiFullVisReflOnly(SimpleGraph<Vertex> graph) {

        try {
            //Create empty Gurobi environment, setup logfile name and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "test2.log");
            env.start();
            Instant start = Instant.now(); //start timer

            //Create an empty model
            GRBModel model = new GRBModel(env);
            HashMap<Integer, GRBVar> vars = new HashMap<>();
            //GRBVar[] vars = new GRBVar[graph.getN()];
            //add all vertices to the model and set up a variable Array
            for (Vertex v : graph.getV()) {
                vars.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + v.getID()));
            }

            GRBLinExpr exprNeighs;
            GRBLinExpr exprObj = new GRBLinExpr();
            GRBLinExpr exprConvexConstr = new GRBLinExpr();

            for (Vertex v : graph.getV()) {
                if (v.getvType().contains("C")) {
                    exprConvexConstr.addTerm(1.0, vars.get(v.getID()));
                }
                //add vertex to objective expression
                exprObj.addTerm(1.0, vars.get(v.getID()));
                //put all neighbors in a constraint
                exprNeighs = new GRBLinExpr();
                exprNeighs.addTerm(1.0, vars.get(v.getID()));
                for (Vertex vert : graph.neighbors(v)) {
                    exprNeighs.addTerm(1.0, vars.get(vert.getID()));
                }
                model.addConstr(exprNeighs, GRB.GREATER_EQUAL, 1.0, "c_" + v.getID());
            }

            model.addConstr(exprConvexConstr, GRB.EQUAL, 0.0, "noConv");

            //set objective function
            model.setObjective(exprObj, GRB.MINIMIZE);


            //disable printing log info to console
            model.set(GRB.IntParam.LogToConsole, 0);

            //set the allowed gap small enough
            model.set(GRB.DoubleParam.MIPGap, 1 / (double) graph.getN());

            //set thread count to 1
            env.set(GRB.IntParam.Threads, 1);

            //update model
            model.update();


            //run optimization
            model.optimize();
            Instant end = Instant.now();





            //Store the selected guards into HashSet
            HashSet<Vertex> guards = new HashSet<>();
            for (Vertex v : graph.getV()) {
                String varName = "x" + v.getID();
                if (model.getVarByName(varName).get(GRB.DoubleAttr.X) >= 0.99) {
                    guards.add(v);
                }
            }

            model.dispose();
            env.dispose();

            Duration runTime = Duration.between(start, end);
            System.out.println("time for optimization: " + runTime);

            return new AbstractMap.SimpleEntry<>(guards, runTime);

        } catch (GRBException exc) {
            System.out.println(exc.getErrorCode() + " : " + exc.getMessage());
            System.out.println("FEHLER");
        }
        return new AbstractMap.SimpleEntry<>(new HashSet<>(), Duration.ZERO);
    }


    //unused, test for partial cover (cover as much of the terrain as possible with only guardnum guards)
    static HashSet<Vertex> SolveTerrainGurobiPartialCover(SimpleGraph<Vertex> graph, int guardnum) {

        HashSet<Vertex> guards = new HashSet<>();
        try {
            //Create empty Gurobi environment, setup logfile name and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "test3.log");
            env.start();
            int i = 0;
            //Create an empty model
            GRBModel model = new GRBModel(env);

            HashMap<Integer, GRBVar> vertvars = new HashMap<>();
            HashMap<Integer, GRBVar> guardvars = new HashMap<>();


            //add all vertices to the model twice and set up a variable Array
            for (Vertex v : graph.getV()) {
                vertvars.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x_" + v.getID()));
                guardvars.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z_" + v.getID()));
            }

            GRBQuadExpr exprNeighs;
            GRBLinExpr exprObj = new GRBLinExpr();
            GRBLinExpr exprGuardBound = new GRBLinExpr();

            //add constraints
            for (Vertex v : graph.getV()) {
                //add vertex to objective expression
                exprObj.addTerm(1.0, vertvars.get(v.getID()));
                exprGuardBound.addTerm(1.0, guardvars.get(v.getID()));
                //put all neighbors in a constraint: sum_(vert in V: vert in N(v)) x_v * z_v = 0
                exprNeighs = new GRBQuadExpr();
                for (Vertex vert : graph.neighbors(v)) {
                    exprNeighs.addTerm(1.0, vertvars.get(vert.getID()), guardvars.get(v.getID()));
                }
                model.addQConstr(exprNeighs, GRB.EQUAL, 0.0, "c_" + v.getID());
            }
            double bound = (double) graph.getN() - (double) guardnum;
            model.addConstr(exprGuardBound, GRB.EQUAL, bound, "c_guards");


            //set objective function
            model.setObjective(exprObj, GRB.MINIMIZE);

            //update model
            model.update();

            //disable printing log info to console
            model.set(GRB.IntParam.LogToConsole, 0);

            //set the allowed gap small enough
            model.set(GRB.DoubleParam.MIPGap, 1 / (double) graph.getN());

            Instant start = Instant.now();
            //run optimization
            model.optimize();
            Instant end = Instant.now();

            /*
            for (GRBVar x : model.getVars()) {
                System.out.println(x.get(GRB.StringAttr.VarName)
                        + " " + x.get(GRB.DoubleAttr.X));
            }
             */

            //System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            //Store the selected guards into HashSet
            for (Vertex v : graph.getV()) {
                String varName = "z_" + v.getID();
                if (model.getVarByName(varName).get(GRB.DoubleAttr.X) <= 0.1) {
                    guards.add(v);
                }
            }


            model.dispose();
            env.dispose();

            System.out.println("time for optimization: " + Duration.between(start, end));

        } catch (GRBException exc) {
            System.out.println(exc.getErrorCode() + " : " + exc.getMessage());
            System.out.println("FEHLER");
        }
        return guards;
    }


    //unused, test for partial cover (cover as much of the terrain as possible with only guardnum guards)
    static HashSet<Vertex> SolveTerrainGurobiPartialCover2(SimpleGraph<Vertex> graph, int guardnum) {

        HashSet<Vertex> guards = new HashSet<>();
        try {
            //Create empty Gurobi environment, setup logfile name and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "test3.log");
            env.start();

            //Create an empty model
            GRBModel model = new GRBModel(env);
            HashMap<Integer, GRBVar> vertvars = new HashMap<>();
            HashMap<Integer, GRBVar> guardvars = new HashMap<>();
            HashMap<Integer, GRBVar> vertVarsMinus = new HashMap<>();
            //HashMap<Integer, GRBVar> guardVarsMinus = new HashMap<>();
            HashMap<Integer, GRBVar> vertAbs = new HashMap<>();
            //HashMap<Integer, GRBVar> guardAbs = new HashMap<>();


            //GRBVar[] vars = new GRBVar[graph.getN()];
            //add all vertices to the model twice and set up a variable Array
            for (Vertex v : graph.getV()) {
                vertvars.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x_" + v.getID()));
                guardvars.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "z_" + v.getID()));

                //add helpvars for minus
                vertVarsMinus.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "vertMinus_" + v.getID()));
                //guardVarsMinus.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "guardMinus_" + v.getID()));

                GRBLinExpr vertMinus = new GRBLinExpr();
                vertMinus.addTerm(1.0, vertvars.get(v.getID()));
                vertMinus.addTerm(-1.0, vertVarsMinus.get(v.getID()));
                model.addConstr(vertMinus, GRB.EQUAL, 0.0, "vertMinus_" + v.getID());

                //GRBLinExpr guardMinus = new GRBLinExpr();
                //vertMinus.addTerm(1.0, guardvars.get(v.getID()));
                //vertMinus.addTerm(-1.0, guardVarsMinus.get(v.getID()));
                //model.addConstr(guardMinus, GRB.EQUAL, 0.0, "guardMinus_" + v.getID());

                //add helpvars for abs
                vertAbs.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.CONTINUOUS, "vertAbs_" + v.getID()));
                //guardAbs.put(v.getID(), model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "guardAbs_" + v.getID()));

                model.addGenConstrAbs(vertAbs.get(v.getID()), vertVarsMinus.get(v.getID()), "AbsVert_" + v.getID());
                //model.addGenConstrAbs(guardAbs.get(v.getID()), guardVarsMinus.get(v.getID()), "AbsGuard_" + v.getID());


            }

            //init expressions that contain will contain sum_(vert in V: vert in N(v)) |x_vert - 1| * z_v = 0
            //and sum_(vert in V: vert in N(v)) x_vert  * |z_v - 1| = 0
            GRBQuadExpr exprNeighsvert;
            //GRBQuadExpr exprNeighsguard;
            GRBLinExpr exprObj = new GRBLinExpr();
            GRBLinExpr exprGuardBound = new GRBLinExpr();

            //add constraints
            for (Vertex v : graph.getV()) {
                //add vertex to objective expression
                exprObj.addTerm(1.0, vertvars.get(v.getID()));
                //setup guard bound
                exprGuardBound.addTerm(1.0, guardvars.get(v.getID()));

                //put all neighbors in a constraint: sum_(vert in V: vert in N(v)) |x_vert - 1| * z_v = 0
                exprNeighsvert = new GRBQuadExpr();
                //exprNeighsguard = new GRBQuadExpr();
                for (Vertex vert : graph.neighbors(v)) {
                    exprNeighsvert.addTerm(1.0, vertAbs.get(vert.getID()), guardvars.get(v.getID()));
                    //exprNeighsguard.addTerm(1.0, vertvars.get(vert.getID()) , guardAbs.get(v.getID()));
                }
                //add constrains
                model.addQConstr(exprNeighsvert, GRB.EQUAL, 0.0, "c_" + v.getID());
                //model.addQConstr(exprNeighsguard, GRB.EQUAL, 0.0, "c_" + v.getID());
            }

            //tell gurobi to use exactly k guards
            model.addConstr(exprGuardBound, GRB.EQUAL, guardnum, "c_guards");

            //set objective function
            model.setObjective(exprObj, GRB.MINIMIZE);

            //update model
            model.update();

            //disable printing log info to console
            model.set(GRB.IntParam.LogToConsole, 0);

            //set the allowed gap small enough
            model.set(GRB.DoubleParam.MIPGap, 1 / (double) graph.getN());

            Instant start = Instant.now();
            //run optimization
            model.optimize();
            Instant end = Instant.now();

            /*
            for (GRBVar x : model.getVars()) {
                System.out.println(x.get(GRB.StringAttr.VarName)
                        + " " + x.get(GRB.DoubleAttr.X));
            }
             */

            //System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            //Store the selected guards into HashSet
            for (Vertex v : graph.getV()) {
                String varName = "z_" + v.getID();
                if (model.getVarByName(varName).get(GRB.DoubleAttr.X) > 0.999) {
                    guards.add(v);
                }
            }


            //count amount of vertices covered
            HashSet<Vertex> coveredVerts = new HashSet<>();
            for (Vertex v : guards) {
                coveredVerts.addAll(graph.neighbors(v));
            }
            System.out.println("Covered " + coveredVerts.size() + " out of " + graph.getN() + " vertices using " + guards.size() + " guards.");

            model.dispose();
            env.dispose();

            System.out.println("time for optimization: " + Duration.between(start, end));

        } catch (GRBException exc) {
            System.out.println(exc.getErrorCode() + " : " + exc.getMessage());
            System.out.println("FEHLER");
        }
        return guards;
    }


    /** Method that solves discrete orthogonal terrain guarding for an orthogonal terrain with gurobi and the use of callbacks.
     * The callbacks are implemented, such that the visibility graph gets only computed partially in each step, instead of
     * computing it entirely in the beginning
     *
     * @param terrainList ArrayList representation of terrain
     * @param addProb probability for adding convex vertices (for which their subgraph in the visibility graph gets computed) in each callback step
     * @param relationConvNonGuard if NumConvexVerticesUnguarded / totalNumConvexVertices < relationConvNonGuard, the algorithm adds all unguarded convex vertices to the set for which the subgraph of the visibility graph gets computed
     * @return Set that contains the selected guards and String that contains the runTime, the number of convex vertices visited, the number of reflex vertices visited, number of computed edges in the visibility graph, the number of callback calls and whether or not the terrain is guarded in the end.
     */
    static AbstractMap.SimpleEntry<HashSet<Vertex>, String> SolveTerrainGurobiWithCallback(ArrayList<Vertex> terrainList, double addProb, double relationConvNonGuard) {

        try {
            //Create empty Gurobi environment, setup logfile name and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "test1.log");
            env.start();
            Instant start = Instant.now(); //start timer

            //Create an empty model
            GRBModel model = new GRBModel(env);

            //allow adding lazies
            model.set(GRB.IntParam.LazyConstraints, 1);

            //init hashmap for gurobi vars
            HashMap<Vertex, GRBVar> vars = new HashMap<>();

            //init objective expression
            GRBLinExpr exprObj = new GRBLinExpr();

            //setup set of all convex vertices
            HashSet<Vertex> convexVertices = new HashSet<>();

            //init visibility graph and add all vertices to the model and set up a variable set and update objective expression
            SimpleGraph<Vertex> visGraph = new SimpleGraph<>();
            for (Vertex v : terrainList) {
                visGraph.addVertex(v);

                //add reflex vertices to model and objective expression
                if (!v.getvType().contains("C")) {
                    vars.put(v, model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "x" + v.getID()));
                    exprObj.addTerm(1.0, vars.get(v));
                }else{
                    //add convex vertices to set
                    convexVertices.add(v);
                }
            }

            //set objective function
            model.setObjective(exprObj, GRB.MINIMIZE);
            System.out.println("size objective: " + exprObj.size());

            //Setup first set

            //read local minima
            HashSet<Vertex> localMins = Reduction.getLocalMins(terrainList);

            //always add first and last vertex, they are sometimes hard to get to
            if (terrainList.get(0).getvType().contains("C")) {
                localMins.add(terrainList.get(0));
            }
            if (terrainList.get(terrainList.size() - 1).getvType().contains("C")) {
                localMins.add(terrainList.get(terrainList.size() - 1));
            }

            //update visGraph (add all edges of localMins)
            VisibilityMethods.visSubGraph(visGraph, terrainList, localMins);


            //add initial constraints
            for (Vertex v : localMins) {
                GRBLinExpr exprReflNeighs = new GRBLinExpr();
                for (Vertex refl : visGraph.neighbors(v)) {
                    exprReflNeighs.addTerm(1.0, vars.get(refl));
                }
                model.addConstr(exprReflNeighs, GRB.GREATER_EQUAL, 1.0, "c_" + v.getID());
            }


            //disable printing log info to console
            model.set(GRB.IntParam.LogToConsole, 0);

            //set the allowed gap small enough to guarantee exact solution
            model.set(GRB.DoubleParam.MIPGap, 1 / (double) visGraph.getN());

            //set thread count to 1
            env.set(GRB.IntParam.Threads, 1);

            //setup set that stores visited reflex vertices for callback method
            HashSet<Vertex> reflexVisited = new HashSet<>();

            //setup counter for visited convex vertices
            int visConvCount = localMins.size();

            //setup call counter
            int calls = 0;

            //add lazyConstraints with callback
            CallbackOTG cb = new CallbackOTG(visGraph, vars, terrainList, addProb, reflexVisited, convexVertices, relationConvNonGuard, visConvCount, calls);
            model.setCallback(cb);

            //update model
            model.update();


            //run optimization
            model.optimize();
            Instant end = Instant.now();


            //Store the selected guards into HashSet
            HashSet<Vertex> guards = new HashSet<>();
            for (Vertex v : vars.keySet()) {
                String varName = "x" + v.getID();
                if (model.getVarByName(varName).get(GRB.DoubleAttr.X) >= 0.99) {
                    guards.add(v);
                }
            }

            //save number of edges in visGraph
            int edgeNumVis = visGraph.getM();

            //dispose model
            model.dispose();
            env.dispose();


            System.out.println("Added information of reflex vertices: " + reflexVisited.size());

            Duration runTime = Duration.between(start, end);
            System.out.println("time for optimization: " + runTime.toString());

            //check if terrain is guarded
            boolean isGuarded = CheckGuarding.isGuardedRB(visGraph, guards);

            //build output string
            String outputString = runTime + ";" + visConvCount + ";" + reflexVisited.size() + ";" + edgeNumVis + ";" + calls + ";" + isGuarded;

            return new AbstractMap.SimpleEntry<>(guards, outputString);

        } catch (GRBException exc) {
            System.out.println(exc.getErrorCode() + " : " + exc.getMessage());
            System.out.println("Error in Gurobi");
        }
        return new AbstractMap.SimpleEntry<>(new HashSet<>(), Duration.ZERO.toString());
    }
}
