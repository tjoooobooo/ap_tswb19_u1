package de.thm.ap.records;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.records.Stats;
import de.thm.ap.records.model.Record;

import static org.junit.Assert.assertEquals;


public class StatsTest {
    private Stats stats;

    @Before
    public void setUp() throws Exception {
        List<Record> records = new ArrayList<>();

        records.add(new Record("CS1013", "Objektorientierte Programmierung", 2016, true, true, 6, 73));
        records.add(new Record("MN1007", "Diskrete Mathematik", 2016, false, true, 6, 81));
        records.add(new Record("CS1019", "Compilerbau", 2017, false, false, 6, 81));
        records.add(new Record("CS1020", "Datenbanksysteme", 2017, false, false, 6, 92));

        stats = new Stats(records);
    }

    @Test
    public void getSumCrp() throws Exception {
        assertEquals(24, stats.getSumCrps());
    }

    @Test
    public void getCrpToEnd() throws Exception {
        assertEquals(156, stats.getCrpToEnd());
    }

    @Test
    public void getSumHalfWeighted() throws Exception {
        assertEquals(2, stats.getSumHalfWeighted());
    }

    @Test
    public void getAverageMark() throws Exception {
        assertEquals(83, stats.getAverageMark());
    }

}