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

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.flurry.android.FlurryPerformance;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class RecyclerviewAdapter extends RecyclerView.Adapter<RecyclerviewAdapter.UrlViewHolder> {

    private static String[] sUrlList = {
            "https://www.flurry.com",
            "https://developer.yahoo.com/flurry/docs",
            "https://www.google.com/search?q=Flurry",
            "https://en.wikipedia.org/wiki/Flurry_(company)",
            "https://www.npmjs.com/package/react-native-flurry-sdk",
            "https://github.com/flurry/react-native-flurry-sdk"
    };

    private LayoutInflater inflater;

    RecyclerviewAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public UrlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new UrlViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UrlViewHolder holder, int position) {
        // TODO: show wait
        String url = sUrlList[position];
        new RunOkhttpAsyncTask(holder, url).execute();
    }

    @Override
    public int getItemCount() {
        return sUrlList.length;
    }

    static class UrlViewHolder extends RecyclerView.ViewHolder {
        WebView mWebView;

        @SuppressLint("SetJavaScriptEnabled")
        UrlViewHolder(View itemView) {
            super(itemView);

            mWebView = itemView.findViewById(R.id.web_item);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setInitialScale(100);
        }
    }

    private static class RunOkhttpAsyncTask extends AsyncTask<Void, Void, String> {
        private UrlViewHolder mHolder;
        private String mUrl;

        private RunOkhttpAsyncTask(UrlViewHolder holder, String url) {
            mHolder = holder;
            mUrl = url;
        }

        @Override
        protected String doInBackground(Void... voids) {
            return runOkhttpInterceptorEventListenerFactory(mUrl);
        }

        @Override
        protected void onPostExecute(String urlText) {
            super.onPostExecute(urlText);

            mHolder.mWebView.loadDataWithBaseURL("", urlText, "text/html", "UTF-8", "");
            FlurryPerformance.reportFullyDrawn();
        }
    }

    private static String runOkhttpInterceptorEventListenerFactory(String url) {
        // Example of FlurryPerformance.HttpInterceptor & HttpEventListener.Factory
        // Note: Run both for comparison
        FlurryPerformance.HttpInterceptor interceptor = new FlurryPerformance.HttpInterceptor("FlurryHTTP.Interceptor");
        FlurryPerformance.HttpEventListener.Factory eventFactory = new FlurryPerformance.HttpEventListener.Factory("FlurryHTTP.EventListener.Factory");

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .eventListenerFactory(eventFactory)
                .build();
        return runOkhttp(client, url);
    }

    private static String runOkhttp(final OkHttpClient client, final String url) {
        try {
            FlurryPerformance.ResourceLogger resourceLogger = new FlurryPerformance.ResourceLogger();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            ResponseBody responseBody = response.body();
            String responseStr = (responseBody != null) ? responseBody.string() : null;

            resourceLogger.logEvent("FlurryHTTP.Resource");
            return responseStr;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}