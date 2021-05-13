package com.dl.kong;

import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;


public class Boss {

    public static void main(String[] args) {
        Worker worker = new Worker("worker-1");
        worker.start();
        try( ServerSocketChannel ssc = ServerSocketChannel.open();
             Selector sel = Selector.open()) {
            ssc.bind(new InetSocketAddress("127.0.0.1",8888));
            ssc.configureBlocking(false);
            ssc.register(sel, SelectionKey.OP_ACCEPT);
            while(sel.select() > 0){
                Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                while(it.hasNext()){
                    SelectionKey curKey = it.next();
                    it.remove();
                    if(curKey.isAcceptable()){
                        ServerSocketChannel tmpSSC = (ServerSocketChannel)curKey.channel();
                        SocketChannel sc = tmpSSC.accept();
                        System.out.println(sc);
                        sc.configureBlocking(false);
                        worker.register(sc);
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
