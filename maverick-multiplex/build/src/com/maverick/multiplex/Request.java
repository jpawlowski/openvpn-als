package com.maverick.multiplex;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;

import com.maverick.util.ByteArrayWriter;

public class Request {
    byte[] requestData;
    String requestName;

    public Request(String requestName) {
        this(requestName, (byte[]) null);
    }

    public Request(String requestName, byte[] requestData) {
        this.requestName = requestName;
        this.requestData = requestData;
    }

    public Request(String requestName, Object[] values) throws IOException {
        this.requestName = requestName;
        ByteArrayWriter writer = new ByteArrayWriter();
        for (int index = 0; index < values.length; index++) {
            Object value = values[index];
            if (value instanceof BigInteger) {
                writer.writeBigInteger((BigInteger) value);
            } else if (value instanceof Integer) {
                writer.writeInt(((Integer) value).intValue());
            } else if (value instanceof Long) {
                writer.writeUINT64(((Long) value).longValue());
            } else if (value instanceof Short) {
                writer.writeShort(((Short) value).shortValue());
            } else if (value instanceof Boolean) {
                writer.writeBoolean(((Boolean) value).booleanValue());
            } else if (value instanceof Date) {
                writer.writeUINT64(((Date) value).getTime());
            } else {
                writer.writeString(value.toString());
            }
        }
        requestData = writer.toByteArray();
    }

    public String getRequestName() {
        return requestName;
    }

    public byte[] getRequestData() {
        return requestData;
    }

    public void setRequestData(byte[] requestData) {
        this.requestData = requestData;
    }
}