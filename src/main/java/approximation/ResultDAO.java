package approximation;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Function;

@Data
@AllArgsConstructor
public class ResultDAO {
    private ApproximationType type;
    private double[] solution;
    private Function<Double, Double> function;
    private double deviation;
    private double correlation;
    private double r2;

    public ResultDAO(double[] solution, Function<Double, Double> function, double deviation, ApproximationType type, double r2) {
        this.solution = solution;
        this.function = function;
        this.deviation = deviation;
        this.type = type;
        this.r2 = r2;
    }
}
