package com.example.Ex4;

import android.util.Log;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClient {

    private static final String TAG = "TcpClient";

    private boolean mRun;
    private String serverIP;
    private int serverPort;

    // used to send messages
    private PrintWriter mBufferOut;
    private Socket socket;

    public TcpClient() {
        this.mRun = false;
    }

    public void SetConnectionInfo(String ip, int port) {
        this.serverIP = ip;
        this.serverPort = port;
    }

    public boolean IsRunning() {
        return mRun;
    }

    // create a socket with the information currently set.
    public void Start() {
        mRun = true;

        try {
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            //create a socket to make the connection with the server
            Log.d("TCP Client", "C: Connecting...");
            socket = new Socket(serverAddr, serverPort);
            try {
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                Log.d(TAG, "connected successfully");
            } catch (Exception e) {
                Log.e(TAG, "failed to get print writer of socket", e);
            }
        } catch (Exception e) {
            Log.e(TAG, "failed to create a socket or parse ip", e);
        }

    }

    // Stop the client by closing the socket and flushing the buffer.
    public void Stop() {
        mRun = false;

        try {
            socket.close();
        } catch (Exception e) {
            Log.e(TAG, "failed closing the socket.");
        }

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mBufferOut = null;
    }

    // Sends a given message via client in another thread.
    public void SendMessage(final String message) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (mBufferOut != null) {
                    Log.d(TAG, "Sending: " + message);
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }
}
