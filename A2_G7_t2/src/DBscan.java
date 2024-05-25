import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class DBscan {
    CSV file;
    ArrayList<Point> data_points;

    public void ClassifyPoints(int minPts, double eps) {
        int border = 0;
        int core = 0;
        int noise = 0;

        int num_clusters = 0;
        HashSet<Point> core_points = new HashSet<>();
        List<HashSet<Integer>> clusters = new ArrayList<>();
        for(Point p : this.data_points) {
            int  neighbor_count = 0;
            boolean check_core = false;
            boolean check_border = false;
            for(Point other_point : this.data_points) {
                if(other_point.GetID() == p.GetID())
                    continue;

                if(p.Distance(other_point) <= eps) {
                    neighbor_count++;
                    if(neighbor_count >= minPts) {
                        core_points.add(p);
                        core++;
                        check_core = true;
                    }

                    if(core_points.contains(other_point)) {
                        check_border = true;
                    }
                }
            }

            if(check_core)
                continue;

            if(check_border) {
                border++;
            } else {
                noise++;
            }
        }

        System.out.printf("Core : %d, Border : %d, Noise : %d\n", core, border, noise);

        for(Point p : core_points) {
            if(num_clusters == 0 || !Check_Point_In_Cluster(p, clusters)) {
                clusters.add(new HashSet<>());
                clusters.get(num_clusters).add(p.GetID());
                num_clusters++;
            }
            int cluster_id = GetClusterID(p, clusters);

            for(Point other_point : this.data_points) {
                if(other_point.GetID() == p.GetID())
                    continue;

                if(p.Distance(other_point) <= eps) {
                    int other_point_cluster_id = GetClusterID(other_point, clusters);
                    if(other_point_cluster_id == -1) {
                        clusters.get(cluster_id).add(other_point.GetID());
                    } else if(other_point_cluster_id != cluster_id) {
                        clusters.get(cluster_id).addAll(clusters.get(other_point_cluster_id));
                        clusters.remove(other_point_cluster_id);
                        num_clusters--;
                        cluster_id = GetClusterID(p, clusters);
                    }
                }
            }
        }

        for (int i = 0; i < clusters.size(); i++) {
            System.out.printf("Cluster #%d => ", i+1);
            PriorityQueue<Integer> pq_point_id = new PriorityQueue<>();
            for (Integer id : clusters.get(i)) {
                pq_point_id.add(id);
            }
            while(!pq_point_id.isEmpty()) {
                System.out.printf("p%d ", pq_point_id.poll());
            }
            System.out.println();
        }
    }

    public boolean Check_Point_In_Cluster(Point p, List<HashSet<Integer>> clusters) {
        for (HashSet<Integer> cluster : clusters) {
            if(cluster.contains(p.GetID())) {
                return true;
            }
        }
        return false;
    }

    public int GetClusterID(Point p, List<HashSet<Integer>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            if(clusters.get(i).contains(p.GetID())) {
                return i;
            }
        }
        return -1;
    }

    public double Approximate_Eps(int minPts) {
        double app_eps;
        PriorityQueue<Double> pq_kth_point_distance = new PriorityQueue<>();
        for (Point dataPoint : data_points) {
            PriorityQueue<Double> pq_point_distance = new PriorityQueue<>();
            for (Point otherPoint : data_points) {
                if(dataPoint.GetID() == otherPoint.GetID())
                    continue;

                pq_point_distance.add(dataPoint.Distance(otherPoint));
            }
            for(int i = 0; i < minPts; i++) {
                pq_point_distance.poll();
            }
            pq_kth_point_distance.add(pq_point_distance.poll());
        }
        List<Double> kth_point_distance = new ArrayList<>();
        while(!pq_kth_point_distance.isEmpty()) {
            kth_point_distance.add(pq_kth_point_distance.poll());
        }

        System.out.println("Approximate Eps\n");
        double previous = 0;
        for (Double v : kth_point_distance) {
            System.out.printf("[%d] %f\n", kth_point_distance.indexOf(v) + 1, v - previous);
            previous = v;
        }

        return 0;
    }

    DBscan(String _filePath) {
        this.file = new CSV(_filePath);
        this.data_points = new ArrayList<>();

        String data_buf;
        int index = 1;
        while((data_buf = this.file.readLine()) != null) {
            String[] data = data_buf.split(",");
            this.data_points.add(new Point(index, Double.parseDouble(data[1]), Double.parseDouble(data[2])));
            index++;
        }
    }
}
