package iii.ideas.emotionunittester;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

//import com.iii.more.EmotionHandler;
//import com.iii.more.cmp.CMPHandler;
//import com.iii.more.cmp.semantic.SemanticWordCMPHandler;
//import com.iii.more.cmp.semantic.SemanticWordCMPParameters;

import org.json.JSONObject;

import java.util.HashMap;

import sdk.ideas.common.Logs;
import com.iii.more.EmotionHandler;

public class MainActivity extends AppCompatActivity
{
    private EmotionHandler mEmotionHandler = null;
    
    //Jugo server connect
    //private SemanticWordCMPHandler mSemanticWordCMPHandler = null;
    
    private Button mButton = null;
    
    private static final String send_word = "我要聽白雪公主的故事";
    
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            HashMap<String, String> data = (HashMap<String, String>) msg.obj;
            Logs.showTrace("what: " + String.valueOf(msg.what) + " result: " + String.valueOf(msg.arg1) +
                    "" + " from: " + String.valueOf(msg.arg2));
            Logs.showTrace("data: " + data);
            //which module == msg.what == 4258 ==> emotion module
            if (msg.what == 4258)
            {
                //result == msg.arg1 == 1 => ok no error
                
                //from == msg.arg2 == 0 => from emotion detect function, data in this same time
                // HashMap<String, String> = msg.obj =  {DISGUST=xx, ENGAGEMENT=xx, ANGER=xx, VALENCE=xx, CONTEMPT=xx,
                // SADNESS=xx, JOY=xx, FEAR=xx, ATTENTION=xx, SURPRISE=xx}
                //the value of emotions equal -1 is mean no faces detected and no value output
                
                //from == msg.arg2 == 1 => from emotion camera function, data in this same time
                // HashMap <String, String> = msg.obj = {message=detect no face} or {message=detect face}
            }
            //which module == msg.what == 9424 ==> Semantic Word module
            else if (msg.what == 9424)
            {
                //result == msg.arg1 == 1 => ok no error
                if (msg.arg1 == 1)
                {
                    HashMap<String, String> message = (HashMap<String, String>) msg.obj;
                    if (message.containsKey("message"))
                    {
                        try
                        {
                            JSONObject responseData = new JSONObject(message.get("message"));
                            if (responseData.has("activity"))
                            {
                                Logs.showTrace("[MainActivity] activity Data:" + responseData.getJSONObject
                                        ("activity").toString());
                                if (responseData.getJSONObject("activity").length() != 0)
                                {
                                    //you can print this part or reference document and get what you want
                                }
                                else
                                {
                                    Logs.showError("[MainActivity] No Activity Data!!");
                                }
                                
                            }
                            else
                            {
                                Logs.showError("[MainActivity] No Activity Data!!");
                            }
                            
                        }
                        catch (Exception e)
                        {
                            Logs.showError("[MainActivity] ERROR" + e.toString());
                        }
                    }
                }
                //result == msg.arg1 != 1 => some error occur,usually io exception
                else
                {
                    Logs.showError("[MainActivity] ERROR while sending message to CMP Controller");
                    
                }
            }
            
        }
    };
    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager
                .PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA))
            {
                finish();
            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1010);
            }
        }
        else
        {
            // Permission has already been granted
            init();
        }
        
    }
    
    //to init module
    public void init()
    {
        mEmotionHandler = new EmotionHandler(MainActivity.this);
        mEmotionHandler.setHandler(mHandler);
        mEmotionHandler.init();
        mEmotionHandler.start();
        
       // CMPHandler.setIPAndPort("54.199.198.94", 2310);
       // mSemanticWordCMPHandler = new SemanticWordCMPHandler(this);
       // mSemanticWordCMPHandler.setHandler(mHandler);
        
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
               // mSemanticWordCMPHandler.sendSemanticWordCommand(SemanticWordCMPParameters.getWordID(),
               //         SemanticWordCMPParameters.TYPE_REQUEST_STORY, send_word);
            }
        });
    }
    
    
    @Override
    public void onDestroy()
    {
        mEmotionHandler.stop();
        super.onDestroy();
    }
    
    
    
    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode)
        {
            case 1010:
            {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    init();
                }
                else
                {
                    finish();
                }
            }
            break;
        }
    }
}
