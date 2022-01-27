/*
 * Copyright (c) 2019-2022 Heiko Bornholdt
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */
package city.sane.wot.binding.websocket.message;

import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.ExposedThing;
import city.sane.wot.thing.property.ExposedThingProperty;

import java.util.Map;
import java.util.function.Consumer;

public class WriteProperty extends ThingInteractionWithContent {
    private WriteProperty() {
        super();
    }

    public WriteProperty(String thingId, String name, Content value) {
        super(thingId, name, value);
    }

    @Override
    public void reply(Consumer<AbstractServerMessage> replyConsumer,
                      Map<String, ExposedThing> things) {
        String id = getThingId();
        ExposedThing thing = things.get(id);

        if (thing != null) {
            String name = getName();
            ExposedThingProperty<Object> property = thing.getProperty(name);

            if (property != null) {
                Content payload = getValue();

                try {
                    Object input = ContentManager.contentToValue(payload, property);

                    property.write(input).thenAccept(output -> {
                        try {
                            replyConsumer.accept(new WritePropertyResponse(getId(), ContentManager.valueToContent(output)));
                        }
                        catch (ContentCodecException e) {
                            replyConsumer.accept(new ServerErrorResponse(this, "Unable to parse output of write operation: " + e.getMessage()));
                        }
                    });
                }
                catch (ContentCodecException e) {
                    // unable to parse paylod
                    replyConsumer.accept(new ServerErrorResponse(this, "Unable to parse given input " + e.getMessage()));
                }
            }
            else {
                // Property not found
                replyConsumer.accept(new ClientErrorResponse(this, "Property not found"));
            }
        }
        else {
            // Thing not found
            replyConsumer.accept(new ClientErrorResponse(this, "Thing not found"));
        }
    }

    @Override
    public String toString() {
        return "WriteProperty{" +
                "value=" + value +
                ", thingId='" + thingId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
