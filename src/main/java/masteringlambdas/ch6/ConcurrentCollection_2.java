package masteringlambdas.ch6;

import org.openjdk.jmh.annotations.*;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@State(Scope.Benchmark)
@Fork(1)
public class ConcurrentCollection_2
{

    private List<String> strList;

    @Param({"1000", "10000", "100000", "1000000"})
    int size;

    @Setup(Level.Trial)
    public void setup() {
        Random r = new Random();
        Supplier<String> strSupplier = () -> new BigInteger(128,r).toString();
        strList = Stream.generate(strSupplier).limit(size).collect(Collectors.toList());
    }

    @Benchmark
    public Map<String,Long> sequential() {
        return strList.parallelStream()
                .collect(Collectors.groupingBy(a -> a.substring(0, 6), Collectors.counting()));
    }

    @Benchmark
    public Map<String,Long> sequentialPresize() {
        return strList.parallelStream()
                .collect(Collectors.groupingBy(a -> a.substring(0, 6), () -> new HashMap<String, Long>(size*2), Collectors.counting()));
    }

    @Benchmark
    public Map<String,Long> concurrent() {
        return strList.parallelStream()
                .collect(Collectors.groupingByConcurrent(a -> a.substring(0, 6), Collectors.counting()));
    }

    @Benchmark
    public Map<String,Long> concurrentPresize() {
        return strList.parallelStream()
                .collect(Collectors.groupingByConcurrent(a -> a.substring(0, 6), () -> new ConcurrentHashMap<String,Long>(size*2), Collectors.counting()));
    }


    @Benchmark
    public Map<String,Long> sequentialPresizeCHM() {
        return strList.parallelStream()
                .collect(Collectors.groupingBy(a -> a.substring(0, 6), () -> new ConcurrentHashMap<String, Long>(size*2), Collectors.counting()));
    }

}
