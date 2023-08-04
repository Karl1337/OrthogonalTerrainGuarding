package de.TerrainGuarding;

import sipura.graphs.SimpleGraph;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;


public class runExperiments {
    public static void main(String[] args) {

        //["java", "-jar", "TerrainGuarding.jar", output_file, graph_file,
        //        algo_type, str(problem_id), str(time_limit), max_steps, degCapIntersect, degCapSubsets, relaxParam]

        //["java", "-jar", "TerrainGuarding.jar", output_file, graph_file,
        //        algo_type, str(problem_id), str(time_limit), max_steps, degCapIntersect, degCapSubsets, relaxParam]

        String output_file = args[0]; //stores path to output file
        String graph_file = args[1]; //stores filename of terrain-file (whole path)
        String algo_type = args[2]; //type of algo
        long time_limit = Long.parseLong(args[4]); //timeLimit in seconds
        int max_steps = Integer.parseInt(args[5]); //max steps only relevant for reduction
        int degCapIntersect = Integer.parseInt(args[6]); //only relevant for reduction
        int degCapSubset = Integer.parseInt(args[7]); //only relevant for reduction
        double relaxParam = Double.parseDouble(args[8]); //only relevant for reduction
        double addProb = Double.parseDouble(args[9]); //only relevant for callbacks
        double relationConv = Double.parseDouble(args[10]); //only relevant for callbacks

        System.out.println("Started working on algo " + algo_type + " on file " + graph_file);

        if (algo_type.equals("solveRBVanilla")) {

            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;Terrain free of consecutive collinear vertices;" +
                            "Number reflex vertices;Number convex vertices;Number vertices;Number edges in visibility graph;" +
                            "Time for computing visibility graph;Time Gurobi took to solve;Number of selected guards;Terrain guarded" + "\n");
                }




                //read terrain
                ArrayList<Vertex> terrain = FileReadAndWrite.readOrthTerrainTxt(graph_file);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal
                boolean coll = TerrainBuild.isFreeOfCollinearVertices(terrain); //check if free of consecutive collinear vertices

                //count convex and reflex vertices
                int convCount = 0;
                int reflCount = 0;
                for (Vertex v : terrain) {
                    if (v.getvType().contains("C")) {
                        convCount++;
                    } else {
                        reflCount++;
                    }
                }

                //save total number of vertices
                int vertexCount = terrain.size();


                //create RB visibility graph and stop time
                Instant startVis = Instant.now();
                SimpleGraph<Vertex> visGraph = VisibilityMethods.bipartiteVisGraphOrth(terrain, time_limit);
                Instant endVis = Instant.now();
                long timeForVis = Duration.between(startVis, endVis).toMillis();

                //save number of edges in visibility graph
                int edgeCount = visGraph.getM();

                //solve discrete orthogonal terrain guarding with gurobi
                AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionGurobi = TerrainILPs.SolveTerrainGurobiRB(visGraph);

                //check if terrain is guarded
                boolean isGuarded = CheckGuarding.isGuardedRB(visGraph, solutionGurobi.getKey());

                //save number of selected guards
                int guardNum = solutionGurobi.getKey().size();

                //save time Gurobi took to solve
                long timeForGurobi = solutionGurobi.getValue().toMillis();


                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";" + coll + ";" + reflCount + ";" +
                        convCount + ";" + vertexCount + ";" + edgeCount + ";" + timeForVis + ";" + timeForGurobi + ";" +
                        guardNum + ";" + isGuarded + "\n");

                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


        if (algo_type.equals("solveRBReduction")) {
            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;Terrain free of consecutive collinear vertices;" +
                            "Number reflex vertices;Number convex vertices;Number vertices;Number edges in visibility graph;" +
                            "Time for computing visibility graph;Time Gurobi took to solve;Number of selected guards;Terrain guarded;" +
                            "Number vertices after reduction;Number edges after reduction;Number Guards selected by reduction;" +
                            "Number guards selected by gurobi;Steps reduction;max_steps;degCapIntersection;degCapSubsets;timeForReduction" + "\n");
                }




                //read terrain
                ArrayList<Vertex> terrain = FileReadAndWrite.readOrthTerrainTxt(graph_file);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal
                boolean coll = TerrainBuild.isFreeOfCollinearVertices(terrain); //check if free of consecutive collinear vertices

                //count convex and reflex vertices
                int convCount = 0;
                int reflCount = 0;
                for (Vertex v : terrain) {
                    if (v.getvType().contains("C")) {
                        convCount++;
                    } else {
                        reflCount++;
                    }
                }

                //save total number of vertices
                int vertexCountBefore = terrain.size();


                //create RB visibility graph and stop time
                Instant startVis = Instant.now();
                SimpleGraph<Vertex> visGraph = VisibilityMethods.bipartiteVisGraphOrth(terrain, time_limit);
                Instant endVis = Instant.now();
                long timeForVis = Duration.between(startVis, endVis).toMillis();

                //save number of edges in visibility graph
                int edgeCountBefore = visGraph.getM();

                //reduce graph
                Instant startReduc = Instant.now();
                AbstractMap.SimpleEntry<Integer, HashSet<Vertex>> guardsReduc = Reduction.reduceBipartiteGraphRelax(visGraph, max_steps, degCapIntersect, degCapSubset, relaxParam, 300);
                Instant endReduc = Instant.now();

                //save edge and vertex count after reduction
                int vertexCountAfter = visGraph.getN();
                int edgeCountAfter = visGraph.getM();

                //save time for reduction
                long timeForReduc = Duration.between(startReduc, endReduc).toMillis();

                //save steps reduction took
                int stepsReduc = guardsReduc.getKey();

                //init hashset that contains all guards
                HashSet<Vertex> guards = new HashSet<>(guardsReduc.getValue());

                //save number of guards found by reduction
                int guardsNumReduc = guards.size();

                //solve discrete orthogonal terrain guarding on restgraph with gurobi
                AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionGurobi = TerrainILPs.SolveTerrainGurobiRB(visGraph);

                //add guards found by gurobi
                guards.addAll(solutionGurobi.getKey());

                //check if terrain is guarded
                boolean isGuarded = CheckGuarding.isGuardedRB(visGraph, guards);

                //save number of selected guards
                int guardNumGurobi = solutionGurobi.getKey().size();
                int guardNum = guards.size();

                //save time Gurobi took to solve
                long timeForGurobi = solutionGurobi.getValue().toMillis();

                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";" + coll + ";" + reflCount + ";" +
                        convCount + ";" + vertexCountBefore + ";" + edgeCountBefore + ";" + timeForVis + ";" + timeForGurobi + ";" +
                        guardNum + ";" + isGuarded + ";" + vertexCountAfter + ";" + edgeCountAfter + ";" + guardsNumReduc + ";" + guardNumGurobi + ";" +
                        stepsReduc + ";" + max_steps + ";" + degCapIntersect + ";" + degCapSubset + ";" + timeForReduc + "\n");

                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


        if (algo_type.equals("solveRBReduceAllMethods")) {
            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;Terrain free of consecutive collinear vertices;" +
                            "Number reflex vertices;Number convex vertices;Number vertices;Number edges in visibility graph;" +
                            "Time for computing visibility graph;Time Gurobi took to solve;Number of selected guards;Terrain guarded;" +
                            "Number vertices after reduction;Number edges after reduction;Number Guards selected by reduction;" +
                            "Number guards selected by gurobi;Steps reduction;max_steps;degCapIntersection;degCapSubsets;timeForReduction" + "\n");
                }




                //read terrain
                ArrayList<Vertex> terrain = FileReadAndWrite.readOrthTerrainTxt(graph_file);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal
                boolean coll = TerrainBuild.isFreeOfCollinearVertices(terrain); //check if free of consecutive collinear vertices

                //count convex and reflex vertices
                int convCount = 0;
                int reflCount = 0;
                for (Vertex v : terrain) {
                    if (v.getvType().contains("C")) {
                        convCount++;
                    } else {
                        reflCount++;
                    }
                }

                //save total number of vertices
                int vertexCountBefore = terrain.size();


                //create RB visibility graph and stop time
                Instant startVis = Instant.now();
                SimpleGraph<Vertex> visGraph = VisibilityMethods.bipartiteVisGraphOrth(terrain, time_limit);
                Instant endVis = Instant.now();
                long timeForVis = Duration.between(startVis, endVis).toMillis();

                //save number of edges in visibility graph
                int edgeCountBefore = visGraph.getM();

                //reduce graph
                Instant startReduc = Instant.now();
                AbstractMap.SimpleEntry<Integer, HashSet<Vertex>> guardsReduc = Reduction.reduceBipartiteGraphRelaxInclConv(visGraph, max_steps, degCapIntersect, degCapSubset, relaxParam, 300);
                Instant endReduc = Instant.now();

                //save edge and vertex count after reduction
                int vertexCountAfter = visGraph.getN();
                int edgeCountAfter = visGraph.getM();

                //save time for reduction
                long timeForReduc = Duration.between(startReduc, endReduc).toMillis();

                //save steps reduction took
                int stepsReduc = guardsReduc.getKey();

                //init hashset that contains all guards
                HashSet<Vertex> guards = new HashSet<>(guardsReduc.getValue());

                //save number of guards found by reduction
                int guardsNumReduc = guards.size();

                //solve discrete orthogonal terrain guarding on restgraph with gurobi
                AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionGurobi = TerrainILPs.SolveTerrainGurobiRB(visGraph);

                //add guards found by gurobi
                guards.addAll(solutionGurobi.getKey());

                //check if terrain is guarded
                boolean isGuarded = CheckGuarding.isGuardedRB(visGraph, guards);

                //save number of selected guards
                int guardNumGurobi = solutionGurobi.getKey().size();
                int guardNum = guards.size();

                //save time Gurobi took to solve
                long timeForGurobi = solutionGurobi.getValue().toMillis();

                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";" + coll + ";" + reflCount + ";" +
                        convCount + ";" + vertexCountBefore + ";" + edgeCountBefore + ";" + timeForVis + ";" + timeForGurobi + ";" +
                        guardNum + ";" + isGuarded + ";" + vertexCountAfter + ";" + edgeCountAfter + ";" + guardsNumReduc + ";" + guardNumGurobi + ";" +
                        stepsReduc + ";" + max_steps + ";" + degCapIntersect + ";" + degCapSubset + ";" + timeForReduc + "\n");

                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


        if (algo_type.equals("solveOrthFullVis")) {
            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;Terrain free of consecutive collinear vertices;" +
                            "Number reflex vertices;Number convex vertices;Number vertices;Number edges in visibility graph;" +
                            "Time for computing visibility graph;Time Gurobi took to solve;Number of selected guards;Terrain guarded;" +
                            "number convex guards" + "\n");
                }




                //read terrain
                ArrayList<Vertex> terrain = FileReadAndWrite.readOrthTerrainTxt(graph_file);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal
                boolean coll = TerrainBuild.isFreeOfCollinearVertices(terrain); //check if free of consecutive collinear vertices

                //count convex and reflex vertices
                int convCount = 0;
                int reflCount = 0;
                for (Vertex v : terrain) {
                    if (v.getvType().contains("C")) {
                        convCount++;
                    } else {
                        reflCount++;
                    }
                }

                //save total number of vertices
                int vertexCount = terrain.size();


                //create RB visibility graph and stop time
                Instant startVis = Instant.now();
                SimpleGraph<Vertex> visGraph = VisibilityMethods.fullVisgraph(terrain, time_limit);
                Instant endVis = Instant.now();
                long timeForVis = Duration.between(startVis, endVis).toMillis();

                //save number of edges in visibility graph
                int edgeCount = visGraph.getM();

                //solve discrete orthogonal terrain guarding with gurobi on full visgraph
                AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionGurobi = TerrainILPs.SolveTerrainGurobiFullVis(visGraph);

                //save guards found by gurobi
                HashSet<Vertex> guards = new HashSet<>(solutionGurobi.getKey());

                //count convex guards
                int numConvGuards = 0;
                for(Vertex v: guards){
                    if(v.getvType().contains("C")){
                        numConvGuards ++;
                    }
                }


                //check if terrain is guarded
                boolean isGuarded = CheckGuarding.isGuardedFull(visGraph, guards);

                //save number of selected guards
                int guardNum = guards.size();

                //save time Gurobi took to solve
                long timeForGurobi = solutionGurobi.getValue().toMillis();

                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";" + coll + ";" + reflCount + ";" +
                        convCount + ";" + vertexCount + ";" + edgeCount + ";" + timeForVis + ";" + timeForGurobi + ";" +
                        guardNum + ";" + isGuarded + ";" + numConvGuards + "\n");

                //close filewriter
                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


        if (algo_type.equals("solveOrthCallback")) {
            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;Terrain free of consecutive collinear vertices;" +
                            "Number reflex vertices;Number convex vertices;Number vertices;" +
                            "Time Gurobi took to solve incl Callbacks;Number of selected guards;Terrain guarded;" +
                            "Number convex visited;Number reflex visited;Number visited combined;Number of times callback was called;" +
                            "Number edges partial visGraph;addProb;relation convex to non guarded;relation visited convex to convex;" +
                            "Relation visited reflex to reflex;Relation visited overall" +"\n");
                }




                //read terrain
                ArrayList<Vertex> terrain = FileReadAndWrite.readOrthTerrainTxt(graph_file);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal
                boolean coll = TerrainBuild.isFreeOfCollinearVertices(terrain); //check if free of consecutive collinear vertices

                //count convex and reflex vertices
                int convCount = 0;
                int reflCount = 0;
                for (Vertex v : terrain) {
                    if (v.getvType().contains("C")) {
                        convCount++;
                    } else {
                        reflCount++;
                    }
                }

                //save total number of vertices
                int vertexCount = terrain.size();

                //solve discrete orthogonal terrain guarding with gurobi using callbacks
                AbstractMap.SimpleEntry<HashSet<Vertex>, String> solutionGurobi = TerrainILPs.SolveTerrainGurobiWithCallback(terrain, addProb, relationConv);

                //save guards found by gurobi
                HashSet<Vertex> guards = new HashSet<>(solutionGurobi.getKey());
                //save number of selected guards
                int guardNum = guards.size();

                //save returns of gurobi
                String results = solutionGurobi.getValue();
                String[] resultArr = results.split(";");
                long timeForGurobi = Duration.parse(resultArr[0]).toMillis();
                int visitedConvex = Integer.parseInt(resultArr[1]);
                int visitedReflex = Integer.parseInt(resultArr[2]);
                int edgeNumVis = Integer.parseInt(resultArr[3]);
                int callNum = Integer.parseInt(resultArr[4]);
                boolean isGuarded = Boolean.parseBoolean(resultArr[5]);
                int visitedCombined = visitedReflex + visitedConvex;
                double relConvConv = (double) visitedConvex/convCount;
                double relReflRefl = (double) visitedReflex/convCount;
                double relVisited = (double) visitedCombined/vertexCount;





                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";" + coll + ";" + reflCount + ";" +
                        convCount + ";" + vertexCount + ";" + timeForGurobi + ";" +
                        guardNum + ";" + isGuarded + ";" + visitedConvex + ";" + visitedReflex + ";" + visitedCombined +
                        ";" + callNum + ";" + edgeNumVis + ";" + addProb + ";" +  relationConv +";" + relConvConv +
                        ";" + relReflRefl + ";" + relVisited + "\n");

                //close filewriter
                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


        if (algo_type.equals("solveNonOrthFullVis")) {
            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;" +
                            "Number vertices;Number edges in visibility graph;" +
                            "Time for computing visibility graph;Time Gurobi took to solve;Number of selected guards;Terrain guarded;" + "\n");
                }



                //read terrain
                boolean skip = graph_file.contains("PlanckFiles");
                ArrayList<double[]> vList = FileReadAndWrite.xYtxtToArrayList(graph_file, " ", skip);
                ArrayList<Vertex> terrain = TerrainBuild.xYListToNonOrthTerrainWithoutRemoval(vList);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal



                //save total number of vertices
                int vertexCount = terrain.size();


                //create RB visibility graph and stop time
                Instant startVis = Instant.now();
                SimpleGraph<Vertex> visGraph = VisibilityMethods.fullVisgraph(terrain, time_limit);
                Instant endVis = Instant.now();
                long timeForVis = Duration.between(startVis, endVis).toMillis();

                //save number of edges in visibility graph
                int edgeCount = visGraph.getM();

                //solve discrete orthogonal terrain guarding with gurobi on full visgraph
                AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionGurobi = TerrainILPs.SolveTerrainGurobiFullVis(visGraph);

                //save guards found by gurobi
                HashSet<Vertex> guards = new HashSet<>(solutionGurobi.getKey());


                //check if terrain is guarded
                boolean isGuarded = CheckGuarding.isGuardedFull(visGraph, guards);

                //save number of selected guards
                int guardNum = guards.size();

                //save time Gurobi took to solve
                long timeForGurobi = solutionGurobi.getValue().toMillis();

                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";"  +  vertexCount + ";" + edgeCount + ";" + timeForVis + ";" + timeForGurobi + ";" +
                        guardNum + ";" + isGuarded + ";" + "\n");

                //close filewriter
                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


        if (algo_type.equals("reduceSingle")) {
            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;Terrain free of consecutive collinear vertices;" +
                            "Number reflex vertices;Number convex vertices;Number vertices;Number edges in visibility graph;" +
                            "Time for computing visibility graph;Time reduction private neighborhoods;Number vertices after private;" +
                            "Number edges after private;Time reduction convex neighbors;Number vertices after convex neighbors;" +
                            "Number edges after convex neighbors;Time reduction intersect;Number vertices after intersect;" +
                            "Number edges after intersect;Time reduction subsets;Number vertices after subsets;Number edges after Subsets" + "\n");
                }



                //read terrain
                ArrayList<Vertex> terrain = FileReadAndWrite.readOrthTerrainTxt(graph_file);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal
                boolean coll = TerrainBuild.isFreeOfCollinearVertices(terrain); //check if free of consecutive collinear vertices

                //count convex and reflex vertices
                int convCount = 0;
                int reflCount = 0;
                for (Vertex v : terrain) {
                    if (v.getvType().contains("C")) {
                        convCount++;
                    } else {
                        reflCount++;
                    }
                }

                //save total number of vertices
                int vertexCountBefore = terrain.size();


                //create RB visibility graph and stop time
                Instant startVis = Instant.now();
                SimpleGraph<Vertex> visGraph = VisibilityMethods.bipartiteVisGraphOrth(terrain, time_limit);
                Instant endVis = Instant.now();
                long timeForVis = Duration.between(startVis, endVis).toMillis();

                //copy graph for reduction
                SimpleGraph<Vertex> graphcopy = visGraph.copy();

                //save number of edges in visibility graph
                int edgeCountBefore = visGraph.getM();

                //reduce graph with 4 different methods

                Instant startReducPriv = Instant.now();
                Reduction.reductionPrivateSmarter(graphcopy);
                Instant endReducPriv = Instant.now();
                long timePriv = Duration.between(startReducPriv, endReducPriv).toMillis(); //save time
                int vertexNumPriv = graphcopy.getN();
                int edgeNumPriv = graphcopy.getM();

                //reset graph
                graphcopy = visGraph.copy();

                Instant startReducRed = Instant.now();
                Reduction.reductionDeleteConv(graphcopy);
                Reduction.removeDegZeroVertices(graphcopy);
                Instant endReducRed = Instant.now();

                long timeRed = Duration.between(startReducRed, endReducRed).toMillis(); //save time
                int vertexNumRed = graphcopy.getN();
                int edgeNumRed = graphcopy.getM();

                //reset graph
                graphcopy = visGraph.copy();

                Instant startReducInter = Instant.now();
                Reduction.reductionIntersectCapped(graphcopy, -1);
                Instant endReducInter = Instant.now();
                long timeInter = Duration.between(startReducInter, endReducInter).toMillis(); //save time
                int vertexNumInter = graphcopy.getN();
                int edgeNumInter = graphcopy.getM();

                //reset graph
                graphcopy = visGraph.copy();

                Instant startReducSubset = Instant.now();
                Reduction.reductionViaSubsetsRootWithDelCapped(graphcopy, -1);
                Instant endReducSubset = Instant.now();
                long timeSub = Duration.between(startReducSubset, endReducSubset).toMillis(); //save time
                int vertexNumSub = graphcopy.getN();
                int edgeNumSub = graphcopy.getM();



                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";" + coll + ";" + reflCount + ";" +
                        convCount + ";" + vertexCountBefore + ";" + edgeCountBefore + ";" + timeForVis + ";" +
                        timePriv + ";" + vertexNumPriv + ";" + edgeNumPriv + ";" + timeRed + ";" + vertexNumRed + ";" +
                        edgeNumRed + ";" + timeInter + ";" + vertexNumInter + ";" + edgeNumInter + ";" + timeSub + ";"
                        + vertexNumSub + ";" + edgeNumSub +"\n");

                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


        if (algo_type.equals("solveOrthFullVisOnlyRefl")) {
            try {

                FileWriter resultsRB;
                File file = new File(output_file);
                boolean file_exists = file.exists();
                resultsRB = new FileWriter(file, true);

                if (!file_exists) {
                    resultsRB.write("Used algorithm;Terrain File Name;Terrain orthogonal;Terrain free of consecutive collinear vertices;" +
                            "Number reflex vertices;Number convex vertices;Number vertices;Number edges in visibility graph;" +
                            "Time for computing visibility graph;Time Gurobi took to solve;Number of selected guards;Terrain guarded;" +
                            "number convex guards" + "\n");
                }




                //read terrain
                ArrayList<Vertex> terrain = FileReadAndWrite.readOrthTerrainTxt(graph_file);
                boolean orth = TerrainBuild.isOrthogonal(terrain); //check for orthogonal
                boolean coll = TerrainBuild.isFreeOfCollinearVertices(terrain); //check if free of consecutive collinear vertices

                //count convex and reflex vertices
                int convCount = 0;
                int reflCount = 0;
                for (Vertex v : terrain) {
                    if (v.getvType().contains("C")) {
                        convCount++;
                    } else {
                        reflCount++;
                    }
                }

                //save total number of vertices
                int vertexCount = terrain.size();


                //create RB visibility graph and stop time
                Instant startVis = Instant.now();
                SimpleGraph<Vertex> visGraph = VisibilityMethods.fullVisgraph(terrain, time_limit);
                Instant endVis = Instant.now();
                long timeForVis = Duration.between(startVis, endVis).toMillis();

                //save number of edges in visibility graph
                int edgeCount = visGraph.getM();

                //solve discrete orthogonal terrain guarding with gurobi on full visgraph
                AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionGurobi = TerrainILPs.SolveTerrainGurobiFullVisReflOnly(visGraph);

                //save guards found by gurobi
                HashSet<Vertex> guards = new HashSet<>(solutionGurobi.getKey());

                //count convex guards
                int numConvGuards = 0;
                for(Vertex v: guards){
                    if(v.getvType().contains("C")){
                        numConvGuards ++;
                    }
                }


                //check if terrain is guarded
                boolean isGuarded = CheckGuarding.isGuardedFull(visGraph, guards);

                //save number of selected guards

                int guardNum = guards.size();

                //save time Gurobi took to solve
                long timeForGurobi = solutionGurobi.getValue().toMillis();

                //write results for current terrain in line in results-file
                resultsRB.write(algo_type + ";" + graph_file + ";" + orth + ";" + coll + ";" + reflCount + ";" +
                        convCount + ";" + vertexCount + ";" + edgeCount + ";" + timeForVis + ";" + timeForGurobi + ";" +
                        guardNum + ";" + isGuarded + ";" + numConvGuards + "\n");

                //close filewriter
                resultsRB.close();

            } catch (IOException err) {
                System.out.println("Error when writing results-file: " + err.getMessage());
            }
        }


    }
}
