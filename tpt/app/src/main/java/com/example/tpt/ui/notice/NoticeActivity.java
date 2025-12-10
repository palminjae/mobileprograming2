package com.example.tpt.ui.notice;

import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpt.R;
import com.example.tpt.model.Notice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NoticeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NoticeAdapter adapter;
    ArrayList<Notice> noticeList = new ArrayList<>();

    WebView tempWebView;
    String baseUrl = "https://www.changwon.ac.kr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);

        recyclerView = findViewById(R.id.noticeRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeAdapter(this, noticeList);
        recyclerView.setAdapter(adapter);

        CookieManager.getInstance().setAcceptCookie(true);

        tempWebView = new WebView(this);
        tempWebView.getSettings().setJavaScriptEnabled(true);

        tempWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                String cookies = CookieManager.getInstance().getCookie(url);
                Log.d("COOKIE", "쿠키: " + cookies);

                loadNoticeWithCookie(cookies);
            }
        });

        tempWebView.loadUrl("https://www.changwon.ac.kr/portal/na/ntt/selectNttList.do?mi=13532&bbsId=2932");
    }

    private void loadNoticeWithCookie(String cookie) {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("mi", "13532")
                .add("bbsId", "2932")
                .add("pageIndex", "1")
                .add("searchCondition", "0")
                .build();

        Request request = new Request.Builder()
                .url("https://www.changwon.ac.kr/portal/na/ntt/selectNttListAjax.do")
                .addHeader("Cookie", cookie)
                .addHeader("User-Agent", "Mozilla/5.0")
                .addHeader("X-Requested-With", "XMLHttpRequest")
                .addHeader("Referer", "https://www.changwon.ac.kr/portal/na/ntt/selectNttList.do?mi=13532&bbsId=2932")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("DEBUG_JSON", json);

                try {
                    JSONObject obj = new JSONObject(json);
                    JSONArray list = obj.getJSONArray("resultList");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject item = list.getJSONObject(i);

                        String title = item.getString("nttSj");
                        String date = item.getString("createDt");
                        String nttSn = item.getString("nttSn");

                        String link = baseUrl + "/portal/na/ntt/selectNttInfo.do?mi=13532&nttSn=" + nttSn;

                        noticeList.add(new Notice(title, date, link));
                    }

                    runOnUiThread(() -> adapter.notifyDataSetChanged());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
}