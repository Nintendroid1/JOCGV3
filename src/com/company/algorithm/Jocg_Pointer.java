package com.company.algorithm;

import com.company.element.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Jocg_Pointer extends Algo{

    private int INF;
    private int currentBFS;

    public long preprocess;
    private long start;
    private long end;
    int shortestD;
    public Graph graph;

    public DataRecorder dr;

    ArrayList<Vertex> exloredV;

    ArrayList<Vertex> path;



    DataStructure.CycleArray<DataStructure.DisV> zeros;
    DataStructure.CycleArray<DataStructure.DisV> ones;
    DataStructure.CycleArray<DataStructure.DisV> twos;
    DataStructure.CycleArray<DataStructure.DisV> temp;

    public Jocg_Pointer(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;

        zeros = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);
        ones = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);
        twos = new DataStructure.CycleArray<>(graph.vertices.size()/2+4);

        this.dr = new DataRecorder();
    }

    public void start(){
        dr.set(dr.numV,graph.vertices.size());
        dr.set(dr.pre_iterationN,0);
        start = System.currentTimeMillis();
        for(Graph g:graph.pieces) {
            Hop_NoHash hop = new Hop_NoHash(g);
            hop.print = false;
            hop.checkGraph = true;
            hop.start();
//            this.pre_dve2+=hop.dve2;
//            this.pre_bve2+=hop.bve2;
            dr.add(dr.pre_bfsVisitedE,hop.dr.get(dr.hk_bfsVisitedE));
            dr.add(dr.pre_dfsVisitedE,hop.dr.get(dr.pre_dfsVisitedE));
            if(hop.dr.get((dr.pre_iterationN)) > dr.get(dr.pre_iterationN)){
                dr.set(dr.pre_iterationN,hop.dr.get(dr.pre_iterationN));
            }
        }
//        if(!graph.pieces.isEmpty()){
//            dr.set(dr.pre_iterationN,dr.get(dr.pre_iterationN)/graph.pieces.size());
//        }
        end = System.currentTimeMillis();
        preprocess = end - start;
        dr.set(dr.pre_runningTime,preprocess);

        start = System.currentTimeMillis();
        exloredV = new ArrayList<>();
        while(newbfs()){
            dr.add(dr.iterationN,1);
            /*
             * visitedE: store all visited edges in the current phase
             * any edges in vistedE won't be explored in the current phase
             */

            for(Vertex v:graph.vertices){
                /*
                 * tempE: store visited edges in dfs(v) -> an adjacent list
                 * path: store the augmenting path returned by dfs(v)
                 */
                if(v.label == Label.B || !v.isFree()){
                    continue;
                }
                path = new ArrayList<>();
                //tempE, path will be updated in newdfs
                int count = 0;
                if(newdfs(v)){
                    count += 1;
                    /*
                     * iterate the path
                     * if an path is in an affected piece, add this piece to affectedP
                     */
                    Vertex parent = null;
                    for(Vertex uu:path){
                        for(Vertex u:new Vertex[]{uu, uu.matching}){
                            if(parent!=null && parent.piece == u.piece){
                                //u.piece.affectedP = true;
                                u.piece.affectedP.bfs = currentBFS;
                                u.piece.affectedP.dfs = count;
                            }
                            parent = u;
                        }
                    }
                }

                if(!exloredV.isEmpty()){
                    //del+=exloredV.size();
                    for(Vertex ev:exloredV){
                        ev.deleteE_clean(currentBFS,count);
                        ev.explored = false;
                    }
                    exloredV = new ArrayList<>();
                }
            }
        }
        dr.set(dr.runningTime,System.currentTimeMillis() - start);
        dr.set(dr.matching_count,graph.matchCount());
    }

    private boolean newbfs(){
        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.B){
                continue;
            }
            if(v.isFree()){
                zeros.addLast(new DataStructure.DisV(v,0));
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!(zeros.isEmpty()&&ones.isEmpty()&&twos.isEmpty())){
            if(zeros.isEmpty()){
                while (zeros.isEmpty()){
                    temp = zeros;
                    zeros = ones;
                    ones = twos;
                    twos = temp;
                }
            }

            Vertex v;
            DataStructure.DisV dv;
            dv = zeros.pop();

            v = dv.v;



            if(dv.d > shortestD){
                continue;
            }
            if(v == null){
                shortestD = dv.d;
                continue;
            }
            if(v.distance > shortestD){
                continue;
            }
            if(dv.d > v.distance){
                continue;
            }
            v.reset();
            dr.add(dr.jocg_bfsVisitedE,1);
            for(Vertex u:v.edges){
                //this.bve2+=1;
                if(u == v.matching){
                    continue;
                }
                u.reset();
                int distance;
                if(u.matching != null){
                    distance =  graph.getWeight(v,u) + graph.getWeight(u,u.matching);
                }
                else{
                    distance = graph.getWeight(v,u);
                }

                if(v.distance + distance > shortestD){
                    continue;
                }
                if(u.matching == null || u.matching.distance > v.distance + distance){
                    if(u.matching != null){
                        u.matching.distance = v.distance + distance;
                    }
                    if(distance == 0){
                        zeros.addLast(new DataStructure.DisV(u.matching,v.distance));
                    }
                    else if(distance == 1){
                        ones.addLast(new DataStructure.DisV(u.matching,v.distance+distance));
                    }
                    else{
                        twos.addLast(new DataStructure.DisV(u.matching,v.distance+distance));
                    }
                }
            }


        }
        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }

    private boolean newdfs(Vertex startV){
        ArrayList<Vertex> tempPath = new ArrayList<>();
        tempPath.add(startV);
        startV.explored = true;
        exloredV.add(startV);
        while(!tempPath.isEmpty()){
            Vertex v = tempPath.get(tempPath.size()-1);
            if(v == null){
                //augment path
                Vertex head = null;
                for(Vertex tail:tempPath){
                    if(head != null && head.label == Label.A){
                        head.match(tail);
                        path.add(head);
                        path.add(tail);
                    }
                    head = tail;
                }
                return true;
            }

            Vertex u = v.next();
            if(u == null){
                tempPath.remove(tempPath.size()-1);
                if(!tempPath.isEmpty()){
                    tempPath.remove(tempPath.size()-1);
                }
                continue;
            }
            if(u.matching != null && u.matching.explored){
                continue;
            }

            dr.add(dr.jocg_dfsVisitedE,1);

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }
                Vertex next = u.next();
                if(next != null){
                    next.explored = true;
                    exloredV.add(next);
                }
                tempPath.add(u);
                tempPath.add(next);
            }
        }
        return false;
    }


    private boolean newdfs_recur(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }
        if(v.explored){
            return false;
        }
        else{
            v.explored = true;
            exloredV.add(v);
        }
        while(true){
            Vertex u = v.next();
            if(u == null){
                break;
            }

            if(u.matching != null && u.matching.explored){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }
                if(newdfs(u.next())){
                    u.match(v);
                    path.add(u);
                    path.add(v);
                    //v.reserve(0);
                    return true;
                }
            }
        }

        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        //dist.put(v,INF);
        return false;
    }

    private boolean newbfs_temp(){
        //int[][] marker = new int[graph.vertices.size()][graph.vertices.size()];
        DataStructure.CycleArray<DataStructure.DisV> cycleArray = new DataStructure.CycleArray<>(graph.vertices.size());
        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                cycleArray.addLast(new DataStructure.DisV(v,0));
                //marker[v.id] = 1;
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!cycleArray.isEmpty()){

            Vertex v;
            DataStructure.DisV dv;
            dv = cycleArray.pop();
            v = dv.v;
            if(dv.d > v.distance){
                continue;
            }
            //marker[v.id] = 0;
            if(v.distance > shortestD){
                continue;
            }
            v.reset();

            if(v.label == Label.A){
                //children = v.edges;

                for(Vertex u:v.edges){
                    if(u == v.matching){
                        continue;
                    }
                    //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                    if(u.distance > v.distance + graph.getWeight(u,v)){
                        u.distance = v.distance + graph.getWeight(u,v);
                        if(graph.getWeight(u,v) == 0){
                            if(u.matching == null){
                                shortestD = v.distance;
                            }
                            else if(graph.getWeight(u,u.matching) == 0
                                    && u.matching.distance > v.distance){
                                u.matching.distance = v.distance;
                                u.reset();
                                cycleArray.addFirst(new DataStructure.DisV(u.matching,u.matching.distance));
                            }
                            else {
                                cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                            }
                        }
                        else{
                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                        }

                    }

//                        if(graph.getWeight(u,v) == 0){
//                            cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
//                            //marker[u.id] = 1;
//                        }
//                        else{
//                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
//                            //marker[u.id] = 1;
//                        }
                    }
            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    continue;
                }

                Vertex u = v.matching;
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);

                    if(graph.getWeight(u,v) == 0){
                        cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                    }
                    else{
                        cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                    }
                }
            }
        }

        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }


    private boolean newbfs_backup(){
        //int[][] marker = new int[graph.vertices.size()][graph.vertices.size()];
        DataStructure.CycleArray<DataStructure.DisV> cycleArray = new DataStructure.CycleArray<>(graph.vertices.size());
        shortestD = INF;
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                cycleArray.addLast(new DataStructure.DisV(v,0));
                //marker[v.id] = 1;
                v.distance = 0;
            }
            else{
                v.distance = INF;
            }
        }

        while(!cycleArray.isEmpty()){

            Vertex v;
            DataStructure.DisV dv;
            dv = cycleArray.pop();
            v = dv.v;
            if(dv.d > v.distance){
                continue;
            }
            //marker[v.id] = 0;
            if(v.distance > shortestD){
                continue;
            }

            v.reset();

            if(v.label == Label.A){
                //children = v.edges;

                for(Vertex u:v.edges){
                    if(v.label == Label.A && u == v.matching){
                        continue;
                    }
                    //dist.get(u) > dist.get(v) + graph.getWeight(u,v)
                    if(u.distance > v.distance + graph.getWeight(u,v)){
                        u.distance = v.distance + graph.getWeight(u,v);

                        if(graph.getWeight(u,v) == 0){
                            cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                            //marker[u.id] = 1;
                        }
                        else{
                            cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                            //marker[u.id] = 1;
                        }
                    }
                }

            }
            else{
                //when we find free B
                if(v.matching == null){
                    shortestD = v.distance;
                    continue;
                }

                Vertex u = v.matching;
                if(u.distance > v.distance + graph.getWeight(u,v)){
                    u.distance = v.distance + graph.getWeight(u,v);

                    if(graph.getWeight(u,v) == 0){
                        cycleArray.addFirst(new DataStructure.DisV(u,v.distance));
                    }
                    else{
                        cycleArray.addLast(new DataStructure.DisV(u,v.distance+1));
                    }
                }
            }
        }

        System.out.println("Jocg bfs returns: " + shortestD);
        currentBFS = shortestD;
        //currentBFS = dist.get(null);
        return shortestD != INF;
    }

    private boolean newdfs_back(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }

        //add v to tempE
        //v (- A
        //parent (- B

        if(v.explored){
            return false;
        }
        else{
            v.explored = true;
            exloredV.add(v);
        }
        while(true){
            Vertex u = v.next();

            if(u == null){
                break;
            }
            if(u.matching != null && u.matching.explored){
                continue;
            }

            if(dist(u.matching) - dist(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null){
                    if(!u.hasNext()){
                        continue;
                    }
                }

                if(newdfs(u.next())){
                    u.match(v);
                    path.add(u);
                    path.add(v);
                    return true;
                }
            }
        }

        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        //dist.put(v,INF);
        return false;
    }

    private int dist(Vertex v){
        if(v == null){
            return shortestD;
        }
        else{
            return v.distance;
        }
    }
}
