package com.company.generator;

import com.company.element.Graph;
import com.company.element.Label;
import com.company.element.Vertex;

import java.util.*;

public class GraphGen {
    class Point{
        double x; double y;
        Point(double x,double y){
            this.x = x;this.y = y;
        }
    }

    int seed;
    Random r;
    public GraphGen(int seed){
        this.seed = seed;
        this.r = new Random(seed);
    }


    public Graph generate(int num, double large, double middle, double small, double bottleneck){

        /*
         * Assumption:
         * Large is divisible by middle
         * Middle is divisible by small
         * Bottleneck is less than Middle
         */
        assert (int)(large%middle) == 0;
        assert (int)(middle%small) == 0;
        assert bottleneck < middle;
        assert (int)(bottleneck%(2*small)) == 0;

        int small_grid_num_one_middle = (int)(middle/small);
        int small_grid_num_one_row = (int)(large/small);


        /*
         * generate points and vertices
         */
        ArrayList<Point> points = new ArrayList<>(num);
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(num);
        for(int i = 0; i < num; i++){
            Point point = new Point(r.nextDouble()*large,r.nextDouble()*large);
            points.add(point);
            if(i < num/2){
                vertices.add(new Vertex(i, Label.A));
            }
            else{
                vertices.add(new Vertex(i, Label.B));
            }
        }

        //generate edges < bottleneck
        {
            for(int i = 0; i < num; i++){
                ArrayList<Vertex> edges = new ArrayList<>();
                for(int j = 0; j < num; j++){
                    Point source = points.get(i);
                    Point target = points.get(j);
                    //if labels are same, or two vertexes are same, not calculate distance
                    if(source == target || vertices.get(i).label == vertices.get(j).label){
                        continue;
                    }
                    double x = source.x - target.x;
                    double y = source.y - target.y;
                    double d = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                    if(d <= bottleneck){
                        edges.add(vertices.get(j));
                    }
                }
                vertices.get(i).edges = edges;
            }
        }

        /*
         * according to small grids, generate cells
         */
        ArrayList<Vertex>[][] cells = new ArrayList[small_grid_num_one_row][small_grid_num_one_row];

        {
            for(int i = 0; i < num; i++){
                Point point = points.get(i);
                int x = search(point.x, small);
                int y = search(point.y, small);

                assert point.x >= x * small && point.x <= (x + 1) * small;
                assert point.y >= y * small && point.y <= (y + 1) * small;

                if(cells[x][y] == null){
                    cells[x][y] = new ArrayList<Vertex>();
                }
                cells[x][y].add(vertices.get(i));
            }
        }

        int[] horizontal = new int[small_grid_num_one_row];
        int[] vertical = new int[small_grid_num_one_row];

        {
            for(int i = 0; i < small_grid_num_one_row; i++){
                for(int j = 0; j < small_grid_num_one_row; j++){
                    int size;
                    if(cells[i][j] == null){
                        size = 0;
                    }
                    else{
                        size = cells[i][j].size();
                    }
                    horizontal[i]+=size;
                    vertical[j]+=size;
                }
            }
        }

        //find the combination with the least number of boundary points
        Integer bestBCx = Integer.MAX_VALUE;
        Integer idxx = null;
        Integer bestBCy = Integer.MAX_VALUE;
        Integer idxy = null;

        {
            int lowbound = (int)(bottleneck/small/2);
            int highbound = (int)(middle/small - 1) - (int)(bottleneck/small/2 + 1);

            //for(int i = 0; i < small_grid_num_one_middle; i++){
            for(int i = lowbound; i <= highbound; i++){
                int currBC = 0;

//                if(i - (bottleneck/small/2 - 1) - 1 < 0){
//                    continue;
//                }
//                if(i + (large/middle - 1) * (middle/small) + (bottleneck/small/2 - 1) >= large/small){
//                    continue;
//                }

                for(int j = 0; j < large/middle; j++){
                    for(int k = 0; k < bottleneck/small/2; k++){
                        int position1 = (int)(i+j*(middle/small) - k);
                        int position2 = (int)(i+j*(middle/small)) + k + 1;
                        currBC+=horizontal[position1];
                        currBC+=horizontal[position2];
                    }
                }
                if(currBC < bestBCx){
                    bestBCx = currBC;
                    idxx = i;
                }
            }
        }

        {
            for(int i = 0; i < small_grid_num_one_middle; i++){
                int currBC = 0;

                if(i - (bottleneck/small/2 - 1) - 1 < 0){
                    continue;
                }
                if(i + (large/middle - 1) * (middle/small) + (bottleneck/small/2 - 1) >= large/small){
                    continue;
                }

                for(int j = 0; j < large/middle; j++){
                    for(int k = 0; k < bottleneck/small/2; k++){
                        int position1 = (int)(i+j*(middle/small) - k - 1);
                        int position2 = (int)(i+j*(middle/small)) + k;
                        currBC+=vertical[position1];
                        currBC+=vertical[position2];
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

        Graph[][] pieces = new Graph[(int)Math.pow(large/middle + 1,2)][(int)Math.pow(large/middle + 1,2)];
        ArrayList<Graph> piecesset = new ArrayList<>();

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
                int x = (int)Math.floor(Math.floor(i-idxx)/small_grid_num_one_middle) + 1;
                int y = (int)Math.floor(Math.floor(j-idxy)/small_grid_num_one_middle) + 1;


                if(pieces[x][y] == null){
                    pieces[x][y] = new Graph();
                    piecesset.add(pieces[x][y]);
                }
                pieces[x][y].vertices.addAll(cells[i][j]);

                for(Vertex v:cells[i][j]){
                    v.piece = pieces[x][y];
                }
            }
        }

        //test Graph if valid
        {
            int count = 0;
            for(Graph graph:piecesset){
                count += graph.vertices.size();
            }
            assert count == vertices.size();
        }

        Graph graph = new Graph();

        graph.vertices = vertices;
        graph.pieces = piecesset;
        assert vertices.size()%2 == 0;
        graph.numV = vertices.size()/2;
        for(Vertex v:graph.vertices){
            graph.edgeNum += v.edges.size();
        }
        assert graph.edgeNum%2 == 0;
        graph.edgeNum = graph.edgeNum/2;

        for(Vertex v: graph.vertices){
            ArrayList<Vertex> ones = new ArrayList<>();
            ArrayList<Vertex> zeros = new ArrayList<>();
            for(Vertex u:v.edges){
                if(u.piece == v.piece){
                    zeros.add(u);
                }
                else{
                    ones.add(u);
                }
            }
            ones.addAll(zeros);
            v.edges = ones;
        }

        return graph;

    }

    private int search(double coord,double small) {
        return (int)(coord/small);
    }
}
