package masteringlambdas.ch6;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import sun.print.resources.serviceui;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@Fork(1)
public class Sorted {

    @Param( {"100", "1000", "10000", "100000", "1000000", "10000000" })
    public int N;

    @Param( {"0", "100" })
    public int Q;

    private List<Integer> iterativeList, streamList;

    public static void main(String[] args) {
        Sorted s = new Sorted();
        s.setUp();
        System.out.println(s.iterativeList);
    }

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
        for (Integer i : iterativeList) {
            Blackhole.consumeCPU(Q);
        }
        Collections.sort(iterativeList);
        return iterativeList;
    }

    @Benchmark
    public Optional<Integer> sequential() {
        return streamList.stream()
                .peek(i -> Blackhole.consumeCPU(Q))
                .sorted()
                .filter(l -> false)
                .findFirst();
    }

    @Benchmark
    public Optional<Integer> parallel() {
        return streamList.stream().parallel()
                .peek(i -> Blackhole.consumeCPU(Q))
                .sorted()
                .filter(l -> false)
                .findFirst();
    }
}
