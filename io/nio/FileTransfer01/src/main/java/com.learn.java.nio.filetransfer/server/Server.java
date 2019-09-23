package com.learn.java.nio.filetransfer.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;

public class Server {
    public static final int MAGIC_WORD = 488345049;
    public static final int OVER = -8274636;

    public void runServer(int port, String dest) {
        try {
            ServerSocketChannel serverChannel = ServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress("0.0.0.0", port));
            ByteBuffer shortBuffer = ByteBuffer.allocate(4);

            System.out.println("server listening at port:" + port);
            while (true) {
                SocketChannel socketChannel = serverChannel.accept();
                System.out.println("receive connection:" + socketChannel.getRemoteAddress());
                socketChannel.configureBlocking(true);
                /**   | magicword(4bit) | nameLength(4bit) | fileLength(4bit) | name(nbit) | file(nbit) |  **/
                int magicWord = readInt(socketChannel, shortBuffer);
                if (magicWord != MAGIC_WORD) {
                    socketChannel.write(ByteBuffer.wrap("Unknown exception.".getBytes()));
                    socketChannel.close();
                    continue;
                }

                while (true) {
                    int nameLength = readInt(socketChannel, shortBuffer);

                    if (nameLength == OVER) {
                        break;
                    }

                    int fileLength = readInt(socketChannel, shortBuffer);
                    String name = readString(socketChannel, nameLength);
                    DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
                    df.setGroupingSize(3);
                    System.out.println("开始接收文件:" + name + "，大小:" + df.format(fileLength));

                    File destFile = new File(dest, name);
                    if (!destFile.getParentFile().exists()) {
                        destFile.getParentFile().mkdirs();
                    }
                    FileOutputStream os = new FileOutputStream(destFile);
                    long read = 0, l;
                    while (read < fileLength) {
                        l = os.getChannel().transferFrom(socketChannel, read, fileLength - read);
                        if (l > 0) {
                            read += l;
                            if (l > 0.01 * fileLength) {
                                System.out.println("接收文件:" + name + "，进度:" + (read * 100 / fileLength) + "%...");
                            }
                        }
                    }
                    os.close();
                    System.out.println("文件接收完成，查看:" + destFile.getAbsolutePath());
                }
                socketChannel.close();
                System.out.println("文件接收完成,关闭连接");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int readInt(SocketChannel socketChannel, ByteBuffer shortBuffer) throws IOException {
        socketChannel.read(shortBuffer);
        while (shortBuffer.hasRemaining()) {
            socketChannel.read(shortBuffer);
        }
        shortBuffer.flip();
        int num = shortBuffer.getInt();
        shortBuffer.clear();
        return num;
    }

    private String readString(SocketChannel socketChannel, int length) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        socketChannel.read(byteBuffer);
        while (byteBuffer.hasRemaining()) {
            socketChannel.read(byteBuffer);
        }
        byteBuffer.flip();
        return new String(byteBuffer.array());
    }

}
