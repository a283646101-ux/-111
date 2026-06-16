package com.zzyl.websocket.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.util.Assert;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Data
public class WsBinaryEnvelope {

    private WsMessageType type;
    private String traceId;
    private String from;
    private String to;
    private String room;
    private String contentType = "application/octet-stream";
    private long timestamp = System.currentTimeMillis();
    private byte[] payload;

    public static WsBinaryEnvelope from(ByteBuffer source, ObjectMapper objectMapper) throws IOException {
        ByteBuffer buffer = source.slice();
        Assert.isTrue(buffer.remaining() >= 4, "invalid binary frame");
        int headerLength = buffer.getInt();
        Assert.isTrue(headerLength > 0 && headerLength <= buffer.remaining(), "invalid binary header length");
        byte[] headerBytes = new byte[headerLength];
        buffer.get(headerBytes);
        WsBinaryEnvelope envelope = objectMapper.readValue(new String(headerBytes, StandardCharsets.UTF_8), WsBinaryEnvelope.class);
        byte[] body = new byte[buffer.remaining()];
        buffer.get(body);
        envelope.setPayload(body);
        return envelope;
    }

    public static ByteBuffer toBuffer(WsBinaryEnvelope envelope, ObjectMapper objectMapper) throws IOException {
        byte[] header = objectMapper.writeValueAsBytes(envelope.copyWithoutPayload());
        byte[] payload = envelope.getPayload() == null ? new byte[0] : envelope.getPayload();
        ByteBuffer buffer = ByteBuffer.allocate(4 + header.length + payload.length);
        buffer.putInt(header.length);
        buffer.put(header);
        buffer.put(payload);
        buffer.flip();
        return buffer;
    }

    private WsBinaryEnvelope copyWithoutPayload() {
        WsBinaryEnvelope envelope = new WsBinaryEnvelope();
        envelope.setType(this.type);
        envelope.setTraceId(this.traceId);
        envelope.setFrom(this.from);
        envelope.setTo(this.to);
        envelope.setRoom(this.room);
        envelope.setContentType(this.contentType);
        envelope.setTimestamp(this.timestamp);
        envelope.setPayload(null);
        return envelope;
    }
}
