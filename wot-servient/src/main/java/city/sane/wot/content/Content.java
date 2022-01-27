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
package city.sane.wot.content;

import java.util.Arrays;
import java.util.Objects;

/**
 * Represents any serialized content. Enables the transfer of arbitrary data structures.
 */
public class Content {
    public static final Content EMPTY_CONTENT = new Content(ContentManager.DEFAULT, new byte[0]);
    private final String type;
    private final byte[] body;

    private Content() {
        type = null;
        body = null;
    }

    public Content(byte[] body) {
        this(ContentManager.DEFAULT, body);
    }

    public Content(String type, byte[] body) {
        this.type = type;
        this.body = body;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), Arrays.hashCode(getBody()));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Content)) {
            return false;
        }
        return Objects.equals(getType(), ((Content) obj).getType()) && Arrays.equals(getBody(), ((Content) obj).getBody());
    }

    @Override
    public String toString() {
        return "Content{" +
                "type='" + type + '\'' +
                ", body=" + Arrays.toString(body) +
                ", new String(body)=" + new String(body) +
                '}';
    }

    public String getType() {
        return type;
    }

    public byte[] getBody() {
        return body;
    }
}
