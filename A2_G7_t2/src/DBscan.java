import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;

public class DBscan {
    CSV file;
    ArrayList<Point> data_points;

    public void ClassifyPoints(int minPts, double eps) {
        int noise = 0;

        List<Point> core_points = new ArrayList<>();
        List<Point> noise_points = new ArrayList<>();
        // Classify Points and Eliminate Noise
        for(Point p : this.data_points) {
            int  neighbor_count = 0;
            for(Point other_point : this.data_points) {
                if(other_point.GetID() == p.GetID())
                    continue;

                if(p.Distance(other_point) <= eps) {
                    neighbor_count++;
                }
                if(neighbor_count >= minPts - 1) {
                    core_points.add(p);
                    break;
                }
            }
        }

        if(core_points.isEmpty()) {
            System.out.println("No core points");
            return;
        }

        for(Point corePoint : core_points) {
            this.data_points.remove(corePoint);
        }

        for(Point p : this.data_points) {
            boolean Check_Border = false;
            for(Point corePoint : core_points) {
                if(p.Distance(corePoint) <= eps) {
                    Check_Border = true;
                    break;
                }
            }

            if(Check_Border)
                continue;

            noise_points.add(p);
            noise++;
        }

        for(Point noisePoint : noise_points) {
            this.data_points.remove(noisePoint);
        }

        // Clustering Core Points
        List<HashSet<Integer>> clusters = Connect_Core_Points(core_points, eps);
        int num_clusters = clusters.size();

        int num_in_clusters = 0;
        for (HashSet<Integer> cluster : clusters) {
            num_in_clusters += cluster.size();
        }

        System.out.printf("In clusters, num of points is %d\n", num_in_clusters);

        // Clustering Border Points
        for(Point border_p : this.data_points) {
            double shortest_distance = Double.MAX_VALUE;
            Point shortest_core_point = null;
            for(Point core_p : core_points) {
                if(border_p.Distance(core_p) < shortest_distance) {
                    shortest_distance = border_p.Distance(core_p);
                    shortest_core_point = core_p;
                }
            }
            if(shortest_core_point == null)
                continue;

            for (HashSet<Integer> cluster : clusters) {
                if(cluster.contains(shortest_core_point.GetID())) {
                    cluster.add(border_p.GetID());
                    break;
                }
            }
        }

        System.out.printf("Number of clusters : %d\n", num_clusters);
        System.out.printf("Number of noise : %d\n", noise);

        for (int i = 0; i < clusters.size(); i++) {
            PriorityQueue<Integer> pq_point_id = new PriorityQueue<>();
            pq_point_id.addAll(clusters.get(i));
            while(!pq_point_id.isEmpty()) {
                System.out.printf("%d", pq_point_id.poll());
                if(!pq_point_id.isEmpty())
                    System.out.printf(",");
            }
            System.out.println();
        }
    }

    public int GetClusterID(Point p, List<HashSet<Integer>> clusters) {
        for (int i = 0; i < clusters.size(); i++) {
            if(clusters.get(i).contains(p.GetID())) {
                return i;
            }
        }
        return -1;
    }

    public List<HashSet<Integer>> Connect_Core_Points(List<Point> core_points, double eps) {
        List<HashSet<Integer>> clusters = new ArrayList<>();
        int num_clusters = 0;
        List<Point> id_stack = new ArrayList<>();
        List<Point> history_stack = new ArrayList<>();
        List<Point> core_points_copy = new ArrayList<>(core_points);

        id_stack.add(core_points_copy.get(0));

        // 모든 포인트들을 순회하며 clustering
        while(!id_stack.isEmpty() || !core_points_copy.isEmpty()) {
            //스택이 비어있으면 지금까지 순회한 core point들을 제거하고 다음 core point를 스택에 넣음
            if(id_stack.isEmpty()) {
                Erase_clustered_points(core_points_copy, clusters);
                if(core_points_copy.isEmpty())
                    break;
                else
                    id_stack.add(core_points_copy.get(0));
            }
            Point p = id_stack.get(0);
            history_stack.add(p);
            id_stack.remove(0);

            int cluster_id = GetClusterID(p, clusters);

            // 포인트 p가 아직 클러스터링 되지 않았다면 새로운 클러스터 생성
            if (num_clusters == 0 || cluster_id == -1) {
                clusters.add(new HashSet<>());
                clusters.get(num_clusters).add(p.GetID());
                cluster_id = num_clusters;
                num_clusters++;
            }

            // 포인트 p와 eps 거리 이내의 포인트들을 찾아서 클러스터에 추가
            for (Point other_point : core_points_copy) {
                if (other_point.GetID() == p.GetID())
                    continue;

                if (p.Distance(other_point) <= eps) {
                    int other_point_cluster_id = GetClusterID(other_point, clusters);

                    // 이미 스택에 포인트가 있었던 경우가 아니면 스택에 추가
                    if(!id_stack.contains(other_point) && !history_stack.contains(other_point))
                        id_stack.add(other_point);
                    if (other_point_cluster_id == -1) {
                        clusters.get(cluster_id).add(other_point.GetID());
                    } else if (other_point_cluster_id != cluster_id) {
                        System.out.printf("Merge %d and %d\n", cluster_id, other_point_cluster_id);
                        int bigger_cluster_id = Math.max(cluster_id, other_point_cluster_id);
                        int smaller_cluster_id = Math.min(cluster_id, other_point_cluster_id);
                        clusters.get(smaller_cluster_id).addAll(clusters.get(bigger_cluster_id));
                        clusters.remove(bigger_cluster_id);
                        num_clusters--;
                        cluster_id = GetClusterID(p, clusters);
                    }
                }
            }

            core_points_copy.remove(p);
        }

        return clusters;
    }

    public void Erase_clustered_points(List<Point> core_point, List<HashSet<Integer>> clusters)
    {
        for (HashSet<Integer> cluster : clusters) {
            for (Integer i : cluster) {
                for (Point p : core_point) {
                    if (p.GetID() == i) {
                        core_point.remove(p);
                        break;
                    }
                }
            }
        }
    }

    public double Approximate_Eps(int minPts) {
        // add k-th neighbor distance
        PriorityQueue<Double> pq_kth_point_distance = new PriorityQueue<>();
        for (Point dataPoint : data_points) {
            PriorityQueue<Double> pq_point_distance = new PriorityQueue<>();
            for (Point otherPoint : data_points) {
                if(dataPoint.GetID() == otherPoint.GetID())
                    continue;

                pq_point_distance.add(dataPoint.Distance(otherPoint));
            }
            for(int i = 0; i < minPts - 2; i++) {
                pq_point_distance.poll();
            }
            pq_kth_point_distance.add(pq_point_distance.poll());
        }


        List<Double> kth_point_distance = new ArrayList<>();
        while(!pq_kth_point_distance.isEmpty()) {
            kth_point_distance.add(pq_kth_point_distance.poll());
        }
        double longest_distance = kth_point_distance.get(kth_point_distance.size() - 1);

        kth_point_distance.replaceAll(v -> v / longest_distance);

        for (Double v : kth_point_distance) {
            System.out.println(v);
        }

        double largest_gap = 0;
        int largest_gap_x = 0;

        for (Double v : kth_point_distance) {
            double normalized_x = (double)(kth_point_distance.indexOf(v) + 1)/(double)kth_point_distance.size();
            if(largest_gap < normalized_x - v) {
                largest_gap = normalized_x - v;
                largest_gap_x = kth_point_distance.indexOf(v);
            }
        }

        System.out.printf("Largest gap : %f\n", largest_gap);

        return kth_point_distance.get(largest_gap_x) * longest_distance;
    }

    public int Approximate_MinPts(double eps)
    {
        // 3부터 시작할지 4부터 시작할지 모름
        double previous_eps = 0;
        double now_eps;
        int k = 3;
        while(true) {
            now_eps = Approximate_Eps(k);
            if(now_eps > eps) {
                break;
            }
            k++;
            previous_eps = now_eps;
        }

        if(k == 3)
            return 3;

        if(eps - previous_eps <= now_eps - eps) {
            return k - 1;
        } else {
            return k;
        }
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
