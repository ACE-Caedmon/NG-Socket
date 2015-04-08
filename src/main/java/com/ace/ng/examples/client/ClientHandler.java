package com.ace.ng.examples.client;

import com.ace.ng.codec.ByteCustomBuf;
import com.ace.ng.codec.CustomBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by Administrator on 2014/6/9.
 */
public class ClientHandler implements  Runnable{
    private Socket socket;
    public ClientHandler(Socket socket){
        this.socket=socket;
    }
    @Override
    public void run() {

        try {
            InputStream in=socket.getInputStream();
            OutputStream out=socket.getOutputStream();
            BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
            out.write(writeMessage(reader.readLine()));
            byte[] dst=new byte[1024];
            out.flush();
            int len=0;
            ByteBuf buffer=Unpooled.buffer();
            CustomBuf buf=new ByteCustomBuf(buffer);
            len=in.read(dst);
            buffer.writeBytes(dst,0,len);
            String serverMessage=readMessage(buf);
            System.out.println("Client reviced:"+serverMessage);
            socket.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    private String readMessage(CustomBuf buf){
        short length=buf.readShort();//包长
        byte code=buf.readByte();//错误码
        boolean isEncrypt=buf.readBoolean();//是否加密
        byte offset=buf.readByte();//密码索引,不加密无用
        short cmd=buf.readShort();//指令
        String content=buf.readString();//内容
        return  content;

    }
    private byte[] writeMessage(String content){
        int contentLength=content.getBytes(Charset.forName("UTF-8")).length;
        short cmd=1;// 2个字节
        boolean encrypted=false;// 1个字节
        byte offset=0;//密码索引，1个字节,不加密的话传0就可以
        int increment=0;//自增ID 4个字节
        ByteBuf buffer=Unpooled.buffer();
        buffer.writeShort((short)(contentLength+2+1+1+4+2));
        CustomBuf buf=new ByteCustomBuf(buffer);

        buf.writeBoolean(encrypted);
        buf.writeByte(offset);
        buf.writeInt(increment);
        buf.writeShort(cmd);
        buf.writeString(content);
        byte[] result=new byte[buffer.readableBytes()];
        buffer.readBytes(result);
        return result;
    }
}
