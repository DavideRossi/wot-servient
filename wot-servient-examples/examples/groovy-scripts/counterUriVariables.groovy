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
def thing = wot.produce([
        id         : 'counter',
        title      : 'counter',
        description: 'counter example Thing',
        '@context' : ['https://www.w3.org/2019/wot/td/v1', [iot: 'http://example.org/iot']]
])

print('Produced ' + thing.title)

thing.addProperty(
        'count',
        [
                type        : 'integer',
                description : 'current counter value',
                'iot:Custom': 'example annotation',
                observable  : true,
                readOnly    : false
        ],
        0)

thing.addAction(
        'increment',
        [
                description : 'Incrementing counter value with optional step value as uriVariable',
                input       : ['type': 'object'],
                uriVariables: [
                        step: ['type': 'integer', 'minimum': 1, 'maximum': 250]
                ]
        ],
        { data, options ->
            println('Incrementing, data= ' + data + ', options= ' + options)
            thing.properties['count'].read().thenApply { count ->
                def step = 1
                if (data && 'step' in data) {
                    step = data['step']
                }
                def value = count + step
                thing.properties['count'].write(value)
            }
        })

thing.addAction(
        'decrement',
        [
                description : 'Decrementing counter value with optional step value as uriVariable',
                uriVariables: [
                        step: ['type': 'integer', 'minimum': 1, 'maximum': 250]
                ]
        ],
        { data, options ->
            println('Decrementing ' + options)
            thing.properties['count'].read().thenApply { count ->
                def step = 1
                if (data && 'step' in data) {
                    step = data['step']
                }
                def value = count - step
                thing.properties['count'].write(value)
            }
        })

thing.addAction(
        'reset',
        {
            println('Resetting')
            thing.properties['count'].write(0)
        })

thing.expose().whenComplete { r, e ->
    if (e == null) {
        println(thing.title + ' ready')
    }
    else {
        println('Error: ' + e)
    }
}
