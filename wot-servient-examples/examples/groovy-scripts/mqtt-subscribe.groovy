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
def td = '''{
    "@context": "https://www.w3.org/2019/td/v1",
    "title": "MQTT Counter",
    "id": "urn:dev:wot:mqtt:counter",
    "actions" : {
        "resetCounter": {
            "forms": [
                    {"href": "mqtt://test.mosquitto.org:1883/MQTT-Test/actions/resetCounter",  "mqtt:qos":  0, "mqtt:retain" : false}
            ]
        }
    },
    "events": {
        "temperature": {
            "data": {
                "type": "integer"
            },
            "forms": [
                    {"href": "mqtt://test.mosquitto.org:1883/MQTT-Test/events/counterEvent",  "mqtt:qos":  0, "mqtt:retain" : false}
            ]
        }
    }
}
'''

println('=== TD ===')
println(td)
println('==========')

def source = wot.consume(td)

source.events.temperature.observer().subscribe(
        { x -> println('onNext: ' + x) },
        { e -> println('onError: ' + e) },
        { -> println('onCompleted') }
)
println('Subscribed')

new Timer().schedule({
    source.actions.resetCounter.invoke()
    println('Reset counter!')
}, 20000, 20000)