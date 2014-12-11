package org.masteringlambdas.ch4;

import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collector;

import static org.junit.Assert.assertEquals;

public class PointGroupCollectorTest {

    private final static int MAX_DISTANCE = 3;
    private List<Point> startingList;

    @Before
    public void setup() {
        startingList = Arrays.asList(new Point(21, 0), new Point(33, 0), new Point(35, 0), new Point(23, 1), new Point(25, -1), new Point(31, 0));
    }

    @Test
    public void testIterativeVersion() {
        Deque<Deque<Point>> grouped = new PointGroupingCollector().iterativeGroupByProximity(startingList, MAX_DISTANCE);
        int[] groupSizes = {1, 2, 2, 1};
        assertEquals(groupSizes,
                grouped.stream()
                        .mapToInt(Deque::size)
                        .toArray());
    }

    @Test
    public void testCollector() {
        Collector<Point, ?, Deque<Deque<Point>>> pointGroupingCollector = PointGroupingCollector.createCollector(MAX_DISTANCE);
        Deque<Deque<Point>> grouped = startingList.stream()
                .collect(pointGroupingCollector);
        int[] groupSizes = {1, 2, 2, 1};
        assertEquals(groupSizes,
                grouped.stream()
                        .mapToInt(Deque::size)
                        .toArray());
    }
}
