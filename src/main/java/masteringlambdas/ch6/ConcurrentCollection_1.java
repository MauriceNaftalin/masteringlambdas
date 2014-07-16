package masteringlambdas.ch6;

import org.openjdk.jmh.annotations.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@State(Scope.Benchmark)
@Fork(1)
public class ConcurrentCollection_1 {

    private List<String> strList;

    @Setup(Level.Trial)
    public void setup() {
        Random r = new Random();
        Supplier<String> strSupplier = () -> new BigInteger(128,r).toString();
        strList = Stream.generate(strSupplier).limit(1000).collect(Collectors.toList());
    }

    @Benchmark
    public Map<String,Long> sequential() {
        return strList.parallelStream()
                .collect(Collectors.groupingBy(a -> a.substring(0, 6), Collectors.counting()));
    }

    @Benchmark
    public Map<String,Long> concurrent() {
        return strList.parallelStream()
                .collect(Collectors.groupingByConcurrent(a -> a.substring(0, 6), Collectors.counting()));
    }
}