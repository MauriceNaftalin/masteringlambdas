package org.masteringlambdas.ch4;


import java.awt.*;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class PointGroupingCollector {

    Deque<Deque<Point>> iterativeGroupByProximity(List<Point> points, int maxDistance) {
        Deque<Deque<Point>> grouped = new ArrayDeque<>();
        for (Point p : points) {
            if (grouped.isEmpty()) grouped.add(new ArrayDeque<>());
            Deque<Point> lastSegment = grouped.getLast();
            if (! lastSegment.isEmpty() && lastSegment.getLast().distance(p) > maxDistance) {
                Deque<Point> newSegment = new ArrayDeque<>();
                newSegment.add(p);
                grouped.add(newSegment);
            } else {
                lastSegment.add(p);
            }
        }
        return grouped;
    }

    public static Collector<Point,?,Deque<Deque<Point>>> createCollector(int maxDistance) {

        Supplier<Deque<Deque<Point>>> supplier =
                () -> {
                    Deque<Deque<Point>> ddp = new ArrayDeque<>();
                    ddp.add(new ArrayDeque<>());
                    return ddp;
                };

        BiConsumer<Deque<Deque<Point>>, Point> accumulator = (ddp, p) -> {
            Deque<Point> last = ddp.getLast();
            if (!last.isEmpty()
                    && last.getLast().distance(p) > maxDistance) {
                Deque<Point> dp = new ArrayDeque<>();
                dp.add(p);
                ddp.add(dp);
            } else {
                last.add(p);
            }
        };

        BinaryOperator<Deque<Deque<Point>>> combiner = (left, right) -> {
            Deque<Point> leftLast = left.getLast();
            if (leftLast.isEmpty()) return right;
            Deque<Point> rightFirst = right.getFirst();
            if (rightFirst.isEmpty()) return left;
            Point p = rightFirst.getFirst();
            if (leftLast.getLast().distance(p) <= maxDistance) {
                leftLast.addAll(rightFirst);
                right.removeFirst();
            }
            left.addAll(right);
            return left;
        };


        return Collector.of(supplier, accumulator, combiner);
    }
}
