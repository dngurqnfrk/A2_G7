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
            Integer N = 20;
            
            List<Point> resultCentroidList = null;
            List<Double> resultDistList = new ArrayList<>();
            // estimate k
            double random = Math.random();
            Integer startIdx = (int) (random * pointsList.size());
            if (K == null) {
                List<Point>[] k_PointList = new ArrayList[N];
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
                Double maxDist = Double.MIN_VALUE;
                for (int i = 1; i < N; i++) {
                    Double diff = -resultDistList.get(i) + resultDistList.get(i-1);
                    if (diff < 0) {
                        maxDist = diff;
                        K = i;
                        break;
                    }
                    else if (maxDist < diff) {
                        maxDist = diff;
                        K = i + 1;
                    }
                }
                for (Double d: resultDistList) {
                    System.out.println(d);
                }
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
        while (changed) {
            changed = false;
            for (Point p: pointsList) {
                if (p.cluster == -1 || p.cluster == 0) {
                    p.cluster = 0;
                    Double minDist = Double.MAX_VALUE;
                    for (Point c: centroids) {
                        Double dist = Math.pow(distance(p, c), 2);
                        if (dist < minDist) {
                            minDist = dist;
                            if (p.cluster != c.cluster) {
                                changed = true;
                            }
                            p.cluster = c.cluster;
                        }
                    }
                }
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
            break;
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