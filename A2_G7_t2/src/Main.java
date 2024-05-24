// A2_G7_t2 : DB scan 알고리즘

public class Main {
    public static void main(String[] args) {
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
        double eps = 0.5;

        // read all points
        DBscan db = new DBscan("./artd_31.csv");

        // 모든 포인트 분류

        for (int i = 1; i <= 5; i++) {
            //TIP Press <shortcut actionId="Debug"/> to start debugging your code. We have set one <icon src="AllIcons.Debugger.Db_set_breakpoint"/> breakpoint
            // for you, but you can always add more by pressing <shortcut actionId="ToggleLineBreakpoint"/>.
            System.out.println("i = " + i);
        }
    }
}