/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
package org.example;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import com.google.gson.Gson;

public class Block {

    //declare variables
    int index;
    Timestamp timestamp;
    String data;
    int difficulty;
    BigInteger nonce;
    String previousHash;

    //constructor
    public Block(int index, Timestamp timestamp, String data, int difficulty) {
        this.index = index;
        this.timestamp = timestamp;
        this.data = data;
        this.difficulty = difficulty;
    }

    //simple index getter method
    public int getIndex() {
        return index;
    }

    //simple index setter method
    public void setIndex(int index) {
        this.index = index;
    }

    //simple timestamp getter method
    public Timestamp getTimestamp() {
        return timestamp;
    }

    //simple timestamp setter method
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    //simple data getter method
    public String getData() {
        return data;
    }

    //simple data setter method
    public void setData(String data) {
        this.data = data;
    }

    //simple difficulty getter method
    public int getDifficulty() {
        return difficulty;
    }
    //simple difficulty setter method
    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    //simple nonce getter method
    public BigInteger getNonce() {
        return nonce;
    }

    //simple nonce setter method
    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    //simple previous hash getter method
    public String getPreviousHash() {
        return previousHash;
    }

    //simple previous hash setter method
    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    //This method computes a hash of the concatenation of the index, timestamp, data, previousHash, nonce, and difficulty.
    public String calculateHash() {
        //concatenate the input
        String input = index + timestamp.toString() + data + previousHash + nonce + difficulty;
        String hash = "";

        try {
            //choose algorithm
            String hashmethod = "SHA-256";
            //implementation of hashing
            MessageDigest md = MessageDigest.getInstance(hashmethod);
            //update messagedigest
            md.update(input.getBytes());
            //stores in byte array
            byte[] hashvalue = md.digest();
            //used to print hash value
            hash = bytesToHex(hashvalue);

        } catch (NoSuchAlgorithmException e) {
            //if hashing is not available
            System.out.println("No hashing available" + e);
            e.printStackTrace();
        }

        return hash;
    }

    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    //used this code from LAB 1
    public static String bytesToHex(byte[] bytes) {

        char[] hexChars = new char[bytes.length * 2];

        for (int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 15];
        }

        return new String(hexChars);
    }

    //The proof of work methods finds a good hash. It increments the nonce until it produces a good hash.
    public String proofOfWork(){
        String hash = "";
        //initializes nonce to 0
        nonce = BigInteger.valueOf(0);
        //leading zero pattern
        String leadingzero = "0".repeat(difficulty);
        //checks if hash starts with given number of leading zeros
        while(!hash.startsWith(leadingzero)){
            //increment nonce by 1
            nonce = nonce.add(BigInteger.valueOf(1));
            //set hash by calling calculate hash method
            hash = calculateHash();
        }
        //returns good hash
        return hash;
    }

    //overrides toString method of Java
    @Override
    public String toString() {
        //set previous hash
        Block b = new Block (index, timestamp, data, difficulty);
        b.nonce = nonce;
        b.setPreviousHash(previousHash);
        //used this code from prereq of Project 3
        Gson gson = new Gson();
        String messageToSend = gson.toJson(b);
        return messageToSend;

    }
}
