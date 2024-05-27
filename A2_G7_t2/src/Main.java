// A2_G7_t2 : DB scan 알고리즘

public class Main {
    public static void main(String[] args) {
        String filePath = args[0];

        DBscan db = new DBscan(filePath);

        if(args.length > 3) {
            System.out.println("Invalid arguments");
            return;
        }

        int minPts;
        double eps;

        if(args.length == 2) {
            try{
                minPts = Integer.parseInt(args[1]);
                eps = db.Approximate_Eps(minPts);
                System.out.printf("Estimated Eps : %f\n", eps);
            } catch (NumberFormatException e) {
                eps  = Double.parseDouble(args[1]);
                minPts = db.Approximate_MinPts(eps);
                System.out.printf("Estimated MinPts : %d\n", minPts);
            }
        } else {
            minPts = Integer.parseInt(args[1]);
            eps = Double.parseDouble(args[2]);
        }

        db.ClassifyPoints(minPts, eps);
    }
}