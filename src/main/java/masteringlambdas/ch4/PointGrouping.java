package masteringlambdas.ch4;


import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collector;

import static java.util.Comparator.comparingDouble;

@State(Scope.Benchmark)
@Fork(1)
public class PointGrouping {

    List<Point> points;
    final static int MAX_SPAN = 3;

    @Param( {"0", "50", "100" })
    public int Q;

    public static void main(String[] args) {
        new PointGrouping().run();
    }

    @Setup(Level.Trial)
    public void setUp() {
        //        List<Point> Points = Arrays.asList(new Point(21,0),new Point(33,0),new Point(35,0),new Point(23,1),new Point(25,-1),new Point(31,0));
        Random r = new Random();
        points = new ArrayList<>();
        for (int i = 0; i < 20000; i++) {
            Point p = new Point();
            p.setLocation(r.nextInt(40000), r.nextInt(2));
            points.add(p);
        }
        Collections.sort(points,comparingDouble(p -> p.distance(0, 0)));
    }

    public void run() {
        setUp();
        Deque<Deque<Point>> dp;
        dp = seqStream();
        dp = parStream();
        dp = iterative();
    }

    @Benchmark
    public Deque<Deque<Point>> seqStream() {
        return points.stream()
                .peek(p -> Blackhole.consumeCPU(Q))
                .collect(myCollector);
    }

    @Benchmark
    public Deque<Deque<Point>> parStream() {
        return points.parallelStream()
                .peek(p -> Blackhole.consumeCPU(Q))
                .collect(myCollector);
    }

    @Benchmark
    public Deque<Deque<Point>> iterative() {
        Deque<Deque<Point>> ddp = new ArrayDeque<>();
        for (Point p : points) {
            if (ddp.isEmpty()) ddp.add(new ArrayDeque<>());
            Deque<Point> last = ddp.getLast();
            if (! last.isEmpty() && last.getLast().distance(p) > MAX_SPAN ) {
                Deque<Point> db = new ArrayDeque<>();
                db.add(p);
                ddp.add(db);
            } else {
                last.add(p);
            }
            Blackhole.consumeCPU(Q);
        }
        return ddp;
    }

    static Collector<Point,?,Deque<Deque<Point>>> myCollector = Collector.of(
            () -> {
                Deque<Deque<Point>> ddp = new ArrayDeque<>();
                ddp.add(new ArrayDeque<>());
                return ddp;
            },
            (ddp, p) -> {
                Deque<Point> last = ddp.getLast();
                if (! last.isEmpty() && last.getLast().distance(p) > MAX_SPAN) {
                    Deque<Point> db = new ArrayDeque<>();
                    db.add(p);
                    ddp.add(db);
                } else {
                    last.add(p);
                }
            },
            (Deque<Deque<Point>> left, Deque<Deque<Point>> right) -> {
                Deque<Point> leftLast = left.getLast();
                if (leftLast.isEmpty()) return right;
                Deque<Point> rightFirst = right.getFirst();
                if (rightFirst.isEmpty()) return left;
                Point p = right.getFirst().getFirst();
                if (leftLast.getLast().distance(p) <= MAX_SPAN) {
                    leftLast.addAll(rightFirst);
                    right.removeFirst();
                }
                left.addAll(right);
                return left;
            });
}
