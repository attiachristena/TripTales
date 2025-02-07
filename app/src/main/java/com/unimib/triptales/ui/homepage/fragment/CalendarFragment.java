package com.unimib.triptales.ui.homepage.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.DiaryDao;
import com.unimib.triptales.model.Diary;
import com.unimib.triptales.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private List<EventDay> events = new ArrayList<>();
    private DiaryDao diaryDao;
    private HashMap<Calendar, String> diaryNameMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        diaryDao = AppRoomDatabase.getDatabase(getContext()).diaryDao();
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        calendarView = view.findViewById(R.id.calendarView);

        //con diaryViewModel osservare la lista dei diari
        calendarView.setOnCalendarDayClickListener(eventDay -> {
            Calendar clickedDay = normalizeCalendar(eventDay.getCalendar());
            String diaryName = diaryNameMap.get(clickedDay);

            if (diaryName != null) {
                Toast.makeText(getContext(), "Viaggio: " + diaryName, Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        loadCalendarData(SharedPreferencesUtils.getLoggedUserId());
    }

    private void updateCalendar(List<Diary> diaries) {
        // Pulisci la lista degli eventi precedenti
        events.clear();
        diaryNameMap.clear();

        for (Diary diary : diaries) {
            String startDate = diary.getStartDate();
            String endDate = diary.getEndDate();
            String diaryName = diary.getName();

            // Converti le date in oggetti Calendar
            Calendar startCalendar = convertStringToCalendar(startDate);
            Calendar endCalendar = convertStringToCalendar(endDate);

            // Aggiungi le date alla mappa per il Toast
            diaryNameMap.put(normalizeCalendar(startCalendar), diaryName);
            diaryNameMap.put(normalizeCalendar(endCalendar), diaryName);

            // Aggiungi gli eventi con le icone
            events.add(new EventDay(startCalendar, R.drawable.baseline_flight_takeoff_24));
            events.add(new EventDay(endCalendar, R.drawable.baseline_flight_land_24));
        }

        calendarView.setEvents(events);
    }

    private void loadCalendarData(String userId) {
        new Thread(() -> {
            List<Diary> diaries = diaryDao.getAllDiaries(userId);

            // Passa i dati al main thread
            requireActivity().runOnUiThread(() -> updateCalendar(diaries));
        }).start();
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
        int month = Integer.parseInt(dateParts[1]) - 1; // I mesi vanno da 0 a 11
        int year = Integer.parseInt(dateParts[2]);

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar;
    }

}
