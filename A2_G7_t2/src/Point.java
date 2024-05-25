public class Point {
    final private int id;
    final private double x;
    final private double y;
    private int  cluster_id;

    double Distance(Point p) {
        return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2));
    }

    int GetID() { return id; }
    double GetX() { return x; }
    double GetY() { return y; }
    void SetClusterID(int id) { cluster_id = id; }
    int GetClusterID() { return cluster_id; }
    public Point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.cluster_id = -1;
    }
}
