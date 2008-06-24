package com.adito.boot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import javax.net.ServerSocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 */
public class CustomServerSocketFactory extends ServerSocketFactory {

    private static final Log log = LogFactory.getLog(CustomServerSocketFactory.class);
    private static ServerSocketFactory instance;
    private static Class<? extends ServerSocketFactory> socketFactoryImpl = CustomServerSocketFactory.class;

    @Override
    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog) throws IOException {
        return new ServerSocket(port, backlog);
    }

    @Override
    public ServerSocket createServerSocket(int port, int backlog, InetAddress address) throws IOException {
        return new ServerSocket(port, backlog, address);
    }

    public static ServerSocketFactory getDefault() {
        try {
            return instance == null ? instance = (ServerSocketFactory) socketFactoryImpl.newInstance() : instance;
        } catch (Exception e) {
            log.error("Could not create instance of class " + socketFactoryImpl.getCanonicalName(), e);
            return instance == null ? instance = new CustomServerSocketFactory() : instance;
        }
    }

    /**
     * @param socketFactoryImpl
     */
    public static void setFactoryImpl(Class<? extends ServerSocketFactory> socketFactoryImpl) {
        CustomServerSocketFactory.socketFactoryImpl = socketFactoryImpl;
    }
}