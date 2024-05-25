//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
// A2_G7_t1 : k-means++ 알고리즘
public class k_means {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.exit(1);
        }
        try {
            String fileName = args[0];
            Integer K = null;
            if (args.length == 2) 
                K = Integer.valueOf(args[1]);
            String line;
            String[] tokens;
            // read data
    
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            List<Point> pointsList = new ArrayList<>();
            List<Integer> set = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                tokens = line.split(",");
                pointsList.add(new Point(tokens[0], Double.valueOf(tokens[1]), Double.valueOf(tokens[2]), -1));
                set.add(Integer.parseInt(tokens[3]));
            }
            br.close();
            // System.out.println("degree:"+degree(new Point("1", 0.0, 0.0, -1), new Point("2", 1.0, 0.0,-1), new Point("3", 2.0, 0.0,-1)));
            Integer N = 30;
            
            List<Point> resultCentroidList = null;
            List<Double> resultDistList = new ArrayList<>();
            // estimate k
            double random = Math.random();
            Integer startIdx = (int) (random * pointsList.size());
            if (K == null) {
                List<Point>[] k_PointList = new ArrayList[N];
                // iterate k from 1 to N
                for (int i = 0; i < N; i++) {
                    for (Point p: pointsList) {
                        p.cluster = -1;
                    }
                    K = i + 1;
                    // System.out.println("K: " + K);
                    resultCentroidList = k_means_plus_plus(pointsList, K, startIdx);
                    // averge distance of centroids
                    Double sumDist = 0.0;
                    Double J = calJ(resultCentroidList, pointsList);
                    // for (int j = 0; j < K; j++) {
                    //     for (int k = j+1; k < K; k++) {
                    //         sumDist += distance(resultCentroidList.get(j), resultCentroidList.get(k));
                    //     }
                    // }
                    // sumDist /= (K * (K-1) / 2);
                    // resultDistList.add(sumDist);
                    resultDistList.add(J);
                    k_PointList[i] = new ArrayList<>(); 
                    for (Point p: pointsList) {
                        k_PointList[i].add(p.copyResult());
                    }
                }

                // find elbow point
                Double dy = resultDistList.get(N-1) - resultDistList.get(0);
                Double dx = (double) N - 1;
                Double maxDist = Double.MIN_VALUE;
                for (int i = 0; i < N; i++) {
                    Double dist = Math.abs(dy * i - dx * (resultDistList.get(i) - resultDistList.get(0))) / Math.sqrt(dy * dy + dx * dx);
                    // System.out.println("i: "+i+" dist: " + dist + " maxDist: " + maxDist);
                    if (dist > maxDist) {
                        maxDist = dist;
                        K = i + 1;
                    }
                }

                // Double maxDist = Double.MIN_VALUE;
                // Double minDegree = Math.PI;
                // for (int i = 2; i < N; i++) {
                //     // Point p1 = new Point("x1", 1.0, resultDistList.get(i-2), -1);
                //     // Point p2 = new Point("x2", 2.0, resultDistList.get(i-1), -1);
                //     // Point p3 = new Point("x3", 3.0, resultDistList.get(i), -1);
                //     // System.out.println(p1.y+" "+p2.y+" "+p3.y);
                //     Double d1 = resultDistList.get(i-2);
                //     Double d2 = resultDistList.get(i-1);
                //     Double d3 = resultDistList.get(i);
                //     Double r1 = d1 - d2;
                //     Double r2 = d2 - d3;
                //     // Double diff = -resultDistList.get(i) + resultDistList.get(i-1);
                //     // Double dg = degree(p1, p2, p3);
                //     Double diff = r1 - r2;
                //     // Double dg = (degree(p1, p2, p3) < 0)? Math.PI - degree(p1, p2, p3): degree(p1, p2, p3);
                //     // System.out.println("diff: " + diff + " dg: " + dg);
                //     if (r2 < 0) {
                //         maxDist = diff;
                //         K = i;
                //         break;
                //     }
                //     else if (diff > maxDist) {
                //         maxDist = diff;
                //         K = i - 1;
                //     }
                // }
                // for (Double d: resultDistList) {
                //     System.out.println(d);
                // }
                pointsList = k_PointList[K-1]; 
                System.out.println("estimated k: " + K);   
            }
            // k-means++ 
            else {
                resultCentroidList = k_means_plus_plus(pointsList, K, startIdx);
            }

            
            
            // print result
            List<String>[] culsterName = new ArrayList[K];
            for (int i = 0; i < K; i++) {
                culsterName[i] = new ArrayList<>();
                for (Point p: pointsList) {
                    if (p.cluster == i+1) {
                        // System.out.println(p.name + " " + p.x + " " + p.y + " " + p.cluster);
                        culsterName[i].add(p.name);
                    }
                }
            }
            // System.out.println("estimated k: " + K);
            for (int i = 0; i < K; i++) {
                System.out.print("Cluster #" + (i+1) + " => ");
                for (String name: culsterName[i]) {
                    System.out.print(name + " ");
                }
                System.out.println();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static Double degree(Point p1, Point p2, Point p3) {
        Double x1 = p1.x - p2.x;
        Double y1 = p1.y - p2.y;
        Double x2 = p3.x - p2.x;
        Double y2 = p3.y - p2.y;
        Double innerProduct = x1 * x2 + y1 * y2;
        Double dist1 = Math.sqrt(x1 * x1 + y1 * y1);
        Double dist2 = Math.sqrt(x2 * x2 + y2 * y2);
        return Math.acos(innerProduct / (dist1 * dist2));
    }
    static Double distance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }
    static Double calJ(List<Point> centroids, List<Point> pointsList) {
        Double sumDist = 0.0;
        for (Point p: pointsList) {
            for (Point c: centroids) {
                if (p.cluster == c.cluster) {
                    Double dist = Math.pow(distance(p, c), 2);
                    sumDist += dist;
                }
            }
        }
        return sumDist;
    }
    static List<Point> k_means_plus_plus(List<Point> pointsList, Integer K, Integer startIdx) {
        // generate K centroids
        
        List<Point> centroids = new ArrayList<>();
        centroids.add(pointsList.get(startIdx).copy());
        centroids.get(0).cluster = 1;
        for (int i = 2; i <= K; i++) {
            Point newCentroid = null;
            Double maxDist = Double.MIN_VALUE;
            for (Point p: pointsList) {
                Double sumDist = 0.0;
                if (p.cluster == -1) {
                    for (Point c: centroids) {
                        Double dist = distance(p, c);
                        sumDist += dist;
                        if (sumDist > maxDist) {
                            maxDist = sumDist;
                            newCentroid = p;
                        }
                    }
                }
            }
            newCentroid.cluster = 0;
            newCentroid = newCentroid.copy();
            newCentroid.cluster = i;
            centroids.add(newCentroid);
        }
        // for (Point c: centroids) {
        //     System.out.println(c.name + " " + c.x + " " + c.y + " " + c.cluster);
        // }

        // update cluster and re-calculate centroids
        boolean changed = true;
        int cnt = 0;
        while (changed) {
            changed = false;
            cnt++;
            // System.out.println("cnt: " + cnt);
            for (Point p: pointsList) {
                Point selectedCentroid = null;
                Double minDist = Double.MAX_VALUE;
                for (Point c: centroids) {
                    Double dist = distance(p, c);
                    if (dist < minDist) {
                        minDist = dist;
                        selectedCentroid = c;
                        // p.cluster = c.cluster;
                    }
                }
                if (p.cluster != selectedCentroid.cluster) {
                    changed = true;
                    // System.out.println(p.cluster+" "+selectedCentroid.cluster);
                }
                p.cluster = selectedCentroid.cluster;
                // if (p.cluster == -1 || p.cluster == 0) {
                //     p.cluster = 0;
                // }
            }
            for (Point c: centroids) {
                c.x = 0.0;
                c.y = 0.0;
                Integer count = 0;
                for (Point p: pointsList) {
                    if (p.cluster == c.cluster) {
                        c.x += p.x;
                        c.y += p.y;
                        count++;
                    }
                }
                c.x /= count;
                c.y /= count;
            }
            // for(Point c: centroids) {
            //     System.out.println(c.name + " " + c.x + " " + c.y + " " + c.cluster);
            // }
        }
        return centroids;
    }
} 

class Point {
    String name;
    Double x;
    Double y;
    Integer cluster;
    public Point(String name, Double x, Double y, Integer cluster) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.cluster = cluster;
    }
    public Point copy() {
        return new Point("copy_" + this.name, this.x, this.y, this.cluster);
    }
    public Point copyResult() {
        return new Point(this.name, this.x, this.y, this.cluster);
    }
}