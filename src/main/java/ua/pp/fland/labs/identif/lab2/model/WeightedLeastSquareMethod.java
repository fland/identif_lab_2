package ua.pp.fland.labs.identif.lab2.model;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.log4j.Logger;
import ua.pp.fland.labs.identif.lab2.model.LeastSquareMethod;

/**
 * @author Maxim Bondarenko
 * @version 1.0 9/29/11
 */

public class WeightedLeastSquareMethod extends LeastSquareMethod{
    private static final Logger log = Logger.getLogger(LeastSquareMethod.class);

    private RealMatrix xValues;

    private RealMatrix yValues;

    private RealMatrix weights;

    public WeightedLeastSquareMethod(double[][] xValuesArray, double[][] yValuesArray, double[][] weightsArray) {
        super(xValuesArray, yValuesArray);

        xValues = new Array2DRowRealMatrix(xValuesArray);
        yValues = new Array2DRowRealMatrix(yValuesArray);
        weights = new Array2DRowRealMatrix(weightsArray);
    }

    public RealMatrix process() {
        final RealMatrix xValuesTransposed = xValues.transpose();
        RealMatrix multipliedXValues = xValuesTransposed.multiply(weights);
        multipliedXValues = multipliedXValues.multiply(xValues);
        final RealMatrix tempInvX = new LUDecompositionImpl(multipliedXValues).getSolver().getInverse();
        RealMatrix tempXYMatrix = xValuesTransposed.multiply(weights);
        tempXYMatrix = tempXYMatrix.multiply(yValues);

        return tempInvX.multiply(tempXYMatrix);
    }
}
