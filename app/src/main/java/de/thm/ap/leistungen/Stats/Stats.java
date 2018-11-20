package de.thm.ap.leistungen.Stats;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.leistungen.model.Record;

public class Stats {

    private List<Record> records = new ArrayList<>();
    private int recordsCount, halfWeight, sumCrps, crpToEnd, avarage;

    public Stats(List<Record>[] recordsParam) {
        for(List<Record> recordList : recordsParam) this.records.addAll(recordList);
        recordsCount = records.size();
        halfWeight = getSumHalfWeighted();
        sumCrps = getSumCrps();
        crpToEnd = getCrpToEnd();
        avarage = getAverageMark();
    }
    public Stats(List<Record> records) {
        this.records = records;
        recordsCount = records.size();
        halfWeight = getSumHalfWeighted();
        sumCrps = getSumCrps();
        crpToEnd = getCrpToEnd();
        avarage = getAverageMark();
    }

    @NonNull
    public String toString(){
        String out = "Leistungen " + recordsCount + "\n" +
                "50% Leistungen " + halfWeight + "\n" +
                "Summe Crp " + sumCrps + "\n" +
                "Crp bis Ziel " + crpToEnd + "\n" +
                "Durchschnitt " ;
        out += (avarage > 0 ? avarage + "%" : "/");
        if(recordsCount > 0) return out;
        else return "Keine Leistungen vorhanden";
    }

    private int getSumCrps() {
        int sumCrps = 0;
        for(Record record : records) {
            if(record.hasPassed())
            sumCrps += record.getCrp();
        }
        return sumCrps;
    }

    private int getCrpToEnd() {
        int crpEnd = 180;
        return (crpEnd - getSumCrps() < 0? 0 : crpEnd - getSumCrps());
    }

    private int getSumHalfWeighted() {
        int sumHalfWeighted = 0;
        for(Record record : records) {
            if(record.isHalfWeighted())
            sumHalfWeighted++;
        }
        return sumHalfWeighted;
    }

    public int getAverageMark() {
        int points = 0;
        int avarageMark = 0;
        for(Record record : records) {
            if(record.hasPassed() && record.hasMark()){
                if(record.isHalfWeighted()) {
                    avarageMark += (record.getMark() * record.getCrp() * 0.5);
                    points += 0.5 * record.getCrp();
                } else {
                    avarageMark += record.getMark() * record.getCrp();
                    points += record.getCrp();
                }
            }
        }
        if(points != 0) {
            avarageMark = avarageMark /points;
        } else avarageMark = 0;
        return avarageMark;
    }

    public List<Record> getRecords(){
        return records;
    }
}
