package de.TerrainGuarding;
import gurobi.*;

public class GurobiTest {
    public static void main(String[] args) {
        try {
            //Create empty Gurobi environment, setup logfile name and start
            GRBEnv env = new GRBEnv(true);
            env.set("logFile", "test1.log");
            env.start();

            //Create an empty model
            GRBModel model = new GRBModel(env);

            // Create variables
            GRBVar x = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, "x");
            GRBVar y = model.addVar(0.0, 1.0, 1.0, GRB.BINARY, "y");
            GRBVar z = model.addVar(0.0, 1.0, 2.0, GRB.BINARY, "z");


            // Set objective: maximize x + y + 2 z
            GRBLinExpr expr = new GRBLinExpr();
            /*
            expr.addTerm(1.0, x); expr.addTerm(1.0, y); expr.addTerm(2.0, z);
             */
            //Set sense to maximize (-1:max, 1:min)
            model.set(GRB.IntAttr.ModelSense, -1);


            // Add constraint: x + 2 y + 3 z <= 4
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x); expr.addTerm(2.0, y); expr.addTerm(3.0, z);
            model.addConstr(expr, GRB.LESS_EQUAL, 4.0, "c0");

            // Add constraint: x + y >= 1
            expr = new GRBLinExpr();
            expr.addTerm(1.0, x); expr.addTerm(1.0, y);
            model.addConstr(expr, GRB.GREATER_EQUAL, 1.0, "c1");

            // Optimize model
            model.optimize();

            System.out.println(x.get(GRB.StringAttr.VarName)
                    + " " +x.get(GRB.DoubleAttr.X));
            System.out.println(y.get(GRB.StringAttr.VarName)
                    + " " +y.get(GRB.DoubleAttr.X));
            System.out.println(z.get(GRB.StringAttr.VarName)
                    + " " +z.get(GRB.DoubleAttr.X));

            System.out.println("Obj: " + model.get(GRB.DoubleAttr.ObjVal));

            //dispose model and environment
            model.dispose();
            env.dispose();

        }catch(GRBException exc){
            System.out.println(exc.getErrorCode() + " : " + exc.getMessage());
            System.out.println("FEHLER");
        }


    }
}
