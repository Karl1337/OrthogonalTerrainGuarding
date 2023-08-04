package de.TerrainGuarding;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;


public class FileReadAndWrite {
    /**
     * Method that reads a .txt file containing x and y values of points line by line in the following line format:
     * xValue[]yValue. [] represents any kind of unique spacer between the x and y value like e.g. " " or ", ". The
     * lines of the file must be sorted in ascending order by x values
     *
     * @param filepath    location of the .txt file to read
     * @param regexInFile unique spacer used in the file in every line (e.g. " " or ", ")
     * @return ArrayList that contains the x and y values of the point set stored in arrays of type double
     * @throws IOException if there occurs any error when reading the file
     */
    static ArrayList<double[]> xYtxtToArrayList(String filepath, String regexInFile, boolean skipFirstLine) throws IOException {
        File terrainFile = new File(filepath);
        //read in file
        Scanner terrainFileScan = new Scanner(terrainFile);
        ArrayList<double[]> list = new ArrayList<>();

        if (!terrainFileScan.hasNextLine()) {
            System.out.println("File contains no lines. Emptyset will be returned");
            return list;
        }

        //skip first line whenever necessary
        if (skipFirstLine) {
            terrainFileScan.nextLine();
        }

        //go through file line by line and store rounded x and y values in ArrayList
        while (terrainFileScan.hasNextLine()) {
            String[] vert = terrainFileScan.nextLine().split(regexInFile);
            double[] xy = {Double.parseDouble(vert[0]), Double.parseDouble(vert[1])};
            list.add(xy);
        }
        terrainFileScan.close();
        return list;
    }



    /**
     * Method that reads a .txt file representing an orthogonal terrain. The .txt file must contain the vertex
     * information line by line in the following format: vertexID xValue yValue vertexType. The vertex IDs must hereby
     * start with 0 and end with #vertices-1 and be sorted in ascending order in the file. Further the IDs must be chosen, such
     * that the x values are also sorted in ascending order automatically when sorting by ID. The method totxt is
     * designed, such that all attributes of vertices get written such that this function can read it.
     *
     * @param filepath path incl. filename + .txt where the file to read is located
     * @return ArrayList containing the vertices of the terrain in the same order as the file (ascending by ID and
     * therefore also automatically by x value)
     * @throws IOException if there occurs any error when reading the file
     */
    static ArrayList<Vertex> readOrthTerrainTxt(String filepath) throws IOException {

        File terrainFile = new File(filepath);
        //read in file
        Scanner terrainFileScan = new Scanner(terrainFile);
        Vertex v;
        ArrayList<Vertex> list = new ArrayList<>();
        while (terrainFileScan.hasNextLine()) {
            String[] vert = terrainFileScan.nextLine().split(" ");
            v = new Vertex(Integer.parseInt(vert[0]), Double.parseDouble(vert[1]), Double.parseDouble(vert[2]), vert[3]);
            list.add(v);
        }
        terrainFileScan.close();
        return list;
    }



    /**
     * Method that reads a .txt file representing a polygon and unfolds it by rotating all non visited vertices
     * by 180 degree around the last visited one whenever the current would violate x monotony
     *
     * @param filepath path incl. filename + .txt where the file to read is located
     * @return ArrayList containing the vertices of the terrain in the same order as in the file
     * @throws IOException
     */
    static ArrayList<double[]> readAGPLIB_unfold(String filepath) throws IOException {
        File aGPFile = new File(filepath);
        Scanner aGPFileScan = new Scanner(aGPFile);
        //read all values in string format
        ArrayList<String> list = new ArrayList<>();
        while (aGPFileScan.hasNext()) {
            list.add(aGPFileScan.next());
        }

        list.remove(list.get(0)); //remove first value, this just contains amount of vertices in file

        //create ArrayList with doubles
        ArrayList<Double> doublevals = new ArrayList<>();
        for (String str : list) {
            String[] vals = str.split("/");
            if (vals.length<2){
                continue;
            }
            doublevals.add(Double.parseDouble(vals[0]) / Double.parseDouble(vals[1]));
        }
        //Create ArrayList with double arrays that contains the vertices of the polygon in counterclockwise order
        ArrayList<double[]> vList = new ArrayList<>();
        for (int i = 0; i < doublevals.size(); i = i + 2) {
            vList.add(new double[]{doublevals.get(i), doublevals.get(i + 1)});
        }

        double x_last = vList.get(0)[0];
        //We now unfold the whole terrain.
        int listSize = vList.size();
        for (int i = 1; i < listSize; i++) {
            //in case the current vertex is not left of the last one, there is nothing to do
            if (vList.get(i)[0] >= x_last) {
                //do noting!
            } else {
                //initially this function was meant to rotate by custom angle
                //calculate alpha, the angle between the edges incident to the last vertex
                double[] lastV = new double[]{vList.get(i - 1)[0], vList.get(i - 1)[1]};
                //double[] edge0 = new double[]{vList.get(i - 1)[0] - vList.get(i - 2)[0], vList.get(i - 1)[1] - vList.get(i - 2)[1]};
                //double[] edge1 = new double[]{vList.get(i - 1)[0] - vList.get(i)[0], vList.get(i - 1)[1] - vList.get(i)[1]};
                //double normEdge0 = Math.sqrt(Math.pow(edge0[0], 2) + Math.pow(edge0[0], 2));
                //double normEdge1 = Math.sqrt(Math.pow(edge1[0], 2) + Math.pow(edge1[0], 2));
                //double scaleprod = edge0[0] * edge1[0] + edge0[1] * edge1[1];
                //double alpha = Math.acos(scaleprod / (normEdge0 * normEdge1));
                double beta = 180.0 * (Math.PI / 180);
                //now rotate all vertices of the polygon around the last visited vertex (i-1)
                for (int j = i; j < listSize; j++) {
                    double[] rotatedVertex = new double[2];
                    double[] currV = new double[]{vList.get(j)[0], vList.get(j)[1]};
                    //double[] helpV = new double[]{lastV[0] - currV[0], lastV[1] - currV[1]};
                    double[] helpV = new double[]{currV[0] - lastV[0], currV[1] - lastV[1]};
                    //rotate helpV
                    rotatedVertex[0] = Math.cos(beta) * helpV[0] + (-1) * Math.sin(beta) * helpV[1] + lastV[0];
                    rotatedVertex[1] = Math.sin(beta) * helpV[0] + Math.cos(beta) * helpV[1] + lastV[1];
                    vList.set(j, rotatedVertex);
                }
            }
            x_last = vList.get(i)[0];
        }

        return vList;
    }



    /**
     * Method that writes a .txt file containing the vertex information of a given terrain line by line to a specified
     * location
     *
     * @param list     the ArrayList representing the orthogonal Terrain you want to write to a .txt file
     * @param filePath the location you want to store the file (including filename + .txt). If already exists, existing
     *                 file gets overwritten!
     */
    static void toTxt(ArrayList<Vertex> list, String filePath) throws IOException {
        FileWriter terrainFile = new FileWriter(filePath);
        for (Vertex v : list) {
            terrainFile.write(v.getID() + " " + v.getX() + " " + v.getY() + " " + v.getvType() + "\n");
        }
        terrainFile.close();
    }


    /** Method that writes a .txt file from an array containing double arrays (to create points only files without vertex information)
     *
     * @param list ArrayList that contains point information
     * @param filePath  path where to store .txt file
     */
    static void toTxtPoints(ArrayList<double[]> list, String filePath) {
        try {
            FileWriter terrainFile = new FileWriter(filePath);
            for (int i = 0; i < list.size(); i++) {
                terrainFile.write(list.get(i)[0] + " " + list.get(i)[1] + "\n");
            }
            terrainFile.close();
        } catch (IOException err) {
            System.out.println(err.getMessage());
        }
    }


}
