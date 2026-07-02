package fr.sdv.etloff.etl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.springframework.batch.item.ItemReader;

public class PartitionedCsvLineReader implements ItemReader<String> {

    private final Path csvPath;
    private final long startLine;
    private final long endLine;
    private final long startByte;

    private BufferedReader reader;
    private long currentLine;
    private boolean initialized;

    public PartitionedCsvLineReader(Path csvPath, long startLine, long endLine, long startByte) {
        this.csvPath = csvPath;
        this.startLine = startLine;
        this.endLine = endLine;
        this.startByte = startByte;
    }

    @Override
    public String read() throws Exception {
        if (!initialized) {
            openAtStart();
            initialized = true;
        }
        if (currentLine > endLine) {
            close();
            return null;
        }
        String line = reader.readLine();
        currentLine++;
        return line;
    }

    private void openAtStart() throws IOException {
        RandomAccessFile file = new RandomAccessFile(csvPath.toFile(), "r");
        file.seek(startByte);
        reader = new BufferedReader(
                new InputStreamReader(new RandomAccessFileInputStream(file), StandardCharsets.UTF_8));
        currentLine = startLine;
    }

    private void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    private static final class RandomAccessFileInputStream extends InputStream {

        private final RandomAccessFile file;

        private RandomAccessFileInputStream(RandomAccessFile file) {
            this.file = file;
        }

        @Override
        public int read() throws IOException {
            return file.read();
        }

        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            return file.read(b, off, len);
        }

        @Override
        public void close() throws IOException {
            file.close();
        }
    }
}
