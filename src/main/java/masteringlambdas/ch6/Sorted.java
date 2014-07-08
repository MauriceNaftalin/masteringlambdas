package mastering.performance.ch6;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import sun.print.resources.serviceui;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

/*
java -XX:-TieredCompilation -jar target/microbenchmarks.jar ".*Sorted.*"
*/

@State(Scope.Benchmark)
@Fork(1)
public class Sorted {

    @Param( {"100", "1000", "10000", "100000", "1000000", "10000000" })
    public int N;

    private List<Integer> iterativeList, streamList;

    @Setup(Level.Trial)
    public void setUp() {
        Random r = new Random();
        iterativeList = new ArrayList<>();
        streamList = new ArrayList<>();
        for (int i = 0; i < N ; i++) {
            int e = new BigInteger(16, r).intValue();
            iterativeList.add(e);
            streamList.add(e);
        }
    }

    @Benchmark
    public List<Integer> iterative() {
        Collections.sort(iterativeList);
        return iterativeList;
    }

    @Benchmark
    public Optional<Integer> sequential() {
        return streamList.stream()
                .sorted()
                .filter(l -> false)
                .findFirst();
    }

    @Benchmark
    public Optional<Integer> parallel() {
        return streamList.stream().parallel()
                .sorted()
                .filter(l -> false)
                .findFirst();
    }
}
