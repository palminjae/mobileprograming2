package com.example.tpt.ui.meal;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class WeeklyMealFragment extends Fragment {

    private ViewPager2 viewPager;
    private TextView tvTitle;
    private String cafeteriaUrl;
    private String cafeteriaName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekly_meal, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tvTitle = view.findViewById(R.id.tv_cafeteria_title);

        if (getArguments() != null) {
            cafeteriaUrl = getArguments().getString("url");
            cafeteriaName = getArguments().getString("title");
            tvTitle.setText(cafeteriaName + " 주간 식단");
        }

        startCrawling();
        return view;
    }

    private void startCrawling() {
        new Thread(() -> {
            List<DailyMeal> weeklyMeals = new ArrayList<>();
            try {
                String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
                Document doc = Jsoup.connect(cafeteriaUrl)
                        .userAgent(userAgent)
                        .timeout(30000)
                        .get();

                if (cafeteriaUrl.contains("dorm") || cafeteriaUrl.contains("nttSn")) {
                    parseDormitory(doc, weeklyMeals);
                } else {
                    parseStandard(doc, weeklyMeals);
                }

            } catch (Exception e) {
                e.printStackTrace();
                weeklyMeals.clear();
                weeklyMeals.add(new DailyMeal("오류 발생", e.getMessage(), "다시 시도해주세요", ""));
            }

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (weeklyMeals.isEmpty()) {
                        Toast.makeText(getContext(), "데이터 없음", Toast.LENGTH_SHORT).show();
                    } else {
                        DailyMealAdapter adapter = new DailyMealAdapter(getActivity(), weeklyMeals);
                        viewPager.setAdapter(adapter);

                        int todayIndex = getTodayIndex();
                        if (todayIndex < weeklyMeals.size()) {
                            viewPager.setCurrentItem(todayIndex, false);
                        }
                    }
                });
            }
        }).start();
    }
    private void parseDormitory(Document doc, List<DailyMeal> weeklyMeals) {
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
    }
    private String formatDormMenu(String rawMenu) {
        if (rawMenu == null) return "";
        return rawMenu.replaceAll("\\)\\s+", ")\n").trim();
    }
    private void parseStandard(Document doc, List<DailyMeal> weeklyMeals) {
        Elements tables = doc.select("table.diet_table");
        if (tables.isEmpty()) tables = doc.select("table");
        if (tables.isEmpty()) return;
        Element table = tables.first();
        Elements rows = table.select("tr");
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
    }
    private String cleanAndMergeMenu(String rawHtml) {
        String text = rawHtml.replace("<br>", "\n").replaceAll("<[^>]*>", "").replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").trim();
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
    }
    private String appendBlock(String current, String newBlock) {
        if (newBlock.isEmpty() || newBlock.contains("운영없음")) return current;
        if (current.isEmpty()) return newBlock;
        return current + "\n\n" + newBlock;
    }
    private int getTodayIndex() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek >= Calendar.MONDAY && dayOfWeek <= Calendar.FRIDAY) {
            return dayOfWeek - Calendar.MONDAY;
        }
        return 0;
    }
}