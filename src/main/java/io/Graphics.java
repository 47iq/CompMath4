package io;

import java.util.function.Function;

public interface Graphics {
    void display(double left, double right, Function<Double, Double> function);
}
