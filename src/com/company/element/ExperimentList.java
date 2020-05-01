package com.company.element;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ExperimentList {
    public static class Experiment{
        ArrayList<DataRecorder> dataList;
        ArrayList<Double> bottleList;
        public double totalRunningTime;
        public Experiment(){
            dataList = new ArrayList<>();
            bottleList = new ArrayList<>();
            totalRunningTime = 0;
        }
        public void add(double guessValue, DataRecorder dr){
            bottleList.add(guessValue);
            dataList.add(dr);
        }

        public void add(Experiment ex){
            bottleList.addAll(ex.bottleList);
            dataList.addAll(ex.dataList);
        }

        public double getFinalBottleNeck(){
            return bottleList.get(bottleList.size()-1);
        }

        public void writeResult(BufferedWriter writer) throws IOException {
            //writer.append("Total_Running_Time : " + totalRunningTime + "\n");
            for(int i = 0; i < dataList.size(); i++){
                writer.append("Bottleneck_Guess :"+ bottleList.get(i) + "\n");
                writer.append(dataList.get(i).printResult()+"\n");
                writer.append("***\n");
            }

        }

        public void printResult(String algo) throws IOException {
            BufferedWriter writer
                    = new BufferedWriter(new FileWriter(
                            algo + ".txt"));
            writer.write("");

            for(int i = 0; i < dataList.size(); i++){
                writer.append("Bottleneck Guess :"+ bottleList.get(i) + "\n");
                writer.append(dataList.get(i).printResult()+"\n");
            }
            writer.close();
        }
    }

    ArrayList<Experiment> experiments;

    public ExperimentList(){
        experiments = new ArrayList<>();
    }

    public void newExpe(){
        experiments.add(new Experiment());
    }

    public void add(Experiment ex){
        experiments.add(ex);
    }

    public void add(double guessValue, DataRecorder dr){
        experiments.get(experiments.size()-1).add(guessValue,dr);
    }



}
