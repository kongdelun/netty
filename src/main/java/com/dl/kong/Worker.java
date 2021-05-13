package com.dl.kong;

import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class Worker {

    private String name;
    private Thread thread;
    private Selector sel;
    private volatile CountDownLatch cdl;
    private volatile boolean isRunning = false;

    public Worker(String name){
        this.name = name;
    }

    private void init() throws Exception {
        this.sel = Selector.open();
        this.cdl = new CountDownLatch(1);
        this.thread = new Thread(()->{
           while(isRunning){
               try {
                   if (sel.select() > 0){
                       Iterator<SelectionKey> it = sel.selectedKeys().iterator();
                       while(it.hasNext()) {
                           SelectionKey curKey = it.next();
                           it.remove();
                           if (curKey.isReadable()) {

                               SocketChannel tmpSC = (SocketChannel) curKey.channel();
                               List<Byte> bytes = (List<Byte>) curKey.attachment();
                               ByteBuffer bb = ByteBuffer.allocate(64);
                               tmpSC.read(bb);
                               bb.flip();
                               for(int i = 0; i < bb.limit();i++){
                                   if (bb.get(i) == 92){
                                       bytes.stream().forEach( b ->{
                                           System.out.print((char) b.byteValue());
                                       });
                                       bytes.clear();
                                   }else{
                                       bytes.add(bb.get(i));
                                   }
                               }
                           }
                       }
                   } else {
                      cdl.await();
                      System.out.println("register ok !");
                   }

               } catch (Exception e) {
                   e.printStackTrace();
               }
           }
        }, name);
    }

    public synchronized void register(SocketChannel sc){
        if (isRunning){
            try {
                cdl = new CountDownLatch(1);
                sel.wakeup();
                List<Byte> bytes = new ArrayList<>(64);
                sc.register(sel, SelectionKey.OP_READ, bytes);
                cdl.countDown();
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void start(){
        if (!isRunning){
            try {
                init();
                isRunning = true;
                thread.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void stop(){
        isRunning = false;
    }
}
