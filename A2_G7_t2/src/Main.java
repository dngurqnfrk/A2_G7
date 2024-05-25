// A2_G7_t2 : DB scan 알고리즘

public class Main {
    public static void main(String[] args) {
        DBscan db = new DBscan("./artd-31.csv");
        db.Approximate_Eps(4);
        /*
        String filePath = args[0];

        if(args.length > 3) {
            System.out.println("Invalid arguments");
            return;
        }

        int minPts;
        double eps;

        if(args.length == 2) {
            try{
                minPts = Integer.parseInt(args[1]);
                // appropriate eps value
            } catch (NumberFormatException e) {
                System.out.println("argument is double");
                eps  = Double.parseDouble(args[1]);
                // appropriate minPts value
            }
        } else {
            minPts = Integer.parseInt(args[1]);
            eps = Double.parseDouble(args[2]);
        }
         */

        int minPts = 4;
        double eps = 10000;

        db.ClassifyPoints(minPts, eps);
    }
}