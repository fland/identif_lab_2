package ua.pp.fland.labs.identif.lab2.gui;

import org.apache.commons.math.MathException;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.log4j.Logger;
import ua.pp.fland.labs.identif.lab2.model.LeastSquareMethod;
import ua.pp.fland.labs.identif.lab2.model.WeightedLeastSquareMethod;
import ua.pp.fland.labs.identif.lab2.gui.utils.BoxLayoutUtils;
import ua.pp.fland.labs.identif.lab2.gui.utils.ComponentUtils;
import ua.pp.fland.labs.identif.lab2.gui.utils.GUITools;
import ua.pp.fland.labs.identif.lab2.gui.utils.StandardBordersSizes;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * @author Maxim Bondarenko
 * @version 1.0 9/29/11
 */

public class MainWindow {
    private static final Logger log = Logger.getLogger(MainWindow.class);

    private final static Dimension MAIN_FRAME_SIZE = new Dimension(500, 200);

    private final static String PROCESS_BTN_TEXT = "Process";
    private final static String PROCESS_WEIGHTED_BTN_TEXT = "Process Weighted";
    private final static String EXIT_BTN_TEXT = "Exit";

    private final static String MATRIX_SIZE_LABEL_TEXT = "Matrix Width: ";
    private final static String X_VALUES_LABEL_TEXT = "X Values: ";
    private final static String Y_VALUES_LABEL_TEXT = "Y Values: ";
    private final static String WEIGHTS_LABEL_TEXT = "Weights: ";

    private final JFrame mainFrame;

    private final JTextField matrixSizeInput;
    private final JTextField xValuesInput;
    private final JTextField yValuesInput;
    private final JTextField weightsInput;

    public MainWindow() {
        mainFrame = new JFrame("Lab 2");
        mainFrame.setSize(MAIN_FRAME_SIZE);
        mainFrame.setResizable(false);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        matrixSizeInput = new JTextField("6");
        GUITools.fixTextFieldSize(matrixSizeInput);
        matrixSizeInput.setCaretPosition(0);

        xValuesInput = new JTextField("{4, 2, 1}, {1, 2, 3}, {2, 5, 7}, {1, 1, 4}, {1, 1, 1}, {1, -1, -2}");
        GUITools.fixTextFieldSize(xValuesInput);
        xValuesInput.setCaretPosition(0);

        yValuesInput = new JTextField("21.1, 13.9, 33.2, 10.9, 7.9, -3.0");
        GUITools.fixTextFieldSize(yValuesInput);
        yValuesInput.setCaretPosition(0);

        weightsInput = new JTextField("1.1, 0.9, 1.2, 0.9, 0.9, 0.7");
        GUITools.fixTextFieldSize(weightsInput);
        weightsInput.setCaretPosition(0);

        final JPanel mainPanel = BoxLayoutUtils.createVerticalPanel();
        mainPanel.setBorder(new EmptyBorder(StandardBordersSizes.MAIN_BORDER.getValue()));
        ComponentUtils.setSize(mainPanel, MAIN_FRAME_SIZE.width, MAIN_FRAME_SIZE.height);

        mainPanel.add(createInputPanels());
        mainPanel.add(Box.createRigidArea(StandardDimension.VER_RIGID_AREA.getValue()));
        mainPanel.add(createButtonsPanel(mainFrame));

        mainFrame.add(mainPanel);
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    private JPanel createButtonsPanel(final JFrame mainFrame) {
        JPanel buttonsPanel = BoxLayoutUtils.createHorizontalPanel();

        JButton processButton = new JButton(PROCESS_BTN_TEXT);
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                log.debug("Process btn pressed");
                try {
                    int matrixSize = Integer.parseInt(matrixSizeInput.getText());
                    log.debug("Matrix size: " + matrixSize);
                    String xValuesText = xValuesInput.getText();
                    log.debug("X Values: " + xValuesText);

                    String xValues[] = xValuesText.split("\\}");
                    for (int i = 0; i < xValues.length; i++) {
                        xValues[i] = xValues[i].replace(", {", "");
                        xValues[i] = xValues[i].replace("{", "");
                    }

                    double[][] xValuesArray = new double[matrixSize][3];
                    for (int i = 0; i < xValues.length; i++) {
                        String row[] = xValues[i].split(", ");
                        for (int j = 0; j < row.length; j++) {
                            xValuesArray[i][j] = Double.parseDouble(row[j]);
                        }
                    }

                    String yValues[] = yValuesInput.getText().split(",");
                    for (int i = 0; i < yValues.length; i++) {
                        yValues[i] = yValues[i].replace(" ", "");
                    }

                    double[][] yValuesArray = new double[matrixSize][1];
                    for (int i = 0; i < yValues.length; i++) {
                        yValuesArray[i][0] = Double.parseDouble(yValues[i]);
                    }

                    LeastSquareMethod leastSquareMethod = new LeastSquareMethod(xValuesArray, yValuesArray);
                    RealMatrix coefficientsMatrix = leastSquareMethod.process();

                    String resText = "b[0] = " + coefficientsMatrix.getRow(0)[0] + "\nb[1] = " +
                            coefficientsMatrix.getRow(1)[0] + "\nb[2] = " + coefficientsMatrix.getRow(2)[0];

                    double dispersion = leastSquareMethod.calculateDispersion(coefficientsMatrix.getColumn(0));
                    resText = resText + "\nDispersion: " + dispersion;

                    resText = resText + "\nCoeffs Dispertion: " +
                            leastSquareMethod.calculateCoeffsDispersion(coefficientsMatrix.getColumn(0), dispersion);

                    resText = resText + "\nConfidence Interval: " +
                            leastSquareMethod.calculateConfidenceInterval(coefficientsMatrix.getColumn(0), dispersion);

                    JOptionPane.showMessageDialog(mainFrame, "Calculated coefficients:\n" + resText, "Result",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    log.error("There was some errors while parsing input data. Exception: " + e, e);
                } catch (MathException e) {
                    log.error("Exception: " + e, e);
                }
            }
        });

        JButton processWeightedButton = new JButton(PROCESS_WEIGHTED_BTN_TEXT);
        processWeightedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                log.debug("Process weighted btn pressed");
                try {
                    int matrixSize = Integer.parseInt(matrixSizeInput.getText());
                    log.debug("Matrix size: " + matrixSize);
                    String xValuesText = xValuesInput.getText();
                    log.debug("X Values: " + xValuesText);

                    String xValues[] = xValuesText.split("\\}");
                    for (int i = 0; i < xValues.length; i++) {
                        xValues[i] = xValues[i].replace(", {", "");
                        xValues[i] = xValues[i].replace("{", "");
                    }

                    double[][] xValuesArray = new double[matrixSize][3];
                    for (int i = 0; i < xValues.length; i++) {
                        String row[] = xValues[i].split(", ");
                        for (int j = 0; j < row.length; j++) {
                            xValuesArray[i][j] = Double.parseDouble(row[j]);
                        }
                    }

                    String yValues[] = yValuesInput.getText().split(",");
                    for (int i = 0; i < yValues.length; i++) {
                        yValues[i] = yValues[i].replace(" ", "");
                    }

                    double[][] yValuesArray = new double[matrixSize][1];
                    for (int i = 0; i < yValues.length; i++) {
                        yValuesArray[i][0] = Double.parseDouble(yValues[i]);
                    }

                    String weightsValues[] = weightsInput.getText().split(",");
                    for (int i = 0; i < weightsValues.length; i++) {
                        weightsValues[i] = weightsValues[i].replace(" ", "");
                    }

                    double[][] weightsValuesArray = new double[matrixSize][matrixSize];
                    for(int i = 0; i < matrixSize; i++){
                        Arrays.fill(weightsValuesArray[i], 0);
                    }
                    for (int i = 0; i < weightsValues.length; i++) {
                        weightsValuesArray[i][i] = Double.parseDouble(weightsValues[i]);
                    }

                    WeightedLeastSquareMethod leastSquareMethod = new WeightedLeastSquareMethod(xValuesArray,
                            yValuesArray, weightsValuesArray);
                    RealMatrix coefficientsMatrix = leastSquareMethod.process();

                    String resText = "b[0] = " + coefficientsMatrix.getRow(0)[0] + "\nb[1] = " +
                            coefficientsMatrix.getRow(1)[0] + "\nb[2] = " + coefficientsMatrix.getRow(2)[0];

                    double dispersion = leastSquareMethod.calculateDispersion(coefficientsMatrix.getColumn(0));
                    resText = resText + "\nDispersion: " + dispersion;

                    resText = resText + "\nCoeffs Dispertion: " +
                            leastSquareMethod.calculateCoeffsDispersion(coefficientsMatrix.getColumn(0), dispersion);

                    resText = resText + "\nConfidence Interval: " +
                            leastSquareMethod.calculateConfidenceInterval(coefficientsMatrix.getColumn(0), dispersion);

                    JOptionPane.showMessageDialog(mainFrame, "Calculated coefficients:\n" + resText, "Result",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException e) {
                    log.error("There was some errors while parsing input data. Exception: " + e, e);
                } catch (MathException e) {
                    log.error("Exception: " + e, e);
                }
            }
        });

        JButton exitButton = new JButton(EXIT_BTN_TEXT);
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                log.debug("Exit btn pressed");
                shutdown();
            }
        });

        GUITools.createRecommendedMargin(processButton, exitButton, processWeightedButton);
        GUITools.makeSameSize(processButton, exitButton, processWeightedButton);

        buttonsPanel.add(processButton);
        buttonsPanel.add(Box.createRigidArea(StandardDimension.HOR_RIGID_AREA.getValue()));
        buttonsPanel.add(processWeightedButton);
        buttonsPanel.add(Box.createRigidArea(StandardDimension.HOR_RIGID_AREA.getValue()));
        buttonsPanel.add(exitButton);

        return buttonsPanel;
    }

    private JPanel createInputPanels() {
        JPanel inputsPanel = BoxLayoutUtils.createVerticalPanel();

        JPanel sizesInputPanel = BoxLayoutUtils.createHorizontalPanel();
        JLabel matrixSizeLabel = new JLabel(MATRIX_SIZE_LABEL_TEXT);
        sizesInputPanel.add(matrixSizeLabel);
        sizesInputPanel.add(Box.createRigidArea(StandardDimension.HOR_HALF_RIGID_AREA.getValue()));
        sizesInputPanel.add(matrixSizeInput);

        JPanel xMatrixInputPanel = BoxLayoutUtils.createHorizontalPanel();
        JLabel xValuesLabel = new JLabel(X_VALUES_LABEL_TEXT);
        xMatrixInputPanel.add(xValuesLabel);
        xMatrixInputPanel.add(Box.createRigidArea(StandardDimension.HOR_HALF_RIGID_AREA.getValue()));
        xMatrixInputPanel.add(xValuesInput);

        JPanel yMatrixInputPanel = BoxLayoutUtils.createHorizontalPanel();
        JLabel yValuesLabel = new JLabel(Y_VALUES_LABEL_TEXT);
        yMatrixInputPanel.add(yValuesLabel);
        yMatrixInputPanel.add(Box.createRigidArea(StandardDimension.HOR_HALF_RIGID_AREA.getValue()));
        yMatrixInputPanel.add(yValuesInput);

        JPanel weightsInputPanel = BoxLayoutUtils.createHorizontalPanel();
        JLabel weightsValuesLabel = new JLabel(WEIGHTS_LABEL_TEXT);
        weightsInputPanel.add(weightsValuesLabel);
        weightsInputPanel.add(Box.createRigidArea(StandardDimension.HOR_HALF_RIGID_AREA.getValue()));
        weightsInputPanel.add(weightsInput);

        GUITools.makeSameSize(matrixSizeLabel, xValuesLabel, yValuesLabel, weightsValuesLabel);

        inputsPanel.add(sizesInputPanel);
        inputsPanel.add(Box.createRigidArea(StandardDimension.VER_RIGID_AREA.getValue()));
        inputsPanel.add(xMatrixInputPanel);
        inputsPanel.add(Box.createRigidArea(StandardDimension.VER_RIGID_AREA.getValue()));
        inputsPanel.add(yMatrixInputPanel);
        inputsPanel.add(Box.createRigidArea(StandardDimension.VER_RIGID_AREA.getValue()));
        inputsPanel.add(weightsInputPanel);

        return inputsPanel;
    }

    private void shutdown() {
        mainFrame.setVisible(false);
        mainFrame.dispose();
    }
}
