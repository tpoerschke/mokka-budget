package de.timkodiert.mokka.chart;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import lombok.Setter;

import de.timkodiert.mokka.i18n.LanguageManager;

public class HalfDonutChart extends VBox {

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

    private final Canvas canvas = new Canvas(400, 220);
    private final GridPane legendContainer = new GridPane();
    private final Tooltip tooltip = new Tooltip();
    private final List<LegendEntry> legendEntries = new ArrayList<>();
    private final IntegerProperty focusedIndexProperty = new SimpleIntegerProperty(-1);

    private List<Data> dataList = List.of();
    private int hoveredIndex = -1;

    @Setter
    private LanguageManager languageManager;

    public HalfDonutChart() {
        setSpacing(10);
        setAlignment(Pos.TOP_CENTER);
        getChildren().addAll(canvas, legendContainer);
        canvas.minWidth(400);
        legendContainer.setMinWidth(200);
        legendContainer.setPadding(new Insets(10, 10, 10, 0));
        legendContainer.setVgap(5);
        legendContainer.setHgap(10);
        legendContainer.setAlignment(Pos.TOP_CENTER);

        tooltip.setShowDelay(Duration.ZERO);
        tooltip.setHideDelay(Duration.ZERO);
        canvas.setOnMouseMoved(this::handleCanvasMouseMoved);
        canvas.setOnMouseClicked(this::handleCanvasMouseClicked);
        setOnMouseExited(e -> setHoveredIndex(-1, null));

        focusedIndexProperty.addListener((observable, oldValue, newValue) -> {
            redraw();
            updateLegendHighlight();
        });
    }

    public void showData(List<Data> dataList) {
        this.dataList = List.copyOf(dataList);
        setHoveredIndex(-1, null);
        buildLegend();
        redraw();
    }

    public void redrawWithLegend() {
        redraw();
        updateLegendHighlight();
    }

    public IntegerProperty focusedIndexProperty() {
        return focusedIndexProperty;
    }

    private void buildLegend() {
        legendContainer.getChildren().clear();
        legendEntries.clear();
        for (int i = 0; i < dataList.size(); i++) {
            final int index = i;
            Data data = dataList.get(i);

            Pane colorBox = createColorBox(data.color());
            Label label = new Label(legendText(data), colorBox);
            label.setGraphicTextGap(8);
            label.setCursor(Cursor.HAND);

            HBox legendEntryBox = new HBox(label);
            legendEntryBox.setCursor(Cursor.HAND);
            legendEntryBox.setOnMouseEntered(event -> setHoveredIndex(index, event));
            legendEntryBox.setOnMouseClicked(event -> focusedIndexProperty.set(index));

            legendEntries.add(new LegendEntry(legendEntryBox, colorBox, label));
            legendContainer.add(legendEntryBox, i % 2, i / 2);
        }
        updateLegendHighlight();
    }

    private void handleCanvasMouseClicked(MouseEvent event) {
        int clickedSegment = findSegmentAt(event.getX(), event.getY());
        focusedIndexProperty.set(clickedSegment);
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
        redrawWithLegend();
    }

    private void updateLegendHighlight() {
        for (int i = 0; i < legendEntries.size(); i++) {
            LegendEntry entry = legendEntries.get(i);
            boolean hovered = i == hoveredIndex;
            boolean focused = i == focusedIndexProperty.get();
            Color color = dataList.get(i).color();
            entry.colorBox().setBackground(createColorBackground(hovered || focused ? color.brighter() : color));
            entry.label().setStyle(hovered ? "-fx-font-weight: bold;" : "");

            if (focused) {
                entry.colorBox().setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, LEGEND_CORNER, BorderStroke.THIN)));
            } else {
                entry.colorBox.setBorder(Border.EMPTY);
            }
        }
    }

    private Pane createColorBox(Color color) {
        Pane colorBox = new Pane();
        colorBox.setPrefSize(LEGEND_COLOR_SIZE, LEGEND_COLOR_SIZE);
        colorBox.setBackground(createColorBackground(color));
        return colorBox;
    }

    private Background createColorBackground(Color color) {
        return new Background(new BackgroundFill(color, LEGEND_CORNER, Insets.EMPTY));
    }

    private String legendText(Data data) {
        String label = languageManager.resourceBundleContainsKey(data.label()) ? languageManager.get(data.label()) : data.label();
        return "%s (%d%%)".formatted(label, data.percentage());
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
            if (i != hoveredIndex && i != focusedIndexProperty.get()) {
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
        int focusedIndex = focusedIndexProperty.get();
        if (focusedIndex >= 0 && focusedIndex < dataList.size()) {
            currentAngle = segmentStartAngle(focusedIndex);
            Data focusedData = dataList.get(focusedIndex);
            double focusedAngle = (double) focusedData.percentage() / TOTAL_PERCENTAGE * 180;
            HalfDonutPainter.of(gc, RADIUS + HOVER_RADIUS_OFFSET, CENTER_X, CENTER_Y)
                            .drawSegmentWithBorder(focusedData.color().brighter(), Color.BLACK, 2, currentAngle, focusedAngle);
            HalfDonutPainter.of(gc, INNER_CIRCLE_RADIUS, CENTER_X, CENTER_Y).drawSegmentWithBorder(Color.WHITE, Color.BLACK, 2, currentAngle, focusedAngle);
        }
        HalfDonutPainter.of(gc, INNER_CIRCLE_RADIUS - 1, CENTER_X, CENTER_Y).drawSegment(Color.WHITE, START_ANGLE, -360);
    }

    private double segmentStartAngle(int index) {
        return dataList.stream()
                       .limit(index)
                       .mapToDouble(data -> (double) data.percentage() / TOTAL_PERCENTAGE * 180)
                       .sum();
    }

    public record Data(String label, Color color, int percentage) {}

    private record LegendEntry(HBox row, Pane colorBox, Label label) {}
}
