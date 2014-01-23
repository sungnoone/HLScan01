package net.snoone.HLScan01;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.net.Uri;
import android.widget.*;

import android.view.View;

import java.io.*;
import java.io.FileOutputStream;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // 載入預設網址
        txtDefaultUrl = (TextView)findViewById(R.id.txtDefaultUrl);
        txtDefaultUrl.setText(readDefaultUrl());
    }

    public TextView textMsg;
    public TextView txtDefaultUrl;
    String filename = "myfile";//儲存網址位置檔案


    //啟動掃描
    public void onStartScanClick(View view){
        textMsg = (TextView)findViewById(R.id.txtMessage);
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        if(getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size()==0){
            //未裝Zxing
            textMsg.setText("未安裝zxing元件，請至Google Play商店，搜尋Zxing安裝");
        }else{
            //已裝Zxing
            intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
            startActivityForResult(intent, 0);
        }
    }

    //掃描結果
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        textMsg = (TextView)findViewById(R.id.txtMessage);
        if(requestCode==0){
            if(resultCode==RESULT_OK){
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Intent myBrowser;
                if(contents.startsWith("http://") || contents.startsWith("https://")){
                    //合法網址
                    myBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(contents));
                    startActivity(myBrowser);
                }else {
                    //不合法網址
                    //取得使用者預定義的網址
                    try{
                        //TextView textUrl = (TextView)findViewById(R.id.txtDefaultUrl);
                        String urlString = readDefaultUrl();
                        myBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString+contents));
                        startActivity(myBrowser);
                    }catch (Exception e){
                        textMsg.setText(e.toString());
                    }
                }
            }else if (resultCode==RESULT_CANCELED){
                //使用者中止
                textMsg.setText("中止!!");
            }
        }
    }

    //編輯預設網址　
    public void onEditDialogShow(View view){
        textMsg = (TextView)findViewById(R.id.txtMessage);
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("定義預設網址");
        dialog.setContentView(R.layout.editurl);
        dialog.show();
        final EditText ed = (EditText)dialog.findViewById(R.id.editurl);
        ed.setText(readDefaultUrl());
        final Button btnSave = (Button)dialog.findViewById(R.id.btnSave);
        final Button btnCancel = (Button)dialog.findViewById(R.id.btnCancel);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textMsg.setText("Save");
                writeDefaultUrl(ed.getText().toString());
                txtDefaultUrl.setText(readDefaultUrl());
                dialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //textMsg.setText("Cancel");
                textMsg.setText("編輯中止!!");
                dialog.dismiss();

            }
        });
    }

    //儲存預設網址
    public void writeDefaultUrl(String urlstr){
        textMsg = (TextView)findViewById(R.id.txtMessage);
        FileOutputStream outputStream;
        FileInputStream inputStream;
        try{
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(urlstr.getBytes());
            outputStream.close();
            textMsg.setText("儲存完畢\n");
        }catch (Exception e){
            textMsg.setText("儲存網址錯誤:"+e.toString());
        }
    }

    //讀取預設網址
    public String readDefaultUrl() {
        //String defaultUrl = null;
        textMsg = (TextView)findViewById(R.id.txtMessage);
        FileInputStream inputStream;
        try {
            inputStream= openFileInput(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine())!=null){
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        }catch (Exception e){
            textMsg.setText("讀取預設網址錯誤:"+e.toString());
            return "";
        }
    }
}
