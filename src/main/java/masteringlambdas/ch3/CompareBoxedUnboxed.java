package mastering.performance.ch3;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.BlackHole;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@Fork(1)
public class CompareBoxedUnboxed {

    @Param( {"1", "10", "100", "1000", "10000", "100000", "1000000" })
    public int N;

    private final int P = 4;

    private List<Integer> integerList;
    private int[] intArray;

    @Setup(Level.Trial)
    public void setUp() {
        integerList = IntStream.range(0, N).boxed().collect(toList());
        intArray = IntStream.range(0,N).toArray();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(P));
    }

    @GenerateMicroBenchmark
    public Optional<Integer> boxed() {
        return integerList.stream()
                .map(i -> i + 1)
                .max(Integer::compareTo);
    }

    @GenerateMicroBenchmark
    public OptionalInt unboxed() {
        return Arrays.stream(intArray)
                .map(i -> i + 1)
                .max();
    }
}
