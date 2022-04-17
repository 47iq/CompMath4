package approximation;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

public class ApproximationImpl implements Approximation {

    @Override
    public ResultDAO approximate(InputDAO inputDAO, ApproximationType type) {
        return switch (type) {
            case CUBIC -> approximateCubic(inputDAO);
            case POWER -> approximatePower(inputDAO);
            case LINEAR -> approximateLinear(inputDAO);
            case QUADRATIC -> approximateQuadratic(inputDAO);
            case EXPONENTIAL -> approximateExponential(inputDAO);
            default -> approximateLogarithmic(inputDAO);
        };
    }

    private ResultDAO approximateLinear(InputDAO inputDAO) {
        double x = 0;
        double x2 = 0;
        double y = 0;
        double xy = 0;
        for (int i = 0; i < inputDAO.getFunctionValues().length; i++) {
            x += inputDAO.getFunctionValues()[i][0];
            x2 += Math.pow(inputDAO.getFunctionValues()[i][0], 2);
            y += inputDAO.getFunctionValues()[i][1];
            xy += inputDAO.getFunctionValues()[i][0] * inputDAO.getFunctionValues()[i][1];
        }
        double[] solution = getSystemSolution(new double[][]{
                        {x2, x},
                        {x, inputDAO.getFunctionValues().length}},
                new double[] {
                        xy, y
                });
        Function<Double, Double> function = a -> a * solution[0] + solution[1];
        return new ResultDAO(ApproximationType.LINEAR, solution, function, calculateDeviation(inputDAO.getFunctionValues(), function), calculateCorrelation(inputDAO.getFunctionValues()), calculateR2(inputDAO.getFunctionValues(), function));
    }

    private ResultDAO approximateQuadratic(InputDAO inputDAO) {
        double x = 0;
        double x2 = 0;
        double x3 = 0;
        double x4 = 0;
        double y = 0;
        double xy = 0;
        double x2y = 0;
        for (int i = 0; i < inputDAO.getFunctionValues().length; i++) {
            x += inputDAO.getFunctionValues()[i][0];
            x2 += Math.pow(inputDAO.getFunctionValues()[i][0], 2);
            x3 += Math.pow(inputDAO.getFunctionValues()[i][0], 3);
            x4 += Math.pow(inputDAO.getFunctionValues()[i][0], 4);
            y += inputDAO.getFunctionValues()[i][1];
            xy += inputDAO.getFunctionValues()[i][0] * inputDAO.getFunctionValues()[i][1];
            x2y += Math.pow(inputDAO.getFunctionValues()[i][0], 2) * inputDAO.getFunctionValues()[i][1];
        }
        double[] solution = getSystemSolution(new double[][]{
                    {x4, x3, x2},
                    {x3, x2, x},
                    {x2, x, inputDAO.getFunctionValues().length}
                }, new double[]{
                        x2y, xy, y
                });
        Function<Double, Double> function = a -> a * a * solution[0] + a * solution[1] + solution[2];
        return new ResultDAO(solution, function, calculateDeviation(inputDAO.getFunctionValues(), function), ApproximationType.QUADRATIC, calculateR2(inputDAO.getFunctionValues(), function));
    }

    private ResultDAO approximateCubic(InputDAO inputDAO) {
        double x = 0;
        double x2 = 0;
        double x3 = 0;
        double x4 = 0;
        double x5 = 0;
        double x6 = 0;
        double y = 0;
        double xy = 0;
        double x2y = 0;
        double x3y = 0;
        for (int i = 0; i < inputDAO.getFunctionValues().length; i++) {
            x += inputDAO.getFunctionValues()[i][0];
            x2 += Math.pow(inputDAO.getFunctionValues()[i][0], 2);
            x3 += Math.pow(inputDAO.getFunctionValues()[i][0], 3);
            x4 += Math.pow(inputDAO.getFunctionValues()[i][0], 4);
            x5 += Math.pow(inputDAO.getFunctionValues()[i][0], 5);
            x6 += Math.pow(inputDAO.getFunctionValues()[i][0], 6);
            y += inputDAO.getFunctionValues()[i][1];
            xy += inputDAO.getFunctionValues()[i][0] * inputDAO.getFunctionValues()[i][1];
            x2y += Math.pow(inputDAO.getFunctionValues()[i][0], 2) * inputDAO.getFunctionValues()[i][1];
            x3y += Math.pow(inputDAO.getFunctionValues()[i][0], 3) * inputDAO.getFunctionValues()[i][1];
        }
        double[] solution = getSystemSolution(new double[][]{
                {x6, x5, x4, x3},
                {x5, x4, x3, x2},
                {x4, x3, x2, x},
                {x3, x2, x, inputDAO.getFunctionValues().length}
        }, new double[] {
                x3y, x2y, xy, y
        });
        Function<Double, Double> function = a -> a * a * a * solution[0] + a * a * solution[1] + a * solution[2] + solution[3];
        return new ResultDAO(solution, function, calculateDeviation(inputDAO.getFunctionValues(), function), ApproximationType.CUBIC, calculateR2(inputDAO.getFunctionValues(), function));
    }

    private ResultDAO approximateLogarithmic(InputDAO inputDAO) {
        double[][] originalValues = inputDAO.getFunctionValues().clone();
        double[][] newValues = originalValues.clone();
        inputDAO.setFunctionValues(Arrays.stream(newValues).map(x -> x[0] > 0 ? new double[]{Math.log(x[0]), x[1]} : new double[]{x[0], x[1]}).toArray(double[][]::new));
        ResultDAO resultDAO = approximateLinear(inputDAO);
        Function<Double, Double> logFunction = x -> resultDAO.getSolution()[0] * Math.log(x) + resultDAO.getSolution()[1];
        return new ResultDAO(resultDAO.getSolution(), logFunction, calculateDeviation(originalValues, logFunction), ApproximationType.LOGARITHMIC, calculateR2(originalValues, logFunction));
    }

    private ResultDAO approximateExponential(InputDAO inputDAO) {
        double[][] originalValues = inputDAO.getFunctionValues().clone();
        double[][] newValues = originalValues.clone();
        inputDAO.setFunctionValues(Arrays.stream(newValues).map(x -> x[1] > 0 ? new double[]{x[0], Math.log(x[1])} : new double[]{x[0], x[1]}).toArray(double[][]::new));
        ResultDAO resultDAO = approximateLinear(inputDAO);
        double[] solution = resultDAO.getSolution();
        solution[1] = Math.exp(solution[1]);
        Function<Double, Double> expFunction = x -> solution[1] * Math.exp(x * solution[0]);
        return new ResultDAO(solution, expFunction, calculateDeviation(originalValues, expFunction), ApproximationType.EXPONENTIAL, calculateR2(originalValues, expFunction));
    }

    private ResultDAO approximatePower(InputDAO inputDAO) {
        double[][] originalValues = inputDAO.getFunctionValues().clone();
        double[][] newValues = originalValues.clone();
        inputDAO.setFunctionValues(Arrays.stream(newValues).map(x -> (x[0] > 0 & x[1] > 0) ? new double[]{Math.log(x[0]), Math.log(x[1])} : new double[]{x[0], x[1]}).toArray(double[][]::new));
        ResultDAO resultDAO = approximateLinear(inputDAO);
        double[] solution = resultDAO.getSolution();
        solution[1] = Math.exp(solution[1]);
        Function<Double, Double> powFunction = x -> solution[1] * Math.pow(x, solution[0]);
        return new ResultDAO(solution, powFunction, calculateDeviation(originalValues, powFunction), ApproximationType.POWER, calculateR2(originalValues, powFunction));
    }

    private double calculateR2(double[][] values, Function<Double, Double> function) {
        AtomicReference<Double> upper = new AtomicReference<>((double) 0);
        AtomicReference<Double> lowerLeft = new AtomicReference<>((double) 0);
        AtomicReference<Double> lowerRight = new AtomicReference<>((double) 0);
        Arrays.stream(values).forEach(x -> {
            upper.updateAndGet(v -> (v + Math.pow(x[1] - function.apply(x[0]), 2)));
            lowerLeft.updateAndGet(v -> (v + Math.pow(function.apply(x[0]), 2)));
            lowerRight.updateAndGet(v -> (v + function.apply(x[0])));
        });
        return 1 - upper.get() / ((lowerLeft.get() - Math.pow(lowerRight.get(), 2) / values.length));
    }

    private double calculateCorrelation(double[][] functionValues) {
        double n = functionValues.length;
        double averageX = Arrays.stream(functionValues).map(x -> x[0]).reduce(Double::sum).get() / n;
        double averageY = Arrays.stream(functionValues).map(x -> x[1]).reduce(Double::sum).get() / n;
        return Arrays.stream(functionValues)
                .map(x -> new double[]{x[0] - averageX, x[1] - averageY})
                .mapToDouble(x -> x[0] * x[1]).sum() /
                (Math.pow(Arrays.stream(functionValues)
                        .map(x -> Math.pow(x[0] - averageX, 2))
                        .reduce(Double::sum).get() * Arrays.stream(functionValues)
                        .map(x -> Math.pow(x[1] - averageY, 2))
                        .reduce(Double::sum).get(), 0.5));
    }

    private double calculateDeviation(double[][] functionValues, Function<Double, Double> function) {
        return Arrays.stream(functionValues).map(x -> Math.pow(x[1] - function.apply(x[0]), 2)).reduce(Double::sum).get();
    }

    private double[] getSystemSolution(double[][] matrix, double[] constants) {
        DecompositionSolver solver = new LUDecomposition(new Array2DRowRealMatrix(matrix)).getSolver();
        return solver.solve(new ArrayRealVector(constants)).toArray();
    }
}
