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
import com.fasterxml.jackson.databind.ObjectMapper

def things = [:]

new Timer().schedule({
    def stations = new ObjectMapper().readValue(new URL('http://data.klimabotschafter.de/weatherdata/JSON_Hamburgnet.json'), Map.class)

    for (station in stations.values()) {
        def st_name = station['st_name']

        def exposedThing = things[st_name]

        if (!exposedThing) {
            // create and expose thing
            def thing = [
                    id        : 'KlimabotschafterWetterstationen:' + st_name,
                    title     : 'KlimabotschafterWetterstationen:' + st_name,
                    '@type'   : 'Thing',
                    '@context': [
                            'http://www.w3.org/ns/td',
                            [
                                    om   : 'http://www.wurvoc.org/vocabularies/om-1.8/',
                                    saref: 'https://w3id.org/saref#',
                                    sch  : 'http://schema.org/',
                                    sane : 'https://sane.city/',
                            ]
                    ],
            ]

            exposedThing = wot.produce(thing)

            exposedThing.addProperty(
                    'Temp_2m',
                    [
                            '@type'             : 'saref:Temperature',
                            description         : 'Temperatur in 2m in Grad Celsisus',
                            'om:unit_of_measure': 'om:degree_Celsius',
                            type                : 'number',
                            readOnly            : true,
                            observable          : true
                    ]
            )

            exposedThing.addProperty(
                    'Upload_time',
                    [
                            description: 'Letzter Upload der Daten in UTC',
                            type       : 'string',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Press_sea',
                    [
                            '@type'    : 'saref:Pressure',
                            description: 'Luftdruck in hPa auf Meeresnivea',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Wind_gust',
                    [
                            description: 'Stärkste Windböe in m/s der letzten 10 Min',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Wind_dir',
                    [
                            description: 'Windrichtung in Grad (0-360)',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Rain_year',
                    [
                            description: 'Jahresniederschlag',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Rain_month',
                    [
                            description: 'Monatlicher Niederschlag in mm',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Rain_day',
                    [
                            description: 'Tagesniederschlagsmenge (00-00 Uhr) in mm',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Rain_rate',
                    [
                            description: 'Aktuelle Regenrate in mm/hr',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'longitude',
                    [
                            '@type'   : 'sch:longitude',
                            type      : 'number',
                            readOnly  : true,
                            observable: true
                    ]
            )

            exposedThing.addProperty(
                    'Wind_avg',
                    [
                            description: 'Windgeschwindigkeit (10 Min Mittel) in m/s',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'UV_rad',
                    [
                            description: 'UV-Index (Einheit siehe Wikipedia)',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Hum_2m',
                    [
                            '@type'    : 'saref:Humidity',
                            description: 'Relative Luftfeuchtigkeit 2 m in %',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'Solar_rad',
                    [
                            description: 'Globalstrahlung in W/m^2',
                            type       : 'number',
                            readOnly   : true,
                            observable : true
                    ]
            )

            exposedThing.addProperty(
                    'latitude',
                    [
                            '@type'   : 'sch:latitude',
                            type      : 'number',
                            readOnly  : true,
                            observable: true
                    ]
            )

            things[st_name] = exposedThing

            exposedThing.expose()
        }

        // set property values
        [
                'Temp_2m', 'Upload_time', 'Press_sea', 'Wind_gust', 'Wind_dir', 'Rain_year', 'Rain_month', 'Rain_day', 'Rain_rate', 'longitude', 'Wind_avg',
                'UV_rad', 'Hum_2m', 'Solar_rad', 'latitude'
        ].each {
            exposedThing.properties[it].write(station[it])
        }
    }
}, 0, 60 * 1000)