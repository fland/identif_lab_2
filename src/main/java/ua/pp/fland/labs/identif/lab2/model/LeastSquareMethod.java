package ua.pp.fland.labs.identif.lab2.model;

import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.log4j.Logger;

import static org.apache.commons.math.util.FastMath.pow;

/**
 * @author Maxim Bondarenko
 * @version 1.0 9/29/11
 */

public class LeastSquareMethod {
    private static final Logger log = Logger.getLogger(LeastSquareMethod.class);

    private final static double P = 0.95;

    private final RealMatrix xValues;

    private final RealMatrix yValues;

    private final int experimentsNum;

    public LeastSquareMethod(double[][] xValuesArray, double[][] yValuesArray) {
        xValues = new Array2DRowRealMatrix(xValuesArray);
        yValues = new Array2DRowRealMatrix(yValuesArray);

        experimentsNum = yValuesArray.length;
    }

    public RealMatrix process() {
        final RealMatrix xValuesTransposed = xValues.transpose();
        final RealMatrix multipliedXValues = xValuesTransposed.multiply(xValues);
        final RealMatrix tempInvX = new LUDecompositionImpl(multipliedXValues).getSolver().getInverse();
        final RealMatrix tempXYMatrix = xValuesTransposed.multiply(yValues);

        return tempInvX.multiply(tempXYMatrix);
    }

    public double calculateDispersion(final double[] coeffsArray) {
        double sum = 0;
        for (int i = 0; i < experimentsNum; i++) {
            double[] currRow = xValues.getRow(i);
            sum = sum + pow((yValues.getRow(i)[0] - (currRow[0] * coeffsArray[0] + currRow[1] * coeffsArray[1]
                    + currRow[2] * coeffsArray[2])), 2.0);
        }

        return (1.0 / (experimentsNum - 2.0)) * sum;
    }

    public double calculateCoeffsDispersion(final double[] coeffsArray) {
        if (coeffsArray.length != 3) {
            String msg = "Unexpected array length: " + coeffsArray.length + ". Expected length 3";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double dispersion = calculateDispersion(coeffsArray);

        double coeffsSum = coeffsArray[0] + coeffsArray[1] + coeffsArray[2];

        return dispersion / coeffsSum;
    }

    public double calculateCoeffsDispersion(final double[] coeffsArray, double dispersion) {
        if (coeffsArray.length != 3) {
            String msg = "Unexpected array length: " + coeffsArray.length + ". Expected length 3";
            log.error(msg);
            throw new IllegalArgumentException(msg);
        }

        double coeffsSum = coeffsArray[0] + coeffsArray[1] + coeffsArray[2];

        return dispersion / coeffsSum;
    }

    public double calculateConfidenceInterval(final double[] coeffsArray) throws MathException {
        TDistribution tDistribution = new TDistributionImpl(2);

        try {
            double dispersion = calculateDispersion(coeffsArray);
            return tDistribution.inverseCumulativeProbability(P) * dispersion
                    * calculateCoeffsDispersion(coeffsArray, dispersion);
        } catch (MathException e) {
            log.error("Exception: " + e, e);
            throw new MathException(e);
        }
    }

    public double calculateConfidenceInterval(final double[] coeffsArray, double dispersion) throws MathException {
        TDistribution tDistribution = new TDistributionImpl(2);

        try {
            return tDistribution.inverseCumulativeProbability(P) * dispersion
                    * calculateCoeffsDispersion(coeffsArray, dispersion);
        } catch (MathException e) {
            log.error("Exception: " + e, e);
            throw new MathException(e);
        }
    }
}
