package vamz.kst.fri.clientapp;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Messenger _toServiceMessenger;
    private Handler _activityHandler = new ClientAppMessageHandler();
    private Messenger _activityMessenger = new Messenger(_activityHandler);

    private boolean _isServiceConnected = false;

    private Button _connectionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _connectionButton = (Button) findViewById(R.id.button_connection);
    }

    public void hook_service(View v){
        connectToService();
    }

    private void send(Message message){

        if (_toServiceMessenger == null){
            makeToast("Najskor sa pripoj na sluzbu");
            return;
        }

        try {
            _toServiceMessenger.send(Message.obtain(message));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    private void connectToService(){

        if (!_isServiceConnected) {
            //pripojenie ke sluzbe
            _connectionButton.setText("Try to connecting to service...");

            // cez plny nazov triedy ako znenie intentu
            String targetNamespace = "vamz.kst.fri.serverapp";
            Intent mIntent = new Intent(targetNamespace+".SourceService");
            mIntent.setPackage(targetNamespace);

            //alebo cez vlastné znenie intentu
//        Intent mIntent = new Intent("superTajny.Intent.One");
//        mIntent.setClassName(targetNamespace,targetNamespace+".SourceService");

            if (bindService(mIntent, new CustomServiceConnection(), Context.BIND_AUTO_CREATE)){
                _connectionButton.setText("Connect to service, again...");
            }
        } else {
            //odpojenie ak je pripojena

            _connectionButton.setText("Try disconnect service, again...");
            send(Message.obtain(null, ObjectContainer.MSG_CONNECTION_DISCONNECT));
        }
    }

    public void bt_search(View v){
        EditText ed = (EditText) findViewById(R.id.editText_search);
        Bundle b = new Bundle();
        b.putString("key", ed.getText().toString());

        send(Message.obtain(null, ObjectContainer.MSG_DATA_SEARCH, b ));
    }

    public void bt_refreshStats(View v){
        refreshCount();
    }

    public void bt_add(View v){
        EditText ed = (EditText) findViewById(R.id.editText);

        Bundle b = new Bundle();
        b.putString("key", ed.getText().toString());

        send(Message.obtain(null, ObjectContainer.MSG_DATA_PUSH, b ));
    }

    public void makeToast(String message){
        Toast.makeText(getApplicationContext(), "Data boli ulozene", Toast.LENGTH_SHORT).show();
        Log.i("tmsg", message);
    }


    private void refreshCount(){
        send(Message.obtain(null, ObjectContainer.MSG_DATA_COUNT));
    }

    public class ClientAppMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg){

            switch (msg.what){

                case ObjectContainer.MSG_CONNECTION_OK:
                    _isServiceConnected = true;
                    _connectionButton.setText("Disconnect from service");
                    break;
                case ObjectContainer.MSG_CONNECTION_DISCONNECT:
                    _isServiceConnected = false;
                    _connectionButton.setText("Connect to service");
                    break;

                case ObjectContainer.MSG_DATA_Ok:
                    switch (msg.arg1) {
                        case ObjectContainer.MSG_DATA_PUSH:
                            Toast.makeText(getApplicationContext(), "Data boli ulozene", Toast.LENGTH_SHORT).show();
                            refreshCount();
                            break;
                        case ObjectContainer.MSG_DATA_SEARCH:
                            TextView ed = (TextView) findViewById(R.id.text_result);
                            Bundle bs = (Bundle) msg.obj;
                            ed.setText(bs.getString("key"));
                            break;
                        case ObjectContainer.MSG_DATA_COUNT:
                            TextView st = (TextView) findViewById(R.id.textView_stats);
                            st.setText("Pocet dat v ulozenych v sluzbe: " + msg.arg2);
                            break;
                    }
                    break;

                case ObjectContainer.MSG_DATA_NOT_FOUND:
                    Toast.makeText(getApplicationContext(), "Data neboli najdene", Toast.LENGTH_SHORT).show();
                    break;

                default:
                    super.handleMessage(msg);

            }
        }
    }

    public class CustomServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            _toServiceMessenger = new Messenger(service);
            //odoslanie messengera pre komunikaciu sluzba > aktivita
            send(Message.obtain(null, ObjectContainer.MSG_CREATE_CONNECTION, _activityMessenger));
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            _toServiceMessenger = null;
        }
    }
}
