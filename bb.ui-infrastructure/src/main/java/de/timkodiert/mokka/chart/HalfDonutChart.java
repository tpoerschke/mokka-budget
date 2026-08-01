package de.timkodiert.mokka.chart;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class HalfDonutChart extends HBox {

    static final int START_ANGLE = 180;
    static final int BORDER_WIDTH = 5;
    private static final int TOTAL_PERCENTAGE = 100;
    private static final double RADIUS = 180;
    private static final double HOVER_RADIUS_OFFSET = 8;
    private static final double CENTER_X = 200;
    private static final double CENTER_Y = 200;
    private static final double INNER_CIRCLE_RADIUS = RADIUS - RADIUS / 1.6;
    private static final double LEGEND_COLOR_SIZE = 12;
    private static final CornerRadii LEGEND_CORNER = new CornerRadii(3);

    private final Canvas canvas = new Canvas(400, 300);
    private final VBox legendContainer = new VBox(6);
    private final Tooltip tooltip = new Tooltip();
    private final List<LegendEntry> legendEntries = new ArrayList<>();

    private List<Data> dataList = List.of();
    private int hoveredIndex = -1;

    public HalfDonutChart() {
        setSpacing(10);
        getChildren().addAll(canvas, legendContainer);
        canvas.minWidth(400);
        canvas.minHeight(300);
        legendContainer.setMinWidth(180);
        legendContainer.setPadding(new Insets(10, 10, 10, 0));

        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);
        canvas.setOnMouseMoved(this::handleCanvasMouseMoved);
        setOnMouseExited(e -> setHoveredIndex(-1, null));
    }

    public void showData(List<Data> dataList) {
        this.dataList = List.copyOf(dataList);
        setHoveredIndex(-1, null);
        buildLegend();
        redraw();
    }

    private void buildLegend() {
        legendContainer.getChildren().clear();
        legendEntries.clear();
        for (int i = 0; i < dataList.size(); i++) {
            final int index = i;
            Data data = dataList.get(i);

            Pane colorSwatch = createColorSwatch(data.color());
            Label label = new Label(legendText(data), colorSwatch);
            label.setGraphicTextGap(8);
            label.setCursor(Cursor.HAND);

            HBox row = new HBox(label);
            row.setCursor(Cursor.HAND);
            row.setOnMouseEntered(event -> setHoveredIndex(index, event));

            legendEntries.add(new LegendEntry(row, colorSwatch, label));
            legendContainer.getChildren().add(row);
        }
        updateLegendHighlight();
    }

    private void handleCanvasMouseMoved(MouseEvent event) {
        setHoveredIndex(findSegmentAt(event.getX(), event.getY()), event);
    }

    private void setHoveredIndex(int index, MouseEvent event) {
        if (index == hoveredIndex) {
            return;
        }
        hoveredIndex = index;
        if (index >= 0) {
            Data data = dataList.get(index);
            tooltip.setText(legendText(data));
            if (event != null) {
                tooltip.show(this, event.getScreenX() + 12, event.getScreenY() + 12);
            }
            canvas.setCursor(Cursor.HAND);
        } else {
            tooltip.hide();
            canvas.setCursor(Cursor.DEFAULT);
        }
        updateLegendHighlight();
        redraw();
    }

    private void updateLegendHighlight() {
        for (int i = 0; i < legendEntries.size(); i++) {
            LegendEntry entry = legendEntries.get(i);
            boolean hovered = i == hoveredIndex;
            Color color = dataList.get(i).color();
            entry.colorSwatch().setBackground(createColorBackground(hovered ? color.brighter() : color));
            entry.label().setStyle(hovered ? "-fx-font-weight: bold;" : "");
        }
    }

    private Pane createColorSwatch(Color color) {
        Pane colorSwatch = new Pane();
        colorSwatch.setPrefSize(LEGEND_COLOR_SIZE, LEGEND_COLOR_SIZE);
        colorSwatch.setBackground(createColorBackground(color));
        return colorSwatch;
    }

    private Background createColorBackground(Color color) {
        return new Background(new BackgroundFill(color, LEGEND_CORNER, Insets.EMPTY));
    }

    private String legendText(Data data) {
        return "%s (%d%%)".formatted(data.label(), data.percentage());
    }

    private int findSegmentAt(double x, double y) {
        if (dataList.isEmpty()) {
            return -1;
        }
        double dx = x - CENTER_X;
        double dy = y - CENTER_Y;
        double distance = Math.hypot(dx, dy);
        if (distance < INNER_CIRCLE_RADIUS || distance > RADIUS + HOVER_RADIUS_OFFSET) {
            return -1;
        }
        double angleDeg = Math.toDegrees(Math.atan2(dy, dx));
        if (angleDeg < 0) {
            angleDeg += 360;
        }
        if (angleDeg > 0 && angleDeg < START_ANGLE) {
            return -1;
        }
        double arcPosition = angleDeg >= START_ANGLE ? angleDeg - START_ANGLE : 180;

        double currentAngle = 0;
        for (int i = 0; i < dataList.size(); i++) {
            double segmentAngle = (double) dataList.get(i).percentage() / TOTAL_PERCENTAGE * 180;
            double segmentEnd = currentAngle + segmentAngle;
            boolean isLastSegment = i == dataList.size() - 1;
            if (arcPosition >= currentAngle && (isLastSegment ? arcPosition <= segmentEnd : arcPosition < segmentEnd)) {
                return i;
            }
            currentAngle = segmentEnd;
        }
        return -1;
    }

    private void redraw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawChart(gc, dataList);
    }

    private void drawChart(GraphicsContext gc, List<Data> dataList) {
        double currentAngle = 0;

        HalfDonutPainter painter = HalfDonutPainter.of(gc, RADIUS, CENTER_X, CENTER_Y);
        for (int i = 0; i < dataList.size(); i++) {
            Data data = dataList.get(i);
            double angle = (double) data.percentage() / TOTAL_PERCENTAGE * 180;
            if (i != hoveredIndex) {
                painter.drawSegmentWithBorder(data.color(), currentAngle, angle);
            }
            currentAngle += angle;
        }
        if (hoveredIndex >= 0 && hoveredIndex < dataList.size()) {
            currentAngle = segmentStartAngle(hoveredIndex);
            Data hoveredData = dataList.get(hoveredIndex);
            double hoveredAngle = (double) hoveredData.percentage() / TOTAL_PERCENTAGE * 180;
            HalfDonutPainter.of(gc, RADIUS + HOVER_RADIUS_OFFSET, CENTER_X, CENTER_Y).drawSegmentWithBorder(hoveredData.color().brighter(), currentAngle, hoveredAngle);
        }
        HalfDonutPainter.of(gc, INNER_CIRCLE_RADIUS, CENTER_X, CENTER_Y).drawSegment(Color.WHITE, START_ANGLE, -180);
        drawDebug(gc, CENTER_X, CENTER_Y);
    }

    private double segmentStartAngle(int index) {
        return dataList.stream()
                       .limit(index)
                       .mapToDouble(data -> (double) data.percentage() / TOTAL_PERCENTAGE * 180)
                       .sum();
    }

    private void drawDebug(GraphicsContext gc, double centerX, double centerY) {
        gc.setFill(Color.RED);
        gc.fillRect(centerX - RADIUS - 2, centerY - RADIUS - 2, 4, 4);

        gc.setFill(Color.GREEN);
        gc.fillRect(centerX - 2, centerY - 2, 4, 4);
    }

    public record Data(String label, Color color, int percentage) {}

    private record LegendEntry(HBox row, Pane colorSwatch, Label label) {}
}
