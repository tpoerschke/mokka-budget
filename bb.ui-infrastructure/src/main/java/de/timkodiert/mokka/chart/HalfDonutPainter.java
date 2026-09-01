package de.timkodiert.mokka.chart;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

public class HalfDonutPainter {

    private final GraphicsContext gc;
    private final double radius;
    private final double diameter;
    private final double centerX;
    private final double centerY;

    private HalfDonutPainter(GraphicsContext gc, double radius, double centerX, double centerY) {
        this.gc = gc;
        this.radius = radius;
        this.diameter = radius * 2;
        this.centerX = centerX;
        this.centerY = centerY;
    }

    public static HalfDonutPainter of(GraphicsContext gc, double radius, double centerX, double centerY) {
        return new HalfDonutPainter(gc, radius, centerX, centerY);
    }

    void drawSegment(Color color, double currentAngle, double angle) {
        gc.setFill(color);
        gc.fillArc(centerX - radius,
                   centerY - radius,
                   diameter,
                   diameter,
                   HalfDonutChart.START_ANGLE - currentAngle,
                   -angle,
                   ArcType.ROUND);
    }

    void drawSegmentWithBorder(Color color, double currentAngle, double angle) {
        drawSegmentWithBorder(color, Color.WHITE, HalfDonutChart.BORDER_WIDTH, currentAngle, angle);
    }

    void drawSegmentWithBorder(Color color, Color borderColor, double borderWidth, double currentAngle, double angle) {
        drawSegment(color, currentAngle, angle);
        gc.setStroke(borderColor);
        gc.setLineWidth(borderWidth);
        gc.strokeArc(
                centerX - radius,
                centerY - radius,
                diameter,
                diameter,
                HalfDonutChart.START_ANGLE - currentAngle,
                -angle,
                ArcType.ROUND
        );
    }
}
