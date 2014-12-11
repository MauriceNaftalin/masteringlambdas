package org.masteringlambdas.ch3;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Fundamentals {
    public static void main(String[] args) {

        // Page 45
        //========

        IntStream.iterate(1, i -> i * 2)
                .limit(10)
                .forEachOrdered(System.out::println);


        IntStream.range(1, 6).forEach(System.out::print); //prints 123456

        // Page 46
        //========

        List<Integer> intList = Arrays.asList(1, 2, 3);

        OptionalDouble maxDistance =
                intList.stream()
                        .map(i -> new Point(i % 3, i / 3))
                        .mapToDouble(p -> p.distance(0, 0))
                        .max();

        DoubleStream ds = intList.stream()
                .map(i -> new Point(i % 3, i / 3))
                .mapToDouble(p -> p.distance(0, 0));

        OptionalDouble maxDistance1 = ds.max();

        // Page 48
        //========

        Optional<Integer> max = Arrays.asList(1,2,3,4,5).stream()
                .map(i -> i + 1)
                .max(Integer::compareTo);

        // Page 49
        //========

        OptionalInt max1 = IntStream.rangeClosed(1, 5)
                .map(i -> i + 1)
                .max();

        DoubleStream ds1 = IntStream.rangeClosed(1, 10).asDoubleStream();

        Stream<Integer> is = IntStream.rangeClosed(1, 10).boxed();

        Stream<Integer> integerStream = Stream.of(1, 2);
        IntStream is1 = integerStream.mapToInt(Integer::intValue);
    }
}
