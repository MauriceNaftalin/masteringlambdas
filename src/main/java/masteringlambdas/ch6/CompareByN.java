package masteringlambdas.ch6;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@Fork(1)
public class CompareByN {

    @Param({"1", "10", "100", "1000", "10000", "100000", "1000000"})
    public int N;

    @Param({"5", "50", "500"})
    public int Q;

    private List<Integer> integerList;

    @Setup(Level.Trial)
    public void setUp() {
        integerList = IntStream.range(0, N).boxed().collect(toList());
    }

    @Benchmark
    public void iterative(Blackhole bh) {
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
