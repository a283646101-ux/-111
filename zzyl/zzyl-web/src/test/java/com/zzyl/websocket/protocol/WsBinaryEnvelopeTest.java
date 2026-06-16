package com.zzyl.websocket.protocol;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class WsBinaryEnvelopeTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldSerializeAndDeserialize() throws Exception {
        WsBinaryEnvelope envelope = new WsBinaryEnvelope();
        envelope.setType(WsMessageType.DIRECT);
        envelope.setTraceId("t-1");
        envelope.setFrom("u1");
        envelope.setTo("u2");
        envelope.setContentType("application/octet-stream");
        envelope.setPayload("hello".getBytes(StandardCharsets.UTF_8));

        ByteBuffer buffer = WsBinaryEnvelope.toBuffer(envelope, objectMapper);
        WsBinaryEnvelope decoded = WsBinaryEnvelope.from(buffer, objectMapper);

        Assert.assertEquals(WsMessageType.DIRECT, decoded.getType());
        Assert.assertEquals("u2", decoded.getTo());
        Assert.assertArrayEquals("hello".getBytes(StandardCharsets.UTF_8), decoded.getPayload());
    }
}
