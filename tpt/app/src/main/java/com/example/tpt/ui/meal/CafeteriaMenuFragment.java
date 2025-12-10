package com.example.tpt.ui.meal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.tpt.R;

public class CafeteriaMenuFragment extends Fragment {

    private final String URL_BONGLIM = "https://www.changwon.ac.kr/kor/di/diView/dietView.do?mi=10198&kind=B";
    private final String URL_SARIM = "https://www.changwon.ac.kr/kor/di/diView/dietView.do?mi=10199&kind=S";
    private final String URL_DORM = "https://www.changwon.ac.kr/dorm/na/ntt/selectNttInfo.do?nttSn=1392414&mi=10079&editYn=N&listCo=15&hasManageBbsAuth=false&dietAuth=N";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cafeteria_menu, container, false);

        View btnBonglim = view.findViewById(R.id.card_bongrim);
        View btnSarim = view.findViewById(R.id.card_sarim);
        View btnDorm = view.findViewById(R.id.card_dormitory);

        btnBonglim.setOnClickListener(v -> openDetail("봉림관", URL_BONGLIM));
        btnSarim.setOnClickListener(v -> openDetail("사림관", URL_SARIM));
        btnDorm.setOnClickListener(v -> openDetail("학생생활관", URL_DORM));

        return view;
    }

    private void openDetail(String title, String url) {
        WeeklyMealFragment fragment = new WeeklyMealFragment();

        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("url", url);
        fragment.setArguments(args);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}