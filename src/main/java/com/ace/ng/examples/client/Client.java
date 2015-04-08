package com.ace.ng.examples.client;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Administrator on 2014/6/9.
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket=new Socket("127.0.0.1",8001);
        new Thread(new ClientHandler(socket)).start();

    }
}
