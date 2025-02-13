package com.unimib.triptales.ui.homepage.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.unimib.triptales.R;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.repository.diary.IDiaryRepository;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.ui.homepage.viewmodel.HomeViewModel;
import com.unimib.triptales.util.ServiceLocator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private final List<EventDay> events = new ArrayList<>();
    private final HashMap<Calendar, String> diaryNameMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        IDiaryRepository diaryRepository = ServiceLocator.getINSTANCE().getDiaryRepository(getContext());
        HomeViewModel homeViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(diaryRepository, requireActivity().getApplication())).get(HomeViewModel.class);
        calendarView = view.findViewById(R.id.calendarView);

        calendarView.setOnCalendarDayClickListener(eventDay -> {
            Calendar clickedDay = normalizeCalendar(eventDay.getCalendar());
            String diaryName = diaryNameMap.get(clickedDay);

            if (diaryName != null) {
                Toast.makeText(getContext(), getString(R.string.viaggio) + " "
                        + diaryName, Toast.LENGTH_SHORT).show();
            }
        });

        homeViewModel.getDiariesLiveData().observe(getViewLifecycleOwner(), this::updateCalendar);
    }

    private void updateCalendar(List<Diary> diaries) {
        events.clear();
        diaryNameMap.clear();

        for (Diary diary : diaries) {
            String startDate = diary.getStartDate();
            String endDate = diary.getEndDate();
            String diaryName = diary.getName();

            Calendar startCalendar = convertStringToCalendar(startDate);
            Calendar endCalendar = convertStringToCalendar(endDate);

            diaryNameMap.put(normalizeCalendar(startCalendar), diaryName);
            diaryNameMap.put(normalizeCalendar(endCalendar), diaryName);

            events.add(new EventDay(startCalendar, R.drawable.baseline_flight_takeoff_24));
            events.add(new EventDay(endCalendar, R.drawable.baseline_flight_land_24));
        }
        calendarView.setEvents(events);
    }

    private Calendar normalizeCalendar(Calendar calendar) {
        Calendar normalized = Calendar.getInstance();
        normalized.set(calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        normalized.set(Calendar.MILLISECOND, 0);
        return normalized;
    }

    private Calendar convertStringToCalendar(String dateString) {
        String[] dateParts = dateString.split("/");
        int day = Integer.parseInt(dateParts[0]);
        int month = Integer.parseInt(dateParts[1]) - 1;
        int year = Integer.parseInt(dateParts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar;
    }
}
