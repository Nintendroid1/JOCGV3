package com.company.algorithm;

import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class Jocg_OPT {

    int INF;
    Graph graph;
    HashMap<Vertex,Integer> dist;
//    HashMap<Vertex,HashSet<Vertex>> visitedE;
//    HashMap<Vertex,HashSet<Vertex>> tempE;
    LinkedList<Vertex> path;

    public int iterate;

    public Jocg_OPT(Graph graph){
        this.INF = Integer.MAX_VALUE;
        this.graph = graph;
        this.dist = new HashMap<>();
    }

    public void start(){

        for(Graph g:graph.pieces) {
            Hop hop = new Hop(g);
            hop.start();
        }

        iterate = 0;
        while(newbfs()){
            iterate+=1;
            graph.resetVisit();
            for(Vertex v:graph.freeV(Label.A)){
                /*
                 * path: store the augmenting path returned by dfs(v)
                 */
                path = new LinkedList<>();
                if(newdfs(v,null)){
                    HashSet<Graph> affectedP = new HashSet<>();
                    for(Vertex u:path){
                        affectedP.add(u.piece);
                    }
                    for(Vertex a: graph.V(Label.A)){
                        for(Vertex b:new HashSet<>(a.tempE)){
                            if(!(affectedP.contains(a.piece) && affectedP.contains(b.piece))){
                                a.tempE.remove(b);
                            }
                        }
                    }
                }
                for(Vertex a:graph.V(Label.A)){
                    a.visitedE.addAll(a.tempE);
                    a.tempE = new HashSet<>();
                }
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

    private boolean newdfs(Vertex v,Vertex parent){
        /*
         * v = u.matching for some u
         * if v == null, we know u is free, so we find an augmenting path
         */

        if(v == null){
            return true;
        }

        if(parent != null){
            v.tempE.add(parent);
        }

        v.explored = true;

        for(Vertex u:v.edges){

            if(u.matching != null && (u.matching.visitedE.contains(u) || u.matching.tempE.contains(u))){
                continue;
            }

            if(dist.get(u.matching) - dist.get(v) == graph.getWeight(u,v) + graph.getWeight(u,u.matching)){

                if(newdfs(u.matching,u)){
                    u.match(v);
                    path.addFirst(u);
                    path.addFirst(v);
                    return true;
                }
            }

        }
        return false;
    }

    private boolean containE(HashMap<Vertex,HashSet<Vertex>> visitedE, Vertex a, Vertex b){
        return (visitedE.containsKey(a) && visitedE.get(a).contains(b));
    }


}
