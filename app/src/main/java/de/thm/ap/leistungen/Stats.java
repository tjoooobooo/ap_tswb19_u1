package de.thm.ap.leistungen;

import android.support.annotation.NonNull;

import java.util.List;

import de.thm.ap.leistungen.model.Record;

public class Stats {

    List<Record> records;
    private int sumCrps = 0;
    private int sumHalfWeighted = 0;
    private int avarageMark = 0;

    Stats(List<Record> records){
        this.records = records;
    }

    int getSumCrps() {
        sumCrps = 0;
        for(Record record : records) {
            if(record.passed())
            sumCrps += record.getCrp();
        }
        return sumCrps;
    }

    int getCrpToEnd() {
        int crpEnd = 180;
        return (crpEnd - getSumCrps() < 0? 0 : crpEnd - getSumCrps());
    }

    int getSumHalfWeighted() {
        sumHalfWeighted = 0;
        for(Record record : records) {
            if(record.isHalfWeighted())
            sumHalfWeighted++;
        }
        return sumHalfWeighted;
    }

    int getAverageMark() {
        int points = 0;
        avarageMark = 0;
        for(Record record : records) {
            if(record.passed() && record.hasMark()){
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
            avarageMark = avarageMark/points;
        } else avarageMark = 0;
        return avarageMark;
    }

    @NonNull
    public String toString(){
        String out = "Leistungen " + records.size() + "\n" +
                "50% Leistungen " + getSumHalfWeighted() + "\n" +
                "Summe Crp " + getSumCrps() + "\n" +
                "Crp bis Ziel " + getCrpToEnd() + "\n" +
                "Durchschnitt " ;
        out += (getAverageMark() > 0 ? getAverageMark() + "%" : "/");
        if(records.size() > 0) return out;
        else return "Keine Leistungen vorhanden";
    }
}
