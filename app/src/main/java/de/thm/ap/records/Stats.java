package de.thm.ap.records;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import de.thm.ap.records.model.Record;

public class Stats {

    List<Record> records = new ArrayList<>();
    private int sumCrps = 0;
    private int sumHalfWeighted = 0;
    private int avarageMark = 0;

    Stats(List<Record> records){
        this.records = records;
    }

    int getSumCrps() {
        for(Record record : records) sumCrps += record.getCrp();
        return sumCrps;
    }

    int getCrpToEnd() {
        int crpEnd = 180;
        return (crpEnd - getSumCrps() < 0? 0 : crpEnd - getSumCrps());
    }

    int getSumHalfWeighted() {
        for(Record record : records) {
            if(record.isHalfWeighted())
            sumHalfWeighted++;
        }
        return sumHalfWeighted;
    }

    int getAverageMark() {
        int points = 0;
        for(Record record : records) {
            if(record.getMark() >= 50){
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
                "Durchschnitt " + getAverageMark() + "%";
        if(avarageMark != 0) {
            return out;
        } else return "Keine Leistungen vorhanden";
    }
}
