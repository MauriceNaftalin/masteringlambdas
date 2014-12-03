package masteringlambdas.ch6;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@Fork(1)
public class CompareByP {

    @Param( {"1", "2", "4" })
    public int P;

    private final int Q = 500;
    private final int N = 100;

    private List<Integer> integerList;

    @Setup(Level.Trial)
    public void setUp() {
        integerList = IntStream.range(0, N).boxed().collect(toList());
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", Integer.toString(P));
    }

    @Benchmark
    public void loop(Blackhole bh) {
        for (Integer i : integerList) {
            Blackhole.consumeCPU(Q);
            bh.consume(i);
        }
    }

    @Benchmark
    public Optional<Integer> sequential() {
        return integerList.stream()
                .filter(l -> {
                    Blackhole.consumeCPU(Q);
                    return false;
                })
                .findFirst();
    }

    @Benchmark
    public Optional<Integer> parallel() {
        return integerList.stream().parallel()
                .filter(l -> {
                    Blackhole.consumeCPU(Q);
                    return false;
                })
                .findFirst();
    }
}
