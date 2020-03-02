package com.example.whenshecomes;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import static java.lang.Thread.sleep;

public class Connection {
    int HEADER_SIZE = 32;
    String ip;
    int port;
    Socket socket;
    ServerSocket serverSocket;
    OutputStreamWriter writer;
    DataInputStream receiver;

    boolean isConnected = false;
    TextView statusTv;
    Activity activity;
    Button recButton;
    public Connection(String ip, int port, Activity main){
        this.ip = ip;
        this.port = port;
        this.activity = main;
        statusTv = activity.findViewById(R.id.statusTv);
        recButton = activity.findViewById(R.id.reconnectButton);
    }


    public void listen(){
        Thread thread = new Thread () {
            @Override
            public void run()
            {
                while(true){
                    try {
                        serverSocket = new ServerSocket(1338);
                        Socket s = null;
                        s = serverSocket.accept();
                        DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                        byte[] bytes = new byte[6];
                        in.read(bytes);
                        statusTv.setText(in.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        };
        thread.start();
    }

    public void estabilish(){

        Thread thread  = new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    writer = new OutputStreamWriter(socket.getOutputStream());
                    isConnected = true;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusTv.setText("CONNECTED");

                            listen();
                            recButton.setVisibility(View.GONE);
                            statusTv.setVisibility(View.VISIBLE);
                        }
                    });


                } catch (final IOException e) {
                    isConnected = false;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            statusTv.setText("NO CONNECTION");
                            recButton.setVisibility(View.VISIBLE);
                            statusTv.setVisibility(View.GONE);
                        }
                    });

                    //recButton.setVisibility(View.VISIBLE);
                }
            }
            };
        thread.start();
    }


    public void check(){
        send();
    }


    private String createHeader(String func, String msg){
        String ml = ""+msg.length();
        //statusTv.setText(ml);
        String header = ml+"|"+func;
        while(header.length() != HEADER_SIZE){
            header += " ";
        }
        return header;
    }

    public void send(final String func, final String args){

        Thread thread  = new Thread() {
            @Override
            public void run() {
                if(!isConnected){return;}
                String message = createHeader(func, args) + args;
                try {
                   //statusTv.setText("SENT " + message);
                    writer.write(message);
                    writer.flush();

                } catch (IOException e) {
                    isConnected = false;
                    estabilish();
                }

            }

    };
    thread.start();

    }

    public void send(final String func){

        Thread thread  = new Thread() {
            @Override
            public void run() {
                if(!isConnected){return;}
                String message = "0|"+func;
                try {
                    writer.write(message);
                    writer.flush();

                } catch (IOException e) {
                    isConnected = false;
                    estabilish();
                }

            }

        };
        thread.start();

    }

    public void send(){

        Thread thread  = new Thread() {
            @Override
            public void run() {
                if(!isConnected){return;}
                String message = "";
                try {
                    //statusTv.setText("SENT " + message);
                    writer.write(message);
                    writer.flush();

                } catch (IOException e) {
                    isConnected = false;
                    estabilish();
                }

            }

        };
        thread.start();

    }
}
