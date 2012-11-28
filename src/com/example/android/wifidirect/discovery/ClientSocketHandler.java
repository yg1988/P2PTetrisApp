
package com.example.android.wifidirect.discovery;

import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocketHandler extends Thread {

    private static final String TAG = "ClientSocketHandler";
    private Handler handler;
    private NetworkManager TetrisGame;
    private InetAddress mAddress;

    public ClientSocketHandler(Handler handler, InetAddress groupOwnerAddress) {
        this.handler = handler;
        this.mAddress = groupOwnerAddress;
    }

    @Override
    public void run() {
        Socket socket = new Socket();
        try {
            socket.bind(null);
            socket.connect(new InetSocketAddress(mAddress.getHostAddress(),
                    WiFiServiceDiscoveryActivity.SERVER_PORT), 5000);
            Log.d(TAG, "Launching the I/O handler");
            
            
            /*TetrisGame here is created by the clientsocket handler and latter published*/
            TetrisGame = new NetworkManager(socket, handler);
            new Thread(TetrisGame).start();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return;
        }
    }
//we don't know if this is a good practice thread-safe way to publish a object
    public NetworkManager getChat() {
        return TetrisGame;
    }

}
