package vamz.kst.fri.serverapp;

import android.app.Service;
import android.content.Intent;
import android.os.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SourceService extends Service {


    private Messenger _messenger = new Messenger(new SourceServiceMessageHandler());
    private Messenger _messengerToActivity = null;

    private List<String> _secrets = new ArrayList<String>();



    public SourceService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return _messenger.getBinder();
    }


    private class SourceServiceMessageHandler extends Handler {

        private void sleep(long time){
            try {
                Thread.sleep(time);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void send(Message msg){
            try {
                _messengerToActivity.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleMessage(Message msg){

            //create answer delay
            sleep(1000);

            switch (msg.what){

                case MessageStates.MSG_CONNECTION_DISCONNECT:
                    send(Message.obtain(null, MessageStates.MSG_CONNECTION_DISCONNECT));
                    _messengerToActivity = null;
                    break;

                case MessageStates.MSG_CREATE_CONNECTION:
                    _messengerToActivity = (Messenger) msg.obj; // predanie priamo objektu bez pouzitia parcelable!
                    send(Message.obtain(null, MessageStates.MSG_CONNECTION_OK));
                    break;

                case MessageStates.MSG_DATA_PUSH:
                    Bundle b = (Bundle) msg.obj;
                    _secrets.add(b.getString("key"));
                    send(Message.obtain(null, MessageStates.MSG_DATA_Ok, msg.what, 0));
                    break;

                case MessageStates.MSG_DATA_SEARCH:

                    Bundle bs = (Bundle) msg.obj;
                    String needle = bs.getString("key").toLowerCase();

                    String found = null;

                    for(Iterator<String> i = _secrets.iterator(); i.hasNext(); ) {
                        String item = i.next();
                        if (item.toLowerCase().contains(needle)){
                            found = item;
                            break; //find first
                        }
                    }
                    if (found != null){
                        Bundle f = new Bundle();
                        f.putString("key", found);
                        send(Message.obtain(null, MessageStates.MSG_DATA_Ok, msg.what, 0, f));
                    } else{
                        send(Message.obtain(null, MessageStates.MSG_DATA_NOT_FOUND, msg.what, 0));
                    }

                    break;

                case MessageStates.MSG_DATA_COUNT:
                    send(Message.obtain(null, MessageStates.MSG_DATA_Ok, msg.what, _secrets.size()));
                    break;

                default:
                    super.handleMessage(msg);

            }
        }
    }
}
