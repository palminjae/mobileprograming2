package com.example.tpt.ui.meal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import com.example.tpt.R;

public class CafeteriaMenuFragment extends Fragment {

    private final String URL_BONGLIM = "https://www.changwon.ac.kr/kor/di/diView/dietView.do?mi=10198&kind=B";
    private final String URL_SARIM = "https://www.changwon.ac.kr/kor/di/diView/dietView.do?mi=10199&kind=S";
    private final String URL_DORM = "https://www.changwon.ac.kr/dorm/na/ntt/selectNttInfo.do?nttSn=1392414&mi=10079&editYn=N&listCo=15&hasManageBbsAuth=false&dietAuth=N";

    // 뷰를 생성하고 각 식당별 클릭 이벤트를 설정하는 함수
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cafeteria_menu, container, false);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                    getParentFragmentManager().popBackStack();
                }
                if (getActivity() != null) {
                    View fragmentContainer = getActivity().findViewById(R.id.fragment_container);
                    if (fragmentContainer != null) {
                        fragmentContainer.setVisibility(View.GONE);
                    }
                    View mainContent = getActivity().findViewById(R.id.card_announcements);
                    if (mainContent != null && mainContent.getParent() instanceof View) {
                        ((View) mainContent.getParent()).setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        View btnBonglim = view.findViewById(R.id.card_bongrim);
        View btnSarim = view.findViewById(R.id.card_sarim);
        View btnDorm = view.findViewById(R.id.card_dormitory);

        btnBonglim.setOnClickListener(v -> openDetail("봉림관", URL_BONGLIM));
        btnSarim.setOnClickListener(v -> openDetail("사림관", URL_SARIM));
        btnDorm.setOnClickListener(v -> openDetail("학생생활관", URL_DORM));

        return view;
    }

    // 선택된 식당 정보를 담아 식단 상세 프래그먼트로 이동하는 함수
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