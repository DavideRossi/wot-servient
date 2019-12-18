package city.sane.wot.binding.websocket;

import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.binding.websocket.message.*;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.*;

public class WebsocketProtocolClientIT {
    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    private WebsocketProtocolClientFactory clientFactory;
    private ProtocolClient client;
    private WebSocketServer server;

    @Before
    public void setUp() {
        clientFactory = new WebsocketProtocolClientFactory();
        clientFactory.init().join();

        client = clientFactory.getClient();

        server = new MyWebSocketServer(new InetSocketAddress(8080));
        server.start();
    }

    @After
    public void tearDown() throws IOException, InterruptedException {
        clientFactory.destroy().join();
        server.stop();
    }

    @Test(timeout = 20 * 1000L)
    public void readResource() throws ContentCodecException, ExecutionException, InterruptedException {
        Form form = new Form.Builder()
                .setHref("ws://localhost:8080")
                .setOp(Operation.READ_PROPERTY)
                .setOptional("websocket:message", Map.of(
                        "type", "readProperty",
                        "thingId", "counter",
                        "name", "count"
                ))
                .build();

        assertEquals(ContentManager.valueToContent(1337), client.readResource(form).get());
    }

    @Test(timeout = 20 * 1000L)
    public void writeResource() throws ContentCodecException, ExecutionException, InterruptedException {
        Form form = new Form.Builder()
                .setHref("ws://localhost:8080")
                .setOp(Operation.WRITE_PROPERTY)
                .setOptional("websocket:message", Map.of(
                        "type", "writeProperty",
                        "thingId", "counter",
                        "name", "count"
                ))
                .build();

        assertEquals(Content.EMPTY_CONTENT, client.writeResource(form, ContentManager.valueToContent(1337)).get());
    }

    @Test(timeout = 20 * 1000L)
    public void invokeResource() throws ContentCodecException, ExecutionException, InterruptedException {
        Form form = new Form.Builder()
                .setHref("ws://localhost:8080")
                .setOp(Operation.INVOKE_ACTION)
                .setOptional("websocket:message", Map.of(
                        "type", "invokeAction",
                        "thingId", "counter",
                        "name", "increment"
                ))
                .build();

        assertEquals(ContentManager.valueToContent(43), client.invokeResource(form).get());
    }

    private static class MyWebSocketServer extends WebSocketServer {
        public MyWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {

        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {

        }

        @Override
        public void onMessage(WebSocket conn, String requestJson) {
            try {
                AbstractClientMessage request = JSON_MAPPER.readValue(requestJson, AbstractClientMessage.class);

                AbstractServerMessage response = null;
                if (request instanceof ReadProperty) {
                    response = new ReadPropertyResponse(request.getId(), ContentManager.valueToContent(1337));
                }
                else if (request instanceof WriteProperty) {
                    response = new WritePropertyResponse(request.getId(), Content.EMPTY_CONTENT);
                }
                else if (request instanceof InvokeAction) {
                    response = new InvokeActionResponse(request.getId(), ContentManager.valueToContent(43));
                }

                String responseJson = JSON_MAPPER.writeValueAsString(response);
                conn.send(responseJson);
            }
            catch (IOException | ContentCodecException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            throw new RuntimeException(ex);
        }

        @Override
        public void onStart() {

        }
    }
}