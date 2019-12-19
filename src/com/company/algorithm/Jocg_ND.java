package com.company.algorithm;

import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.HashMap;
import java.util.LinkedList;

public class Jocg_ND {

    int INF;
    Graph graph;
    HashMap<Vertex,Integer> dist;
    public int iterate;

    public Jocg_ND(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;
        this.dist = new HashMap<>();
    }

    public void start(){

        for(Graph g:graph.pieces) {
            Hop hop = new Hop(g);
            hop.start();
        }


//        Hop hop = new Hop(graph);
//        hop.mode = 1;
//        hop.start();

        iterate = 0;
        while(newbfs()){
            iterate+=1;
            graph.resetExplore();
            for(Vertex v:graph.freeV(Label.A)){
                assert v.matching == null;
                dfs(v);
            }
        }
    }

    private boolean newbfs(){
        LinkedList<Vertex> queue = new LinkedList<>();
        dist = new HashMap<>();
        dist.put(null,INF);
        for(Vertex v:graph.vertices){
            if(v.label == Label.A && v.isFree()){
                queue.addLast(v);dist.put(v,0);
            }
            else{
                dist.put(v,INF);
            }
        }
        while(!queue.isEmpty()){
            Vertex v = queue.pop();
            if(dist.get(v) <= dist.get(null)){
                if(v.label == Label.A){
                    for(Vertex u:v.edges){
                        if(dist.get(u) == INF){
                            if(graph.getWeight(u,v) == 0){
                                queue.addFirst(u);
                            }
                            else{
                                queue.addLast(u);
                            }
                            dist.put(u,dist.get(v) + graph.getWeight(u,v));
                        }
                    }
                }
                else{
                    if(v.matching != null){
                        if(graph.getWeight(v.matching,v) == 0){
                            queue.addFirst(v.matching);
                        }
                        else{
                            queue.addLast(v.matching);
                        }
                    }
                    dist.put(v.matching,dist.get(v) + graph.getWeight(v,v.matching));
                }
            }
        }
        System.out.println("new bfs returns: " + dist.get(null));
        return dist.get(null) != INF;
    }



    private boolean dfs(Vertex v){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }
        v.explored = true;

        for(Vertex u:v.edges){
            assert dist != null;
            assert u != null;
            if(dist.get(u.matching) - dist.get(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){
                if(u.matching != null && u.matching.explored){
                    continue;
                }
                if(dfs(u.matching)){
                    u.match(v);
                    return true;
                }
            }

        }
        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        dist.put(v,INF);
        return false;
    }


}
