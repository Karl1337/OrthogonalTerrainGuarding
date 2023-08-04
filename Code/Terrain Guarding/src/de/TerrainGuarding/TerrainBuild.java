package de.TerrainGuarding;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;
import java.util.*;


public class TerrainBuild {


    /** Method that creates set of random points of increasing x value and writes it to .txt file.
     *
     * @param filePath path where to store txt
     * @param pointNum number of x-y pairs to be created
     * @param min_y minimum y value
     * @param max_y maximum y value
     * @param min_stepSize minimal step size
     * @param max_stepSize maximal step size
     */
    static void randomPoints(String filePath, int pointNum, double min_y, double max_y, double min_stepSize, double max_stepSize) {
        if (pointNum < 2) {
            System.out.println("minimum size for pointNum is 2");
            return;
        }
        if (min_y > max_y - 0.1) {
            System.out.println("min_y must be at most max_y - 0.1");
            return;
        }
        if (min_stepSize > max_stepSize) {
            System.out.println("min_stepSize must be at most max_stepSize");
            return;
        }
        if (min_stepSize < 0.1) {
            System.out.println("min_stepSize must be at least 0.1");
            return;
        }

        try {
            FileWriter rndPoints = new FileWriter(filePath);
            double last_x = 0.0; //helpvar that saves last x value in the iteration to add to next generated value (set to 0.0 for first iteration)
            double last_y = 0.0; //helpvar that saves the last y value of iteration in order to prohibit getting the same y value 2 times in the row (set to 0.0 for the while loop in first iteration)
            double y_value = 0.0; //contains y value for the terrain of current iteration (set to 0.0 for the while loop in first iteration)
            double x_value; //contains x value for the terrain of current iteration

            for (int i = 0; i < pointNum; i++) {
                while (last_y == y_value) { //this prevents getting the same y_value 2 times in the row
                    y_value = Math.random() * (max_y - min_y) + min_y; //assign random value between min_y and max_y to y_value
                }
                x_value = Math.random() * (max_stepSize - min_stepSize) + min_stepSize + last_x; //assign random value between min_stepSize and max_stepSize plus last x_value

                if (i < pointNum - 1) {
                    rndPoints.write(x_value + " " + y_value + "\n"); //write a row x and y value into .txt file
                } else {
                    rndPoints.write(x_value + " " + y_value); //write a row containing x and y value into .txt file
                }
                last_x = x_value; //save x_value of the current iteration for the next step
                last_y = y_value; //save y_value of the current iteration for the next step
            }
            rndPoints.close();
        } catch (IOException err) {
            System.out.println("The following Error occurred when writing the files: " + err);
        }
    }


    /** Method that removes consecutive collinear vertices from orthogonal terrains
     *
     * @param terrain orthogonal terrain
     */
    static void removeCollinearVertices(ArrayList<Vertex> terrain) {
        ArrayList<Vertex> toRemove = new ArrayList<>();
        for (int i = 1; i < terrain.size() - 1; i++) {
            if (terrain.get(i - 1).getX() == terrain.get(i + 1).getX()) {
                toRemove.add(terrain.get(i));
            }
            if (terrain.get(i - 1).getY() == terrain.get(i + 1).getY()) {
                toRemove.add(terrain.get(i));
            }
        }
        //reset IDs ascending by appearance
        for(Vertex v : toRemove){
            terrain.remove(v);
        }


        int IDcount = 0;
        for (Vertex v : terrain) {
            v.setID(IDcount);
            IDcount++;
        }
    }


    /**
     * Method that checks if a orthogonal terrain is free of consecutive collinear vertices (that means no 180 degree vertices)
     *
     * @param list list representing the orthogonal terrain you want to check
     * @return true if the terrain has no 180 degree vertices, false if it has
     */
    static boolean isFreeOfCollinearVertices(ArrayList<Vertex> list) {
        boolean ans = true;
        for (int i = 0; i < list.size() - 2; i++) {
            if (list.get(i).getY() == list.get(i + 1).getY() && list.get(i).getY() == list.get(i + 2).getY()) {
                ans = false;
            } else if (list.get(i).getX() == list.get(i + 1).getX() && list.get(i).getX() == list.get(i + 2).getX()) {
                ans = false;
            }
        }
        return ans;
    }


    /** Method that creates point set of increasing x values that globally resembles a linear like "V" shape and saves it to txt.
     *
     *
     * @param filePath path where to store .txt
     * @param pointNum  number of x-y pairs to be created
     * @param min_ySteps minimum stepsize in vertical direction
     * @param max_ySteps maximum stepsize in vertical direction
     * @param min_xSteps minium stepsize in horizontal direction
     * @param max_xSteps maximum stepsize in horizontal direction
     * @param turnpoint where the minimum of "V" is located (between 0 and pointNum)
     * @param prob_breakOut probability for adding noise
     */
    static void randomPointsValleyBreakOut(String filePath, int pointNum, double min_ySteps, double max_ySteps, double min_xSteps, double max_xSteps, int turnpoint, double prob_breakOut) {
        if (pointNum < 2) {
            System.out.println("minimum size for pointNum is 2");
            return;
        }
        if (prob_breakOut < 0 || prob_breakOut > 0.9) {
            System.out.println("probability for breakout must be between 0 and 0.9");
            return;
        }
        if (turnpoint < 0 || turnpoint > pointNum) {
            System.out.println("turnpoint must lie between 0 and pointNum");
            return;
        }
        if (min_ySteps > max_ySteps - 0.1) {
            System.out.println("min_ySteps must be at most max_ySteps - 0.1");
            return;
        }
        if (min_xSteps > max_xSteps) {
            System.out.println("min_xSteps must be at most max_xSteps");
            return;
        }
        if (min_xSteps < 0.1) {
            System.out.println("min_xSteps must be at least 0.1");
            return;
        }
        double start = Math.max(turnpoint, pointNum - turnpoint) * (max_ySteps - min_ySteps);
        try {
            FileWriter rndPoints = new FileWriter(filePath);
            double last_x = 0.0; //helpvar that saves last x value in the iteration to add to next generated value (set to 0.0 for first iteration)
            double last_y = start; //helpvar that saves the last y value of iteration in order to prohibit getting the same y value 2 times in the row (set to 0.0 for the while loop in first iteration)
            double y_value = 0.0; //contains y value for the terrain of current iteration (set to 0.0 for the while loop in first iteration)
            double x_value; //contains x value for the terrain of current iteration

            for (int i = 0; i < pointNum; i++) {
                if (i < turnpoint) {
                    if (Math.random() < prob_breakOut) {
                        y_value = last_y + Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    } else {
                        y_value = last_y - Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    }
                } else {
                    if (Math.random() < prob_breakOut) {
                        y_value = last_y - Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    } else {
                        y_value = last_y + Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    }
                }

                x_value = Math.random() * (max_xSteps - min_xSteps) + min_xSteps + last_x; //assign random value between min_stepSize and max_stepSize plus last x_value

                if (i < pointNum - 1) {
                    rndPoints.write(x_value + " " + y_value + "\n"); //write a row x and y value into .txt file
                } else {
                    rndPoints.write(x_value + " " + y_value); //write a row containing x and y value into .txt file
                }
                last_x = x_value; //save x_value of the current iteration for the next step
                last_y = y_value; //save y_value of the current iteration for the next step
            }
            rndPoints.close();
        } catch (IOException err) {
            System.out.println("The following Error occurred when writing the file: " + err);
        }
    }


    /** Method that creates point set of increasing x values that globally resembles a quadratic like "U" shape and saves it to txt.
     *
     *
     * @param filePath path where to store .txt
     * @param constant constant factor of quadratic function (constant * x^2)
     * @param pointNum number of x-y pairs to be created
     * @param multiplierBreakout multiplier for controlling noise
     * @param min_xSteps minium stepsize in horizontal direction
     * @param max_xSteps maximum stepsize in horizontal direction
     * @param turnpoint where the minimum of "U" is located (between 0 and pointNum)
     * @param prob_breakOut probability for adding noise
     */
    static void randomPointsValleyBreakOutQuadVariable(String filePath, double constant, int pointNum, double multiplierBreakout, double min_xSteps, double max_xSteps, int turnpoint, double prob_breakOut) {
        if (pointNum < 2) {
            System.out.println("minimum size for pointNum is 2");
            return;
        }
        if (prob_breakOut < 0 || prob_breakOut > 0.9) {
            System.out.println("probability for breakout must be between 0 and 0.9");
            return;
        }
        if (turnpoint < 0 || turnpoint > pointNum) {
            System.out.println("turnpoint must lie between 0 and pointNum");
            return;
        }

        if (min_xSteps > max_xSteps) {
            System.out.println("min_xSteps must be at most max_xSteps");
            return;
        }
        if (min_xSteps < 0.1) {
            System.out.println("min_xSteps must be at least 0.1");
            return;
        }

        try {
            FileWriter rndPoints = new FileWriter(filePath);
            double last_y = 0.0; //helpvar that saves the last y value of iteration in order to prohibit getting the same y value 2 times in the row (set to 0.0 for the while loop in first iteration)
            double y_value = 0.0; //contains y value for the terrain of current iteration (set to 0.0 for the while loop in first iteration)

            double[] x_vals = new double[pointNum];
            x_vals[0] = Math.random() * (max_xSteps - min_xSteps) + min_xSteps;
            for (int i = 1; i < pointNum; i++) {
                x_vals[i] = Math.random() * (max_xSteps - min_xSteps) + min_xSteps + x_vals[i - 1]; //assign random value between min_stepSize and max_stepSize plus last x_value
            }
            double shift = x_vals[turnpoint];

            for (int i = 0; i < pointNum; i++) {
                if (i < turnpoint) {
                    if (Math.random() < prob_breakOut && i > 0) {
                        if (Math.random() < 0.5) {
                            y_value = last_y + multiplierBreakout * Math.random() * constant * Math.abs((x_vals[i - 1] - shift) * (x_vals[i - 1] - shift) - (x_vals[i] - shift) * (x_vals[i] - shift)); //assign random val
                        } else {
                            y_value = last_y - multiplierBreakout * Math.random() * constant * Math.abs((x_vals[i - 1] - shift) * (x_vals[i - 1] - shift) - (x_vals[i] - shift) * (x_vals[i] - shift)); //assign random val
                        }
                    } else {
                        y_value = constant * (x_vals[i] - shift) * (x_vals[i] - shift);
                        //y_value = last_y - Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    }
                } else {
                    if (Math.random() < prob_breakOut) {
                        if (Math.random() < 0.5) {
                            y_value = last_y + multiplierBreakout * Math.random() * constant * Math.abs((x_vals[i - 1] - shift) * (x_vals[i - 1] - shift) - (x_vals[i] - shift) * (x_vals[i] - shift)); //assign random value between min_y and max_y to y_value
                        } else {
                            y_value = last_y - multiplierBreakout * Math.random() * constant * Math.abs((x_vals[i - 1] - shift) * (x_vals[i - 1] - shift) - (x_vals[i] - shift) * (x_vals[i] - shift)); //assign random value between min_y and max_y to y_value
                        }
                    } else {
                        y_value = constant * (x_vals[i] - shift) * (x_vals[i] - shift);
                        //y_value = stepsRight*stepsRight*pointNum;
                        //stepsRight += rightSplit;
                        //y_value = last_y + Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    }
                }

                if (i < pointNum - 1) {
                    rndPoints.write(x_vals[i] + " " + y_value + "\n"); //write a row x and y value into .txt file
                } else {
                    rndPoints.write(x_vals[i] + " " + y_value); //write a row containing x and y value into .txt file
                }
                last_y = y_value; //save y_value of the current iteration for the next step
            }
            rndPoints.close();
        } catch (IOException err) {
            System.out.println("The following Error occurred when writing the file: " + err);
        }
    }


    /** Method that is used to make terrains crooked
     *
     * @param terrain any terrain
     * @param factor factor by which to crook everything to the left of turnpoint
     * @param turnpoint place up to which the terrain gets crooked
     */
    static void crooked(ArrayList<double[]> terrain, double factor, int turnpoint) {
        double shift = terrain.get(turnpoint)[0];
        for (int i = 0; i < turnpoint; i++) {
            double shiftedVal = terrain.get(i)[0] - shift;
            terrain.get(i)[0] = shiftedVal * factor + shift;
        }
    }


    /** Method that creates point set of increasing x values that globally resembles a "V" like shape with lines that bend
     * towards the center and saves it to txt.
     *
     *
     * @param filePath path where to store .txt
     * @param pointNum number of x-y pairs to be created
     * @param min_ySteps minimum stepsize in vertical direction
     * @param max_ySteps maximum stepsize in vertical direction
     * @param min_xSteps minimum stepsize in horizontal direction
     * @param max_xSteps maximum stepsize in horizontal direction
     * @param turnpoint where the minimum of "V" is located (between 0 and pointNum)
     * @param prob_breakOut probability for adding noise
     */
    static void randomPointsValleyBreakOutSteep(String filePath, int pointNum, double min_ySteps, double max_ySteps, double min_xSteps, double max_xSteps, int turnpoint, double prob_breakOut) {
        if (pointNum < 2) {
            System.out.println("minimum size for pointNum is 2");
            return;
        }
        if (prob_breakOut < 0 || prob_breakOut > 0.9) {
            System.out.println("probability for breakout must be between 0 and 0.9");
            return;
        }
        if (turnpoint < 0 || turnpoint > pointNum - 2) {
            System.out.println("turnpoint must lie between 0 and pointNum -2");
            return;
        }
        if (min_ySteps > max_ySteps - 0.1) {
            System.out.println("min_ySteps must be at most max_ySteps - 0.1");
            return;
        }
        if (min_xSteps > max_xSteps) {
            System.out.println("min_xSteps must be at most max_xSteps");
            return;
        }
        if (min_xSteps < 0.1) {
            System.out.println("min_xSteps must be at least 0.1");
            return;
        }

        try {
            FileWriter rndPoints = new FileWriter(filePath);
            double last_y = 0.0; //helpvar that saves the last y value of iteration in order to prohibit getting the same y value 2 times in the row (set to 0.0 for the while loop in first iteration)
            double y_value = 0.0; //contains y value for the terrain of current iteration (set to 0.0 for the while loop in first iteration)

            double[] x_vals = new double[pointNum];
            x_vals[0] = Math.random() * (max_xSteps - min_xSteps) + min_xSteps;
            for (int i = 1; i < pointNum; i++) {
                x_vals[i] = Math.random() * (max_xSteps - min_xSteps) + min_xSteps + x_vals[i - 1]; //assign random value between min_stepSize and max_stepSize plus last x_value
            }
            //double shift = x_vals[turnpoint];

            for (int i = 0; i < pointNum; i++) {
                if (i < turnpoint) {
                    if (Math.random() < prob_breakOut) {
                        if (Math.random() < 0.5) {
                            y_value = last_y + Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                        } else {
                            y_value = last_y - Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                        }
                    } else {
                        y_value = -(x_vals[i]) * (x_vals[i]);
                        //y_value = last_y - Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    }
                } else {
                    if (Math.random() < prob_breakOut) {
                        if (Math.random() < 0.5) {
                            y_value = last_y + Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                        } else {
                            y_value = last_y - Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                        }
                    } else {
                        //y_value = -(x_vals[pointNum + turnpoint - 1 - i]) * (x_vals[pointNum + turnpoint - 1 - i]);
                        y_value = -(x_vals[i] - x_vals[pointNum - 1]) * (x_vals[i] - x_vals[pointNum - 1]) + (-x_vals[turnpoint] * x_vals[turnpoint] + (x_vals[turnpoint + 1] - x_vals[pointNum - 1]) * (x_vals[turnpoint + 1] - x_vals[pointNum - 1]));
                        //y_value = stepsRight*stepsRight*pointNum;
                        //stepsRight += rightSplit;
                        //y_value = last_y + Math.random() * (max_ySteps - min_ySteps); //assign random value between min_y and max_y to y_value
                    }
                }

                if (i < pointNum - 1) {
                    rndPoints.write(x_vals[i] + " " + y_value + "\n"); //write a row x and y value into .txt file
                } else {
                    rndPoints.write(x_vals[i] + " " + y_value); //write a row containing x and y value into .txt file
                }
                last_y = y_value; //save y_value of the current iteration for the next step
            }
            rndPoints.close();
        } catch (IOException err) {
            System.out.println("The following Error occurred when writing the file: " + err);
        }
    }

    /** Method that concatenates terrains by shifting their values such that the first vertex of the one
     * and last vertex of the other terrain has the same position
     *
     * @param terrain1
     * @param terrain2
     */
    static void concatenateTerrains(ArrayList<Vertex> terrain1, ArrayList<Vertex> terrain2) {
        double last_y = terrain1.get(terrain1.size() - 1).getY();
        double last_x = terrain1.get(terrain1.size() - 1).getX();
        double first_y = terrain2.get(0).getY();
        double first_x = terrain2.get(0).getX();

        //offset second terrain so that the last vertex of terrain1 and the first of terrain 2 meet
        for (Vertex v : terrain2) {
            v.setX(v.getX() - (first_x - last_x));
            v.setY(v.getY() - (first_y - last_y));
        }

        //now remove the first vertex in terrain 2 to avoid duplicate
        terrain2.remove(0);

        //add all vertices to second terrain
        terrain1.addAll(terrain2);

        //remove collinear vertices that might pop up where the terrains meet. This also resets the IDs
        //in ascending order along the terrain
        removeCollinearVertices(terrain1);
    }


    /**
     * Method that checks for a given terrain whether it is orthogonal.
     *
     * @param list the ArrayList representing the terrain you want to check
     * @return true if terrain is orthogonal, false if not
     */
    static boolean isOrthogonal(ArrayList<Vertex> list) {
        if (list.size() == 0) {
            System.out.println("The terrain has size 0 and hence is not orthogonal.");
            return false;
        }
        boolean orth = true;
        //check orthogonal
        for (int i = 1; i < list.size() - 1; i++) {
            if (list.get(i).getX() == list.get(i - 1).getX() && list.get(i).getY() == list.get(i + 1).getY()) {
                //do nothing
            } else if (list.get(i).getX() == list.get(i + 1).getX() && list.get(i).getY() == list.get(i - 1).getY()) {
                //do nothing
            } else {
                orth = false;
                break;//stop iteration once its clear the terrain isn't orthogonal
            }
        }
        //special handling for first and last vertex
        if (list.get(0).getX() == list.get(1).getX() || list.get(0).getY() == list.get(1).getY()) {
            //do nothing
        } else {
            orth = false;
        }
        if (list.get(list.size() - 1).getX() == list.get(list.size() - 2).getX() || list.get(list.size() - 1).getY() == list.get(list.size() - 2).getY()) {
            //do nothing
        } else {
            orth = false;
        }

        return orth;
    }


    /**
     * Method that checks the vertices of a given terrain for being a right/left reflex/convex vertex
     * and sets the vType of them accordingly ("RR"/"LR"/"RC"/"LC")
     *
     * @param list list representing the terrain you want to set the vType in
     */
    static void rightLeftVertex(ArrayList<Vertex> list) {
        if (list.size() == 0) {
            System.out.println("The terrain has size 0. No right/left information added");
            return;
        }
        //iterate over whole terrain except first and last vertex
        for (int i = 1; i < list.size() - 1; i++) {
            if (list.get(i).getY() > list.get(i - 1).getY()) {
                list.get(i).setvType("LR"); //Left Reflex
            } else if (list.get(i).getY() < list.get(i - 1).getY()) {
                list.get(i).setvType("LC"); //Left Convex
            } else if (list.get(i).getY() > list.get(i + 1).getY()) {
                list.get(i).setvType("RR"); //Right Reflex
            } else if (list.get(i).getY() < list.get(i + 1).getY()) {
                list.get(i).setvType("RC"); //Right Convex
            }
        }
        //special Handling for first and last Vertex
        //In case the first Vertex is higher than the second one, it is ok to declare it as a Reflex, since the second
        //one is a convex, that needs to be covered and whatever vertex covers the second one also sees the first.
        if (list.get(0).getY() > list.get(1).getY()) {
            list.get(0).setvType("LR");
        } else if (list.get(0).getY() < list.get(1).getY()) {
            list.get(0).setvType("RC");
        } else {
            list.get(0).setvType("LC");
        }
        if (list.get(list.size() - 1).getY() > list.get(list.size() - 2).getY()) {
            list.get(list.size() - 1).setvType("RR");
        } else if (list.get(list.size() - 1).getY() < list.get(list.size() - 2).getY()) {
            list.get(list.size() - 1).setvType("LC");
        } else {
            list.get(list.size() - 1).setvType("RC");
        }
    }


    /**
     * Method that creates an orthogonal Terrain from a list of points. The list in the input must be ordered ascending
     * by x values. The method also gets rid of duplicate points and points that share the same x or y value.
     * Further, a vertex is added in the very beginning that has as y value the y value of the first vertex
     * and as x value the x value of the first vertex minus the average x step size of all vertices.
     *
     * @param list the list of points (sorted by x value) you want to create an orthogonal terrain from
     * @return ArrayList that contains the vertices of the terrain (sorted by ID/x-values in ascending order)
     */
    static ArrayList<Vertex> xYListToOrthTerrain(ArrayList<double[]> list) {
        if (list.size() == 0) {
            return new ArrayList<>();
        }
        //create a new cleaned list that only contains x-y-pairs with distinct x and y values from the next list entry
        ArrayList<double[]> cleanedList = new ArrayList<>();
        for (int i = 0; i < list.size() - 1; i++) {
            if (Math.abs(list.get(i)[0] - list.get(i + 1)[0]) > 0.000001 &&
                    Math.abs(list.get(i)[1] - list.get(i + 1)[1]) > 0.000001) {
                cleanedList.add(list.get(i));
            }
        }
        cleanedList.add(list.get(list.size() - 1));
        if (cleanedList.size() < 2) {
            System.out.println("After orthogonalizing the terrain, there were no vertices left. The input terrain probably was orthogonal already.");
            return new ArrayList<>();
        }
        System.out.println("When orthogonalizing the terrain " + (list.size() - cleanedList.size()) + " vertices were removed since they were next to each other and had same x or y value.");

        double avg = 0; //will be used to store average stepsize in x direction to set first x value
        int n = cleanedList.size();
        for (int i = 0; i < n - 1; i++) {
            avg += Math.abs(cleanedList.get(i)[0] - cleanedList.get(i + 1)[0]) / n;
        }

        double firstX = cleanedList.get(0)[0] - avg; //set first x value

        ArrayList<Vertex> arrList = new ArrayList<>();
        double x; //saves x value in current iteration
        double y; //saves y value in current iteration
        double y_next; //saves y value of next iteration
        Vertex firstV = new Vertex(0, firstX, cleanedList.get(0)[1], null);
        int k = 1; //helpvar to set the ID of added vertices correctly
        arrList.add(firstV); //add first vertex to arrList
        //iterate over list and create vertices that form orthogonal terrain by adding 2 vertices to ArrList in each step
        //the first one is just the original vertex from list, the second one takes the current x, and the next y value
        //thus we add a vertical pair of vertices in each step
        for (int i = 0; i < cleanedList.size() - 1; i++) {
            x = cleanedList.get(i)[0];
            y = cleanedList.get(i)[1];
            y_next = cleanedList.get(i + 1)[1];
            Vertex v1 = new Vertex(k, x, y, null);
            arrList.add(v1);
            Vertex v2 = new Vertex(k + 1, x, y_next, null);
            arrList.add(v2);
            k = k + 2;
        }
        Vertex lastV = new Vertex(k, cleanedList.get(cleanedList.size() - 1)[0], cleanedList.get(cleanedList.size() - 1)[1], null);
        arrList.add(lastV);
        return arrList;
    }


    /** Method that creates a not necessarily orthogonal terrain based on a list of points. No collinear vertices get removed.
     *
     * @param list list of points that represents a terrain
     * @return ArrayList that contains the vertices of the terrain
     */
    static ArrayList<Vertex> xYListToNonOrthTerrainWithoutRemoval(ArrayList<double[]> list) {

        ArrayList<Vertex> arrList = new ArrayList<>();
        double x; //saves x value in current iteration
        double y; //saves y value in current iteration

        for (int i = 0; i < list.size(); i++) {
            x = list.get(i)[0];
            y = list.get(i)[1];
            Vertex v1 = new Vertex(i, x, y, null);
            arrList.add(v1);
        }
        return arrList;
    }


    //only a help-function for the AGB-files that corrects small errors of multiplication by PI
    static ArrayList<Vertex> xYListToNonOrthTerrainWithoutRemovalHelp(ArrayList<double[]> list) {

        ArrayList<Vertex> arrList = new ArrayList<>();

        for (int i = 0; i < list.size()-1; i++) {
            double x1; //saves x value in current iteration
            double y1; //saves y value in current iteration
            double x2; //saves x value in current iteration
            double y2; //saves y value in current iteration
            x1 = list.get(i)[0];
            y1 = list.get(i)[1];
            x2 = list.get(i+1)[0];
            y2 = list.get(i+1)[1];
            if(Math.abs(x1-x2) < 0.001){
                list.get(i+1)[0] = x1;
            }
            if(Math.abs(y1-y2) < 0.001){
                list.get(i+1)[1] = y1;
            }
        }

        for (int i = 0; i < list.size(); i++) {
            double x; //saves x value in current iteration
            double y; //saves y value in current iteration
            x = list.get(i)[0];
            y = list.get(i)[1];
            Vertex v1 = new Vertex(i, x, y, null);
            arrList.add(v1);
        }
        return arrList;
    }

}
