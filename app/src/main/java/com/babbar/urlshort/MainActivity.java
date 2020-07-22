package com.babbar.urlshort;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    Button btn,btncopy;
    EditText eturl;
    TextView tvshow;
    ProgressBar pb;


    public  class DownloadTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... link) {


            String result="";
            URL url;
            HttpURLConnection urlConnection=null;
            //Toast.makeText(getApplicationContext(),"Converting!!!",Toast.LENGTH_SHORT).show();

            try{


                url=new URL(link[0]);

                urlConnection= (HttpURLConnection) url.openConnection();
                InputStream io= urlConnection.getInputStream();
                InputStreamReader reader= new InputStreamReader(io);
                int data=reader.read();
                while (data!=-1)
                {
                    char current = (char)data;
                    result+=current;
                    data=reader.read();
                }



                return result;

            }
            catch (Exception e)
            {
                e.printStackTrace();
                return "failed";
            }


           // return "done";
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn=findViewById(R.id.btn);
        btncopy=findViewById(R.id.btncpy);
        eturl=findViewById(R.id.eturl);
        tvshow=findViewById(R.id.tvshow);
        pb=findViewById(R.id.pb);
         btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  ans= null;
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    connected = true;
                }
                else
                    connected = false;
                if(connected==false)
                    Toast.makeText(getApplicationContext(),"Make sure internet is connected",Toast.LENGTH_SHORT).show();

                DownloadTask task= new DownloadTask();


                try {

                   // pb.setVisibility(View.VISIBLE);
                    ans = task.execute("https://ulvis.net/api.php?url="+eturl.getText().toString()+"&custom&private=1").get();
                    tvshow.setText(ans);
                    Toast.makeText(getApplicationContext(),"Shortened!!!",Toast.LENGTH_SHORT).show();
                } catch (ExecutionException e) {
                    // Log.i("FAILED","t");
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.i("result",ans);
                //pb.setVisibility(View.INVISIBLE);

            }
        });

         btncopy.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                 ClipData clipData = ClipData.newPlainText("text", tvshow.getText());
                 manager.setPrimaryClip(clipData);
                 Toast.makeText(getApplicationContext(),"Copied to Clipboard!",Toast.LENGTH_SHORT).show();

             }
         });


    }

}