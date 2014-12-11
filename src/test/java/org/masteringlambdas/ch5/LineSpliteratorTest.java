package org.masteringlambdas.ch5;


import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.util.Spliterator;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.joining;
import static org.junit.Assert.assertEquals;
import static org.masteringlambdas.ch5.LineSpliterator.DispLine;

public class LineSpliteratorTest {

    private File tempFile;

    @Before
    public void createFile() throws IOException{
        tempFile = File.createTempFile("lineSpliteratorTest", "");
        tempFile.deleteOnExit();
        PrintWriter p = new PrintWriter(tempFile);
        p.print("The moving finger writes; and, having writ,\n" +
                "Moves on: nor all thy Piety nor Wit\n" +
                "Shall lure it back to cancel half a line,\n" +
                "Nor all thy Tears wash out a Word of it\n");
        p.close();
    }

    @Test
    public void testLineSpliterator() throws IOException, InterruptedException {
        Path start = tempFile.toPath();
        try (FileChannel fc = FileChannel.open(start)){
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            Spliterator<DispLine> ls = new LineSpliterator(bb, 0, bb.limit() - 1);
            String collect = StreamSupport.stream(ls, true)
                    .map(dl -> dl.disp + " " + dl.line)
                    .collect(joining());
            assertEquals(collect,
                    "0 The moving finger writes; and, having writ,\n" +
                    "44 Moves on: nor all thy Piety nor Wit\n" +
                    "80 Shall lure it back to cancel half a line,\n" +
                    "122 Nor all thy Tears wash out a Word of it\n");
        }
    }
}
