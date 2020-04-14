package com.company.generator;

import com.company.element.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class GraphGen {
    int seed;
    Random r;
    public GraphGen(int seed){
        this.seed = seed;
    }

    public Graph generate(int num, double large){
        /*
         * generate points and vertices
         */
        this.r = new Random(seed+System.currentTimeMillis());
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(num);
        ArrayList<Point> points = new ArrayList<>();
        for(int i = 0; i < num; i++){
            Point point = new Point(r.nextDouble()*large,r.nextDouble()*large);
            points.add(point);
            if(i < num/2){
                vertices.add(new Vertex(i, Label.A));
            }
            else{
                vertices.add(new Vertex(i, Label.B));
            }
            vertices.get(i).point = point;

            point.v = vertices.get(i);
        }

        Graph graph = new Graph();

        graph.vertices = vertices;
        graph.points = points;

        return graph;
    }

    public static Graph generate(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        String line = scanner.nextLine();
        String[] lines = line.split(" ");
        int num = Integer.parseInt(lines[0]);
        ArrayList<Vertex> vertices = new ArrayList<Vertex>();
        ArrayList<Point> points = new ArrayList<>();
        for(int i = 0; i < num;i++){
            line = scanner.nextLine();
            lines = line.split(" ");
            Point point = new Point(Double.parseDouble(lines[0]),Double.parseDouble(lines[1]));
            points.add(point);
            if(i < num/2){
                vertices.add(new Vertex(i, Label.A));
            }
            else{
                vertices.add(new Vertex(i, Label.B));
            }
            vertices.get(i).point = point;

            point.v = vertices.get(i);
        }
        Graph graph = new Graph();

        graph.vertices = vertices;
        graph.points = points;

        return graph;
    }

    public Graph copy(Graph graph){
        int num = graph.vertices.size();
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(num);
        ArrayList<Point> points = new ArrayList<>();
        for(int i = 0; i < num; i++){
            Point point = graph.vertices.get(i).point;
            points.add(point);
            if(i < num/2){
                vertices.add(new Vertex(i, Label.A));
            }
            else{
                vertices.add(new Vertex(i, Label.B));
            }
            vertices.get(i).point = point;

            point.v = vertices.get(i);
        }

        Graph newGraph = new Graph();

        newGraph.vertices = vertices;
        newGraph.points = points;

        return newGraph;
    }

//    public Graph generate(int num, double large, double middle, double small, double bottleneck){
//
//        /*
//         * Assumption:
//         * Large is divisible by middle
//         * Middle is divisible by small
//         * Bottleneck is less than Middle
//         */
//        this.r = new Random(seed + (int)(System.currentTimeMillis() * 1000));
//        assert (int)(large%middle) == 0;
//        assert (int)(middle%small) == 0;
//        assert bottleneck < middle;
//        assert (int)(bottleneck%(2*small)) == 0;
//
//        int small_grid_num_one_middle = (int)(middle/small);
//        int small_grid_num_one_row = (int)(large/small);
//
//
//        /*
//         * generate points and vertices
//         */
//        ArrayList<Vertex> vertices = new ArrayList<Vertex>(num);
//        ArrayList<Point> points = new ArrayList<>();
//        for(int i = 0; i < num; i++){
//            Point point = new Point(r.nextDouble()*large,r.nextDouble()*large);
//            if(i < num/2){
//                vertices.add(new Vertex(i, Label.A));
//            }
//            else{
//                vertices.add(new Vertex(i, Label.B));
//            }
//            vertices.get(i).point = point;
//            points.add(point);
//            point.v = vertices.get(i);
//        }
//
//
//
//        /*
//         * according to small grids, generate cells
//         */
//        ArrayList<Vertex>[][] cells = new ArrayList[small_grid_num_one_row][small_grid_num_one_row];
//
//        {
//            for(int i = 0; i < num; i++){
//                Point point = vertices.get(i).point;
//                int x = search(point.x, small);
//                int y = search(point.y, small);
//
//                assert point.x >= x * small && point.x <= (x + 1) * small;
//                assert point.y >= y * small && point.y <= (y + 1) * small;
//
//                if(cells[x][y] == null){
//                    cells[x][y] = new ArrayList<Vertex>();
//                }
//                cells[x][y].add(vertices.get(i));
//            }
//        }
//
//        int[] horizontal = new int[small_grid_num_one_row];
//        int[] vertical = new int[small_grid_num_one_row];
//
//        {
//            for(int i = 0; i < small_grid_num_one_row; i++){
//                for(int j = 0; j < small_grid_num_one_row; j++){
//                    int size;
//                    if(cells[i][j] == null){
//                        size = 0;
//                    }
//                    else{
//                        size = cells[i][j].size();
//                    }
//                    horizontal[i]+=size;
//                    vertical[j]+=size;
//                }
//            }
//        }
//
//        //find the combination with the least number of boundary points
//        Integer bestBCx = Integer.MAX_VALUE;
//        Integer idxx = null;
//        Integer bestBCy = Integer.MAX_VALUE;
//        Integer idxy = null;
//
//        {
//            int lowbound = (int)(bottleneck/small/2);
//            int highbound = (int)(middle/small - 1) - (int)(bottleneck/small/2 + 1);
//
//            //for(int i = 0; i < small_grid_num_one_middle; i++){
//            for(int i = lowbound; i <= highbound; i++){
//                int currBC = 0;
//
////                if(i - (bottleneck/small/2 - 1) - 1 < 0){
////                    continue;
////                }
////                if(i + (large/middle - 1) * (middle/small) + (bottleneck/small/2 - 1) >= large/small){
////                    continue;
////                }
//
//                for(int j = 0; j < large/middle; j++){
//                    for(int k = 0; k < bottleneck/small/2; k++){
//                        int position1 = (int)(i+j*(middle/small) - k);
//                        int position2 = (int)(i+j*(middle/small)) + k + 1;
//                        currBC+=horizontal[position1];
//                        currBC+=horizontal[position2];
//                    }
//                }
//                if(currBC < bestBCx){
//                    bestBCx = currBC;
//                    idxx = i;
//                }
//            }
//        }
//
//        {
//            for(int i = 0; i < small_grid_num_one_middle; i++){
//                int currBC = 0;
//
//                if(i - (bottleneck/small/2 - 1) - 1 < 0){
//                    continue;
//                }
//                if(i + (large/middle - 1) * (middle/small) + (bottleneck/small/2 - 1) >= large/small){
//                    continue;
//                }
//
//                for(int j = 0; j < large/middle; j++){
//                    for(int k = 0; k < bottleneck/small/2; k++){
//                        int position1 = (int)(i+j*(middle/small) - k - 1);
//                        int position2 = (int)(i+j*(middle/small)) + k;
//                        currBC+=vertical[position1];
//                        currBC+=vertical[position2];
//                    }
//                }
//                if(currBC < bestBCy){
//                    bestBCy = currBC;
//                    idxy = i;
//                }
//            }
//        }
//
//        assert idxx != null;
//        assert idxy != null;
//
//        Graph[][] pieces = new Graph[(int)Math.pow(large/middle + 1,2)][(int)Math.pow(large/middle + 1,2)];
//        ArrayList<Graph> piecesset = new ArrayList<>();
//
//        //iterate all cell, and assign pieces for all vertex in cells
//        for(int i = 0; i < large/small; i++){
//            for(int j = 0; j < large/small; j++){
//                if(cells[i][j] == null){
//                    continue;
//                }
//
//                /*
//                 * 坐标计算方式
//                 * hint: 平移
//                 */
//                int x = (int)Math.floor(Math.floor(i-idxx)/small_grid_num_one_middle) + 1;
//                int y = (int)Math.floor(Math.floor(j-idxy)/small_grid_num_one_middle) + 1;
//
//
//                if(pieces[x][y] == null){
//                    pieces[x][y] = new Graph();
//                    piecesset.add(pieces[x][y]);
//                }
//                pieces[x][y].vertices.addAll(cells[i][j]);
//
//                for(Vertex v:cells[i][j]){
//                    v.piece = pieces[x][y];
//                }
//            }
//        }
//
//        //test Graph if valid
//        {
//            int count = 0;
//            for(Graph graph:piecesset){
//                count += graph.vertices.size();
//            }
//            assert count == vertices.size();
//        }
//
//        Graph graph = new Graph();
//
//        graph.vertices = vertices;
//        graph.pieces = piecesset;
//        graph.points = points;
//        assert vertices.size()%2 == 0;
//        graph.numV = vertices.size()/2;
//        for(Vertex v:graph.vertices){
//            graph.edgeNum += v.edges.size();
//        }
//        assert graph.edgeNum%2 == 0;
//        graph.edgeNum = graph.edgeNum/2;
//
//        for(Vertex v: graph.vertices){
//            ArrayList<Vertex> ones = new ArrayList<>();
//            ArrayList<Vertex> zeros = new ArrayList<>();
//            for(Vertex u:v.edges){
//                if(u.piece == v.piece){
//                    zeros.add(u);
//                }
//                else{
//                    ones.add(u);
//                }
//            }
//            v.onezeroP = ones.size();
//            ones.addAll(zeros);
//            v.edges = ones;
//        }
//
//        return graph;
//
//    }

    public Graph generate(int num, double side, int part, double small, double bottleneck){

        /*
         * Assumption:
         * Large is divisible by middle
         * Middle is divisible by small
         * Bottleneck is less than Middle
         */
        this.r = new Random(seed + (int)(System.currentTimeMillis() * 1000));
        double middle = side/part;
        assert bottleneck < middle;
        assert (int)(side%small) == 0;
        assert (int)(bottleneck%(2*small)) == 0;

        int small_grid_num_one_row = (int)(side/small);
        int small_grid_num_one_middle = (int)(middle/small);
        /*
         * generate points and vertices
         */
        ArrayList<Point> points = new ArrayList<>(num);
        ArrayList<Vertex> vertices = new ArrayList<Vertex>(num);
        for(int i = 0; i < num; i++){
            Point point = new Point(r.nextDouble()*side,r.nextDouble()*side);
            points.add(point);
            if(i < num/2){
                vertices.add(new Vertex(i, Label.A));
            }
            else{
                vertices.add(new Vertex(i, Label.B));
            }
            point.v = vertices.get(i);
            vertices.get(i).point = point;

        }

        int[] horizontal = new int[small_grid_num_one_row];
        int[] vertical = new int[small_grid_num_one_row];

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

                        double smallx = Math.min(source.x, target.x);
                        double bigx = Math.max(source.x, target.x);
                        double smally = Math.min(source.y, target.y);
                        double bigy = Math.min(source.y, target.y);

                        for(int k = search(smallx,small) + 1; k <= search(bigx,small);k++){
                            if(k*small - smallx < bottleneck/2 && bigx - k*small < bottleneck/2){
                                horizontal[k]+=1;
                            }

                        }
                        for(int k = search(smally,small) + 1; k <= search(bigy,small);k++){
                            if(k*small - smally < bottleneck/2 && bigy - k*small < bottleneck/2){
                                vertical[k]+=1;
                            }

                        }
                    }
                }
                vertices.get(i).edges = edges;
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

            for(int i = lowbound; i <= highbound; i++){
                int currBC = 0;

                for(int j = 0; j < side/middle; j++){
//                    for(int k = 0; k < bottleneck/small/2; k++){
//                        int position1 = (i+j*(small_grid_num_one_middle) - k);
//                        int position2 = (i+j*(small_grid_num_one_middle)) + k + 1;
//                        currBC+=horizontal[position1];
//                        currBC+=horizontal[position2];
//                    }
                    currBC+=horizontal[i+j*(small_grid_num_one_middle)];
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
                if(i + (side/middle - 1) * (middle/small) + (bottleneck/small/2 - 1) >= side/small){
                    continue;
                }

                for(int j = 0; j < side/middle; j++){
                    currBC+=vertical[i+j*(small_grid_num_one_middle)];
                }
                if(currBC < bestBCy){
                    bestBCy = currBC;
                    idxy = i;
                }
            }
        }

        assert idxx != null;
        assert idxy != null;

        Graph[][] pieces = new Graph[part+1][part+1];
        ArrayList<Graph> piecesset = new ArrayList<>();

        for(int i = 0; i < points.size(); i++){
            Point p = points.get(i);
            Vertex v = vertices.get(i);
            int x = (int)((p.x - idxx*small)/middle + 1);
            int y = (int)((p.y - idxy*small)/middle + 1);
            if(pieces[x][y] == null){
                pieces[x][y] = new Graph();
                piecesset.add(pieces[x][y]);
            }
            pieces[x][y].vertices.add(v);
            v.piece = pieces[x][y];
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
        graph.points = points;
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
            v.onezeroP = ones.size();
            ones.addAll(zeros);
            v.edges = ones;
        }

        return graph;

    }


    private int search(double coord,double step) {
        return (int)(coord/step);
    }
}
