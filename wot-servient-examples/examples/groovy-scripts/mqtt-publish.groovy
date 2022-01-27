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
def counter = 0

def thing = wot.produce([
        id         : 'MQTT-Test',
        title      : 'MQTT-Test',
        description: 'Tests a MQTT client that published counter values as an WoT event and subscribes the resetCounter topic as WoT action to reset the own counter.'
])

println('Setup MQTT broker address/port details in wot-servient.conf.json (also see sample in wot-servient.conf.json_mqtt)!')

thing
        .addAction(
                'resetCounter',
                {
                    println('Resetting counter')
                    counter = 0
                })
        .addEvent(
                'counterEvent',
                [
                        type: 'integer'
                ])

thing.expose().whenComplete { r, e ->
    if (e == null) {
        println(thing.title + ' ready')
        new Timer().schedule({
            ++counter
            thing.events.counterEvent.emit(counter)
            println('New count ' + counter)
        }, 0, 1000)
    }
    else {
        println('Error: ' + e)
    }
}