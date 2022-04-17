package io;

import approximation.ApproximationType;
import approximation.InputDAO;
import approximation.ResultDAO;

import java.io.*;
import java.util.Map;
import java.util.stream.Stream;

public class ApproximationIOIImpl implements ApproximationIO {

    BufferedReader reader;
    Writer writer = new PrintWriter(System.out);;
    Graphics graphics = new GraphicsImpl("Graphics");


    @Override
    public InputDAO input() throws IOException {
        reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Введите 0 для ввода с консоли или название файла для ввода из него: ");
        String input = reader.readLine();
        if (!input.equals("0")) {
            while (true) {
                try {
                    reader = new BufferedReader(new FileReader(input));
                    break;
                } catch (Exception e) {
                    System.out.println("Файл не может быть открыт. Повторите ввод:");
                    input = reader.readLine();
                }
            }
        }
        System.out.println("Введите 0 для вывода в консоль или название файла для вывода в него: ");
        String output = reader.readLine();
        if (!output.equals("0")) {
            while (true) {
                try {
                    writer = new BufferedWriter(new FileWriter(output));
                    break;
                } catch (Exception e) {
                    System.out.println("Файл не может быть открыт. Повторите ввод:");
                    output = reader.readLine();
                }
            }
        }
        System.out.println("Введите количество пар точек (x, y):");
        int n;
        double[][] values;
        while (true) {
            try {
                n = Integer.parseInt(reader.readLine());
                if (n <= 6) {
                    System.err.println("Количество пар точек должно быть не менее 6. Повторите ввод:");
                    continue;
                }
                values = new double[n][2];
                break;
            } catch (NumberFormatException e) {
                System.err.println("Введенное значение не является целым числом. Повторите ввод:");
            }
        }
        int i = 0;
        while (i < n) {
            try {
                double[] pair = Stream.of(reader.readLine().split("\s+")).mapToDouble(Double::parseDouble).toArray();
                if (pair.length != 2) {
                    System.err.println("В строке должно быть 2 значения в формате: x y. Повторите ввод:");
                    continue;
                }
                values[i] = pair;
                i++;
            } catch (NumberFormatException e) {
                System.err.println("В строке должно быть 2 значения в формате: x y. Повторите ввод:");
            }
        }
        return new InputDAO(values);
    }

    @Override
    public void output(Map<ApproximationType, ResultDAO> results, ResultDAO finalResult, double left, double right) {
        if(writer != null) {
            results.forEach((x, y) -> {
                try {
                    writer.write(String.format("""
                            Тип функции: %s
                            Функция: %s
                            Мера отклонения: %f
                            R^2: %f
                            %n""", x, switch (x) {
                        case LINEAR -> String.format("%fx +%f", y.getSolution()[0], y.getSolution()[1]);
                        case QUADRATIC -> String.format("%fx^2 + %fx + %f", y.getSolution()[0], y.getSolution()[1], y.getSolution()[2]);
                        case EXPONENTIAL -> String.format("%f*e^(%fx)", y.getSolution()[1], y.getSolution()[0]);
                        case LOGARITHMIC -> String.format("%f*lnx + %f", y.getSolution()[0], y.getSolution()[1]);
                        case POWER -> String.format("%fx^(%f)", y.getSolution()[1], y.getSolution()[0]);
                        default -> String.format("%fx^3 + %fx^2 + %fx + %f", y.getSolution()[0], y.getSolution()[1], y.getSolution()[2], y.getSolution()[3]);
                    }, y.getDeviation(), y.getR2()));
                    writer.flush();
                } catch (IOException e) {
                    System.out.println("Ошибка при записи в файл");
                    System.exit(1);
                }
            });
            try {
                writer.write(String.format("""
                        _____________________
                        Итоговая функция:
                        Тип функции: %s
                        Функция: %s
                        Мера отклонения: %f
                        R^2: %f
                        %n""", finalResult.getType(), switch (finalResult.getType()) {
                    case LINEAR -> String.format("%fx +%f", finalResult.getSolution()[0], finalResult.getSolution()[1]);
                    case QUADRATIC -> String.format("%fx^2 + %fx + %f", finalResult.getSolution()[0], finalResult.getSolution()[1], finalResult.getSolution()[2]);
                    case EXPONENTIAL -> String.format("%fe^(%fx)", finalResult.getSolution()[1], finalResult.getSolution()[0]);
                    case LOGARITHMIC -> String.format("%flnx + %f", finalResult.getSolution()[0], finalResult.getSolution()[1]);
                    case POWER -> String.format("%fx^(%f)", finalResult.getSolution()[1], finalResult.getSolution()[0]);
                    default -> String.format("%fx^3 + %fx^2 + %fx + %f", finalResult.getSolution()[0], finalResult.getSolution()[1],
                            finalResult.getSolution()[2], finalResult.getSolution()[3]);
                }, finalResult.getDeviation(), finalResult.getR2()));
                graphics.display(left, right, finalResult.getFunction());
                writer.flush();
            } catch (IOException e) {
                System.out.println("Ошибка при записи в файл");
                System.exit(1);
            }
        } else {
            results.forEach((x, y) -> System.out.printf("""
                    Тип функции: %s
                    Функция: %s
                    Мера отклонения: %f
                    R^2: %f
                    %n""", x, switch (x) {
                case LINEAR -> String.format("%fx +%f", y.getSolution()[0], y.getSolution()[1]);
                case QUADRATIC -> String.format("%fx^2 + %fx + %f", y.getSolution()[0], y.getSolution()[1], y.getSolution()[2]);
                case EXPONENTIAL -> String.format("%f*e^(%fx)", y.getSolution()[1], y.getSolution()[0]);
                case LOGARITHMIC -> String.format("%f*lnx + %f", y.getSolution()[0], y.getSolution()[1]);
                case POWER -> String.format("%fx^(%f)", y.getSolution()[1], y.getSolution()[0]);
                default -> String.format("%fx^3 + %fx^2 + %fx + %f", y.getSolution()[0], y.getSolution()[1], y.getSolution()[2], y.getSolution()[3]);
            }, y.getDeviation(), y.getR2()));
            System.out.printf("""
                    _____________________
                    Итоговая функция:
                    Тип функции: %s
                    Функция: %s
                    Мера отклонения: %f
                    R^2: %f
                    %n""", finalResult.getType(), switch (finalResult.getType()) {
                case LINEAR -> String.format("%fx +%f", finalResult.getSolution()[0], finalResult.getSolution()[1]);
                case QUADRATIC -> String.format("%fx^2 + %fx + %f", finalResult.getSolution()[0], finalResult.getSolution()[1], finalResult.getSolution()[2]);
                case EXPONENTIAL -> String.format("%fe^(%fx)", finalResult.getSolution()[1], finalResult.getSolution()[0]);
                case LOGARITHMIC -> String.format("%flnx + %f", finalResult.getSolution()[0], finalResult.getSolution()[1]);
                case POWER -> String.format("%fx^(%f)", finalResult.getSolution()[1], finalResult.getSolution()[0]);
                default -> String.format("%fx^3 + %fx^2 + %fx + %f", finalResult.getSolution()[0], finalResult.getSolution()[1],
                        finalResult.getSolution()[2], finalResult.getSolution()[3]);
            }, finalResult.getDeviation(), finalResult.getR2());
            graphics.display(left, right, finalResult.getFunction());
        }
    }
}
