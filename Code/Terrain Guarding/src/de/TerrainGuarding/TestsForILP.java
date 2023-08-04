package de.TerrainGuarding;

import sipura.graphs.SimpleGraph;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class TestsForILP {
    public static void main(String[] args) {

        //Build terrain
        //TerrainBuild.global_valley("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\valley.txt", 10000, 0, 1, 0.5, 2, 20);
        //TerrainBuild.randomTerrain("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\rnd.txt", 100000, 0, 1, 0.5, 2);
        //TerrainBuild.valleySteps("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\valleySteps.txt", 10000, 0, 1, 0.5, 2, 3000);
        //TerrainBuild.randomPointsValleyBreakOutQuadVariable("TestFiles\\GlobalValleyQuad\\testPoints2.txt" , 1,
        //       10000, 4.0, 0.5, 100, 5000,  0.4);



        //TerrainBuild.randomPointsValleyBreakOutQuadVariable("TestFiles\\GlobalValleyQuad\\testPoints2.txt" , 1.0,
        //        10000, 10, 0.5, 100, 2500,  0.2);




        //String filePath = "TestFiles\\Randomized\\NonOrthogonal\\pointsRandom" + 1 + ".txt";
        //TerrainBuild.randomPoints(filePath, 10000, 0, 10, 0.5, 5);

        //TerrainBuild.randomPointsValleyBreakOut("TestFiles\\GlobalValleyQuad\\testPoints3.txt" ,
        //        5000, 0, 200, 0.5, 2, 70, 0.3);
        //TerrainBuild.randomPointsValleyBreakOutSteep("TestFiles\\GlobalValleyQuad\\testPoints2.txt" ,
        //        1000000, 0, 1, 0.5, 2, 500, 0.3);

        try {

            //ArrayList<double[]> vList = TerrainBuild.readAGPLIB_unfold("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\AGBFiles\\orthogonal\\random-2500-10.pol");

            ArrayList<double[]> vList = FileReadAndWrite.xYtxtToArrayList("TestFiles\\GlobalValleyQuad\\testPoints2.txt", " ", false);
            ArrayList<Vertex> terrain = TerrainBuild.xYListToOrthTerrain(vList);
            //ArrayList<double[]> vList2 = TerrainBuild.xYtxtToArrayList("TestFiles\\GlobalValley\\testPoints3.txt", " ");
            //ArrayList<Vertex> terrain2 = TerrainBuild.xYListToOrthTerrain(vList2);
            //TerrainBuild.concatenateTerrains(terrain, terrain2);
            System.out.println("Terrain is orthogonal: " + TerrainBuild.isOrthogonal(terrain));
            System.out.println("Terrain is free of coll: " + TerrainBuild.isFreeOfCollinearVertices(terrain));
            TerrainBuild.removeCollinearVertices(terrain);
            TerrainBuild.rightLeftVertex(terrain);
            FileReadAndWrite.toTxt(terrain, "C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\outputTerrains\\orthogonalTest.txt" );

            int convCount = 0;
            int reflCount = 0;
            for (Vertex v : terrain){
                if(v.getvType().contains("C")){
                    convCount++;
                }else{
                    reflCount++;
                }
            }
            System.out.println("Terrain has " + convCount + " convex vertices and " + reflCount + " reflex vertices.");

/*
            //test general bipartite graph
            SimpleGraph<Vertex> testGraphBipartite = new SimpleGraph<>();
            ArrayList<Vertex> blueSet = new ArrayList<>();
            ArrayList<Vertex> redSet = new ArrayList<>();
            for(int i = 0; i<= 200; i++){
                Vertex blue = new Vertex(i, 0.0, 0.0, "R");
                Vertex red = new Vertex(i+201, 0.0, 0.0, "C");
                blueSet.add(blue);
                redSet.add(red);
                testGraphBipartite.addVertex(blue);
                testGraphBipartite.addVertex(red);
            }
            for(int i = 0; i<= 200; i++){
                for(int j = 0; j<= 200; j++) {
                    if (Math.random() < 0.2) {
                        testGraphBipartite.addEdge(blueSet.get(i), redSet.get(j));
                    }
                }
            }

            Instant startGenBi = Instant.now();
            HashSet<Vertex> guardsGeneralBipartite = TerrainILPs.SolveTerrainGurobi(testGraphBipartite);
            Instant endGenBi = Instant.now();
            System.out.println("Time for general bipartite graph: " + Duration.between(startGenBi, endGenBi));



            //reduction
            Instant startReducGen = Instant.now();
            //graph.maxDegree is slower than getN, and in case N is greater it gets reset by the internal constraint to maxdeg
            //HashSet<Vertex> guards = Reduction.reduceBipartiteGraph(graphOld, 10, 10, graphOld.maxDegree()-10);
            HashSet<Vertex> guardsGen = Reduction.reduceBipartiteGraph(testGraphBipartite, 30, testGraphBipartite.maxDegree(), -1);
            HashSet<Vertex> guards1Gen = TerrainILPs.SolveTerrainGurobi(testGraphBipartite);
            HashSet<Vertex> guardsCombinedGen = new HashSet<>();
            guardsCombinedGen.addAll(guardsGen);
            guardsCombinedGen.addAll(guards1Gen);
            Instant endReducGen = Instant.now();
            System.out.println("Time for general bipartite graph with reduction: " + Duration.between(startReducGen, endReducGen) );
 */

/*
            //read terrain and set right left
            ArrayList<Vertex> terrain = TerrainBuild.readOrthTerrainTxt("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\rnd.txt");
            System.out.println("Terrain is ortogonal: " + TerrainBuild.isOrthogonal(terrain));
            TerrainBuild.rightLeftVertex(terrain);


 */

            //only test for edgecount
            //SimpleGraph<Vertex> graphFullTest = VisibilityMethods.fullVisgraph2(terrain);
            //System.out.println("edgecount in full graph: " + graphFullTest.getM());




            Instant visStart2 = Instant.now();
            SimpleGraph<Vertex> graph = new SimpleGraph<>();
            HashSet<Vertex> subset = new HashSet<>();
            for(Vertex v: terrain){
                graph.addVertex(v);
                if(v.getvType().contains("C")) {
                    subset.add(v);
                }
            }
            System.out.println(graph.getN());
            System.out.println(graph.getM());

            VisibilityMethods.visSubGraph(graph, terrain, subset);

            //VisibilityMethods.exportGraphtxt( graph, "TestFiles\\GlobalValleyQuad\\testGraph.txt");

            //System.out.println("Bipartite: " + VisibilityMethods.isBipartite(graph));
            Instant visEnd2 = Instant.now();
            System.out.println("time for subvis: " + Duration.between(visStart2, visEnd2));

            //write visGraph to File
            //VisibilityMethods.visGraphtotxt("TestFiles\\\\GlobalValleyQuad\\\\testGraph.txt" , graph);

            //SimpleGraph<Vertex> reread = VisibilityMethods.readVisGraphtxt("TestFiles\\\\GlobalValleyQuad\\\\testGraph.txt");
            //System.out.println("Number vertices in reread: " + reread.getN() + " " + reread.getM());


            //create visibility graph
            Instant visStart = Instant.now();
            //SimpleGraph<Vertex> graphOld = VisibilityMethods.createVisibilityGraph(terrain);
            SimpleGraph<Vertex> graphOld = VisibilityMethods.bipartiteVisGraphOrth(terrain, 100000);
            //SimpleGraph<Vertex> graphFull2 = TerrainBuild.fullVisgraph(terrain);
            //SimpleGraph<Vertex> graphFull = VisibilityMethods.fullVisgraph2(terrain);
            Instant visEnd = Instant.now();




            SimpleGraph<Vertex> graph2Old = graphOld.copy();
            System.out.println("runtime of visGraph: " + Duration.between(visStart, visEnd));
            //System.out.println("Full visGraph2: vertices: " + graphFull2.getN() +  " edges : " + graphFull2.getM());
            //System.out.println("Full visGraph: vertices: " + graphFull.getN() +  " edges : " + graphFull.getM());


            boolean testEq = true;
            for(Vertex v : subset){
                if(!graph.neighbors(v).containsAll(graphOld.neighbors(v)) && !v.getvType().contains("C")){
                    testEq = false;
                    String wrong = "Neighbors of " +  String.valueOf(v.getID()) + " (" + v.getvType() + "): ";
                    String wrong2 = String.valueOf(v.getID());
                    for(Vertex vert : graph.neighbors(v)){
                        wrong += " " + vert.getID();
                    }
                    for(Vertex vert : graphOld.neighbors(v)){
                        wrong2 += " " + vert.getID();
                    }
                    System.out.println(wrong);
                    System.out.println(wrong2);
                }

            }
            System.out.println("Neighborhoods are equal: " + testEq);









            /*
            //TEST with visGraph
            for (int i = 0; i < terrain.size(); i++) {
                for (int j = 0; j < terrain.size(); j++) {
                    if (i == j) continue;
                    if (terrain.get(i).getvType().contains("C") && terrain.get(j).getvType().contains("C")) {
                        graphFull.removeEdge(terrain.get(i), terrain.get(j));
                        graph2.removeEdge(terrain.get(i), terrain.get(j));
                    }
                    if (!terrain.get(j).getvType().contains("C") && !terrain.get(i).getvType().contains("C")) {
                        graphFull.removeEdge(terrain.get(i), terrain.get(j));
                        graph2.removeEdge(terrain.get(i), terrain.get(j));
                    }
                }
            }
            System.out.println("number edges in RB/graph2/reducedFULL: " + graph.getM() + " " + graph2.getM() + " " + graphFull.getM());
             */


            //solve RB without reduction
            Instant start2 = Instant.now();
            AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionRB2 = TerrainILPs.SolveTerrainGurobiRB(graph2Old);
            HashSet<Vertex> guards2 = solutionRB2.getKey();
            Instant end2 = Instant.now();

            //solve RB without reduction with callback
            Instant start3 = Instant.now();
            AbstractMap.SimpleEntry<HashSet<Vertex>, String> solutionCallback = TerrainILPs.SolveTerrainGurobiWithCallback(terrain, 0.1, 0.1);
            HashSet<Vertex> guards3 = solutionCallback.getKey();
            Instant end3 = Instant.now();



            //reduction
            Instant start1 = Instant.now();
            //graph.maxDegree is slower than getN, and in case N is greater it gets reset by the internal constraint to maxdeg
            //HashSet<Vertex> guards = Reduction.reduceBipartiteGraph(graphOld, 10, 10, graphOld.maxDegree()-10);
            HashSet<Vertex> guards = Reduction.reduceBipartiteGraph(graphOld, 10, 4, graphOld.maxDegree()-20);
            AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionRB = TerrainILPs.SolveTerrainGurobiRB(graphOld);
            HashSet<Vertex> guards1 = solutionRB.getKey();
            HashSet<Vertex> guardsCombined = new HashSet<>();
            guardsCombined.addAll(guards);
            guardsCombined.addAll(guards1);
            Instant end1 = Instant.now();


            //stepwise solve
            //HashSet<Vertex> guardsSteps = TerrainILPs.SolveTerrainGurobiStepWise2(terrain, 0.3);

            /*
            //solve fullVisGraph
            Instant startFull = Instant.now();
            HashSet<Vertex> guardsFull = TerrainILPs.SolveTerrainGurobiFullVis(graphFull);
            Instant endFull = Instant.now();

            Instant startFullRefl = Instant.now();
            HashSet<Vertex> guardsReflFull = TerrainILPs.SolveTerrainGurobiFullVisReflOnly(graphFull);
            Instant endFullRefl = Instant.now();

             */


            /*
            Instant startPartialCover = Instant.now();
            HashSet<Vertex> guardsPartialCover = TerrainILPs.SolveTerrainGurobiPartialCover(graphFull, 10);
            Instant endPartialCover = Instant.now();
            System.out.println(guardsPartialCover);
            System.out.println(graphFull.getN());

             */

            
            /*
            for (Vertex v:graph.getV()){
                String str = "";
                for(Vertex v2:graph.neighbors(v)){
                    str = str + v2.getID() + " ";
                }
                System.out.println(v.getID() + " neighbors: " + str);
            }
             */


            System.out.println("runtime with preprocessing RB: " + Duration.between(start1, end1));
            System.out.println("runtime without preprocessing RB: " + Duration.between(start2, end2));
            System.out.println("runtime with callbacks: " + Duration.between(start3, end3));
            //System.out.println("runtime without preprocessing Full: " + Duration.between(startFull, endFull));
            //System.out.println("runtime without preprocessing Full Refl Only: " + Duration.between(startFullRefl, endFullRefl));
            System.out.println("Number of guards found by Gurobi with callbacks: " + guards3.size());
            System.out.println("Number of guards found by Gurobi RB: " + guards2.size());
            System.out.println("Number of guards found by reduction RB: " + guards.size());
            System.out.println("Number of guards found by Gurobi after reduction RB: " + guards1.size());
            System.out.println("Combined number of guards RB: " + guardsCombined.size());
            //System.out.println("Number of guards found by stepWise: " + guardsSteps.size());
            //System.out.println("Number of guards found by Gurobi Full: " + guardsFull.size());
            //System.out.println("Number of guards found by Gurobi Full ReflOnly: " + guardsReflFull.size());
            //System.out.println("Number of guards used by Gurobi in partial cover: " + guardsPartialCover.size());
            //System.out.println("runtime of partial cover: " + Duration.between(startPartialCover, endPartialCover));
            System.out.println("The Terrain is guarded (RB): " + CheckGuarding.isGuardedRB(graph2Old, guardsCombined));
            //System.out.println("The Terrain is guarded (callback): " + CheckGuarding.isGuarded(graph2Old, guards3));
            //System.out.println("The Terrain is guarded (full): " + CheckGuarding.isGuardedFull(graphFull, guardsFull));
            //System.out.println("The Terrain is guarded (fullReflOnly): " + CheckGuarding.isGuarded(graphFull, guardsReflFull));
            //int n = graph2Old.getN();
            //int m = graph2Old.getM();
            //double rel = Double.valueOf(m) / (Double.valueOf(n) / 2);
            //System.out.println("RB graph: number of Vertices: " + n + " Number of Edges: " + m + " Relation Edge/Convex: " + rel);
            //System.out.println("Full visgraph: number of Vertices: " + graphFull.getN() + " Number of Edges: " + graphFull.getM());
            //System.out.println("maxdeg: " + graph2.maxDegree());
            //System.out.println("rb based on full bipartite: " + VisibilityMethods.isBipartite(graphOld));
            System.out.println(solutionCallback.getValue());

        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }
}
