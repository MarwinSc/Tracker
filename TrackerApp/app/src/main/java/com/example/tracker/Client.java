package com.example.tracker;

import android.content.Context;
import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    //really static? Maybe whole class static?
    static private File directory;

    public Client(File directory) {
        Client.directory = directory;
    }

    public static void send() {

        /*
        if (args.length != 2) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        //String hostName = args[0];
        //int portNumber = Integer.parseInt(args[1]);
        */

        String hostName = "192.168.0.164";
        int portNumber = Integer.parseInt("4444");
        InetAddress serverAddr = null;
        try {
            serverAddr = InetAddress.getByName(hostName);
        } catch (UnknownHostException e) {
            Log.e("Exception","lol error: " + e.getMessage());
        }

        try (
                Socket socket = new Socket(serverAddr, portNumber);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String fromServer;
            String fromUser;

            while ((fromServer = in.readLine()) != null) {
                System.out.println("Server: " + fromServer);
                if (fromServer.equals("Bye.")) {
                    break;
                } else if (fromServer.equals("Ready")){
                    String data = read();
                    out.println(data);
                } else if (fromServer.equals("WroteData")){
                    out.println("Hajde");
                }
                /*
                fromUser = stdIn.readLine();
                if (fromUser != null) {
                    System.out.println("Client: " + fromUser);
                    out.println(fromUser);
                }
                */
            }
        } catch (UnknownHostException e) {
            Log.e("Exception","lol error: " + e.getMessage());
        } catch (IOException e) {
            Log.e("Exception","lol error: " + e.getMessage());
        }

    }

    static private String read(){
        StringBuilder plaintext = new StringBuilder();
        try {
            FileReader fr = new FileReader(Client.directory + "/data.json");
            int content;
            while((content = fr.read())!=-1){
                plaintext.append((char) content);
            }
        } catch (IOException e) {
            Log.e("Exception","lol error: " + e.getMessage());
        }
        return plaintext.toString();
    }
}

