import approximation.*;
import io.ApproximationIO;
import io.ApproximationIOIImpl;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {
        Approximation approximator = new ApproximationImpl();
        ApproximationIO approximatorIO = new ApproximationIOIImpl();
        InputDAO inputDAO = approximatorIO.input();
        Map<ApproximationType, ResultDAO> results = new HashMap<>();
        Arrays.stream(ApproximationType.values()).forEach(x -> {
            results.put(x, approximator.approximate(new InputDAO(inputDAO), x));
        });
        approximatorIO.output(results,
                results.values().stream().max((x, y) -> (int) ((x.getR2() - y.getR2()) * 1e9)).get(),
                inputDAO.getFunctionValues()[0][0] - 2,
                inputDAO.getFunctionValues()[inputDAO.getFunctionValues().length - 1][0] + 2);
    }
}
