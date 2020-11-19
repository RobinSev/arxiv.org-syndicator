package app.arxivorg.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Statistics implements Initializable {

    @FXML
    private BarChart<String, Double> statsBarChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        XYChart.Series<String, Double> set = new XYChart.Series<>();
        set.getData().addAll(List.of(
                new XYChart.Data<String, Double>("cs.CL", 10.0),
                new XYChart.Data<String, Double>("cs.AI", 25.0),
                new XYChart.Data<String, Double>("cs.gt", 7.0),
                new XYChart.Data<String, Double>("cs.op", 5.0),
                new XYChart.Data<String, Double>("cs.pg", 3.0)));
        statsBarChart.getData().addAll(List.of(set));
    }
}
