package com.example.tpt.ui.meal;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.tpt.model.DailyMeal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.tpt.R;

public class DailyMealFragment extends Fragment {

    private static final String ARG_MEAL = "arg_meal";

    public static DailyMealFragment newInstance(DailyMeal meal) {
        DailyMealFragment fragment = new DailyMealFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_MEAL, meal);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_meal, container, false);

        TextView tvDate = view.findViewById(R.id.tv_date);

        CardView cardBreakfast = view.findViewById(R.id.card_breakfast);

        TextView tvBreakfast = view.findViewById(R.id.tv_breakfast_menu);
        LinearLayout layoutBreakfast = null;
        if (tvBreakfast != null) {
            tvBreakfast.setVisibility(View.GONE);
            if (tvBreakfast.getParent() instanceof LinearLayout) {
                layoutBreakfast = (LinearLayout) tvBreakfast.getParent();
            }
        }

        CardView cardLunch = view.findViewById(R.id.card_lunch);
        LinearLayout layoutLunch = view.findViewById(R.id.layout_lunch_container);

        CardView cardDinner = view.findViewById(R.id.card_dinner);
        LinearLayout layoutDinner = view.findViewById(R.id.layout_dinner_container);

        if (getArguments() != null) {
            DailyMeal meal = (DailyMeal) getArguments().getSerializable(ARG_MEAL);
            if (meal != null) {
                tvDate.setText(meal.getDate());
                if (layoutBreakfast != null) {
                    parseAndStackBlocks(cardBreakfast, layoutBreakfast, meal.getBreakfast());
                }

                parseAndStackBlocks(cardLunch, layoutLunch, meal.getLunch());
                parseAndStackBlocks(cardDinner, layoutDinner, meal.getDinner());
            }
        }
        return view;
    }

    private void parseAndStackBlocks(CardView mainCard, LinearLayout container, String rawText) {
        if (rawText == null || rawText.trim().isEmpty() || rawText.contains("운영 안") || rawText.contains("운영안") || rawText.contains("운영중지")) {
            if (mainCard != null) mainCard.setVisibility(View.GONE);
            return;
        }

        if (mainCard != null) mainCard.setVisibility(View.VISIBLE);
        int childCount = container.getChildCount();
        if (childCount > 1) {
            container.removeViews(1, childCount - 1);
        }

        String[] lines = rawText.split("\n");

        StringBuilder currentContent = new StringBuilder();
        String currentTitle = "일반 메뉴";

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            boolean isBracket = (line.startsWith("<") && line.contains(">")) || (line.startsWith("&lt;") && line.contains("&gt;"));
            boolean isSquare = (line.startsWith("[") && line.contains("]"));

            if (isBracket || isSquare) {
                if (currentContent.length() > 0) {
                    addSmallBlock(container, currentTitle, currentContent.toString());
                    currentContent.setLength(0);
                }
                int start = -1;
                int end = -1;

                if (isBracket) {
                    start = line.indexOf("<");
                    if (start == -1) start = line.indexOf("&lt;");
                    end = line.indexOf(">");
                    if (end == -1) end = line.indexOf("&gt;");
                } else {
                    start = line.indexOf("[");
                    end = line.indexOf("]");
                }

                if (start != -1 && end != -1) {
                    int titleStart = start + (line.startsWith("&") ? 4 : 1);
                    currentTitle = line.substring(titleStart, end);
                }
                int contentStart = end + (line.contains("&gt;") ? 4 : 1);
                if (line.length() > contentStart) {
                    currentContent.append(line.substring(contentStart)).append("\n");
                }
            } else {
                currentContent.append(line).append("\n");
            }
        }
        if (currentContent.length() > 0) {
            addSmallBlock(container, currentTitle, currentContent.toString());
        }
    }
    private void addSmallBlock(LinearLayout container, String title, String content) {
        LinearLayout block = new LinearLayout(getContext());
        block.setOrientation(LinearLayout.VERTICAL);
        block.setBackgroundResource(R.drawable.bg_menu_block);
        block.setPadding(40, 40, 40, 40);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 0, 0, 30); // 블럭 간격
        block.setLayoutParams(params);

        TextView tvTitle = new TextView(getContext());
        tvTitle.setText(title);
        tvTitle.setTextColor(Color.parseColor("#009688"));
        tvTitle.setTypeface(null, Typeface.BOLD);
        tvTitle.setTextSize(16);
        tvTitle.setPadding(0, 0, 0, 15);
        block.addView(tvTitle);

        TextView tvContent = new TextView(getContext());
        tvContent.setText(content.trim());
        tvContent.setTextColor(Color.parseColor("#424242"));
        tvContent.setTextSize(15);
        tvContent.setLineSpacing(0, 1.3f);
        block.addView(tvContent);

        container.addView(block);
    }
}