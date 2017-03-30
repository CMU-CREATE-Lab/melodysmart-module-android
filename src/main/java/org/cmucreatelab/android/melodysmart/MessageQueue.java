package org.cmucreatelab.android.melodysmart;

import android.util.Log;

import com.bluecreation.melodysmart.DataService;

import org.cmucreatelab.android.melodysmart.models.MelodySmartMessage;
import org.cmucreatelab.android.melodysmart.models.Timer;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by mike on 12/28/16.
 *
 * Uses MelodySmart's DataService to send messages. Messages are handled FIFO with a slight delay between sending.
 */
public class MessageQueue<T extends MelodySmartMessage> {

    private ConcurrentLinkedQueue<T> messages;
    private T currentMessage = null; // the current message that is presumably being processed by DataService
    private boolean isWaitingForResponse = false;
    private Timer messageSendingTimer;
    private Timer messageTimeout;

    private static final int MESSAGE_SENDING_TIMER_DELAY_IN_MILLISECONDS = 100;
    // if we do not receive a response within this time, recover
    private static final int MESSAGE_TIMEOUT_WAIT_IN_MILLISECONDS = 3000;


    public MessageQueue(final DeviceHandler deviceHandler) {
        messages = new ConcurrentLinkedQueue<>();
        final DataService dataService = deviceHandler.getDataService();

        messageTimeout = new Timer(MESSAGE_TIMEOUT_WAIT_IN_MILLISECONDS) {
            @Override
            public void timerExpires() {
                if (currentMessage != null) {
                    if (currentMessage.getNumberOfAttemptedSends() <= 1) {
                        Log.e(DeviceHandler.LOG_TAG, "messageTimeout timerExpires; attempting to resend request");
                        messageTimeout.startTimer();

                        // clear previous responses (if any)
                        currentMessage.getResponses().clear();
                        // resend the message
                        for (String request: currentMessage.getRequests()) {
                            dataService.send(request.getBytes());
                        }

                        currentMessage.setNumberOfAttemptedSends(currentMessage.getNumberOfAttemptedSends() + 1);
                    } else {
                        Log.e(DeviceHandler.LOG_TAG,"messageTimeout timerExpires after multiple send attempts; will not process request");
                        if (deviceHandler.disconnectsOnFailedMessage()) {
                            Log.e(DeviceHandler.LOG_TAG,"messageTimeout disconnectsOnFailedMessage");
                            deviceHandler.disconnect();
                        } else {
                            Log.i(DeviceHandler.LOG_TAG,"messageTimeout sending next message");
                            isWaitingForResponse = false;
                            sendNextMessage();
                        }
                    }
                }
            }
        };
        messageSendingTimer = new Timer(MESSAGE_SENDING_TIMER_DELAY_IN_MILLISECONDS) {
            @Override
            public void timerExpires() {
                if (!messages.isEmpty()) {
                    isWaitingForResponse = true;
                    messageTimeout.startTimer();
                    currentMessage = messages.poll();

                    for (String request: currentMessage.getRequests()) {
                        Log.v(DeviceHandler.LOG_TAG,"messageSendingTimer timerExpires: SEND: '"+request+"'");
                        dataService.send(request.getBytes());
                    }

                    currentMessage.setNumberOfAttemptedSends(currentMessage.getNumberOfAttemptedSends() + 1);
                }
            }
        };
    }


    /**
     * Call to notify the MessageQueue that the message response was received.
     *
     * @return the requested FlutterMessage associated with message response.
     */
    public synchronized T notifyMessageReceived(String response) {
        isWaitingForResponse = false;
        messageTimeout.stopTimer();
        T result = this.currentMessage;
        if (this.currentMessage != null) {
            result.addResponse(response);
            // Only send the next message if we have reached the expected response size
            if (result.hasReceivedExpectedResponses()) {
                this.currentMessage = null;
                sendNextMessage();
            } else {
                messageTimeout.startTimer();
            }
        }
        return result;
    }


    // message-sending


    void addMessage(T message) {
        messages.add(message);
        sendNextMessage();
    }


    /**
     * Clears the MessageQueue of all outstanding jobs.
     */
    void clear() {
        Log.v(DeviceHandler.LOG_TAG,"Clearing "+messages.size()+" messages in MessageQueue...");
        messageSendingTimer.stopTimer();
        messages.clear();
        isWaitingForResponse = false;
        currentMessage = null;
        sendNextMessage();
    }


    private void sendNextMessage() {
        if (isWaitingForResponse) {
            Log.w(DeviceHandler.LOG_TAG,"refusing to sendNextMessage since messageQueue.isWaitingForResponse == true");
            return;
        }
        messageSendingTimer.startTimer();
    }

}
