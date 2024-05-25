import java.io.*;

public class CSV {
    private BufferedReader br;

    CSV(String filePath) {
        File csv = new File(filePath);
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String readLine() {
        try {
            return br.readLine();
        } catch (IOException e) {
        throw new RuntimeException(e);
        }
    }

    public void Reload(String filePath) {
        File csv = new File(filePath);
        try {
            br = new BufferedReader(new FileReader(csv));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
