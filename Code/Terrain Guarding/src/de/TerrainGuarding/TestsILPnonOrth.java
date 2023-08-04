package de.TerrainGuarding;

import sipura.graphs.SimpleGraph;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;


public class TestsILPnonOrth {
    public static void main(String[] args) {

        System.out.println(Locale.getDefault());
        Locale.setDefault(Locale.US);
        //Baue terrain
        //TerrainBuild.global_valley("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\valley.txt", 5000, 0, 1, 0.5, 2, 2000);
        //TerrainBuild.randomTerrain("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\rnd.txt", 100000, 0, 1, 0.5, 2);
        //TerrainBuild.valleySteps("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\valleySteps.txt", 10000, 0, 1, 0.5, 2, 3000);
        //TerrainBuild.randomPointsValley("TestFiles\\TrueValley\\testPoints1.txt" ,
        //        100, 0, 1, 0.5, 2, 20);
        //TerrainBuild.randomPointsValleyBreakOutQuadVariable(filePath,1.0, 5000, 100,
        //        0.5, 10, 2500, 0.0);
        try {

            //tests for the given datasets
            //ArrayList<double[]> vList = TerrainBuild.readAGPLIB_unfold("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\AGBFiles\\randsimple-60-10.pol");
            //ArrayList<double[]> vList = FileReadAndWrite.xYtxtToArrayList(filePath, " ", false);
            ArrayList<double[]> vList = FileReadAndWrite.xYtxtToArrayList("C:\\Users\\Lukas Reuter\\IdeaProjects\\TEST\\TestFiles\\InstancesPlanck\\parabolawalk\\parabolawalk-100000-16.terrain", " ", true);
            //TerrainBuild.toTxtPoints(vList , "C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\pointFiles\\testPoints2.txt");



            /*
            for (double[] v : vList) {
                System.out.println(v[0]);
            }
             */

            ArrayList<Vertex> terrain = TerrainBuild.xYListToNonOrthTerrainWithoutRemoval(vList);

            System.out.println("Terrain is orthogonal: " + TerrainBuild.isOrthogonal(terrain));
            FileReadAndWrite.toTxt(terrain, "C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\outputTerrains\\nonOrthogonalTest.txt" );

            /*
            //read terrain and set right left
            ArrayList<Vertex> terrain = TerrainBuild.readOrthTerrainTxt("C:\\Users\\Lukas Reuter\\Desktop\\Terrain guarding\\Java\\txtFiles\\random\\valley.txt");
            System.out.println("Terrain is ortogonal: " + TerrainBuild.isOrthogonal(terrain));
            TerrainBuild.rightLeftVertex(terrain);

             */

            //create visibility graphs
            Instant visStart = Instant.now();
            SimpleGraph<Vertex> graphFull = VisibilityMethods.fullVisgraph(terrain, 5);
            Instant visEnd = Instant.now();
            System.out.println("runtime of visGraph: " + Duration.between(visStart, visEnd));


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


            //solve fullVisGraph
            Instant startFull = Instant.now();
            AbstractMap.SimpleEntry<HashSet<Vertex>, Duration> solutionFull = TerrainILPs.SolveTerrainGurobiFullVis(graphFull);
            HashSet<Vertex> guardsFull = solutionFull.getKey();
            Instant endFull = Instant.now();



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



            System.out.println("runtime without preprocessing Full: " + Duration.between(startFull, endFull));
            System.out.println("Number of guards found by Gurobi Full: " + guardsFull.size());
            //System.out.println("Number of guards used by Gurobi in partial cover: " + guardsPartialCover.size());
            //System.out.println("runtime of partial cover: " + Duration.between(startPartialCover, endPartialCover));
            System.out.println("The Terrain is guarded (full): " + CheckGuarding.isGuardedFull(graphFull, guardsFull));
            int n = graphFull.getN();
            int m = graphFull.getM();
            double rel = Double.valueOf(m) / (Double.valueOf(n) / 2);
            System.out.println("Number of Vertices: " + n + " Number of Edges: " + m + " Relation Edge/Convex: " + rel);
            System.out.println("maxdeg: " + graphFull.maxDegree());

        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }
}
