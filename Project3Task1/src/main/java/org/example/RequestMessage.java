/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
package org.example;

import com.google.gson.Gson;

//client
public class RequestMessage {

    //declare variables
    Integer selection;
    String data;
    Integer difficulty;
    String corrupt;
    Integer id;

    //constructor
    public RequestMessage() {
    }

    //ALL Getters and Setters of the variable
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public String getCorrupt() {
        return corrupt;
    }

    public void setCorrupt(String corrupt) {
        this.corrupt = corrupt;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
