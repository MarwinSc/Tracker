import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class SimpleProtocol {

    private static final int WAITING = 0;
    private static final int PROCESSING = 1;
    private static final int POST = 2;

    private int state = WAITING;

    public String process(String input){
        String output;
        if (state == WAITING){
            state = PROCESSING;
            output = "Ready";
        } else if (state == PROCESSING) {
            writeToFile(input);
            output = "WroteData";
            state = POST;
        } else if (state == POST){
            output = "Bye.";
            state = WAITING;
        } else {
            output = "Can't Process";
        }
        return output;
    }

    private void writeToFile(String data) {
        try {
            OutputStream outputStream = new FileOutputStream("data.json");
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
