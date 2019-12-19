package com.company.algorithm;

import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.HashMap;
import java.util.LinkedList;

public class Hop {
    private Graph graph;
    private HashMap<Vertex,Integer> dist;
    private Integer INF;
    public Integer iterate;
    public Integer mode;

    public Hop(Graph graph){
        this.graph = graph;
        this.dist = new HashMap<>();
        this.INF = Integer.MAX_VALUE;
        this.mode = 0;
        this.iterate = 0;
    }

    public void start(){
        iterate = 0;
        while(bfs()){
            iterate+=1;
            for(Vertex v:graph.freeV(Label.A)){
                assert v.matching == null;
                dfs(v);
            }
        }
    }

    private boolean bfs(){
        LinkedList<Vertex> queue = new LinkedList<>();
        dist = new HashMap<>();
        dist.put(null,INF);
        for(Vertex v:graph.V(Label.A)){
            if(v.isFree()){
                queue.addLast(v); dist.put(v,0);
            }
            else{
                dist.put(v,INF);
            }
        }
        while (!queue.isEmpty()){
            Vertex v = queue.pop();
            if(dist.get(v) < dist.get(null)){
                for(Vertex u:v.edges){
                    /*
                     * Because we only apply HK on the current Graph
                     * So we check if u is in the Graph
                     * If it is, it's easy to see its matching must be in the current graph.
                     */
                    if(graph.vertices.contains(u)){
                        /*
                         * <dist(u.matching) == INF> means
                         * <u.matching> is free or <u.matching> has not been explored yet (in the current BFS)
                         * In either case, we update the dist of <u.matching>
                         */
                        if(dist.get(u.matching) == INF){
                            dist.put(u.matching, dist.get(v) + 1);
                            queue.addLast(u.matching);
                        }
                    }
                }
            }
        }
        //In <dist.get(null)> != INF, means <null> has been explored, which means a free vertex has been explored
        //System.out.println("Hop bfs returns: " + dist.get(null));
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

        for(Vertex u:v.edges){
            if(graph.vertices.contains(u)){
                if(dist.get(u.matching) == dist.get(v) + 1){
                    if(dfs(u.matching)){
                        u.match(v);
                        return true;
                    }
                }
            }

        }
        //set the distance to the explored vertex to INF
        //so no explored vertex will be explored twice
        dist.put(v,INF);
        return false;
    }
}
