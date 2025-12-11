package com.example.tpt.ui.notice;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tpt.R;
import com.example.tpt.model.Notice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 공지사항 목록 Activity
 * WebView를 통한 웹 크롤링으로 공지사항 데이터 수집
 */
public class NoticeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    NoticeAdapter adapter;
    ArrayList<Notice> noticeList = new ArrayList<>();
    ProgressBar progressBar;
    TextView tvStatus;

    WebView webView;
    String baseUrl = "https://www.changwon.ac.kr";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notice);

        // 뒤로가기 버튼 설정
        ImageButton btnBack = findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // 뷰 초기화
        recyclerView = findViewById(R.id.noticeRecycler);
        progressBar = findViewById(R.id.progressBar);
        tvStatus = findViewById(R.id.tvStatus);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoticeAdapter(this, noticeList);
        recyclerView.setAdapter(adapter);

        // 로딩 시작
        showLoading();

        // WebView 설정
        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);

        // JavaScript Interface 추가
        webView.addJavascriptInterface(new WebAppInterface(), "Android");

        webView.setWebViewClient(new WebViewClient() {
            /**
             * 페이지 로딩 완료 후 JavaScript로 HTML 파싱
             */
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.d("WEBVIEW", "페이지 로딩 완료: " + url);

                // JavaScript로 공지사항 데이터 추출
                webView.evaluateJavascript(
                        "(function() {" +
                                "  try {" +
                                "    var items = [];" +
                                "    var rows = document.querySelectorAll('table tbody tr, .board_list tbody tr, .list-group-item');" +
                                "    " +
                                "    for(var i = 0; i < Math.min(rows.length, 20); i++) {" +
                                "      var row = rows[i];" +
                                "      var titleEl = row.querySelector('a, .title');" +
                                "      var dateEl = row.querySelector('.date, td:last-child');" +
                                "      " +
                                "      if(titleEl) {" +
                                "        var title = titleEl.textContent.trim();" +
                                "        var href = titleEl.getAttribute('href') || '';" +
                                "        var date = dateEl ? dateEl.textContent.trim() : '';" +
                                "        " +
                                "        if(title && title.length > 0) {" +
                                "          items.push({" +
                                "            title: title," +
                                "            date: date," +
                                "            url: href" +
                                "          });" +
                                "        }" +
                                "      }" +
                                "    }" +
                                "    return JSON.stringify(items);" +
                                "  } catch(e) {" +
                                "    return JSON.stringify([]);" +
                                "  }" +
                                "})()",
                        new android.webkit.ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                Log.d("JAVASCRIPT_RESULT", "결과: " + value);
                                parseNoticeData(value);
                            }
                        }
                );
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("WEBVIEW_ERROR", "Error: " + description);
                runOnUiThread(() -> showError("웹페이지 로딩 실패: " + description));
            }
        });

        // 공지사항 페이지 로드
        webView.loadUrl("https://www.changwon.ac.kr/portal/na/ntt/selectNttList.do?mi=13532&bbsId=2932");
    }

    /**
     * JavaScript에서 반환된 JSON 데이터 파싱
     */
    private void parseNoticeData(String jsonString) {
        try {
            // JavaScript 반환값 전처리
            if (jsonString.startsWith("\"") && jsonString.endsWith("\"")) {
                jsonString = jsonString.substring(1, jsonString.length() - 1);
                jsonString = jsonString.replace("\\\"", "\"");
                jsonString = jsonString.replace("\\n", "");
            }

            Log.d("PARSE_JSON", "파싱할 데이터: " + jsonString);

            JSONArray items = new JSONArray(jsonString);

            if (items.length() == 0) {
                runOnUiThread(() -> showError("공지사항을 찾을 수 없습니다.\n페이지 구조가 변경되었을 수 있습니다."));
                return;
            }

            noticeList.clear();
            for (int i = 0; i < items.length(); i++) {
                JSONObject item = items.getJSONObject(i);

                String title = item.optString("title", "제목 없음");
                String date = item.optString("date", "날짜 없음");
                String url = item.optString("url", "");

                // 상대 경로를 절대 경로로 변환
                if (url.startsWith("/")) {
                    url = baseUrl + url;
                } else if (!url.startsWith("http")) {
                    url = baseUrl + "/portal/na/ntt/" + url;
                }

                noticeList.add(new Notice(title, date, url));
            }

            runOnUiThread(() -> {
                adapter.notifyDataSetChanged();
                showContent();
                Log.d("NOTICE_SUCCESS", "공지사항 " + noticeList.size() + "개 로드 완료");
            });

        } catch (Exception e) {
            Log.e("PARSE_ERROR", "파싱 오류", e);
            runOnUiThread(() -> showError("데이터 처리 중 오류 발생\n" + e.getMessage()));
        }
    }

    /**
     * 로딩 화면 표시
     */
    private void showLoading() {
        if (progressBar != null) progressBar.setVisibility(View.VISIBLE);
        if (tvStatus != null) {
            tvStatus.setText("공지사항을 불러오는 중...");
            tvStatus.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
    }

    /**
     * 에러 화면 표시
     */
    private void showError(String message) {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (tvStatus != null) {
            tvStatus.setText("❌ " + message + "\n\n새로고침 하거나 나중에 다시 시도해주세요.");
            tvStatus.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) recyclerView.setVisibility(View.GONE);
    }

    /**
     * 컨텐츠 화면 표시
     */
    private void showContent() {
        if (progressBar != null) progressBar.setVisibility(View.GONE);
        if (tvStatus != null) tvStatus.setVisibility(View.GONE);
        if (recyclerView != null) recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * JavaScript Interface (필요시 사용)
     */
    public class WebAppInterface {
        @JavascriptInterface
        public void sendData(String data) {
            Log.d("JS_INTERFACE", "받은 데이터: " + data);
        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.destroy();
        }
        super.onDestroy();
    }
}