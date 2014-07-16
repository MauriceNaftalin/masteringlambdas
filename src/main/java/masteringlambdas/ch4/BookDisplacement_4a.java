package masteringlambdas.ch4;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toConcurrentMap;
import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@Fork(1)
public class BookDisplacement_4a {

    @Param( {"100", "200" })
    public int Q;

    private List<Book> library;

    @Setup(Level.Trial)
    public void setup() {

        Random r = new Random();
        Supplier<String> strSupplier = () -> new BigInteger(64,r).toString();
        library = Stream.generate(strSupplier).limit(1_000_000).map(Book::new).collect(toList());
        Collections.sort(library,comparing(Book::getTitle));

    }

    @Benchmark
    public Deque<DispRecord> iterativeBookDisplacement() {
        Deque<DispRecord> drs = new ArrayDeque<>();
        int runningTotal = 0;
        for (Book b : library) {
            int sum = Arrays.stream(b.getPageCounts()).sum();
            drs.add(new DispRecord(b.getTitle(),runningTotal, sum));
            runningTotal += sum;
            Blackhole.consumeCPU(Q);
        }
        return drs;
    }

    @Benchmark
    public Deque<DispRecord> parallelBookDisplacement() {

        return library.parallelStream()
                .peek(b -> Blackhole.consumeCPU(Q))
                .collect(dispLineDequeCollector);
    }

    @Benchmark
    public Deque<DispRecord> sequentialBookDisplacement() {

        return library.stream()
                .peek(b -> Blackhole.consumeCPU(Q))
                .collect(dispLineDequeCollector);
    }

    Collector<Book,?,Deque<DispRecord>> dispLineDequeCollector = Collector.of(
            ArrayDeque::new,
            (Deque<DispRecord> dqLeft, Book b) -> {
                int disp = dqLeft.isEmpty() ? 0 : dqLeft.getLast().totalDisp();
                dqLeft.add(new DispRecord(b.getTitle(), disp, Arrays.stream(b.getPageCounts()).sum()));
            },
            (left, right) -> {
                if (left.isEmpty()) return right;
                int newDisp = left.getLast().totalDisp();
                for (DispRecord dr : right) {
                    dr.disp += newDisp;
                }
                List<DispRecord> displacedDispLines = right.stream()
                        .map(dr -> new DispRecord(dr.title, dr.disp + newDisp, dr.length))
                        .collect(toList());
                left.addAll(displacedDispLines);
                left.addAll(right);
                return left;
            }//,
    );

    Function<Deque<DispRecord>,Map<String,Integer>> thing = (dq) -> dq.parallelStream().collect(
            toConcurrentMap(vo -> vo.title, vo -> vo.disp, (x,y) -> {throw new IllegalStateException();}));


    class DispRecord {
        int disp, length;
        final String title;
        DispRecord(String t, int d, int l) { this.title = t; this.disp = d; this.length = l; }

        @Override
        public String toString() {
            return title + " " + disp + " " + length;
        }
        int totalDisp() { return disp + length; }
    }

    public static class Book {
        private String title;
        private int[] pageCounts;

        public Book(String title) {
            this.title = title;
            this.pageCounts = new int[]{10};
        }

        public String getTitle() {
            return title;
        }

        public int[] getPageCounts() {
            return pageCounts;
        }

        @Override
        public String toString() {
            return title;
        }
    }
}
