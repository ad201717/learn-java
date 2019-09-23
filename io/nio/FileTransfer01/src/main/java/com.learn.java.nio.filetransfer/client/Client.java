package com.learn.java.nio.filetransfer.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.DecimalFormat;

public class Client {
    public static final int MAGIC_WORD = 488345049;
    public static final int OVER = -8274636;

    public void runClient(String connect, String src) {
        try {
            File file = new File(src);
            if (!file.exists()) {
                System.err.println("文件不存在，请检查:" + src);
                System.exit(-1);
            }

            String host = connect;
            int port = 9999;
            int index;
            if ((index = connect.indexOf(":")) > -1) {
                port = Integer.parseInt(connect.substring(index + 1));
                host = connect.substring(0, index);
            }
            SocketChannel socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(true);
            socketChannel.connect(new InetSocketAddress(host, port));

            sendMagicWord(socketChannel);
            if (file.isDirectory()) {
                sendDir(socketChannel, file.getParentFile().getAbsolutePath(), file);
            } else {
                sendFile(socketChannel, file.getParentFile().getAbsolutePath(), file);
            }
            sendOver(socketChannel);
            socketChannel.shutdownOutput();
            socketChannel.close();
            System.out.println("文件传输完成");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMagicWord(SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(MAGIC_WORD);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }

    private void sendOver(SocketChannel socketChannel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(4);
        byteBuffer.putInt(OVER);
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
    }

    private void sendDir(SocketChannel socketChannel, String root, File file) throws IOException {
        File[] files = file.listFiles();
        if (files.length == 0) {
            return;
        }

        for (File f : files) {
            if (f.isFile()) {
                sendFile(socketChannel, root, f);
            } else {
                sendDir(socketChannel, root, f);
            }
        }
    }

    private void sendFile(SocketChannel socketChannel, String root, File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        String fileName = file.getAbsolutePath().substring(root.length());
        int fileLength = is.available();
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setGroupingSize(3);
        System.out.println("开始传输文件:" + fileName + "，大小:" + df.format(fileLength));

        ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 4 + fileName.getBytes().length);
        byteBuffer.putInt(fileName.getBytes().length);
        byteBuffer.putInt(fileLength);
        byteBuffer.put(fileName.getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);

        long send = 0;
        while (send < fileLength) {
            long l = is.getChannel().transferTo(send, fileLength - send, socketChannel);
            if (l > 0) {
                send += l;
                if (l > 0.01 * fileLength) {
                    System.out.println("传输文件:" + fileName + "，进度:" + (send * 100 / fileLength) + "%...");
                }
            }
        }
    }

}
