package org.masteringlambdas.performance.ch1;

import org.openjdk.jmh.annotations.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Fork(1)
@State(Scope.Benchmark)
public class MaxDistance {

    @Param({"1", "100", "10000", "1000000"})
    public int length;

    public List<Integer> intList;

    @Setup(Level.Trial)
    public void setUpIntList() {
        intList = IntStream.range(0,length).boxed().collect(toList());
    }

    @Benchmark
    public OptionalDouble sequential() {
        return intList.stream()
                .map(i -> new Point(i % 3, i / 3))
                .mapToDouble(p -> p.distance(0, 0))
                .max();
    }

    @Benchmark
    public OptionalDouble parallel() {
        return intList.stream().parallel()
                .map(i -> new Point(i % 3, i / 3))
                .mapToDouble(p -> p.distance(0, 0))
                .max();
    }

    @Benchmark
    public double loop() {
        List<Point> pointList = new ArrayList<>();
        for (Integer i : intList) {
            pointList.add(new Point(i % 3, i / 3));
        }
        double maxDistance = Double.MIN_VALUE;
        for (Point p : pointList) {
            maxDistance = Math.max(p.distance(0, 0), maxDistance);
        }
        return maxDistance;
    }
}
