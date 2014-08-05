package masteringlambdas.ch6;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;


/*
    Run with -Xmx2000M to reduce variance especially in ordered case due to heavy gc overhead
*/

@State(Scope.Benchmark)
@Fork(1)
public class Ordered {

    private List<String> stringList;

    @Setup(Level.Trial)
    public void main() {
        Random r = new Random();
        Supplier<String> strSupplier = () -> new BigInteger(64,r).toString();
        stringList = Stream.generate(strSupplier).limit(1_000_000).collect(toList());
    }

    @Benchmark
    public long unordered() {
        return stringList.parallelStream()
                .unordered()
                .distinct()
                .count();
    }

    @Benchmark
    public long ordered() {
        return stringList.parallelStream()
                .distinct()
                .count();
    }
}
