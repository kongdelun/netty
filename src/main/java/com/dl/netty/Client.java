package com.dl.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

public class Client {
   public static void main(String[] args){
       new Bootstrap()
               .group(new NioEventLoopGroup(1))
               .channel(SocketChannel.class);

   }
}
