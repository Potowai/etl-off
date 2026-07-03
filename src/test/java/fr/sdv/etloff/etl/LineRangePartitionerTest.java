package fr.sdv.etloff.etl;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.batch.item.ExecutionContext;

class LineRangePartitionerTest {

    @Test
    void partitionneEnQuatre() {
        long[] offsets = {10, 20, 30, 40, 50, 60, 70, 80};
        CsvFileAccess acces = new CsvFileAccess(offsets);
        LineRangePartitioner p = new LineRangePartitioner(acces, 4);
        Map<String, ExecutionContext> partitions = p.partition(4);
        assertEquals(4, partitions.size());
    }

    @Test
    void partitionAvecStartByte() {
        long[] offsets = {10, 20, 30, 40};
        CsvFileAccess acces = new CsvFileAccess(offsets);
        LineRangePartitioner p = new LineRangePartitioner(acces, 2);
        Map<String, ExecutionContext> partitions = p.partition(2);
        assertEquals(10L, partitions.get("partition0").getLong("startByte"));
    }
}
