package com.xiaopeng.huamidemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";

    private TextView tvResult;
    private Button btnSend1;
    private Button btnSend2;
    private String content1 = "测试1234abcdABCD";
    private String content2 = "测试json";
    private EditText edIp;
    private static final int PORT_NUM = 1024;
    private Button btnServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        startServerThread();

        btnSend1 = (Button) findViewById(R.id.btn_send1);
        btnSend2 = (Button) findViewById(R.id.btn_send2);
        btnServer = (Button) findViewById(R.id.btn_server);
        tvResult = (TextView) findViewById(R.id.tv_result);
        edIp = (EditText) findViewById(R.id.ed_ip);
        btnSend1.setOnClickListener(this);
        btnSend2.setOnClickListener(this);
        btnServer.setOnClickListener(this);

    }

    private void sendMsg(String inputContent) {
        //                String inputContent = input.getText().toString();
//        String inputContent = null;
        tvResult.append("client:\n" + inputContent + "\n");
        //启动线程 向服务器发送和接收信息
        new MyThread(inputContent).start();
    }


    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                tvResult.append("server:\n" + msg.obj + "\n");
            }else if(msg.what == 2){
                Toast.makeText(MainActivity.this,"服务器收到了",Toast.LENGTH_LONG).show();
                tvResult.append("server:\n" + msg.obj + "\n");
            }
        }

    };

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_send1) {
            sendMsg("测试按钮1  发送:" + content1);
        } else if (v.getId() == R.id.btn_send2) {
            CommonRequestBean bean = new CommonRequestBean();
            bean.setMsg_id("1343506507657");
            CommonRequestBean.MsgContentBean contentBean = new CommonRequestBean.MsgContentBean();
            contentBean.setCmd_type(2);
            contentBean.setCmd_value(80);
            bean.setMsg_content(contentBean);
            bean.setMsg_type(2);
            bean.setService_type(4);
            bean.setTarget_id(20015);
            content2 = new Gson().toJson(bean);
            sendMsg("测试按钮2 发送:" + content2);
        }else if(v.getId() == R.id.btn_server){
            startServerThread();
            Toast.makeText(MainActivity.this,"请在输入框中输入本机的ip和端口",Toast.LENGTH_LONG).show();
        }
    }

    class MyThread extends Thread {

        public String content;

        public MyThread(String str) {
            content = str;
        }

        @Override
        public void run() {
            //定义消息
            Message msg = null;
            try {
                //连接服务器 填写对方服务器的地址 并设置连接超时为5秒
                Socket socket = new Socket();
                //http://10.192.148.71/

                //晓波手机
//                String ipString = "10.192.169.133";
//                String ipString = "10.192.167.7";

                //华为手机
                String ipString = "192.168.43.131";
                int port = PORT_NUM;
                if (!TextUtils.isEmpty(edIp.getText())) {
                    String[] split = edIp.getText().toString().split(":");
                    if (split != null) {
                        ipString = split[0];
                        port = Integer.valueOf(split[1]);
                    }
                }
                msg = myHandler.obtainMessage(1);
                msg.obj = "准备连接"+ipString+":"+port;
                myHandler.sendMessage(msg);
                socket.connect(new InetSocketAddress(ipString, port), 5000);

                //获取输入输出流
                OutputStream ou = socket.getOutputStream();
                //获取输出输出流
                BufferedReader bff = new BufferedReader(new InputStreamReader(
                        socket.getInputStream()));
                //向服务器发送信息
                ou.write(content.getBytes("utf-8"));
                ou.flush();

                //读取发来服务器信息
                String result = "";
                String buffer = "";
                while ((buffer = bff.readLine()) != null) {
                    result = result + buffer;
                }
                msg = myHandler.obtainMessage(1);
                msg.obj = result.toString();
                //发送消息 修改UI线程中的组件
                myHandler.sendMessage(msg);
                //关闭各种输入输出流
                bff.close();
                ou.close();
                socket.close();
            } catch (SocketTimeoutException aa) {
                //连接超时 在UI界面显示消息
                msg = myHandler.obtainMessage(1);
                msg.obj = "服务器连接超时！请检查网络是否打开 SocketTimeoutException";
                myHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                msg = myHandler.obtainMessage(1);
                msg.obj = "服务器连接失败！请检查网络是否打开 IOException";
                myHandler.sendMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
                msg = myHandler.obtainMessage(1);
                msg.obj = "未知错误！";
                myHandler.sendMessage(msg);
            }
        }
    }


    private void startServerThread(){
        new Thread() {
            public void run() {
                OutputStream output;
                String serverContent = "接受到了！！！";
                try {
                    ServerSocket serverSocket = new ServerSocket(PORT_NUM);
                    while (true) {
                        Message msg = new Message();
                        msg.what = 2;
                        try {
                            Socket socket = serverSocket.accept();
                            //向client发送消息
                            output = socket.getOutputStream();
                            output.write(serverContent.getBytes("utf-8"));
                            output.flush();
                            socket.shutdownOutput();

                            //获取输入信息
                            BufferedReader bff = new BufferedReader(new InputStreamReader

                                    (socket.getInputStream()));
                            //读取信息
                            String result = "";
                            String buffer = "";
                            while ((buffer = bff.readLine()) != null) {
                                result = result + buffer;
                            }
                            msg.obj = result.toString();
                            myHandler.sendMessage(msg);
                            bff.close();
                            output.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }.start();
    }
}
