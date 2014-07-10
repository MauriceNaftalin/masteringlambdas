package mastering.performance.ch3;

import org.openjdk.jmh.annotations.*;

import java.util.Arrays;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

@State(Scope.Benchmark)
@Fork(1)
public class CompareBoxedUnboxed {

    @Param( {"1", "10", "100", "1000", "10000", "100000", "1000000" })
    public int N;

    private final int P = 4;

    private Integer[] integerArray;
    private int[] intArray;

    @Setup(Level.Trial)
    public void setUp() {
        integerArray = IntStream.range(0, N).boxed().toArray(Integer[]::new);
        intArray = IntStream.range(0,N).toArray();
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(P));
    }

    @Benchmark
    public Optional<Integer> boxed() {
        return Arrays.stream(integerArray)
                .map(i -> i * 5)
                .max(Integer::compareTo);
    }

    @Benchmark
    public OptionalInt unboxed() {
        return Arrays.stream(intArray)
                .map(i -> i * 5)
                .max();
    }
}
