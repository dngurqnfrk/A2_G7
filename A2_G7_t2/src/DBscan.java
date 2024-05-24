import java.util.ArrayList;

public class DBscan {
    CSV file;
    ArrayList<Point> data_points;



    DBscan(String _filePath) {
        this.file = new CSV(_filePath);
        this.data_points = new ArrayList<>();

        String data_buf;
        int index = 0;
        while((data_buf = this.file.readLine()) != null) {
            String[] data = data_buf.split(",");
            this.data_points.add(new Point(index, Double.parseDouble(data[1]), Double.parseDouble(data[2])));
            index++;
        }

        for (Point dataPoint : data_points) {
            System.out.printf("[p%d] : %f, %f\n", dataPoint.GetID(), dataPoint.GetX(), dataPoint.GetY());
        }
    }
}
