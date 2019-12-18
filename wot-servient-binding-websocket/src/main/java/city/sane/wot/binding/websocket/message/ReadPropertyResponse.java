package city.sane.wot.binding.websocket.message;

import city.sane.wot.content.Content;

import java.util.Objects;

public class ReadPropertyResponse extends AbstractServerMessage {
    private final Content value;

    private ReadPropertyResponse() {
        super();
        value = null;
    }

    public ReadPropertyResponse(String id, Content value) {
        super(id);
        this.value = Objects.requireNonNull(value);
    }

    public ReadPropertyResponse(ReadProperty clientMessage, Content value) {
        super(clientMessage);
        this.value = value;
    }

    public Content getValue() {
        return value;
    }
}
