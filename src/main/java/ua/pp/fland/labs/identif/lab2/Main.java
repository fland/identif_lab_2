package ua.pp.fland.labs.identif.lab2;

import org.apache.commons.math.linear.RealMatrix;
import org.apache.log4j.Logger;
import ua.pp.fland.labs.identif.lab2.gui.MainWindow;
import ua.pp.fland.labs.identif.lab2.model.LeastSquareMethod;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Maxim Bondarenko
 * @version 1.0 9/29/11
 */

public class Main {
    private static final Logger log = Logger.getLogger(Main.class);

    public static void main(String[] args) throws InvocationTargetException, InterruptedException, ClassNotFoundException, UnsupportedLookAndFeelException, IllegalAccessException, InstantiationException {
        log.debug("App started ...");

        String input = "{{4, 2, 1}, {2, 4, 3}, {4, 9, 7}, {2, 2, 4}, {2, 3, 1}}";
//        input.
        String values[] = input.split("\\}");
        for (int i = 0; i < values.length; i++) {
            values[i] = values[i].replace("{{", "");
            values[i] = values[i].replace(", {", "");
        }

        double[][] temp = new double[5][3];
        for (int i = 0; i < values.length; i++) {
            String row[] = values[i].split(", ");
            for (int j = 0; j < row.length; j++) {
                temp[i][j] = Double.valueOf(row[j]);
            }
        }

        log.debug(values);

        double[][] xValues = new double[][]{{4, 2, 1}, {2, 4, 3}, {4, 9, 7}, {2, 2, 4}, {2, 3, 1}};
        double[][] yValues = new double[][]{{20.8}, {14.2}, {32.3}, {11.5}, {8.2}};

        LeastSquareMethod leastSquareMethod = new LeastSquareMethod(xValues, yValues);
        RealMatrix res = leastSquareMethod.process();

        double[] weights = new double[]{1.1, 0.9, 1.2, 0.9, 0.9, 0.7};

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }
}
