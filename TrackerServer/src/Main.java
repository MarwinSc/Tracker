import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Main{

    public static void main(String[] args) {

        if (args.length != 1) {
            System.err.println("Usage: java KnockKnockServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try(
            ServerSocket serverSocket = new ServerSocket(portNumber);
            Socket clientSocket = serverSocket.accept();
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ){

            String inputLine, outputLine;

            // Initiate conversation with client
            SimpleProtocol sp = new SimpleProtocol();
            outputLine = sp.process(null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                outputLine = sp.process(inputLine);
                //System.out.println("Client Data: " + outputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye."))
                    break;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
