public class Point {
    final private int id;
    final private double x;
    final private double y;

    double Distance(Point p) {
        return Math.sqrt(Math.pow(x - p.x, 2) + Math.pow(y - p.y, 2));
    }

    int GetID() { return id; }
    double GetX() { return x; }
    double GetY() { return y; }

    public Point(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }
}
