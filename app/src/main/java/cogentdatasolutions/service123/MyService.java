package cogentdatasolutions.service123;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;

public class MyService extends Service {

    private static final String TAG = MyService.class.getSimpleName();
    private BroadcastReceiver receiver;
    private String msg_Body,msg_from;
    private String msg;
    public static final String URL_HIT = "http://10.80.15.119:8080/SpringServiceSample/rest/mca";
    public Pattern p = Pattern.compile("(|^)\\d{6}");
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        _startService();
    }

    private void _startService() {
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arr0, Intent arr1) {
                processReceive(arr0,arr1);

            }
        };

        registerReceiver(receiver,filter);
    }
     public void processReceive(Context context,Intent intent){
         Toast.makeText(context, "Received", Toast.LENGTH_SHORT).show();
         Bundle bundle = intent.getExtras();
         Object[] pdus = (Object[])bundle.get("pdus");
         SmsMessage[] msgs = new SmsMessage[pdus.length];

         for (int i =0;i<msgs.length;i++){
             msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
             msg_from = msgs[i].getOriginatingAddress();
             msg_Body = msgs[i].getMessageBody();
//             System.out.println(msg_Body);
         }

         Log.e(TAG, "processReceive: "+msg_Body );

         if (msg_Body!=null){
             Matcher m = p.matcher(msg_Body);
             if (m.find()){
                 RequestParams params = new RequestParams();
                 params.put("OTP",msg);
                 AsyncHttpClient client = new AsyncHttpClient();
                 client.get(URL_HIT, params, new AsyncHttpResponseHandler() {
                     @Override
                     public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                         Toast.makeText(MyService.this, "Success", Toast.LENGTH_SHORT).show();

                     }

                     @Override
                     public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                         Toast.makeText(MyService.this, "Failed", Toast.LENGTH_SHORT).show();

                     }
                 });

             }
         }
     }
}
