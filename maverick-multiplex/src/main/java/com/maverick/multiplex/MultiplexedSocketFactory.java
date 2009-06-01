package com.maverick.multiplex;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MultiplexedSocketFactory implements SocketFactory {

    static SocketFactory instance;
    static Class factoryImpl = MultiplexedSocketFactory.class;

    public static SocketFactory getDefault() {
        try {
            return instance == null ? instance = (SocketFactory) factoryImpl.newInstance() : instance;
        } catch (Throwable t) {
            return instance == null ? instance = new MultiplexedSocketFactory() : instance;
        }
    }

    public static void setFactoryImpl(Class factoryImpl) {
        MultiplexedSocketFactory.factoryImpl = factoryImpl;
        instance = null;
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return new Socket(host, port);
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return new Socket(host, port);
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException,
                    UnknownHostException {
        return new Socket(host, port, localHost, localPort);
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return new Socket(address, port, localAddress, localPort);
    }
}