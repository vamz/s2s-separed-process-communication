package vamz.kst.fri.serverapp;

/**
 * Created by matko on 18.3.2016.
 */
public class MessageStates {

    public final static int
            MSG_CONNECTION_OK = 0,
            MSG_CONNECTION_DISCONNECT = 1,

    MSG_DATA_PUSH = 1000,
            MSG_DATA_SEARCH = 1001,
            MSG_DATA_NOT_FOUND = 1002,
            MSG_DATA_Ok= 1003,
            MSG_DATA_COUNT = 1004,

    MSG_CREATE_CONNECTION = 100,
            MSG_DISCONNECT = 101
                    ;
}
