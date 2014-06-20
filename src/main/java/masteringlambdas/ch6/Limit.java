package mastering.performance.performancechapter;

import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/*

java -XX:-TieredCompilation -jar target/microbenchmarks.jar ".*Limit.*"

*/

@State(Scope.Benchmark)
@Fork(1)
public class Limit {

//    @Param( {"1", "10", "100", "1000", "10000", "100000", "1000000" })
    public int N=100000;

    private final int Q = 50;
    private final int P = 4;

    private List<Integer> integerList;

    @Setup(Level.Trial)
    public void setUp() {
        integerList = IntStream.range(0, N).boxed().collect(toList());
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(P));
    }

    @GenerateMicroBenchmark
    public Optional<Integer> sequentialOrderedNoLimit() {
        return integerList.stream()
//                .parallel()
//                .unordered()
//                .limit(N)
                .filter(l -> false)
                .findFirst();
    }

    @GenerateMicroBenchmark
    public Optional<Integer> sequentialOrderedLimit() {
        return integerList.stream()
//                .parallel()
//                .unordered()
                .limit(N)
                .filter(l -> false)
                .findFirst();
    }

    @GenerateMicroBenchmark
    public Optional<Integer> sequentialUnorderedNoLimit() {
        return integerList.stream()
//                .parallel()
                .unordered()
//                .limit(N)
                .filter(l -> false)
                .findFirst();
    }

    @GenerateMicroBenchmark
    public Optional<Integer> sequentialUnorderedLimit() {
        return integerList.stream()
//                .parallel()
                .unordered()
                .limit(N)
                .filter(l -> false)
                .findFirst();
    }

    @GenerateMicroBenchmark
    public Optional<Integer> parallelOrderedNoLimit() {
        return integerList.stream()
                .parallel()
//                .unordered()
//                .limit(N)
                .filter(l -> false)
                .findFirst();
    }


    @GenerateMicroBenchmark
    public Optional<Integer> parallelOrderedLimit() {
        return integerList.stream()
                .parallel()
//                .unordered()
                .limit(N)
                .filter(l -> false)
                .findFirst();
    }


    @GenerateMicroBenchmark
    public Optional<Integer> parallelUnorderedNoLimit() {
        return integerList.stream()
                .parallel()
                .unordered()
//                .limit(N)
                .filter(l -> false)
                .findFirst();
    }


    @GenerateMicroBenchmark
    public Optional<Integer> parallelUnorderedLimit() {
        return integerList.stream()
                .parallel()
                .unordered()
                .limit(N)
                .filter(l -> false)
                .findFirst();
    }
}
