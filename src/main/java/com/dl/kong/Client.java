package com.dl.kong;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class Client {

    private SocketChannel sc;
    private Selector selector;

    public Client(){
        try {
            sc = SocketChannel.open();
            sc.connect(new InetSocketAddress("127.0.0.1",8888));
            sc.configureBlocking(false);
            selector  = Selector.open();
            sc.register(selector, SelectionKey.OP_WRITE, ByteBuffer.allocate(1024));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start(){
        try {
            while (selector.select() > 0) {
                Iterator<SelectionKey> it = selector.selectedKeys().iterator();
                while (it.hasNext()){
                    SelectionKey sk = it.next();
                    it.remove();
                    if(sk.isWritable()){
                        Scanner scanner = new Scanner(System.in);
                        String input = new String();
                        while (input.isEmpty()){
                            System.out.print(">>");
                            input = scanner.nextLine();
                        }
                        sc.write(ByteBuffer.wrap(input.getBytes()));
//                        sk.interestOps(SelectionKey.OP_READ);
                    }
//                    else if (sk.isReadable()){
//                        int len = -1;
//                        String str = "server:";
//                        ByteBuffer bf = (ByteBuffer) sk.attachment();
//                        while ((len = sc.read(bf))>0) {
//                            bf.flip();
//                            str += new String(bf.array(),0,len);
//                            bf.clear();
//                            if (len < 1024) { System.out.println(str);break;}
//                        }
//                        sk.interestOps(SelectionKey.OP_WRITE);
//                    }

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }
}
