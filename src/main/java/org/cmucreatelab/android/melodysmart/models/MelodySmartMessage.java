package org.cmucreatelab.android.melodysmart.models;

import android.util.Log;

import org.cmucreatelab.android.melodysmart.DeviceHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mike on 2/9/17.
 */

public class MelodySmartMessage {

    //private String request;
    private int numberOfAttemptedSends;
    private int expectedResponseSize;
    private ArrayList<String> requests,responses;

    // getters
    public ArrayList<String> getRequests() { return requests; }
    public ArrayList<String> getResponses() { return responses; }
    public int getNumberOfAttemptedSends() { return numberOfAttemptedSends; }
    public int getExpectedResponseSize() { return expectedResponseSize; }
    // setters
    public void setNumberOfAttemptedSends(int numberOfAttemptedSends) { this.numberOfAttemptedSends = numberOfAttemptedSends; }
    public void setExpectedResponseSize(int expectedResponseSize) { this.expectedResponseSize = expectedResponseSize; }


    public MelodySmartMessage() {
        this.requests = new ArrayList<>();
        this.responses = new ArrayList<>();
        this.numberOfAttemptedSends = 0;
        this.expectedResponseSize = 1;
    }


    public MelodySmartMessage(String request) {
        this();
        this.requests.add(request);
    }


    public MelodySmartMessage(List<String> requests) {
        this.requests = new ArrayList<>(requests);
        this.responses = new ArrayList<>();
        this.expectedResponseSize = 1;
    }


    public MelodySmartMessage(List<String> requests, int responseSize) {
        this(requests);
        this.expectedResponseSize = responseSize;
    }


    public boolean hasReceivedExpectedResponses() {
        if (expectedResponseSize == responses.size()) {
            return true;
        } else if (expectedResponseSize < responses.size()) {
            Log.w(DeviceHandler.LOG_TAG, "MelodySmartMessage.hasReceivedExpectedResponses: there are more responses than the expected response size!");
        }
        return false;
    }


    public void addResponse(String response) {
        responses.add(response);
    }

}
