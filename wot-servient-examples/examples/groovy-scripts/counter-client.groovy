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
wot.fetch('coap://localhost:5683/counter').thenAccept { td ->
    println('=== TD ===')
    println(td.toJson(true))
    println('==========')

    def thing = wot.consume(td)

    // read property #1
    def read1 = thing.properties['count'].read().get()
    println('CounterClient: count value is ' + read1)

    // increment property #1
    thing.actions['increment'].invoke().get()
    def inc1 = thing.properties['count'].read().get()
    println('CounterClient: count value after increment #1 is ' + inc1)

    // increment property #2
    thing.actions['increment'].invoke().get()
    def inc2 = thing.properties['count'].read().get()
    println('CounterClient: count value after increment #2 is ' + inc2)

    // decrement property
    thing.actions['increment'].invoke().get()
    def dec1 = thing.properties['count'].read().get()
    println('CounterClient: count value after decrement is ' + dec1)
}.join()