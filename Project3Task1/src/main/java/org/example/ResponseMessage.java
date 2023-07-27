/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
package org.example;

import com.google.gson.Gson;
import java.math.BigInteger;

//server
public class ResponseMessage {

    //declare variables
    Integer selection;
    Integer chainSize;
    String chainHash;
    Integer difficulty;
    Integer totalDifficulty;
    Integer hashesPerSecond;
    Double totalExpectedHashes;
    BigInteger nonce;

    String response;

    //constructor
    public ResponseMessage() {
    }

    //ALL Getters and Setters of the variables
    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public int getTotalDifficulty() {
        return totalDifficulty;
    }

    public void setTotalDifficulty(int totalDifficulty) {
        this.totalDifficulty = totalDifficulty;
    }

    public int getHashesPerSecond() {
        return hashesPerSecond;
    }

    public void setHashesPerSecond(int hashesPerSecond) {
        this.hashesPerSecond = hashesPerSecond;
    }

    public double getTotalExpectedHashes() {
        return totalExpectedHashes;
    }

    public void setTotalExpectedHashes(double totalExpectedHashes) {
        this.totalExpectedHashes = totalExpectedHashes;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public String getChainHash() {
        return chainHash;
    }

    public void setChainHash(String chainHash) {
        this.chainHash = chainHash;
    }

    public int getChainSize() {
        return chainSize;
    }

    public void setChainSize(int chainSize) {
        this.chainSize = chainSize;
    }

    //convert String
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
