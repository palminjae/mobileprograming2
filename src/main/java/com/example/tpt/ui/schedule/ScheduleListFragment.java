package com.example.tpt.ui.schedule;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tpt.R;
import com.example.tpt.manager.ScheduleManager;
import com.example.tpt.model.Schedule;
import java.util.ArrayList;
import java.util.List;

public class ScheduleListFragment extends Fragment {

    private View rootView;
    private RecyclerView recyclerScheduleList;
    private View tvEmpty;
    private View chipAll;
    private View chipClass;
    private View chipPersonal;

    private ScheduleAdapter adapter;
    private FilterType currentFilter = FilterType.ALL;

    enum FilterType {
        ALL, CLASS, PERSONAL
    }

    // 뷰를 생성하는 함수
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_schedule_list, container, false);
        return rootView;
    }

    // 뷰 생성 후 초기화를 진행하는 함수
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        try {
            findViews();
            setupRecyclerView();
            setupFilters();
            loadSchedules();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // UI 요소들을 찾는 함수
    private void findViews() {
        recyclerScheduleList = rootView.findViewById(R.id.recyclerScheduleList);
        tvEmpty = rootView.findViewById(R.id.tvEmpty);
        chipAll = rootView.findViewById(R.id.chipAll);
        chipClass = rootView.findViewById(R.id.chipClass);
        chipPersonal = rootView.findViewById(R.id.chipPersonal);
    }

    // 리사이클러뷰를 설정하는 함수
    private void setupRecyclerView() {
        adapter = new ScheduleAdapter();
        recyclerScheduleList.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerScheduleList.setAdapter(adapter);
    }

    // 필터 버튼들의 클릭 리스너를 설정하는 함수
    private void setupFilters() {
        chipAll.setOnClickListener(v -> {
            currentFilter = FilterType.ALL;
            loadSchedules();
        });

        chipClass.setOnClickListener(v -> {
            currentFilter = FilterType.CLASS;
            loadSchedules();
        });

        chipPersonal.setOnClickListener(v -> {
            currentFilter = FilterType.PERSONAL;
            loadSchedules();
        });
    }

    // 필터 조건에 따라 일정을 불러와 화면을 갱신하는 함수
    private void loadSchedules() {
        try {
            List<Schedule> schedules;
            switch(currentFilter) {
                case ALL:
                    schedules = ScheduleManager.getInstance().getAllSchedules();
                    break;
                case CLASS:
                    schedules = ScheduleManager.getInstance().getClassSchedules();
                    break;
                case PERSONAL:
                    schedules = ScheduleManager.getInstance().getPersonalSchedules();
                    break;
                default:
                    schedules = new ArrayList<>();
            }

            if (adapter != null) {
                adapter.updateSchedules(schedules);
            }

            if (schedules.isEmpty()) {
                recyclerScheduleList.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                recyclerScheduleList.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 외부에서 목록 새로고침을 요청할 때 사용하는 함수
    public void refreshList() {
        try {
            loadSchedules();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rootView = null;
    }

    class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {

        private List<Schedule> schedules = new ArrayList<>();

        // 데이터셋을 업데이트하고 화면을 갱신하는 함수
        public void updateSchedules(List<Schedule> newSchedules) {
            schedules.clear();
            schedules.addAll(newSchedules);
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_schedule, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.bind(schedules.get(position));
        }

        @Override
        public int getItemCount() {
            return schedules.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tvTitle;
            private TextView tvTime;
            private TextView tvLocation;
            private View viewColor;
            private View btnDelete;

            ViewHolder(View view) {
                super(view);
                tvTitle = view.findViewById(R.id.tvTitle);
                tvTime = view.findViewById(R.id.tvTime);
                tvLocation = view.findViewById(R.id.tvLocation);
                viewColor = view.findViewById(R.id.viewColor);
                btnDelete = view.findViewById(R.id.btnDelete);
            }

            // 일정 데이터를 뷰에 바인딩하고 삭제 기능을 설정하는 함수
            void bind(Schedule schedule) {
                tvTitle.setText(schedule.getTitle());
                tvTime.setText(schedule.getDayOfWeek() + " " +
                        schedule.getStartTime() + " - " + schedule.getEndTime());
                tvLocation.setText(schedule.getLocation().isEmpty() ?
                        "장소 없음" : schedule.getLocation());
                try {
                    viewColor.setBackgroundColor(Color.parseColor(schedule.getColor()));
                } catch (Exception e) {
                    viewColor.setBackgroundColor(Color.parseColor("#2196F3"));
                }

                btnDelete.setOnClickListener(v -> {
                    new android.app.AlertDialog.Builder(v.getContext())
                            .setTitle("일정 삭제")
                            .setMessage("'" + schedule.getTitle() + "' 일정을 삭제하시겠습니까?")
                            .setPositiveButton("삭제", (dialog, which) -> {
                                boolean success = ScheduleManager.getInstance().deleteSchedule(schedule.getId());
                                if (success) {
                                    int position = getAdapterPosition();
                                    if (position != RecyclerView.NO_POSITION) {
                                        schedules.remove(position);
                                        notifyItemRemoved(position);

                                        if (schedules.isEmpty()) {
                                            recyclerScheduleList.setVisibility(View.GONE);
                                            tvEmpty.setVisibility(View.VISIBLE);
                                        }

                                        android.widget.Toast.makeText(v.getContext(),
                                                "일정이 삭제되었습니다", android.widget.Toast.LENGTH_SHORT).show();
                                    }
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                });
            }
        }
    }
}