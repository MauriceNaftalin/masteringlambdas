package mastering.performance.performancechapter;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.logic.BlackHole;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@Fork(1)
public class CompareByQ {

    @Param( {"1", "10", "100", "1000", "10000", "100000", "1000000" })
    public int Q;

    private final int N = 100;
    private final int P = 4;

    private List<Integer> integerList;

    @Setup(Level.Trial)
    public void setUp() {
        integerList = IntStream.range(0, N).boxed().collect(toList());
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(P));
    }

    @GenerateMicroBenchmark
    public void loop(BlackHole bh) {
        for (Integer i : integerList) {
            BlackHole.consumeCPU(Q);
            bh.consume(i);
        }
    }

    @GenerateMicroBenchmark
    public Optional<Integer> sequential() {
        return integerList.stream()
                .filter(l -> {
                    BlackHole.consumeCPU(Q);
                    return false;
                })
                .findFirst();
    }

    @GenerateMicroBenchmark
    public Optional<Integer> parallel() {
        return integerList.stream().parallel()
                .filter(l -> {
                    BlackHole.consumeCPU(Q);
                    return false;
                })
                .findFirst();
    }
}
