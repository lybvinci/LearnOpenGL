package com.lybvinci.learnopengl.objects;

import android.opengl.GLES20;
import android.util.FloatMath;

import com.lybvinci.learnopengl.utils.Geometry;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_TRIANGLE_STRIP;
import static android.opengl.GLES20.glDrawArrays;

import java.util.ArrayList;
import java.util.List;

public class ObjectBuilder {
//    static interface DrawCommand {
//        void draw();
//    }
//    static class GeneratedData {
//        final float[] vertexData;
//        final List<DrawCommand> drawList;
//
//        public GeneratedData(float[] vertexData, List<DrawCommand> drawCommands) {
//            this.vertexData = vertexData;
//            this.drawList = drawCommands;
//        }
//    }
//
//    private static final int FLOATS_PER_VERTEX = 3;
//    private final float[] vertexData;
//    private final List<DrawCommand> drawList = new ArrayList<>();
//    private int offset = 0;
//
//    private ObjectBuilder(int sizeInVertices) {
//        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
//    }
//
//    private static int sizeOfCircleInVertices(int numPoints) {
//        return 1 + (numPoints + 1);
//    }
//
//    private static int sizeOfOpenCylinderInVertices(int numPoints) {
//        return (numPoints + 1) * 2;
//    }
//
//    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
//        int size = sizeOfCircleInVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints);
//        ObjectBuilder builder = new ObjectBuilder(size);
//        Geometry.Circle puckTop = new Geometry.Circle(puck.center.translateY(puck.height / 2f),
//                puck.radius);
//        builder.appendCircle(puckTop, numPoints);
//        builder.appendOpenCylinder(puck, numPoints);
//        return builder.build();
//    }
//
//    static GeneratedData createMallet(Geometry.Point center, float radius, float height, int numPoints) {
//        int size = sizeOfCircleInVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2;
//        ObjectBuilder builder = new ObjectBuilder(size);
//        float baseHeight = height * 0.25f;
//        Geometry.Circle baseCircle = new Geometry.Circle(center.translateY(-baseHeight), radius);
//        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight);
//        builder.appendCircle(baseCircle, numPoints);
//        builder.appendOpenCylinder(baseCylinder, numPoints);
//
//        float handleHeight = height * 0.75f;
//        float handleRadius = radius / 3;
//        Geometry.Circle handleCircle = new Geometry.Circle(center.translateY(height * 0.5f), handleRadius);
//        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight);
//
//        builder.appendCircle(handleCircle, numPoints);
//        builder.appendOpenCylinder(handleCylinder, numPoints);
//
//        return builder.build();
//    }
//
//    private void appendCircle(Geometry.Circle circle, int numPoints) {
//        final int startVertex = offset / FLOATS_PER_VERTEX;
//        final int numVertices = sizeOfCircleInVertices(numPoints);
//        vertexData[offset++] = circle.center.x;
//        vertexData[offset++] = circle.center.y;
//        vertexData[offset++] = circle.center.z;
//
//        for (int i = 0; i <= numPoints; i++) {
//            float angleInRadians = (float) ((float) i / (float) numPoints * Math.PI * 2f);
//            vertexData[offset++] = (float) (circle.center.x + circle.radius * Math.cos(angleInRadians));
//            vertexData[offset++] = circle.center.y;
//            vertexData[offset++] = circle.center.z + (float) (circle.radius * Math.sin(angleInRadians));
//        }
//
//        drawList.add(new DrawCommand() {
//            @Override
//            public void draw() {
//                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, startVertex, numVertices);
//            }
//        });
//    }
//
//    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
//        final int startVertex = offset / FLOATS_PER_VERTEX;
//        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
//        final float yStart = cylinder.center.y - (cylinder.height / 2f);
//        final float yEnd = cylinder.center.y + (cylinder.height / 2f);
//        for (int i = 0; i <= numPoints; i++) {
//            float angleInRadius = (float) (i / numPoints * Math.PI * 2f);
//            float xPosition = (float) (cylinder.center.x + cylinder.radius * Math.cos(angleInRadius));
//            float zPosition = (float) (cylinder.center.z + cylinder.radius * Math.sin(angleInRadius));
//            vertexData[offset++] = xPosition;
//            vertexData[offset++] = yStart;
//            vertexData[offset++] = zPosition;
//            vertexData[offset++] = xPosition;
//            vertexData[offset++] = yEnd;
//            vertexData[offset++] = zPosition;
//        }
//        drawList.add(new DrawCommand() {
//            @Override
//            public void draw() {
//                GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, startVertex, numVertices);
//            }
//        });
//    }
//
//    private GeneratedData build() {
//        return new GeneratedData(vertexData, drawList);
//    }

    private static final int FLOATS_PER_VERTEX = 3;

    static interface DrawCommand {
        void draw();
    }

    static class GeneratedData {
        final float[] vertexData;
        final List<DrawCommand> drawList;

        GeneratedData(float[] vertexData, List<DrawCommand> drawList) {
            this.vertexData = vertexData;
            this.drawList = drawList;
        }
    }

    static GeneratedData createPuck(Geometry.Cylinder puck, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints)
                + sizeOfOpenCylinderInVertices(numPoints);

        ObjectBuilder builder = new ObjectBuilder(size);

        Geometry.Circle puckTop = new Geometry.Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius);

        builder.appendCircle(puckTop, numPoints);
        builder.appendOpenCylinder(puck, numPoints);

        return builder.build();
    }

    static GeneratedData createMallet(
            Geometry.Point center, float radius, float height, int numPoints) {
        int size = sizeOfCircleInVertices(numPoints) * 2
                + sizeOfOpenCylinderInVertices(numPoints) * 2;

        ObjectBuilder builder = new ObjectBuilder(size);

        // First, generate the mallet base.
        float baseHeight = height * 0.25f;

        Geometry.Circle baseCircle = new Geometry.Circle(
                center.translateY(-baseHeight),
                radius);
        Geometry.Cylinder baseCylinder = new Geometry.Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius, baseHeight);

        builder.appendCircle(baseCircle, numPoints);
        builder.appendOpenCylinder(baseCylinder, numPoints);

        // Now generate the mallet handle.
        float handleHeight = height * 0.75f;
        float handleRadius = radius / 3f;

        Geometry.Circle handleCircle = new Geometry.Circle(
                center.translateY(height * 0.5f),
                handleRadius);
        Geometry.Cylinder handleCylinder = new Geometry.Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius, handleHeight);

        builder.appendCircle(handleCircle, numPoints);
        builder.appendOpenCylinder(handleCylinder, numPoints);

        return builder.build();
    }

    private static int sizeOfCircleInVertices(int numPoints) {
        return 1 + (numPoints + 1);
    }

    private static int sizeOfOpenCylinderInVertices(int numPoints) {
        return (numPoints + 1) * 2;
    }

    private final float[] vertexData;
    private final List<DrawCommand> drawList = new ArrayList<DrawCommand>();
    private int offset = 0;

    private ObjectBuilder(int sizeInVertices) {
        vertexData = new float[sizeInVertices * FLOATS_PER_VERTEX];
    }

    private void appendCircle(Geometry.Circle circle, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfCircleInVertices(numPoints);

        // Center point of fan
        vertexData[offset++] = circle.center.x;
        vertexData[offset++] = circle.center.y;
        vertexData[offset++] = circle.center.z;

        // Fan around center point. <= is used because we want to generate
        // the point at the starting angle twice to complete the fan.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);

            vertexData[offset++] =
                    (float) (circle.center.x
                                                + circle.radius * Math.cos(angleInRadians));
            vertexData[offset++] = circle.center.y;
            vertexData[offset++] =
                    (float) (circle.center.z
                                                + circle.radius * Math.sin(angleInRadians));
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices);
            }
        });
    }

    private void appendOpenCylinder(Geometry.Cylinder cylinder, int numPoints) {
        final int startVertex = offset / FLOATS_PER_VERTEX;
        final int numVertices = sizeOfOpenCylinderInVertices(numPoints);
        final float yStart = cylinder.center.y - (cylinder.height / 2f);
        final float yEnd = cylinder.center.y + (cylinder.height / 2f);

        // Generate strip around center point. <= is used because we want to
        // generate the points at the starting angle twice, to complete the
        // strip.
        for (int i = 0; i <= numPoints; i++) {
            float angleInRadians =
                    ((float) i / (float) numPoints)
                            * ((float) Math.PI * 2f);

            float xPosition =
                    (float) (cylinder.center.x
                                                + cylinder.radius * Math.cos(angleInRadians));

            float zPosition =
                    (float) (cylinder.center.z
                                                + cylinder.radius * Math.sin(angleInRadians));

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yStart;
            vertexData[offset++] = zPosition;

            vertexData[offset++] = xPosition;
            vertexData[offset++] = yEnd;
            vertexData[offset++] = zPosition;
        }
        drawList.add(new DrawCommand() {
            @Override
            public void draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices);
            }
        });
    }

    private GeneratedData build() {
        return new GeneratedData(vertexData, drawList);
    }

}
