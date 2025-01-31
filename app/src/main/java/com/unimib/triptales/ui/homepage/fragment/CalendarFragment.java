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

import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.ui.homepage.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    SharedViewModel sharedViewModel;
    List<Calendar> startDates = new ArrayList<>();
    List<Calendar> endDates = new ArrayList<>();
    List<EventDay> events = new ArrayList<>();
    DiaryDao diaryDao;

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

        calendarView = view.findViewById(R.id.calendarView);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        diaryDao = AppRoomDatabase.getDatabase(getContext()).diaryDao();

        sharedViewModel.getStartDate().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String parameter) {
                String[] date = extractDate(parameter);
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1])-1;
                int year = Integer.parseInt(date[2]);
                Calendar startCalendar = Calendar.getInstance();
                startCalendar.set(year, month, day);
                startDates.add(startCalendar);
                loadEvents();
            }
        });

        sharedViewModel.getEndDate().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String parameter) {
                String[] date = extractDate(parameter);
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1])-1;
                int year = Integer.parseInt(date[2]);
                Calendar endCalendar = Calendar.getInstance();
                endCalendar.set(year, month, day);
                endDates.add(endCalendar);
                loadEvents();
            }
        });
    }

    private void loadEvents(){
        events.clear();
        for (Calendar startCalendar : startDates) {
            events.add(new EventDay(startCalendar, R.drawable.baseline_flight_takeoff_24));
        }
        for (Calendar endCalendar : endDates) {
            events.add(new EventDay(endCalendar, R.drawable.baseline_flight_land_24));
        }
        calendarView.setEvents(events);
    }

    private String[] extractDate(String parameter){
        String[] date = new String[3];
        if(parameter.charAt(1) == '/'){
            date[0] = parameter.substring(0, 1);
            parameter = parameter.substring(2);
        }else{
            date[0] = parameter.substring(0, 2);
            parameter = parameter.substring(3);
        }

        String month;
        if(parameter.charAt(1) == '/'){
            date[1] = parameter.substring(0, 1);
            parameter = parameter.substring(2);
        }else{
            date[1] = parameter.substring(0, 2);
            parameter = parameter.substring(3);
        }
        date[2] = parameter;

        return date;
    }
}
