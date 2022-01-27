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
final NAME_PROPERTY_COUNT = 'count'
final NAME_PROPERTY_LAST_CHANGE = 'lastChange'
final NAME_ACTION_INCREMENT = 'increment'
final NAME_ACTION_DECREMENT = 'decrement'
final NAME_ACTION_RESET = 'reset'
final NAME_EVENT_CHANGE = 'change'

def thing = wot.produce([
        title       : 'counter',
        titles      : [
                'en': 'counter',
                'de': 'zähler',
                'it': 'Contatore'
        ],
        description : 'counter example Thing',
        descriptions: [
                'en': 'counter example Thing',
                'de': 'Zähler Beispiel Ding',
                'it': 'Contatore Esempio'
        ],
        '@context'  : ['https://www.w3.org/2019/wot/td/v1', ['iot': 'http://example.org/iot']],
])

println('Produced ' + thing.title)

thing.addProperty(
        NAME_PROPERTY_COUNT,
        [
                type        : 'integer',
                description : 'current counter value',
                descriptions: [
                        'en': 'current counter value',
                        'de': 'Derzeitiger Zähler Stand',
                        'it': 'valore attuale del contatore'
                ],
                'iot:Custom': 'example annotation',
                observable  : true,
                readOnly    : true
        ],
        0)

thing.addProperty(
        NAME_PROPERTY_LAST_CHANGE,
        [
                type        : 'string',
                description : 'last change of counter value',
                descriptions: [
                        'en': 'last change of counter value',
                        'de': 'Letzte Änderung',
                        'it': 'ultima modifica del valore'
                ],
                observable  : true,
                readOnly    : true
        ],
        new Date().toString())

thing.addAction(
        NAME_ACTION_INCREMENT,
        [
                description : 'increment value',
                descriptions: [
                        'en': 'increment value',
                        'de': 'Zähler erhöhen',
                        'it': 'incrementare valore'
                ]
        ],
        {
            println('Incrementing')
            return thing.properties[NAME_PROPERTY_COUNT].read().thenApply { count ->
                let value = count + 1
                thing.properties[NAME_PROPERTY_COUNT].write(value)
                thing.properties[NAME_PROPERTY_LAST_CHANGE].write(new Date().toString())
                thing.events[NAME_EVENT_CHANGE].emit()
            }
        })

thing.addAction(
        NAME_ACTION_DECREMENT,
        [
                description : 'decrement value',
                descriptions: [
                        'en': 'decrement value',
                        'de': 'Zähler verringern',
                        'it': 'decrementare valore'
                ]
        ],
        {
            println('Decrementing')
            return thing.properties[NAME_PROPERTY_COUNT].read().thenApply { count ->
                let value = count - 1
                thing.properties[NAME_PROPERTY_COUNT].write(value)
                thing.properties[NAME_PROPERTY_LAST_CHANGE].write(new Date().toString())
                thing.events[NAME_EVENT_CHANGE].emit()
            }
        })

thing.addAction(
        NAME_ACTION_RESET,
        [
                description : 'reset value',
                descriptions: [
                        'en': 'reset value',
                        'de': 'Zähler resettieren',
                        'it': 'resettare valore'
                ]
        ],
        {
            println('Resetting')
            thing.properties[NAME_PROPERTY_COUNT].write(0)
            thing.properties[NAME_PROPERTY_LAST_CHANGE].write(new Date().toString())
            thing.events[NAME_EVENT_CHANGE].emit()
        })

thing.addEvent(
        NAME_EVENT_CHANGE,
        [
                description : 'change event',
                descriptions: [
                        'en': 'change event',
                        'de': 'Änderungsnachricht',
                        'it': 'resettare valore'
                ]
        ]
)

thing.expose().whenComplete { r, e ->
    if (e == null) {
        println(thing.title + ' ready')
    }
    else {
        println('Error: ' + e)
    }
}