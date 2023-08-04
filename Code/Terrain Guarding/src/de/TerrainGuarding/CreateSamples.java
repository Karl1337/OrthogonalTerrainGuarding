package de.TerrainGuarding;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

//this class was used to create the gadget like samples
public class CreateSamples {
    public static void main(String[] args) {

        
        /*

        //this was used for quadratic like terrains
        for(int i = 1; i <= 20; i++) {

            String filePath = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier300\\Noise50\\crookedValleyQuad" + i + ".txt";
            String filePathOrth = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier300\\Noise50\\crookedValleyQuad" + i + ".txt";
            //String filePathVis = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierBreakout10\\Breakout0\\VisValleyQuad" + i + ".txt";
            //build random quadratic like valleys with breakouts
            TerrainBuild.randomPointsValleyBreakOutQuadVariable(filePath,1.0, 10000, 10,
                    0.5, 10, 5000, 0.5);
            try {

                //read points and create non orth and orth terrain
                ArrayList<double[]> vList = FileReadAndWrite.xYtxtToArrayList(filePath, " ", false);

                //make crooked
                TerrainBuild.crooked(vList, 0.4, 5000);

                ArrayList<Vertex> terrainNonOrth = TerrainBuild.xYListToNonOrthTerrain(vList);
                ArrayList<Vertex> terrainOrth = TerrainBuild.xYListToOrthTerrain(vList);



                //check if terrains created are orthogonal and contain no collinear vertices
                System.out.println("Terrain " + i + " is orthogonal: " + TerrainBuild.isOrthogonal(terrainOrth));
                System.out.println("Terrain " + i + " is free of collinear vertices: " + TerrainBuild.isFreeOfCollinearVertices(terrainOrth));
                System.out.println("Terrain non orth " + i + " is free of collinear vertices: " + TerrainBuild.isFreeOfCollinearVertices(terrainNonOrth));

                //categorize vertices of orthogonal terrain
                TerrainBuild.rightLeftVertex(terrainOrth);

                //write orthogonal terrain to txt
                FileReadAndWrite.toTxt(terrainOrth, filePathOrth);

                //write non-orthogonal terrain to txt
                FileReadAndWrite.toTxtPoints(vList, filePath);


                //construct visibility graphs
                //SimpleGraph<Vertex> orthVisGraphRB = VisibilityMethods.RBGraphBasedOnfullVisgraph2(terrainOrth);
                //SimpleGraph<Vertex> visGraphnonOrth = VisibilityMethods.fullVisgraphNonOrthogonal(terrainNonOrth);

                //write visGraphs in files
                //VisibilityMethods.visGraphtotxt(filePathVis, visGraphnonOrth);
                //VisibilityMethods.visGraphtotxt(filePathVisOrth, orthVisGraphRB);


            }catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }

         */

        /*

        //This was used for random terrains
        for(int i = 1; i <= 100; i++) {

            String filePath = "TestFiles\\Randomized50K\\NonOrthogonal\\pointsRandom" + i + ".txt";
            String filePathOrth = "TestFiles\\Randomized50K\\Orthogonal\\pointsRandomOrth" + i + ".txt";

            TerrainBuild.randomPoints(filePath, 50000, 0, 10, 0.5, 5);
            //TerrainBuild.randomPointsValleyBreakOutQuadVariable(filePath,1.0, 10000, 10,
            //        0.5, 10, 5000, 0.5);

            try {
                //read points and create non orth and orth terrain
                ArrayList<double[]> vList = FileReadAndWrite.xYtxtToArrayList(filePath, " ", false);
                ArrayList<Vertex> terrainNonOrth = TerrainBuild.xYListToNonOrthTerrain(vList);
                ArrayList<Vertex> terrainOrth = TerrainBuild.xYListToOrthTerrain(vList);

                //check if terrains created are orthogonal and contain no collinear vertices
                System.out.println("Terrain " + i + " is orthogonal: " + TerrainBuild.isOrthogonal(terrainOrth));
                System.out.println("Terrain " + i + " is free of collinear vertices: " + TerrainBuild.isFreeOfCollinearVertices(terrainOrth));
                System.out.println("Terrain non orth " + i + " is free of collinear vertices: " + TerrainBuild.isFreeOfCollinearVertices(terrainNonOrth));

                //categorize vertices of orthogonal terrain
                TerrainBuild.rightLeftVertex(terrainOrth);

                //write orthogonal terrain to txt
                FileReadAndWrite.toTxt(terrainOrth, filePathOrth);

                //construct visibility graphs
                //SimpleGraph<Vertex> orthVisGraphRB = VisibilityMethods.RBGraphBasedOnfullVisgraph2(terrainOrth);
                //SimpleGraph<Vertex> visGraphnonOrth = VisibilityMethods.fullVisgraphNonOrthogonal(terrainNonOrth);

                //write visGraphs in files
                //VisibilityMethods.visGraphtotxt(filePathVis, visGraphnonOrth);
                //VisibilityMethods.visGraphtotxt(filePathVisOrth, orthVisGraphRB);


            }catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }


         */

/*
        //This was used for linear global valleys with different noise probabilities
        for(int i = 1; i <= 20; i++) {

            String filePath = "TestFiles\\Crooked\\ValleyLin\\NonOrthogonal\\Noise50\\crookedValleyLin" + i + ".txt";
            String filePathOrth = "TestFiles\\Crooked\\ValleyLin\\Orthogonal\\Noise50\\crookedValleyLin" + i + ".txt";
            //String filePathVis = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierBreakout10\\Breakout0\\VisValleyQuad" + i + ".txt";
            //build random quadratic like valleys with breakouts
            TerrainBuild.randomPointsValleyBreakOut(filePath, 10000, 0.5, 3, 0.5, 3, 5000, 0.5);
            try {

                //read points and create non orth and orth terrain
                ArrayList<double[]> vList = FileReadAndWrite.xYtxtToArrayList(filePath, " ", false);

                //make crooked
                TerrainBuild.crooked(vList, 0.4, 5000);

                ArrayList<Vertex> terrainNonOrth = TerrainBuild.xYListToNonOrthTerrain(vList);
                ArrayList<Vertex> terrainOrth = TerrainBuild.xYListToOrthTerrain(vList);

                //check if terrains created are orthogonal and contain no collinear vertices
                System.out.println("Terrain " + i + " is orthogonal: " + TerrainBuild.isOrthogonal(terrainOrth));
                System.out.println("Terrain " + i + " is free of collinear vertices: " + TerrainBuild.isFreeOfCollinearVertices(terrainOrth));
                System.out.println("Terrain non orth " + i + " is free of collinear vertices: " + TerrainBuild.isFreeOfCollinearVertices(terrainNonOrth));

                //categorize vertices of orthogonal terrain
                TerrainBuild.rightLeftVertex(terrainOrth);

                //write orthogonal terrain to txt
                FileReadAndWrite.toTxt(terrainOrth, filePathOrth);

                //construct visibility graphs
                //SimpleGraph<Vertex> orthVisGraphRB = VisibilityMethods.RBGraphBasedOnfullVisgraph2(terrainOrth);
                //SimpleGraph<Vertex> visGraphnonOrth = VisibilityMethods.fullVisgraphNonOrthogonal(terrainNonOrth);

                //write visGraphs in files
                //VisibilityMethods.visGraphtotxt(filePathVis, visGraphnonOrth);
                //VisibilityMethods.visGraphtotxt(filePathVisOrth, orthVisGraphRB);


            }catch (IOException err) {
                System.out.println(err.getMessage());
            }
        }

 */




        //This was used to orthogonalize the planck data
        /*
        boolean problem = false;
        //Directory where orthogonalized files get stored
        String orthDir = "TestFiles\\PlanckFiles\\InstancesPlanckOrthogonalized\\walk\\";
        //Directory of original files
        File directoryPath = new File("TestFiles\\PlanckFiles\\InstancesPlanck\\walk");
        //List of all files and directories
        File filesList[] = directoryPath.listFiles();
        for (File file : filesList) {
            if (file.getName().substring(file.getName().length() - 7).equals("terrain")) {
                System.out.println("File name: " + file.getName());
                String path = file.getAbsolutePath();
                try {
                    //read file and orthogonalize
                    ArrayList<double[]> vals = FileReadAndWrite.xYtxtToArrayList(path, " ", true);
                    ArrayList<Vertex> terrain = TerrainBuild.xYListToOrthTerrain(vals);
                    System.out.println("The terrain is orthogonal: " + TerrainBuild.isOrthogonal(terrain));
                    System.out.println("The terrain is free of collinear vertices: " + TerrainBuild.isFreeOfCollinearVertices(terrain));
                    if (!TerrainBuild.isOrthogonal(terrain) || !TerrainBuild.isFreeOfCollinearVertices(terrain)) {
                        problem = true;
                    } else {
                        //set vType information and write to file
                        TerrainBuild.rightLeftVertex(terrain);
                        String newFileName = "Orth_" + file.getName();
                        FileReadAndWrite.toTxt(terrain, orthDir + newFileName);
                    }
                } catch (IOException err) {
                    System.out.println(err.getMessage());
                    System.out.println("The following file is affected: " + path);
                }
                System.out.println(" ");
            }

        }
        System.out.println("Problems: " + problem);

         */

/*

        //this was used to create AGBterrains by unfolding them
        File directoryPath = new File("TestFiles\\AGBFiles\\original\\polygons");
        File[] filesList = directoryPath.listFiles();
        assert filesList != null;

        for (File file : filesList) {
            try {

                String pathnameOrth = "TestFiles\\AGBFiles\\Orthogonalized\\" + "orthUnfolded_" + file.getName();
                //String pathnameNonOrth = "TestFiles\\AGBFiles\\NonOrthogonal\\" + "unfolded_" + file.getName();

                System.out.println(file.getName());
                ArrayList<double[]> vList =  FileReadAndWrite.readAGPLIB_unfold(file.getAbsolutePath());


                ArrayList<Vertex> terrain = TerrainBuild.xYListToNonOrthTerrainWithoutRemoval(vList);
                ArrayList<Vertex> terrainOrth = TerrainBuild.xYListToOrthTerrain(vList);

                //for the ones that were orthogonal already
                //ArrayList<Vertex> terrainOrth = TerrainBuild.xYListToNonOrthTerrainWithoutRemovalHelp(vList);
                //TerrainBuild.removeCollinearVertices(terrainOrth);
                TerrainBuild.rightLeftVertex(terrainOrth);

                System.out.println("Terrain-size: " + terrainOrth.size());
                System.out.println("Terrain " + file.getName() + " is orthogonal: " + TerrainBuild.isOrthogonal(terrainOrth));
                System.out.println("Terrain " + file.getName() + " is free: " + TerrainBuild.isFreeOfCollinearVertices(terrainOrth));


                if(vList.size()>0) {
                    //FileReadAndWrite.toTxtPoints(vList, pathnameNonOrth);
                }
                if(terrainOrth.size()>0) {
                    FileReadAndWrite.toTxt(terrainOrth, pathnameOrth);
                }



            } catch (IOException err) {
                System.out.println("error when unfolding");
            }
        }


 */



    }
}
