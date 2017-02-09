package org.cmucreatelab.android.melodysmart.models;

/**
 * Created by mike on 2/9/17.
 */

public class MelodySmartMessage {

    private String request;
    private int numberOfAttemptedSends = 0;

    // getters
    public String getRequest() { return request; }
    public int getNumberOfAttemptedSends() { return numberOfAttemptedSends; }
    // setters
    public void setNumberOfAttemptedSends(int numberOfAttemptedSends) { this.numberOfAttemptedSends = numberOfAttemptedSends; }


    public MelodySmartMessage(String request) {
        this.request = request;
    }

}
