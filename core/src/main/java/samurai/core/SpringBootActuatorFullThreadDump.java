/*
 * Copyright 2021 Yusuke Yamamoto
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package samurai.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class SpringBootActuatorFullThreadDump extends FullThreadDump {
    public SpringBootActuatorFullThreadDump(String header) {
        super(header);
        try {
            JSONObject jsonObject = new JSONObject(header);
            JSONArray threads = jsonObject.getJSONArray("threads");
            for (int i = 0; i < threads.length(); i++) {
                addThreadDump(new SpringBootActuatorThreadDump(threads.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    boolean isThreadHeader(String line) {
        return false;
    }

    @Override
    boolean isThreadFooter(String line) {
        return false;
    }

    @Override
    boolean isThreadDumpContinuing(String line) {
        return false;
    }

}
