package io;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.function.Function;

public class GraphicsImpl extends ApplicationFrame implements Graphics{

    public GraphicsImpl(String title) {
        super(title);
    }

    @Override
    public void display(double left, double right, Function<Double, Double> function) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries(function.hashCode());
        dataset.addSeries(series);
        for (double x = left; x <= right; x += 0.1) {
            series.add(x, function.apply(x));
        }
        JFreeChart chart = ChartFactory.createXYAreaChart(
                "График аппроксимации",
                "X",
                "Y",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );
        ChartPanel panel = new ChartPanel(chart);
        pack();
        setSize(600, 400);
        setContentPane(panel);
        setVisible(true);
    }
}
