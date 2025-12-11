package com.example.tpt.ui.tip;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.tpt.R;

public class StudentTipMenuFragment extends Fragment {

    // 뷰를 생성하고 카드뷰 클릭 리스너를 설정하는 함수
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_tip_menu, container, false);

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

        setupTipCard(view, R.id.card_dorm_tip, "기숙사 TIP");
        setupTipCard(view, R.id.card_library_tip, "도서관 TIP");
        setupTipCard(view, R.id.card_food_tip, "학식 TIP");
        setupTipCard(view, R.id.card_student_card_tip, "학생증 TIP");
        setupTipCard(view, R.id.card_course_tip, "수강신청 TIP");

        return view;
    }

    // 팁 카드의 클릭 이벤트를 설정하여 상세 화면으로 이동시키는 함수
    private void setupTipCard(View parentView, int cardId, String title) {
        View card = parentView.findViewById(cardId);
        if (card != null) {
            card.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), TipDetailActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            });
        }
    }
}