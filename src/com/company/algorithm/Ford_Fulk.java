package com.company.algorithm;

import com.company.element.*;

public class Ford_Fulk extends Algo{
    public Graph graph;
    private Integer INF;
    public boolean print;
    public Integer mode;
    public boolean checkGraph;
    int shortestD;

    public DataRecorder dr;

    public Ford_Fulk(Graph graph){
        this.graph = graph;
        this.INF = Integer.MAX_VALUE;
        this.mode = 0;
        this.print = true;
        this.checkGraph = false;
        this.dr = new DataRecorder();
    }

    public void start(){
        dr.set(dr.numV,graph.vertices.size());// TODO Refactor
        dr.set(dr.number_of_edges,graph.edgeNum);
        long start = System.currentTimeMillis();

        for(Vertex v:graph.vertices){
            dr.add(dr.iterationN,1);

            //Ensure all vertices are no longer considered explored
            graph.reset(true);

            if(v.label == Label.A && v.isFree()){
                dfs(v);
            }
            //assert v.matching == null;
        }

        dr.set(dr.runningTime,System.currentTimeMillis() - start);
        dr.set(dr.matching_count,graph.matchCount());
    }

    private boolean dfs(Vertex v){
        //TODO
//        dr.add(dr.hk_dfsVisitedE,1);

        for(Vertex u:v.edges) {
            if(u.label == Label.B && !u.explored) {
                u.explored = true;

                if(u.isFree() || dfs(u.matching)) {
                    u.match(v);
                    return true;
                }
            }
        }

        return false;
    }
}
