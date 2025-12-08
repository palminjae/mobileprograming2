package com.example.schoolmeal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class CafeteriaDetailFragment extends Fragment {

    private TextView tvResult, tvTitle;
    private String cafeteriaUrl;
    private String cafeteriaTitle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cafeteria_detail, container, false);

        tvTitle = view.findViewById(R.id.tv_cafeteria_name);
        tvResult = view.findViewById(R.id.tv_menu_result);

        if (getArguments() != null) {
            cafeteriaTitle = getArguments().getString("title");
            cafeteriaUrl = getArguments().getString("url");
            tvTitle.setText(cafeteriaTitle);
        }

        startCrawling();
        return view;
    }

    private void startCrawling() {
        new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            try {
                Document doc = Jsoup.connect(cafeteriaUrl)
                        .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/108.0.0.0 Safari/537.36")
                        .timeout(10000)
                        .get();

                Elements rows = doc.select("table tbody tr");

                if (rows.isEmpty()) {
                    rows = doc.select("div.view_cont"); // 일반적인 게시판 본문 클래스
                }

                if (rows.isEmpty()) {
                    sb.append("식단 정보를 찾을 수 없습니다.\nHTML 구조가 변경되었거나 URL을 확인해주세요.");
                } else {
                    for (Element row : rows) {
                        String text = row.text();
                        if (text.length() > 5) {
                            sb.append(text).append("\n");
                            sb.append("-----------------\n");
                        }
                    }
                }

            } catch (IOException e) {
                sb.append("오류 발생: ").append(e.getMessage());
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> tvResult.setText(sb.toString()));
            }
        }).start();
    }
}