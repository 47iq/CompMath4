package io;

import approximation.ApproximationType;
import approximation.InputDAO;
import approximation.ResultDAO;

import java.io.IOException;
import java.util.Map;

public interface ApproximationIO {
    InputDAO input() throws IOException;
    void output(Map<ApproximationType, ResultDAO> results, ResultDAO finalResult, double left, double right);
}
