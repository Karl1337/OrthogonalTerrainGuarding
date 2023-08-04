package de.TerrainGuarding;

import sipura.graphs.SimpleGraph;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class setupExperiments {


    //method to create experiment files
    static void writeOrthFileRBVanilla() {

        String pathForFile = "TestFiles\\Experiments\\experimentsVanillaRB0.txt";
        String[] orthPaths = getOrthogonalLinks();
        String output_file = "TestFiles\\Results\\resultsVanillaRB0.txt";

        try {
            FileWriter experiments = new FileWriter(pathForFile, true);


            for (String orthPath : orthPaths) {

                File directoryPath = new File(orthPath);
                File[] filesList = directoryPath.listFiles();
                assert filesList != null;
                for (File file : filesList) {

                    /*
                    //partial tests
                    if(!file.getName().contains("1000000") && !file.getName().contains("500000")) {
                        continue;
                    }
                     */

                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveRBVanilla" + ";"
                            + 0 + ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + "\n");
                }
            }
            experiments.close();
        } catch (IOException err) {
            System.out.println("Error when writing results-file: " + err.getMessage());
        }
    }


    //method to create experiment files
    static void writeOrthFileRBReduction() {

        String pathForFile = "TestFiles\\Experiments\\experimentsReductionRB1.txt";
        String[] orthPaths = getOrthogonalLinks();
        String output_file = "TestFiles\\Results\\resultsReductionRB0.txt";

        try {
            FileWriter experiments = new FileWriter(pathForFile, true);


            for (String orthPath : orthPaths) {


                File directoryPath = new File(orthPath);
                File[] filesList = directoryPath.listFiles();
                assert filesList != null;
                for (File file : filesList) {

                    /*
                    //partial tests
                    if(file.getName().contains("1000000") || file.getName().contains("500000")){
                        continue;
                    }

                     */


                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveRBReduction" + ";" + 0 +
                            ";" + 3600 + ";" + -1 + ";" + -1 + ";" + -1 + ";" + 1.0 + ";" + 0 + ";" + 0 + "\n");
                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveRBReduction" + ";" + 0 +
                            ";" + 3600 + ";" + 10 + ";" + 10 + ";" + 40 + ";" + 0.9 + ";" + 0 + ";" + 0 + "\n");
                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveRBReduction" + ";" + 0 +
                            ";" + 3600 + ";" + 20 + ";" + 5 + ";" + 20 + ";" + 0.95 + ";" + 0 + ";" + 0 + "\n");
                }
            }
            experiments.close();
        } catch (IOException err) {
            System.out.println("Error when writing results-file: " + err.getMessage());
        }
    }


    //method to create experiment files
    static void writeOrthFileFullVis() {

        String pathForFile = "TestFiles\\Experiments\\experimentsOrthFullVisAll0.txt";
        String[] orthPaths = getOrthogonalLinks();
        String output_file = "TestFiles\\Results\\resultsOrthFullVisAll0.txt";

        try {
            FileWriter experiments = new FileWriter(pathForFile, true);


            for (String orthPath : orthPaths) {


                File directoryPath = new File(orthPath);
                File[] filesList = directoryPath.listFiles();
                assert filesList != null;
                for (File file : filesList) {

                    /*
                    //partial tests
                    if(file.getName().contains("1000000") || file.getName().contains("500000")){
                        continue;
                    }

                     */


                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveOrthFullVis" + ";" + 0 +
                            ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + "\n");
                }
            }
            experiments.close();
        } catch (IOException err) {
            System.out.println("Error when writing results-file: " + err.getMessage());
        }
    }


    //method to create experiment files
    static void writeOrthFileCallback() {

        String pathForFile = "TestFiles\\Experiments\\experimentsOrthCallback0.txt";
        String[] orthPaths = getOrthogonalLinks();
        String output_file = "TestFiles\\Results\\resultsOrthCallback0.txt";

        try {
            FileWriter experiments = new FileWriter(pathForFile, true);


            for (String orthPath : orthPaths) {


                File directoryPath = new File(orthPath);
                File[] filesList = directoryPath.listFiles();
                assert filesList != null;
                for (File file : filesList) {

                    /*
                    //partial tests
                    if(file.getName().contains("1000000") || file.getName().contains("500000")){
                        continue;
                    }

                     */


                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveOrthCallback" + ";" + 0 +
                            ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0.05 + ";" + 0.05 + "\n");

                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveOrthCallback" + ";" + 0 +
                            ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0.1 + ";" + 0.05 + "\n");

                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveOrthCallback" + ";" + 0 +
                            ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0.1 + ";" + 0.1 + "\n");

                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveOrthCallback" + ";" + 0 +
                            ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0.1 + ";" + 0.2 + "\n");


                }
            }
            experiments.close();
        } catch (IOException err) {
            System.out.println("Error when writing results-file: " + err.getMessage());
        }
    }


    static void writeNonOrthFullVis() {

        String pathForFile = "TestFiles\\Experiments\\experimentsFullVisAll0.txt";
        String[] nonOrthPaths = getNonOrthogonalLinks();
        String output_file = "TestFiles\\Results\\resultsFullVisOnlyAll0.txt";

        try {
            FileWriter experiments = new FileWriter(pathForFile, true);


            for (String orthPath : nonOrthPaths) {


                File directoryPath = new File(orthPath);
                File[] filesList = directoryPath.listFiles();
                assert filesList != null;
                for (File file : filesList) {


                    //partial tests
                    if (file.getName().contains("1000000") || file.getName().contains("500000")) {
                        continue;
                    }


                    //write results for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveFullVis" + ";"
                            + 0 + ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + "\n");
                }
            }
            experiments.close();
        } catch (IOException err) {
            System.out.println("Error when writing results-file: " + err.getMessage());
        }
    }


    static void writeReduceOnlyOne() {

        String pathForFile = "TestFiles\\Experiments\\experimentsReducSingle.txt";
        String[] orthPaths = getOrthogonalLinks();
        String output_file = "TestFiles\\Results\\resultsReducSingle.txt";

        try {
            FileWriter experiments = new FileWriter(pathForFile, true);


            for (String orthPath : orthPaths) {

                File directoryPath = new File(orthPath);
                File[] filesList = directoryPath.listFiles();
                assert filesList != null;
                for (File file : filesList) {


                    //only on smaller graphs
                    if (file.getName().contains("1000000") || file.getName().contains("500000")) {
                        continue;
                    }


                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "reduceSingle" + ";"
                            + 0 + ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + "\n");

                }
            }
            experiments.close();
        } catch (IOException err) {
            System.out.println("Error when writing results-file: " + err.getMessage());
        }
    }


    static void writeOrthFileFullVisOnlyRefl() {

        String pathForFile = "TestFiles\\Experiments\\experimentsOrthFullVisOnlyPlanckOnlyRefl0.txt";
        String[] orthPaths = getOrthogonalLinks();
        String output_file = "TestFiles\\Results\\resultsOrthFullOnlyPlanckOnlyRefl0.txt";

        try {
            FileWriter experiments = new FileWriter(pathForFile, true);


            for (String orthPath : Arrays.copyOfRange(orthPaths, 60, 69)) {


                File directoryPath = new File(orthPath);
                File[] filesList = directoryPath.listFiles();
                assert filesList != null;
                for (File file : filesList) {


                    //partial tests
                    if (file.getName().contains("1000000") || file.getName().contains("500000")) {
                        continue;
                    }


                    //write experiments for current terrain in line in results-file
                    experiments.write(output_file + ";" + orthPath + "\\" + file.getName() + ";" + "solveOrthFullVisOnlyRefl" + ";" + 0 +
                            ";" + 3600 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + ";" + 0 + "\n");
                }
            }
            experiments.close();
        } catch (IOException err) {
            System.out.println("Error when writing results-file: " + err.getMessage());
        }
    }


    static private String[] getOrthogonalLinks() {

        String[] orthPaths = new String[69];

        //set paths to linear valleys
        orthPaths[0] = "TestFiles\\GlobalValleyLin\\Orthogonal\\Noise0";
        orthPaths[1] = "TestFiles\\GlobalValleyLin\\Orthogonal\\Noise10";
        orthPaths[2] = "TestFiles\\GlobalValleyLin\\Orthogonal\\Noise20";
        orthPaths[3] = "TestFiles\\GlobalValleyLin\\Orthogonal\\Noise30";
        orthPaths[4] = "TestFiles\\GlobalValleyLin\\Orthogonal\\Noise40";
        orthPaths[5] = "TestFiles\\GlobalValleyLin\\Orthogonal\\Noise50";


        //set paths to quadratic valleys
        orthPaths[6] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise10\\Noise0";
        orthPaths[7] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise10\\Noise10";
        orthPaths[8] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise10\\Noise20";
        orthPaths[9] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise10\\Noise30";
        orthPaths[10] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise10\\Noise40";
        orthPaths[11] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise10\\Noise50";

        orthPaths[12] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise300\\Noise0";
        orthPaths[13] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise300\\Noise10";
        orthPaths[14] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise300\\Noise20";
        orthPaths[15] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise300\\Noise30";
        orthPaths[16] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise300\\Noise40";
        orthPaths[17] = "TestFiles\\GlobalValleyQuad\\Orthogonal\\MultiplierNoise300\\Noise50";


        //set paths to steep valleys
        orthPaths[18] = "TestFiles\\GlobalValleySteep\\Orthogonal\\LowNoise\\Noise0";
        orthPaths[19] = "TestFiles\\GlobalValleySteep\\Orthogonal\\LowNoise\\Noise10";
        orthPaths[20] = "TestFiles\\GlobalValleySteep\\Orthogonal\\LowNoise\\Noise20";
        orthPaths[21] = "TestFiles\\GlobalValleySteep\\Orthogonal\\LowNoise\\Noise30";
        orthPaths[22] = "TestFiles\\GlobalValleySteep\\Orthogonal\\LowNoise\\Noise40";
        orthPaths[23] = "TestFiles\\GlobalValleySteep\\Orthogonal\\LowNoise\\Noise50";

        orthPaths[24] = "TestFiles\\GlobalValleySteep\\Orthogonal\\HighNoise\\Noise0";
        orthPaths[25] = "TestFiles\\GlobalValleySteep\\Orthogonal\\HighNoise\\Noise10";
        orthPaths[26] = "TestFiles\\GlobalValleySteep\\Orthogonal\\HighNoise\\Noise20";
        orthPaths[27] = "TestFiles\\GlobalValleySteep\\Orthogonal\\HighNoise\\Noise30";
        orthPaths[28] = "TestFiles\\GlobalValleySteep\\Orthogonal\\HighNoise\\Noise40";
        orthPaths[29] = "TestFiles\\GlobalValleySteep\\Orthogonal\\HighNoise\\Noise50";


        //set paths to linear valleys crooked
        orthPaths[30] = "TestFiles\\Crooked\\ValleyLin\\Orthogonal\\Noise0";
        orthPaths[31] = "TestFiles\\Crooked\\ValleyLin\\Orthogonal\\Noise10";
        orthPaths[32] = "TestFiles\\Crooked\\ValleyLin\\Orthogonal\\Noise20";
        orthPaths[33] = "TestFiles\\Crooked\\ValleyLin\\Orthogonal\\Noise30";
        orthPaths[34] = "TestFiles\\Crooked\\ValleyLin\\Orthogonal\\Noise40";
        orthPaths[35] = "TestFiles\\Crooked\\ValleyLin\\Orthogonal\\Noise50";


        //set paths to quadratic valleys crooked
        orthPaths[36] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier10\\Noise0";
        orthPaths[37] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier10\\Noise10";
        orthPaths[38] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier10\\Noise20";
        orthPaths[39] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier10\\Noise30";
        orthPaths[40] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier10\\Noise40";
        orthPaths[41] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier10\\Noise50";

        orthPaths[42] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier300\\Noise0";
        orthPaths[43] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier300\\Noise10";
        orthPaths[44] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier300\\Noise20";
        orthPaths[45] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier300\\Noise30";
        orthPaths[46] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier300\\Noise40";
        orthPaths[47] = "TestFiles\\Crooked\\ValleyQuad\\Orthogonal\\NoiseMultiplier300\\Noise50";


        //set paths to steep valleys crooked
        orthPaths[48] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\LowNoise\\Noise0";
        orthPaths[49] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\LowNoise\\Noise10";
        orthPaths[50] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\LowNoise\\Noise20";
        orthPaths[51] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\LowNoise\\Noise30";
        orthPaths[52] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\LowNoise\\Noise40";
        orthPaths[53] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\LowNoise\\Noise50";

        orthPaths[54] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\HighNoise\\Noise0";
        orthPaths[55] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\HighNoise\\Noise10";
        orthPaths[56] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\HighNoise\\Noise20";
        orthPaths[57] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\HighNoise\\Noise30";
        orthPaths[58] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\HighNoise\\Noise40";
        orthPaths[59] = "TestFiles\\Crooked\\ValleySteep\\Orthogonal\\HighNoise\\Noise50";


        //set paths to the orthogonalized planck instances
        orthPaths[60] = "TestFiles\\PlanckFiles\\InstancesPlanckOrthogonalized\\blancmange";
        orthPaths[61] = "TestFiles\\PlanckFiles\\InstancesPlanckOrthogonalized\\concavevalleys";
        orthPaths[62] = "TestFiles\\PlanckFiles\\InstancesPlanckOrthogonalized\\manual";
        orthPaths[63] = "TestFiles\\PlanckFiles\\InstancesPlanckOrthogonalized\\parabolawalk";
        orthPaths[64] = "TestFiles\\PlanckFiles\\InstancesPlanckOrthogonalized\\sinewalk";
        orthPaths[65] = "TestFiles\\PlanckFiles\\InstancesPlanckOrthogonalized\\walk";


        //set path to randomized orthogonal terrains
        orthPaths[66] = "TestFiles\\Randomized50K\\Orthogonal";


        //set path to orthogonal AGPfiles
        orthPaths[67] = "TestFiles\\AGBFiles\\Orthogonal";
        orthPaths[68] = "TestFiles\\AGBFiles\\Orthogonalized";

        return orthPaths;
    }

    static private String[] getNonOrthogonalLinks() {

        String[] orthPaths = new String[68];

        //set paths to linear valleys
        orthPaths[0] = "TestFiles\\GlobalValleyLin\\NonOrthogonal\\Noise0";
        orthPaths[1] = "TestFiles\\GlobalValleyLin\\NonOrthogonal\\Noise10";
        orthPaths[2] = "TestFiles\\GlobalValleyLin\\NonOrthogonal\\Noise20";
        orthPaths[3] = "TestFiles\\GlobalValleyLin\\NonOrthogonal\\Noise30";
        orthPaths[4] = "TestFiles\\GlobalValleyLin\\NonOrthogonal\\Noise40";
        orthPaths[5] = "TestFiles\\GlobalValleyLin\\NonOrthogonal\\Noise50";


        //set paths to quadratic valleys
        orthPaths[6] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise10\\Noise0";
        orthPaths[7] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise10\\Noise10";
        orthPaths[8] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise10\\Noise20";
        orthPaths[9] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise10\\Noise30";
        orthPaths[10] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise10\\Noise40";
        orthPaths[11] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise10\\Noise50";

        orthPaths[12] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise300\\Noise0";
        orthPaths[13] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise300\\Noise10";
        orthPaths[14] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise300\\Noise20";
        orthPaths[15] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise300\\Noise30";
        orthPaths[16] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise300\\Noise40";
        orthPaths[17] = "TestFiles\\GlobalValleyQuad\\NonOrthogonal\\MultiplierNoise300\\Noise50";


        //set paths to steep valleys
        orthPaths[18] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\LowNoise\\Noise0";
        orthPaths[19] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\LowNoise\\Noise10";
        orthPaths[20] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\LowNoise\\Noise20";
        orthPaths[21] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\LowNoise\\Noise30";
        orthPaths[22] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\LowNoise\\Noise40";
        orthPaths[23] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\LowNoise\\Noise50";

        orthPaths[24] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\HighNoise\\Noise0";
        orthPaths[25] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\HighNoise\\Noise10";
        orthPaths[26] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\HighNoise\\Noise20";
        orthPaths[27] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\HighNoise\\Noise30";
        orthPaths[28] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\HighNoise\\Noise40";
        orthPaths[29] = "TestFiles\\GlobalValleySteep\\NonOrthogonal\\HighNoise\\Noise50";


        //set paths to linear valleys crooked
        orthPaths[30] = "TestFiles\\Crooked\\ValleyLin\\NonOrthogonal\\Noise0";
        orthPaths[31] = "TestFiles\\Crooked\\ValleyLin\\NonOrthogonal\\Noise10";
        orthPaths[32] = "TestFiles\\Crooked\\ValleyLin\\NonOrthogonal\\Noise20";
        orthPaths[33] = "TestFiles\\Crooked\\ValleyLin\\NonOrthogonal\\Noise30";
        orthPaths[34] = "TestFiles\\Crooked\\ValleyLin\\NonOrthogonal\\Noise40";
        orthPaths[35] = "TestFiles\\Crooked\\ValleyLin\\NonOrthogonal\\Noise50";


        //set paths to quadratic valleys crooked
        orthPaths[36] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier10\\Noise0";
        orthPaths[37] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier10\\Noise10";
        orthPaths[38] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier10\\Noise20";
        orthPaths[39] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier10\\Noise30";
        orthPaths[40] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier10\\Noise40";
        orthPaths[41] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier10\\Noise50";

        orthPaths[42] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier300\\Noise0";
        orthPaths[43] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier300\\Noise10";
        orthPaths[44] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier300\\Noise20";
        orthPaths[45] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier300\\Noise30";
        orthPaths[46] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier300\\Noise40";
        orthPaths[47] = "TestFiles\\Crooked\\ValleyQuad\\NonOrthogonal\\NoiseMultiplier300\\Noise50";


        //set paths to steep valleys crooked
        orthPaths[48] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\LowNoise\\Noise0";
        orthPaths[49] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\LowNoise\\Noise10";
        orthPaths[50] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\LowNoise\\Noise20";
        orthPaths[51] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\LowNoise\\Noise30";
        orthPaths[52] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\LowNoise\\Noise40";
        orthPaths[53] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\LowNoise\\Noise50";

        orthPaths[54] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\HighNoise\\Noise0";
        orthPaths[55] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\HighNoise\\Noise10";
        orthPaths[56] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\HighNoise\\Noise20";
        orthPaths[57] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\HighNoise\\Noise30";
        orthPaths[58] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\HighNoise\\Noise40";
        orthPaths[59] = "TestFiles\\Crooked\\ValleySteep\\NonOrthogonal\\HighNoise\\Noise50";


        //set paths to the orthogonalized planck instances
        orthPaths[60] = "TestFiles\\PlanckFiles\\InstancesPlanck\\blancmange";
        orthPaths[61] = "TestFiles\\PlanckFiles\\InstancesPlanck\\concavevalleys";
        orthPaths[62] = "TestFiles\\PlanckFiles\\InstancesPlanck\\manual";
        orthPaths[63] = "TestFiles\\PlanckFiles\\InstancesPlanck\\parabolawalk";
        orthPaths[64] = "TestFiles\\PlanckFiles\\InstancesPlanck\\sinewalk";
        orthPaths[65] = "TestFiles\\PlanckFiles\\InstancesPlanck\\walk";


        //set path to randomized orthogonal terrains
        orthPaths[66] = "TestFiles\\Randomized50K\\NonOrthogonal";


        //set path to non-orthogonal AGPFiles
        orthPaths[67] = "TestFiles\\AGBFiles\\NonOrthogonal";

        return orthPaths;
    }


}