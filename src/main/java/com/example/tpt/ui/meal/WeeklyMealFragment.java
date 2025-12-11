package com.example.tpt.ui.meal;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.tpt.R;
import com.example.tpt.model.DailyMeal;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 주간 학식 메뉴 Fragment
 * Jsoup을 이용한 웹 크롤링으로 식단 데이터 수집
 */
public class WeeklyMealFragment extends Fragment {

    private static final String TAG = "WeeklyMealFragment";

    private ViewPager2 viewPager;
    private TextView tvTitle;
    private String cafeteriaUrl;
    private String cafeteriaName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_meal, container, false);

        try {
            viewPager = view.findViewById(R.id.viewPager);
            tvTitle = view.findViewById(R.id.tv_cafeteria_title);

            // 뒤로가기 버튼 설정
            ImageButton btnBack = view.findViewById(R.id.btnBack);
            if (btnBack != null) {
                btnBack.setOnClickListener(v -> {
                    try {
                        if (getParentFragmentManager().getBackStackEntryCount() > 0) {
                            getParentFragmentManager().popBackStack();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "뒤로가기 에러: " + e.getMessage());
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                    }
                });
            }

            if (getArguments() != null) {
                cafeteriaUrl = getArguments().getString("url");
                cafeteriaName = getArguments().getString("title");
                if (tvTitle != null && cafeteriaName != null) {
                    tvTitle.setText(cafeteriaName + " 주간 식단");
                }
            }

            startCrawling();
        } catch (Exception e) {
            Log.e(TAG, "onCreateView 에러: " + e.getMessage(), e);
            if (getContext() != null) {
                Toast.makeText(getContext(), "화면 로딩 중 오류가 발생했습니다", Toast.LENGTH_SHORT).show();
            }
        }

        return view;
    }

    /**
     * 웹 크롤링 시작
     */
    private void startCrawling() {
        new Thread(() -> {
            List<DailyMeal> weeklyMeals = new ArrayList<>();
            try {
                if (cafeteriaUrl == null || cafeteriaUrl.isEmpty()) {
                    throw new Exception("URL이 없습니다");
                }

                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
                Document doc = Jsoup.connect(cafeteriaUrl)
                        .userAgent(userAgent)
                        .timeout(30000)
                        .get();

                // 기숙사와 일반 식당 구분하여 파싱
                if (cafeteriaUrl.contains("dorm") || cafeteriaUrl.contains("nttSn")) {
                    parseDormitory(doc, weeklyMeals);
                } else {
                    parseStandard(doc, weeklyMeals);
                }

            } catch (Exception e) {
                Log.e(TAG, "크롤링 에러: " + e.getMessage(), e);
                weeklyMeals.clear();
                weeklyMeals.add(new DailyMeal("오류 발생",
                        "데이터를 불러오는 중 문제가 발생했습니다.",
                        "잠시 후 다시 시도해주세요.",
                        ""));
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    try {
                        if (weeklyMeals.isEmpty()) {
                            Toast.makeText(getContext(), "식단 데이터가 없습니다", Toast.LENGTH_SHORT).show();
                            // 빈 데이터라도 표시
                            String[] days = {"월", "화", "수", "목", "금"};
                            for(String d : days) {
                                weeklyMeals.add(new DailyMeal(d, "정보 없음", "정보 없음", "정보 없음"));
                            }
                        }

                        if (viewPager != null) {
                            DailyMealAdapter adapter = new DailyMealAdapter(getActivity(), weeklyMeals);
                            viewPager.setAdapter(adapter);

                            int todayIndex = getTodayIndex();
                            if (todayIndex < weeklyMeals.size()) {
                                viewPager.setCurrentItem(todayIndex, false);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "UI 업데이트 에러: " + e.getMessage(), e);
                    }
                });
            }
        }).start();
    }

    /**
     * 기숙사 식당 HTML 파싱
     */
    private void parseDormitory(Document doc, List<DailyMeal> weeklyMeals) {
        try {
            Element table = doc.select("div.view_cont table").first();
            if (table == null) table = doc.select("table").first();
            if (table == null) return;

            String[] days = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};
            for (String day : days) weeklyMeals.add(new DailyMeal(day, "", "", ""));

            Elements rows = table.select("tbody tr");
            if (rows.isEmpty()) rows = table.select("tr");

            String currentType = "아침";
            for (Element row : rows) {
                String text = row.text();
                if (text.contains("월") && text.contains("화")) continue;

                Elements cols = row.select("td");
                if (cols.isEmpty()) continue;

                String firstCol = cols.get(0).text();
                if (firstCol.contains("아침") || firstCol.contains("조식")) currentType = "아침";
                else if (firstCol.contains("점심") || firstCol.contains("중식")) currentType = "점심";
                else if (firstCol.contains("저녁") || firstCol.contains("석식")) currentType = "저녁";

                // 코스명 추출
                String courseName = "";
                if (firstCol.length() <= 2 && (firstCol.contains("A") || firstCol.contains("B") || firstCol.contains("C"))) {
                    courseName = "[" + firstCol.trim() + "코스]";
                } else if (cols.size() > 1) {
                    String secondCol = cols.get(1).text();
                    if (secondCol.length() <= 2 && (secondCol.contains("A") || secondCol.contains("B") || secondCol.contains("C"))) {
                        courseName = "[" + secondCol.trim() + "코스]";
                    }
                }
                if (courseName.isEmpty()) courseName = "[단일 메뉴]";

                int dateIndex = 0;
                for (Element col : cols) {
                    String menu = col.text().trim();
                    if (menu.length() < 3 || menu.contains("아침") || menu.contains("점심") || menu.contains("저녁") || menu.equals("A") || menu.equals("B") || menu.equals("C")) continue;
                    if (dateIndex >= weeklyMeals.size()) break;

                    String formattedMenu = formatDormMenu(menu);
                    String finalMenuText = courseName + "\n" + formattedMenu;

                    DailyMeal dayMeal = weeklyMeals.get(dateIndex);
                    if (currentType.equals("아침")) dayMeal.setBreakfast(appendBlock(dayMeal.getBreakfast(), finalMenuText));
                    else if (currentType.equals("점심")) dayMeal.setLunch(appendBlock(dayMeal.getLunch(), finalMenuText));
                    else if (currentType.equals("저녁")) dayMeal.setDinner(appendBlock(dayMeal.getDinner(), finalMenuText));
                    dateIndex++;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "기숙사 파싱 에러: " + e.getMessage(), e);
        }
    }

    /**
     * 기숙사 메뉴 포맷 정리
     */
    private String formatDormMenu(String rawMenu) {
        if (rawMenu == null) return "";
        return rawMenu.replaceAll("\\)\\s+", ")\n").trim();
    }

    /**
     * 일반 식당 HTML 파싱
     */
    private void parseStandard(Document doc, List<DailyMeal> weeklyMeals) {
        try {
            Elements tables = doc.select("table.diet_table");
            if (tables.isEmpty()) tables = doc.select("table");
            if (tables.isEmpty()) return;

            Element table = tables.first();
            Elements rows = table.select("tr");

            // 날짜 헤더 추출
            Elements dateCols = null;
            for (Element row : rows) {
                if (row.text().contains("월") && row.text().contains("화")) {
                    dateCols = row.select("th, td");
                    break;
                }
            }

            if (dateCols != null) {
                int dayCount = 0;
                for (Element col : dateCols) {
                    String text = col.text();
                    if (text.contains("월") || text.contains("화") || text.contains("수") || text.contains("목") || text.contains("금")) {
                        weeklyMeals.add(new DailyMeal(text, "운영 안 함", "운영 안 함", "운영 안 함"));
                        dayCount++;
                        if (dayCount >= 5) break;
                    }
                }
            }

            if (weeklyMeals.isEmpty()) {
                String[] days = {"월", "화", "수", "목", "금"};
                for(String d : days) weeklyMeals.add(new DailyMeal(d, "운영 안 함", "운영 안 함", "운영 안 함"));
            }

            // 식사 타입별 메뉴 추출
            for (Element row : rows) {
                if (row.text().contains("월") && row.text().contains("화")) continue;

                String mealType = row.select("th").text();
                if (mealType.isEmpty()) {
                    Element firstTd = row.select("td").first();
                    if (firstTd != null) mealType = firstTd.text();
                }

                Elements cols = row.select("td");
                List<String> menus = new ArrayList<>();
                for (Element col : cols) {
                    if (col.text().length() < 5 && (col.text().contains("식") || col.text().contains("운영"))) continue;
                    String menu = cleanAndMergeMenu(col.html());
                    menus.add(menu);
                }

                int limit = Math.min(menus.size(), weeklyMeals.size());
                for (int i = 0; i < limit; i++) {
                    String menu = menus.get(i);
                    if (menu.isEmpty()) continue;

                    if (mealType.contains("조식") || mealType.contains("아침")) {
                        weeklyMeals.get(i).setBreakfast(menu);
                    } else if (mealType.contains("중식") || mealType.contains("점심")) {
                        String current = weeklyMeals.get(i).getLunch();
                        if (!current.contains("운영 안 함")) weeklyMeals.get(i).setLunch(current + "\n" + menu);
                        else weeklyMeals.get(i).setLunch(menu);
                    } else if (mealType.contains("석식") || mealType.contains("저녁")) {
                        String current = weeklyMeals.get(i).getDinner();
                        if (!current.contains("운영 안 함")) weeklyMeals.get(i).setDinner(current + "\n" + menu);
                        else weeklyMeals.get(i).setDinner(menu);
                    } else {
                        String current = weeklyMeals.get(i).getLunch();
                        if (!current.contains("운영 안 함")) weeklyMeals.get(i).setLunch(current + "\n" + menu);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "일반 식당 파싱 에러: " + e.getMessage(), e);
        }
    }

    /**
     * HTML 메뉴 텍스트 정리 및 병합
     */
    private String cleanAndMergeMenu(String rawHtml) {
        try {
            String text = rawHtml.replace("<br>", "\n")
                    .replaceAll("<[^>]*>", "")
                    .replace("&lt;", "<")
                    .replace("&gt;", ">")
                    .replace("&amp;", "&")
                    .trim();

            String[] lines = text.split("\n");
            List<String> resultLines = new ArrayList<>();

            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                boolean isParenthesisLine = line.startsWith("(");
                if (isParenthesisLine && !resultLines.isEmpty()) {
                    int lastIdx = resultLines.size() - 1;
                    String lastLine = resultLines.get(lastIdx);
                    if (lastLine.startsWith("*")) {
                        if (lastIdx > 0) {
                            String foodLine = resultLines.get(lastIdx - 1);
                            resultLines.set(lastIdx - 1, foodLine + " " + line);
                        } else {
                            resultLines.add(line);
                        }
                    } else {
                        resultLines.set(lastIdx, lastLine + " " + line);
                    }
                } else {
                    resultLines.add(line);
                }
            }

            StringBuilder sb = new StringBuilder();
            for (String l : resultLines) sb.append(l).append("\n");
            return sb.toString().trim();
        } catch (Exception e) {
            Log.e(TAG, "메뉴 정리 에러: " + e.getMessage(), e);
            return rawHtml;
        }
    }

    /**
     * 기존 블록에 새 블록 추가
     */
    private String appendBlock(String current, String newBlock) {
        if (newBlock.isEmpty() || newBlock.contains("운영없음")) return current;
        if (current.isEmpty()) return newBlock;
        return current + "\n\n" + newBlock;
    }

    /**
     * 오늘 요일에 해당하는 인덱스 반환
     */
    private int getTodayIndex() {
        try {
            Calendar calendar = Calendar.getInstance();
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
                return dayOfWeek - Calendar.MONDAY;
            }
        } catch (Exception e) {
            Log.e(TAG, "요일 계산 에러: " + e.getMessage(), e);
        }
        return 0;
    }
}