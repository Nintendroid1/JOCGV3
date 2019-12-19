package com.company;

import com.company.element.Label;
import com.company.element.Vertex;

import java.util.HashMap;
import java.util.LinkedList;

public class Abandon {
//    private boolean bfs(){
//        LinkedList<Vertex> queue = new LinkedList<>();
//        dist = new HashMap<>();
//        dist.put(null,INF);
//        for(Vertex v:graph.V(Label.A)){
//            if(v.isFree()){
//                queue.addLast(v); dist.put(v,0);
//            }
//            else{
//                dist.put(v,INF);
//            }
//        }
//        while (!queue.isEmpty()){
//            Vertex v = queue.pop();
//            if(dist.get(v) < dist.get(null)){
//                for(Vertex u:v.edges){
//                    assert u!= null;
//                    /*
//                     * Because we only apply HK on the current Graph
//                     * So we check if u is in the Graph
//                     * If it is, it's easy to see its matching must be in the current graph.
//                     */
//                    if(graph.vertices.contains(u)){
//                        /*
//                         * <dist(u.matching) == INF> means
//                         * <u.matching> is free or <u.matching> has not been explored yet (in the current BFS)
//                         * In either case, we update the dist of <u.matching>
//                         */
//                        if(dist.get(u.matching) == INF){
//                            if(u.matching == null){
//                                dist.put(null, dist.get(v));
//                            }
//                            else{
//                                if(graph.getWeight(u,u.matching) == 0){
//                                    queue.addFirst(u.matching);
//                                }
//                                else{
//                                    queue.addLast(u.matching);
//                                }
//                                dist.put(u.matching, dist.get(v) + graph.getWeight(u,u.matching));
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        //In <dist.get(null)> != INF, means <null> has been explored, which means a free vertex has been explored
//        System.out.println("bfs returns "+dist.get(null));
//        return dist.get(null) != INF;
//    }
}
