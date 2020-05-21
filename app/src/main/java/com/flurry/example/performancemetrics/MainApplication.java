/*
 * Copyright 2020, Verizon Media.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flurry.example.performancemetrics;

import android.app.Application;
import android.util.Log;

import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryPerformance;

public class MainApplication extends Application {
    private static final String TAG = "FlurryPerformanceSample";

    public static final String FLURRY_APIKEY = "MFBSPM6Y3WGTQ5XHZD8P";

    @Override
    public void onCreate() {
        super.onCreate();

        // Init Flurry
        new FlurryAgent.Builder()
                .withLogLevel(Log.VERBOSE)
                .withLogEnabled(true)
                .withPerformanceMetrics(FlurryPerformance.COLD_START | FlurryPerformance.SCREEN_TIME)
                .build(this, FLURRY_APIKEY);
    }

}
