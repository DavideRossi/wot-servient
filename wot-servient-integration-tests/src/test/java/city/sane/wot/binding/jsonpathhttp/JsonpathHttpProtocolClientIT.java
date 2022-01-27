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
package city.sane.wot.binding.jsonpathhttp;

import city.sane.wot.binding.ProtocolClient;
import city.sane.wot.content.Content;
import city.sane.wot.content.ContentCodecException;
import city.sane.wot.content.ContentManager;
import city.sane.wot.thing.form.Form;
import city.sane.wot.thing.form.Operation;
import city.sane.wot.thing.schema.NumberSchema;
import city.sane.wot.thing.schema.ObjectSchema;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonpathHttpProtocolClientIT {
    private static final String KLIMABOTSCHAFTER = "{\"1000750\": {\"Temp_2m\": 20.6, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1006.8, \"Wind_gust\": 1.5, \"Rain_month\": 38.8, \"Rain_rate\": 12.1, \"Rain_day\": 1.6, \"longitude\": 10.2178, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 33.5, \"Wind_dir\": 165, \"latitude\": 53.679851, \"Rain_year\": 417.8, \"st_name\": \"Ahrensburg\"}, \"1000751\": {\"Temp_2m\": 19.7, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1006.4, \"Wind_gust\": 0.4, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.992272, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 38.3, \"Wind_dir\": 174, \"latitude\": 53.572083, \"Rain_year\": 0.0, \"st_name\": \"Turmweg\"}, \"1000747\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.993208, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.504269, \"Rain_year\": 0.0, \"st_name\": \"STSWilhelmsburg\"}, \"1000844\": {\"Temp_2m\": 20.1, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1007.4, \"Wind_gust\": 0.4, \"Rain_month\": 34.8, \"Rain_rate\": 0.0, \"Rain_day\": 0.2, \"longitude\": 10.221534, \"Wind_avg\": 0.4, \"UV_rad\": 0.0, \"Hum_2m\": 70, \"Solar_rad\": 19.3, \"Wind_dir\": 90, \"latitude\": 53.365431, \"Rain_year\": 1447.8, \"st_name\": \"WinsenLuhe\"}, \"1000734\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 3.8, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.865809, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.470411, \"Rain_year\": 238.2, \"st_name\": \"GymSuederelbe\"}, \"1001098\": {\"Temp_2m\": 19.8, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1005.8, \"Wind_gust\": 0.8, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.098655, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 69, \"Solar_rad\": 32.4, \"Wind_dir\": 177, \"latitude\": 53.629985, \"Rain_year\": 1594.2, \"st_name\": \"Heilwig\"}, \"1001099\": {\"Temp_2m\": 20.1, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1006.9, \"Wind_gust\": 0.5, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.098655, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 41.0, \"Wind_dir\": 165, \"latitude\": 53.629985, \"Rain_year\": 0.0, \"st_name\": \"Doerpsweg\"}, \"1000741\": {\"Temp_2m\": 20.0, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1006.2, \"Wind_gust\": 0.3, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.96943, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 32.9, \"Wind_dir\": 162, \"latitude\": 53.430712, \"Rain_year\": 0.0, \"st_name\": \"Sinstorf\"}, \"1000001\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.116661, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.659664, \"Rain_year\": 0.0, \"st_name\": \"Sasel\"}, \"1000999\": {\"Temp_2m\": 19.8, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1006.1, \"Wind_gust\": 1.5, \"Rain_month\": 0.2, \"Rain_rate\": 0.0, \"Rain_day\": 0.2, \"longitude\": 9.973709, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 70, \"Solar_rad\": 37.8, \"Wind_dir\": 173, \"latitude\": 53.460263, \"Rain_year\": 88.4, \"st_name\": \"GoetheHarburg\"}, \"1001271\": {\"Temp_2m\": 19.3, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1008.1, \"Wind_gust\": 2.2, \"Rain_month\": 21.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.260848, \"Wind_avg\": 0.1, \"UV_rad\": 0.0, \"Hum_2m\": 69, \"Solar_rad\": 43.0, \"Wind_dir\": 158, \"latitude\": 53.726228, \"Rain_year\": 370.0, \"st_name\": \"Bargteheide\"}, \"1001292\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 16.4, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.993367, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.520092, \"Rain_year\": 316.6, \"st_name\": \"Spreehafen\"}, \"1001259\": {\"Temp_2m\": 18.3, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1005.3, \"Wind_gust\": 7.6, \"Rain_month\": 19.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 6.792037, \"Wind_avg\": 3.6, \"UV_rad\": 0.0, \"Hum_2m\": 62, \"Solar_rad\": 49.0, \"Wind_dir\": 180, \"latitude\": 51.203956, \"Rain_year\": 4482.0, \"st_name\": \"GSGDuesseldorf\"}, \"1001128\": {\"Temp_2m\": 19.6, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1008.2, \"Wind_gust\": 0.9, \"Rain_month\": 25.8, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.9602, \"Wind_avg\": 0.0, \"UV_rad\": 0.5, \"Hum_2m\": 67, \"Solar_rad\": 58.0, \"Wind_dir\": 180, \"latitude\": 53.641941, \"Rain_year\": 405.8, \"st_name\": \"Ohmoor\"}, \"1000795\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 3.8, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.075051, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.66561, \"Rain_year\": 19.8, \"st_name\": \"Heinegym\"}, \"1000944\": {\"Temp_2m\": 19.9, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1005.9, \"Wind_gust\": 0.4, \"Rain_month\": 22.2, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.002063, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 72, \"Solar_rad\": 69.0, \"Wind_dir\": 185, \"latitude\": 53.55223, \"Rain_year\": 314.2, \"st_name\": \"Altstadt\"}, \"1000868\": {\"Temp_2m\": 20.2, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1005.9, \"Wind_gust\": 0.4, \"Rain_month\": 50.2, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.079314, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 43.0, \"Wind_dir\": 190, \"latitude\": 53.572215, \"Rain_year\": 50.2, \"st_name\": \"CPGWandsbek\"}, \"1000739\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 44.8, \"Rain_rate\": 0.0, \"Rain_day\": 3.0, \"longitude\": 9.692671, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.476195, \"Rain_year\": 93.6, \"st_name\": \"Buxtehude\"}, \"1001300\": {\"Temp_2m\": 19.1, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1009.3, \"Wind_gust\": 0.4, \"Rain_month\": 176.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.896036, \"Wind_avg\": 0.2, \"UV_rad\": 0.0, \"Hum_2m\": 68, \"Solar_rad\": 65.8, \"Wind_dir\": 112, \"latitude\": 53.608341, \"Rain_year\": 176.6, \"st_name\": \"STSEidelstedt\"}, \"1000746\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 0.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.029718, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.670097, \"Rain_year\": 252.2, \"st_name\": \"STSHeidberg\"}, \"1001148\": {\"Temp_2m\": 19.4, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1009.6, \"Wind_gust\": 0.0, \"Rain_month\": 0.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.8761, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 70, \"Solar_rad\": 66.5, \"Wind_dir\": 112, \"latitude\": 53.554177, \"Rain_year\": 181.6, \"st_name\": \"Hochrad\"}, \"1001223\": {\"Temp_2m\": 19.7, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1006.4, \"Wind_gust\": 0.8, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.837969, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 37.1, \"Wind_dir\": 188, \"latitude\": 53.322427, \"Rain_year\": 0.0, \"st_name\": \"Kattenberge\"}, \"1001023\": {\"Temp_2m\": 20.0, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1006.3, \"Wind_gust\": 1.0, \"Rain_month\": 1.8, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.992046, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 37.7, \"Wind_dir\": 173, \"latitude\": 53.49531, \"Rain_year\": 206.2, \"st_name\": \"SHSWilhelmsburg\"}, \"1001025\": {\"Temp_2m\": 20.2, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1006.3, \"Wind_gust\": 0.0, \"Rain_month\": 161.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.2, \"longitude\": 9.954683, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 32.6, \"Wind_dir\": 183, \"latitude\": 53.467989, \"Rain_year\": 161.6, \"st_name\": \"Grumbrechtstr\"}, \"1001024\": {\"Temp_2m\": 19.6, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1007.5, \"Wind_gust\": 0.0, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.098655, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 67.3, \"Wind_dir\": 90, \"latitude\": 53.629985, \"Rain_year\": 18.6, \"st_name\": \"Grootmoor\"}, \"1000925\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.127408, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.580036, \"Rain_year\": 0.0, \"st_name\": \"OHSJenfeld\"}, \"1001022\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 25.0, \"Rain_rate\": 2.3, \"Rain_day\": 5.0, \"longitude\": 9.867153, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.477961, \"Rain_year\": 176.6, \"st_name\": \"Hausbruch\"}, \"1001021\": {\"Temp_2m\": 20.1, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1005.8, \"Wind_gust\": 1.2, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.964274, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 70, \"Solar_rad\": 37.7, \"Wind_dir\": 176, \"latitude\": 53.454603, \"Rain_year\": 219.0, \"st_name\": \"H10Harburg\"}, \"1001020\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.964643, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.464981, \"Rain_year\": 0.0, \"st_name\": \"Heimfeld\"}, \"1001246\": {\"Temp_2m\": 20.1, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1006.6, \"Wind_gust\": 0.5, \"Rain_month\": 36.2, \"Rain_rate\": 0.0, \"Rain_day\": 2.0, \"longitude\": 10.163834, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 70, \"Solar_rad\": 35.3, \"Wind_dir\": 194, \"latitude\": 53.628979, \"Rain_year\": 231.4, \"st_name\": \"STSMeiendorf\"}, \"1000725\": {\"Temp_2m\": 20.1, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1005.9, \"Wind_gust\": -0.0, \"Rain_month\": 0.8, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.145815, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 35.5, \"Wind_dir\": 180, \"latitude\": 53.601502, \"Rain_year\": 312.0, \"st_name\": \"Rahlstedt\"}, \"1001079\": {\"Temp_2m\": 19.2, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1005.5, \"Wind_gust\": 0.0, \"Rain_month\": 6.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.098655, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 78, \"Solar_rad\": 39.5, \"Wind_dir\": 45, \"latitude\": 53.629985, \"Rain_year\": 59.0, \"st_name\": \"Volksdorf\"}, \"1000661\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.825637, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.565077, \"Rain_year\": 227.2, \"st_name\": \"STSBlankenese\"}, \"1000170\": {\"Temp_2m\": 19.9, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1006.3, \"Wind_gust\": 0.4, \"Rain_month\": 28.2, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.399072, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 72, \"Solar_rad\": 32.2, \"Wind_dir\": 180, \"latitude\": 53.620084, \"Rain_year\": 480.6, \"st_name\": \"Trittau\"}, \"1001290\": {\"Temp_2m\": 19.9, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1006.4, \"Wind_gust\": 0.6, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.166273, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 71, \"Solar_rad\": 35.2, \"Wind_dir\": 176, \"latitude\": 53.487398, \"Rain_year\": 0.0, \"st_name\": \"GretelBergmann\"}, \"1001058\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 2.6, \"Rain_rate\": 0.0, \"Rain_day\": 2.6, \"longitude\": 10.12361, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.595102, \"Rain_year\": 2.6, \"st_name\": \"GyulaTrebitsch\"}, \"1001293\": {\"Temp_2m\": 18.9, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1009.1, \"Wind_gust\": 0.9, \"Rain_month\": 36.8, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.925131, \"Wind_avg\": 0.4, \"UV_rad\": 0.0, \"Hum_2m\": 54, \"Solar_rad\": 53.3, \"Wind_dir\": 112, \"latitude\": 53.600148, \"Rain_year\": 472.0, \"st_name\": \"AlbrechtThaer\"}, \"1000808\": {\"Temp_2m\": 19.8, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1007.9, \"Wind_gust\": 0.0, \"Rain_month\": 6.4, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.876182, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 70, \"Solar_rad\": 42.4, \"Wind_dir\": 248, \"latitude\": 53.532632, \"Rain_year\": 158.0, \"st_name\": \"Finkenwerder\"}, \"1001053\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 0.0, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.109628, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.650625, \"Rain_year\": 0.0, \"st_name\": \"Sasel-Redder\"}, \"1000856\": {\"Temp_2m\": null, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": null, \"Wind_gust\": null, \"Rain_month\": 12.6, \"Rain_rate\": 0.0, \"Rain_day\": 9.8, \"longitude\": 10.009157, \"Wind_avg\": null, \"UV_rad\": null, \"Hum_2m\": null, \"Solar_rad\": null, \"Wind_dir\": null, \"latitude\": 53.597295, \"Rain_year\": 12.6, \"st_name\": \"Winterhude\"}, \"1001057\": {\"Temp_2m\": 19.3, \"Upload_time\": \"2019-09-24 16:01:42+00:00\", \"Press_sea\": 1006.2, \"Wind_gust\": -0.1, \"Rain_month\": 10.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 9.956793, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 70, \"Solar_rad\": 36.9, \"Wind_dir\": 176, \"latitude\": 53.460838, \"Rain_year\": 181.0, \"st_name\": \"Weusthoffstr\"}, \"1001019\": {\"Temp_2m\": 19.1, \"Upload_time\": \"2019-09-24 16:01:41+00:00\", \"Press_sea\": 1008.0, \"Wind_gust\": 0.0, \"Rain_month\": 35.6, \"Rain_rate\": 0.0, \"Rain_day\": 0.0, \"longitude\": 10.111298, \"Wind_avg\": 0.0, \"UV_rad\": 0.0, \"Hum_2m\": 75, \"Solar_rad\": 63.0, \"Wind_dir\": 22, \"latitude\": 53.630383, \"Rain_year\": 301.4, \"st_name\": \"Bramfeld\"}}";
    private Service server;
    private ProtocolClient client;

    @BeforeEach
    public void setup() {
        server = Service.ignite().ipAddress("127.0.0.1").port(8080);
        server.init();
        server.awaitInitialization();

        client = new JsonpathHttpProtocolClient();

        // from http://data.klimabotschafter.de/weatherdata/JSON_Hamburgnet.json
        server.get("my-endpoint", (request, response) -> {
            response.type("application/json");
            return KLIMABOTSCHAFTER;
        });
    }

    @AfterEach
    public void tearDown() {
        server.stop();
        server.awaitStop();
    }

    @Test
    public void readResource() throws ExecutionException, InterruptedException, ContentCodecException {
        Form form = new Form.Builder()
                .setHref("jsonpath+http://localhost:8080/my-endpoint")
                .setOp(Operation.READ_PROPERTY)
                .setOptional("sane:jsonPath", "$.[\"1001293\"].Temp_2m")
                .build();

        Content content = client.readResource(form).get();
        Number temp = ContentManager.contentToValue(content, new NumberSchema());

        assertEquals(18.9, temp);
    }

    @Test
    public void readResourceWithoutJsonPath() throws ExecutionException, InterruptedException, ContentCodecException {
        Form form = new Form.Builder()
                .setHref("jsonpath+http://localhost:8080/my-endpoint")
                .setOp(Operation.READ_PROPERTY)
                .build();

        Content content = client.readResource(form).get();
        ContentManager.contentToValue(content, new ObjectSchema());

        // should not fail
        assertTrue(true);
    }

    @Test
    public void readResourceWithInvalidJsonPath() throws ExecutionException, InterruptedException, ContentCodecException {
        Form form = new Form.Builder()
                .setHref("jsonpath+http://localhost:8080/my-endpoint")
                .setOp(Operation.READ_PROPERTY)
                .setOptional("sane:jsonPath", "$.[[[[[[[")
                .build();

        Content content = client.readResource(form).get();
        ContentManager.contentToValue(content, new ObjectSchema());

        // should not fail
        assertTrue(true);
    }
}