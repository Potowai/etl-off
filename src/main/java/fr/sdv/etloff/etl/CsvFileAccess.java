package fr.sdv.etloff.etl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import fr.sdv.etloff.config.EtlProperties;

@Component
public class CsvFileAccess {

    private final Path localPath;
    private final long[] lineOffsets;

    public CsvFileAccess(ResourceLoader resourceLoader, EtlProperties properties) throws IOException {
        Resource resource = resourceLoader.getResource(properties.getCsvPath());
        if (resource.isFile()) {
            this.localPath = Path.of(resource.getFile().toURI());
        } else {
            this.localPath = Files.createTempFile("open-food-facts-", ".csv");
            try (InputStream in = resource.getInputStream()) {
                Files.copy(in, localPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        }
        this.lineOffsets = buildIndex(localPath);
    }

    public Path getLocalPath() { return localPath; }

    public long[] getLineOffsets() { return lineOffsets; }

    public long getDataLineCount() { return lineOffsets.length; }

    public long getStartOffset(long lineNumber) {
        return lineOffsets[(int) lineNumber - 1];
    }

    private static long[] buildIndex(Path csvPath) throws IOException {
        byte[] bytes = Files.readAllBytes(csvPath);
        int pos = skipLine(bytes, 0);
        long[] offsets = new long[16_384];
        int count = 0;
        while (pos < bytes.length) {
            if (count == offsets.length) {
                long[] bigger = new long[offsets.length * 2];
                System.arraycopy(offsets, 0, bigger, 0, count);
                offsets = bigger;
            }
            offsets[count++] = pos;
            pos = skipLine(bytes, pos);
        }
        long[] trimmed = new long[count];
        System.arraycopy(offsets, 0, trimmed, 0, count);
        return trimmed;
    }

    private static int skipLine(byte[] bytes, int start) {
        int i = start;
        while (i < bytes.length && bytes[i] != '\n') i++;
        if (i < bytes.length) i++;
        return i;
    }
}
