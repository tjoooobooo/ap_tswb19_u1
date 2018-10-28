package de.thm.ap.records;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.records.model.Record;

public class Stats {

    List<Record> records = new ArrayList<>();
    int sumCrps = 0;
    int sumHalfWeighted = 0;
    int avarageMark = 0;
    final int crpEnd = 180;

    public Stats(List<Record> records){
        this.records = records;
    }

    public int getSumCrps() {
        for(Record record : records) sumCrps += record.getCrp();
        return sumCrps;
    }

    public int getCrpToEnd() {
        return (crpEnd - getSumCrps() < 0? 0 : crpEnd - getSumCrps());
    }

    public int getSumHalfWeighted() {
        for(Record record : records) {
            if(record.isHalfWeighted())
            sumHalfWeighted++;
        }
        return sumHalfWeighted;
    }

    public int getAverageMark() {
        int weight = 0;
        for(Record record : records) {
            if(record.isHalfWeighted()) {
                avarageMark += (record.getMark() * record.getCrp() * 0.5);
                weight += 0.5 * record.getCrp();
            }
            else {
                avarageMark += record.getMark() * record.getCrp();
                weight += 1 * record.getCrp();
            }
        }
        avarageMark = avarageMark/weight;
        return avarageMark;
    }
}
