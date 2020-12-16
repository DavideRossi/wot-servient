package city.sane.wot.binding.http;

import city.sane.wot.Servient;
import city.sane.wot.binding.ProtocolServerException;
import city.sane.wot.thing.ExposedThing;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import spark.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpProtocolServerTest {
    private Config config;
    private ConfigObject configObject;
    private String bindHost;
    private int bindPort;
    private List<String> addresses;
    private Service httpServer;
    private Map<String, ExposedThing> things;
    private Map<String, Object> security;
    private String securityScheme;
    private Servient servient;
    private ExposedThing thing;

    @BeforeEach
    public void setUp() {
        config = mock(Config.class);
        configObject = mock(ConfigObject.class);
        bindHost = "0.0.0.0";
        bindPort = 80;
        addresses = mock(List.class);
        httpServer = null;
        things = mock(Map.class);
        security = mock(Map.class);
        securityScheme = null;
        servient = mock(Servient.class);
        thing = mock(ExposedThing.class);
    }

    @Test
    public void constructorShouldRejectInvalidSecurityScheme() {
        when(configObject.unwrapped()).thenReturn(Map.of("scheme", "dsadadas"));
        when(config.getObject("wot.servient.http.security")).thenReturn(configObject);

        assertThrows(ProtocolServerException.class, () -> new HttpProtocolServer(config));
    }

//    @Test
//    public void startShouldReturnNull() throws ExecutionException, InterruptedException {
//        HttpProtocolServer server = new HttpProtocolServer(bindHost, bindPort, addresses, httpServer, things, security, securityScheme, false);
//
//        assertNull(server.start(servient).get());
//    }
//
//    @Test
//    public void stopShouldReturnNull() throws ExecutionException, InterruptedException {
//        HttpProtocolServer server = new HttpProtocolServer(bindHost, bindPort, addresses, httpServer, things, security, securityScheme, false);
//
//        assertNull(server.stop().get());
//    }

    @Test
    public void exposeShouldAddForms() {
        HttpProtocolServer server = new HttpProtocolServer(bindHost, bindPort, List.of("http://localhost"), httpServer, things, security, securityScheme, true, bindPort, List.of("http://localhost"));
        server.expose(thing);

        verify(thing, timeout(1 * 1000L)).addForm(any());
    }

    @Test
    public void destroyShouldRemoveThing() {
        when(thing.getId()).thenReturn("counter");

        HttpProtocolServer server = new HttpProtocolServer(bindHost, bindPort, addresses, httpServer, things, security, securityScheme, true, bindPort, addresses);
        server.destroy(thing);

        verify(things, timeout(1 * 1000L)).remove("counter");
    }

    @Test
    public void getDirectoryUrlShouldReturnFristAddress() throws URISyntaxException {
        when(addresses.get(0)).thenReturn("http://0.0.0.0");

        HttpProtocolServer server = new HttpProtocolServer(bindHost, bindPort, addresses, httpServer, things, security, securityScheme, true, bindPort, addresses);

        assertEquals(new URI("http://0.0.0.0"), server.getDirectoryUrl());
    }

    @Test
    public void getThingUrl() throws URISyntaxException {
        when(addresses.get(0)).thenReturn("http://0.0.0.0");

        HttpProtocolServer server = new HttpProtocolServer(bindHost, bindPort, addresses, httpServer, things, security, securityScheme, true, bindPort, addresses);

        assertEquals(new URI("http://0.0.0.0/counter"), server.getThingUrl("counter"));
    }
}