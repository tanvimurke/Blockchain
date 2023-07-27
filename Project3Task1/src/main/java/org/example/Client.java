/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
package org.example;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    //set serverPort
    static int serverPort = 7777;
    //initialize socket
    static Socket clientSocket = null;
    String data = "";


    public static void main(String args[]) throws IOException {
        System.out.println("Blockchain client running");
        System.out.println("We have a visitor");

        try {

            while (true) {
                //bind socket to port
                clientSocket = new Socket("localhost", serverPort);
                //declare requestMessage and ResponseMessage
                RequestMessage requestMessage;
                ResponseMessage responseMessage;

                //display Menu
                System.out.println("0. View basic blockchain status.\n" +
                        "1. Add a transaction to the blockchain.\n" +
                        "2. Verify the blockchain.\n" +
                        "3. View the blockchain.\n" +
                        "4. Corrupt the chain.\n" +
                        "5. Hide the Corruption by repairing the chain.\n" +
                        "6. Exit");
                Scanner sc = new Scanner(System.in);
                int choice = sc.nextInt();

                //after user choice
                switch (choice) {
                    case 0:
                        //initialize requestMessage
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(0);
                        //send request to server
                        request(requestMessage);
                        //get Response from server
                        responseMessage = response();
                        //display repsonse from server
                        System.out.println("Current size of chain: " + responseMessage.getChainSize());
                        System.out.println("Difficulty of most recent block: " + responseMessage.getDifficulty());
                        System.out.println("Total Difficulty for all blocks: " + responseMessage.getTotalDifficulty());
                        System.out.println("Approximate hashes per second on this machine: " + responseMessage.getHashesPerSecond());
                        System.out.println("Expected total hashes required for the whole chain: " + responseMessage.getTotalExpectedHashes());
                        System.out.println("Nonce for most recent block: " + responseMessage.getNonce());
                        System.out.println("Chain hash: " + responseMessage.getChainHash());
                        break;
                    case 1:
                        //get data from user
                        System.out.println("Enter difficulty > 0");
                        Scanner sc1 = new Scanner(System.in);
                        int difficulty = sc1.nextInt();
                        Scanner sc2 = new Scanner(System.in);
                        System.out.println("Enter transaction");
                        String data = sc2.nextLine();
                        //set request messages
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(1);
                        requestMessage.setDifficulty(difficulty);
                        requestMessage.setData(data);
                        //send request to server
                        request(requestMessage);
                        //get response from server and display response
                        responseMessage = response();
                        System.out.println(responseMessage.getResponse());
                        break;

                    case 2:
                        //set request messages
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(2);
                        //send request to server
                        request(requestMessage);
                        //get response from server and display it
                        responseMessage = response();
                        System.out.println(responseMessage.getResponse());
                        break;
                    case 3:
                        //set request messages
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(3);
                        //send request to server
                        request(requestMessage);
                        //get response from server and display it
                        responseMessage = response();
                        System.out.println(responseMessage.getResponse());
                        break;
                    case 4:
                        //get data from user
                        System.out.println("corrupt the Blockchain");
                        System.out.print("Enter block ID of block to corrupt \n");
                        Scanner sc3 = new Scanner(System.in);
                        int id = sc3.nextInt();
                        System.out.print("Enter new data for block " + id + ":\n");
                        sc.nextLine();
                        String corrupt = sc.nextLine();
                        //set request messages
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(4);
                        requestMessage.setId(id);
                        requestMessage.setCorrupt(corrupt);
                        //send request to server
                        request(requestMessage);
                        //get response from server and display it
                        responseMessage = response();
                        System.out.println(responseMessage.getResponse());
                        break;

                    case 5:
                        //set request messages
                        requestMessage = new RequestMessage();
                        requestMessage.setSelection(5);
                        //send request to server
                        request(requestMessage);
                        //get response from server and display it
                        responseMessage = response();
                        System.out.println(responseMessage.getResponse());
                        break;

                    case 6:
                        //exit the system
                        System.exit(0);
                    default:
                        break;
                }

            }
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }

    }

    //method to send request to server
    public static void request(RequestMessage requestString) throws IOException {
        //creates output stream using socket
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
        //stream request string in output
        out.println(requestString.toString());
        //flush
        out.flush();
    }

    //method to get response from server
    public static ResponseMessage response() throws IOException {
        //get the input using sockets
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        // read a line of data from the stream
        String responseString = in.readLine();
        //reference from prereq Project 3
        Gson gson = new Gson();
        ResponseMessage responseMessage = gson.fromJson(responseString, ResponseMessage.class);
        return responseMessage;

    }

}
