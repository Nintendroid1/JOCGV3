package com.company.generator;

import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.io.*;
import java.util.*;


public class EuclideanGen {

    class Point{
        double x; double y;
        Point(double x,double y){
            this.x = x;this.y = y;
        }
    }
    /*
     * generate 2 n vertexes in 2d euclidean space
     */
    int seed;
    public EuclideanGen(int seed){
        this.seed = seed;
    }

    public Graph generate(int num, double large,double middle, double small, double bottleneck){
        /*
         * random seed
         */
        Random r = new Random(seed);

        /*
         * define grid
         * side为middle的整数倍
         * middle为small的整数倍
         * bottleneck为2*small的整数倍
         *
         * step表示一个middle行中有几个small square
         * hstep表示一个完整行中有几个samll square
         */
        assert (int)(large%middle) == 0;
        assert (int)(middle%small) == 0;
        assert bottleneck < middle;
        assert (int)(bottleneck%(2*small)) == 0;

        int step = (int)(middle/small);
        int hstep = (int)(large/small);

        /*
         * generate 2n points in a side*side square
         * generate vertex for each point, and store them in a hashmap
         */
        ArrayList<Point> points = new ArrayList<>();
        HashMap<Point,Vertex> vertices = new HashMap<>();
        for(int i = 0; i < num; i++){
            Point point = new Point(r.nextDouble()*large,r.nextDouble()*large);
            points.add(point);
            if(i < num/2){
                vertices.put(point, new Vertex(i,Label.A));
            }
            else{
                vertices.put(point, new Vertex(i,Label.B));
            }

        }

        //generate edges < bottleneck
        {
            for(Point source:points){
                HashSet<Vertex> edges = new HashSet<>();
                for(Point target:points){
                    //if labels are same, or two vertexes are same, not calculate distance
                    if(source == target || vertices.get(source).label == vertices.get(target).label){
                        continue;
                    }
                    double x = source.x - target.x;
                    double y = source.y - target.y;
                    double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                    if(d <= bottleneck){
                        edges.add(vertices.get(target));
                    }
                }
                vertices.get(source).edges = edges;
            }

            for(Vertex v:vertices.values()){
                for(Vertex u:v.edges){
                    assert v.hasEdgewith(u);
                }
            }
        }

        HashSet<Vertex>[][] cells = new HashSet[hstep][hstep];
        /*
         * each cell is a small*small square
         * insert each point to its cel
         */
        {
            for (Point point : points) {
                //find the cell
                int x = search(point.x, small);
                int y = search(point.y, small);
                assert point.x >= x * small && point.x <= (x + 1) * small;
                assert point.y >= y * small && point.y <= (y + 1) * small;
                if (cells[x][y] == null) {
                    cells[x][y] = new HashSet<Vertex>();
                }
                cells[x][y].add(vertices.get(point));
            }
        }

        /*
         * calculate #vertex in each row and column
         * store the result in "horizontal" and "vertical"
         */
        HashMap<Integer,Integer> horizontal = new HashMap<>();
        HashMap<Integer,Integer> vertical = new HashMap<>();
        {
            for(int i = 0; i < large/small; i++){
                for(int j = 0; j < large/small; j++){
                    int size;
                    if(cells[i][j] == null){
                        size = 0;
                    }
                    else{
                        size = cells[i][j].size();
                    }

                    if(!horizontal.containsKey(i)){
                        horizontal.put(i,size);
                    }
                    else{
                        horizontal.put(i,horizontal.get(i) + size);
                    }

                    if(!vertical.containsKey(j)){
                        vertical.put(j,size);
                    }
                    else{
                        vertical.put(j,vertical.get(j) + size);
                    }
                }
            }
        }



        //find the combination with the least number of boundary points
        Integer bestBCx = Integer.MAX_VALUE;
        Integer idxx = null;
        Integer bestBCy = Integer.MAX_VALUE;
        Integer idxy = null;


        {
            /*
             *x axis
             */
            for(int i = 0; i < middle/small; i++){
                int currBC = 0;

                /*
                 * determine if leftmost or rightmost still in the boundary
                 */

                if(i - (bottleneck/small/2 - 1) - 1 < 0){
                    continue;
                }
                if(i + (large/middle - 1) * (middle/small) + (bottleneck/small/2 - 1) >= large/small){
                    continue;
                }

                for(int j = 0; j < large/middle; j++){
                    for(int k = 0; k < bottleneck/small/2; k++){
                        currBC+=horizontal.get((int)(i+j*(middle/small)) - k - 1);
                        currBC+=horizontal.get((int)(i+j*(middle/small)) + k);

                    }

                }
                if(currBC < bestBCx){
                    bestBCx = currBC;
                    idxx = i;
                }
            }


            /*
             *y axis
             */
            for(int i = 0; i < middle/small; i++){
                int currBC = 0;

                if(i - (bottleneck/small/2 - 1) - 1 < 0){
                    continue;
                }
                if(i + (large/middle - 1) * (middle/small) + (bottleneck/small/2 - 1) >= large/small){
                    continue;
                }
                for(int j = 0; j < large/middle; j++){
                    for(int k = 0; k < bottleneck/small/2; k++){
                        currBC+=vertical.get((int)(i+j*(middle/small)) - k - 1);
                        currBC+=vertical.get((int)(i+j*(middle/small)) + k);

                    }
                }
                if(currBC < bestBCy){
                    bestBCy = currBC;
                    idxy = i;
                }
            }
        }

        assert idxx != null;
        assert idxy != null;

        //assign weights with bestx,besty


        Graph[][] pieces = new Graph[(int)Math.pow(large/middle + 1,2)][(int)Math.pow(large/middle + 1,2)];
        HashMap<Vertex,Graph> piecesTable = new HashMap<>();
        HashSet<Graph> piecesset = new HashSet<>();
        {
            //iterate all cell, and assign pieces for all vertex in cells
            for(int i = 0; i < large/small; i++){
                for(int j = 0; j < large/small; j++){
                    if(cells[i][j] == null){
                        continue;
                    }

                    /*
                     * 坐标计算方式
                     * hint: 平移
                     */
                    int x = (int)Math.floor(Math.floor(i-idxx)/step) + 1;
                    int y = (int)Math.floor(Math.floor(j-idxy)/step) + 1;


                    if(pieces[x][y] == null){
                        pieces[x][y] = new Graph();
                        piecesset.add(pieces[x][y]);
                    }
                    pieces[x][y].vertices.addAll(cells[i][j]);

                    for(Vertex v:cells[i][j]){
                        piecesTable.put(v,pieces[x][y]);
                        v.piece = pieces[x][y];
                    }
                }
            }
        }


        //test Graph if valid
        {
            int count = 0;
            for(Graph graph:piecesset){
                count += graph.vertices.size();
            }
            assert count == vertices.values().size();
        }


        Graph graph = new Graph();
        graph.vertices = new HashSet<>(vertices.values());
        graph.pieces = piecesset;


        return graph;

    }

    public Graph generate(String fileName, double bottleneck, int numEachLabel){
        HashMap<Integer,Graph> pieces = new HashMap<>();
        HashMap<Point,Vertex> points = new HashMap<>();
        try {
            Scanner sc = new Scanner(new File(fileName));
            int count = 0;
            while (sc.hasNext()) {
                String str = sc.nextLine(); //System.out.println(str);
                String[] line = str.split(" ");
                Point point = new Point(Double.parseDouble(line[0]),Double.parseDouble(line[1]));
                int P = Integer.parseInt(line[2]);
                if(count < numEachLabel){
                    points.put(point,new Vertex(count,Label.A));
                }
                else{
                    points.put(point,new Vertex(count,Label.B));
                }

                if(!pieces.containsKey(P)){
                    pieces.put(P,new Graph());
                }
                points.get(point).piece = pieces.get(P);
                pieces.get(P).vertices.add(points.get(point));
                count+=1;
            }
            sc.close();

            //生成长度为bottleneck以下的edge
            {
                for(Point source:points.keySet()){
                    HashSet<Vertex> edges = new HashSet<>();
                    for(Point target:points.keySet()){
                        //如果label相同，或者是source自身，则不计算距离
                        if(source == target || points.get(source).label == points.get(target).label){
                            continue;
                        }
                        double x = source.x - target.x;
                        double y = source.y - target.y;
                        double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                        if(d <= bottleneck){
                            edges.add(points.get(target));
                        }
                    }
                    points.get(source).edges = edges;
                }

                for(Vertex v:points.values()){
                    for(Vertex u:v.edges){
                        assert v.hasEdgewith(u);
                    }
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            // TODO Auto-generated catch block e.printStackTrace();
        }

        HashSet<Vertex> vertices = new HashSet<>(points.values());
        HashSet<Graph> ps = new HashSet<>(pieces.values());
        Graph graph = new Graph();
        graph.vertices = vertices;
        graph.pieces = ps;

        return graph;
    }




    private int search(double coord,double small) {
        return (int)(coord/small);
    }



}
