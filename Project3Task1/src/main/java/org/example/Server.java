/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
package org.example;

import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class Server {

    //create clientSocket
    static Socket clientSocket = null;
    //initialize serverPort
    static int serverPort = 7777;

    public static void main(String args[]) {
        //create a genesis block
        Blockchain bc = new Blockchain();
        Block b = new Block(bc.getChainSize(), bc.getTime(), "Genesis", 2);
        bc.addBlock(b);
        bc.computeHashesPerSecond();

        //print at start of the code
        System.out.println("Blockchain server running");
        System.out.println("We have a visitor");

        try {
            // the server port we are using
            int serverPort = 7777;
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);
            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            Gson gson = new Gson();
            String result = "";

            while(true){
                //listen client socket
                clientSocket = listenSocket.accept();

                // If we get here, then we are now connected to a client.
                // Set up "in" to read from the client socket
                Scanner in = new Scanner(clientSocket.getInputStream());
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                /*
                 * Forever, read a line from the socket print it to the console echo it (i.e. write it) back to the client
                 */
                if (in.hasNext()) {
                    result = in.nextLine();
                    //get client request
                    RequestMessage requestMessage = gson.fromJson(result, RequestMessage.class);
                    //check the option selected by user through client request
                    int choice = requestMessage.getSelection();
                    //declare responseMessage
                    ResponseMessage responseMessage;
                    //after user choice
                    switch (choice) {
                        case 0:
                            //set the response to give to client
                            responseMessage = new ResponseMessage();
                            responseMessage.setSelection(0);
                            responseMessage.setChainSize(bc.getChainSize());
                            responseMessage.setDifficulty(bc.getLatestBlock().getDifficulty());
                            responseMessage.setTotalDifficulty(bc.getTotalDifficulty());
                            responseMessage.setHashesPerSecond(bc.getHashesPerSecond());
                            responseMessage.setTotalExpectedHashes(bc.getTotalExpectedHashes());
                            responseMessage.setNonce(bc.getLatestBlock().getNonce());
                            responseMessage.setChainHash(bc.getChainHash());
                            System.out.println("Response: " + responseMessage.toString());
                            //send response to client
                            reply(responseMessage);
                            break;
                        case 1:
                            System.out.println("Adding a block");
                            //perform the operations- add block
                            Block newBlock = new Block(bc.getChainSize(), bc.getTime(), requestMessage.getData(), requestMessage.getDifficulty());
                            long startTime = System.currentTimeMillis();
                            bc.addBlock(newBlock);
                            long endTime = System.currentTimeMillis();
                            //set response to send to client
                            System.out.println("Setting response to Total execution time to add this block was "+(endTime-startTime)+" milliseconds");
                            responseMessage = new ResponseMessage();
                            responseMessage.setSelection(1);
                            responseMessage.setResponse("Total execution time to add this block was "+(endTime-startTime)+" milliseconds");
                            System.out.println("..." + responseMessage.toString());
                            //send response to client
                            reply(responseMessage);
                            break;

                        case 2:
                            System.out.println("Verifying entire chain");
                            //perform the operations- chain verification
                            startTime = System.currentTimeMillis();
                            System.out.println("Chain verification: " + bc.isChainValid());
                            endTime = System.currentTimeMillis();
                            //set response to send to client
                            responseMessage = new ResponseMessage();
                            responseMessage.setSelection(2);
                            responseMessage.setResponse("Chain verification: " + bc.isChainValid()+"\nTotal execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                            System.out.println("Setting response to Total execution time required to verify the chain was " + (endTime - startTime) + " milliseconds");
                            //send response to client
                            reply(responseMessage);
                            break;

                        case 3:
                            System.out.println("View the Blockchain");
                            //set response to send to client
                            responseMessage = new ResponseMessage();
                            responseMessage.setSelection(3);
                            responseMessage.setResponse(bc.toString());
                            System.out.println("Setting response to "+bc.toString());
                            //send response to client
                            reply(responseMessage);
                            break;

                        case 4:
                            System.out.println("Corrupt the Blockchain");
                            //perform the operations- corrupt blockchain
                            Block corruptBlock = bc.blockchain.get(requestMessage.getId());
                            corruptBlock.setData(requestMessage.getCorrupt());
                            //set response to send to client
                            responseMessage = new ResponseMessage();
                            responseMessage.setSelection(4);
                            responseMessage.setResponse("Block " +requestMessage.getId()+ " now holds "+requestMessage.getCorrupt());
                            System.out.println("Setting response to Block " +requestMessage.getId()+ " now holds "+requestMessage.getCorrupt());
                            //send response to client
                            reply(responseMessage);
                            break;

                        case 5:
                            System.out.println("Repairing the entire chain");
                            //perform the operations- repair blockchain
                            startTime = System.currentTimeMillis();
                            bc.repairChain();
                            endTime = System.currentTimeMillis();
                            //set response to send to client
                            responseMessage = new ResponseMessage();
                            responseMessage.setSelection(5);
                            responseMessage.setResponse("Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
                            System.out.println("Setting response to Total execution time required to repair the chain was " + (endTime - startTime) + " milliseconds");
                            //send response to client
                            reply(responseMessage);
                            break;
                        case 6:
                            //exit
                            System.exit(0);
                        default:
                            break;
                    }
                }
                }

            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
            // If quitting (typically by you sending quit signal) clean up sockets
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

    //method to send response to client
    public static void reply(ResponseMessage responseString) throws IOException {
        //creates output stream using socket
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
        //stream request string in output
        out.println(responseString.toString().replace("\n",""));
        //flush
        out.flush();
    }
}
