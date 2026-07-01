package fr.sdv.etloff.etl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

public class LineRangePartitioner implements Partitioner {

    private final CsvFileAccess csvFileAccess;
    private final int gridSize;

    public LineRangePartitioner(CsvFileAccess csvFileAccess, int gridSize) {
        this.csvFileAccess = csvFileAccess;
        this.gridSize = gridSize;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        long totalLines = csvFileAccess.getDataLineCount();
        Map<String, ExecutionContext> partitions = new HashMap<>();
        long chunk = totalLines / gridSize;
        long remainder = totalLines % gridSize;
        long start = 1;
        for (int i = 0; i < gridSize; i++) {
            long size = chunk + (i < remainder ? 1 : 0);
            if (size == 0) continue;
            long end = start + size - 1;
            ExecutionContext context = new ExecutionContext();
            context.putLong("startLine", start);
            context.putLong("endLine", end);
            context.putLong("startByte", csvFileAccess.getStartOffset(start));
            partitions.put("partition" + i, context);
            start = end + 1;
        }
        return partitions;
    }
}
