package mastering.performance.ch5;

import org.openjdk.jmh.annotations.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Spliterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@State(Scope.Benchmark)
@Fork(1)
public class GrepBWithMappedFile {

    @Param( {"10000", "100000", "1000000" })
    public int N;

    private MappedByteBuffer bb;
    private Pattern patt;

    @Setup(Level.Trial)
    public void setUp() throws IOException {
        PrintWriter p = new PrintWriter(new FileWriter("/tmp/bigfile"));
        StringBuilder sb = new StringBuilder();
        Random r = new Random();
        for (int i = 0; i < N; i++) {
            int wordCount = r.nextInt(20);
            for (int j = 0; j < wordCount; j++) {
                sb.append(new BigInteger(r.nextInt(32) + 3, r));
                sb.append(' ');
            }
            p.println(sb);
            sb.setLength(0);
        }
        p.close();
        Path start = new File("/tmp/bigfile").toPath();
        FileChannel fc = FileChannel.open(start);
        bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        patt = Pattern.compile("12345*");
    }

    @Benchmark
    public List<DispLine> seqStream() throws IOException, InterruptedException {
        Spliterator<DispLine> ls = new LineSpliterator(bb, 0, bb.limit() - 1);
        return StreamSupport.stream(ls, false)
                .filter(l -> patt.matcher(l.line).find())
                .collect(toList());
    }

    @Benchmark
    public List<DispLine> parStream() throws IOException, InterruptedException {
        Spliterator<DispLine> ls = new LineSpliterator(bb, 0, bb.limit() - 1);
        return StreamSupport.stream(ls, true)
                .filter(l -> patt.matcher(l.line).find())
                .collect(toList());
    }

    static class LineSpliterator implements Spliterator<DispLine> {

        private static final int AVG_LINE_LENGTH = 40;
        private ByteBuffer bb;
        private int lo, hi;

        LineSpliterator(ByteBuffer bb, int lo, int hi) {
            this.bb = bb;
            this.lo = lo;
            this.hi = hi;
        }

        public boolean tryAdvance(Consumer<? super DispLine> action) {
            int index = lo;
            StringBuilder sb = new StringBuilder();
            do {
                sb.append((char)bb.get(index));
            } while (bb.get(index++) != '\n');
            action.accept(new DispLine(lo, sb.toString()));
            lo = lo + sb.length();
            return lo <= hi;
        }

        @Override
        public Spliterator<DispLine> trySplit() {
            int index = (lo + hi) >>> 1;
            while (bb.get(index) != '\n') index++;
            LineSpliterator newSpliterator = null;
            if (index != hi) {
                newSpliterator = new LineSpliterator(bb, lo, index);
                lo = index + 1;
            }
            return newSpliterator;
        }

        @Override
        public long estimateSize() {
            return (hi - lo + 1) / AVG_LINE_LENGTH;
        }

        @Override
        public int characteristics() {
            return ORDERED | IMMUTABLE | NONNULL ;
        }
    }

    static class DispLine {
        final int disp;
        final String line;
        DispLine(int d, String l) { disp = d; line = l; }
        public String toString() { return disp + ":" + line; }
    }

    @Benchmark
    public List<DispLine> sequential() throws Exception {
        List<DispLine> disps = new ArrayList<>();
            int nlIndex = 0, currentIndex = 0;
            int disp = 0;
            StringBuilder sb = new StringBuilder();
            while (currentIndex < bb.capacity()) {
                while (nlIndex < bb.capacity() && bb.get(nlIndex) != '\n') {
                    sb.append(bb.get(nlIndex));
                    nlIndex++;
                }
                String line = sb.toString();
                sb.setLength(0);
                if (patt.matcher(line).find()) disps.add(new DispLine(disp,line));
                disp += nlIndex + 1 - currentIndex;
                currentIndex += nlIndex + 1 - currentIndex;
                nlIndex = currentIndex + 1;
            }
        return disps;
    }
}
