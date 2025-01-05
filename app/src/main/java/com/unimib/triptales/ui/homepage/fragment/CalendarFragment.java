package com.unimib.triptales.ui.homepage.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.datepicker.DayViewDecorator;
import com.unimib.triptales.R;
import com.unimib.triptales.model.CountryPolygon;
import com.unimib.triptales.ui.homepage.viewmodel.SharedViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    SharedViewModel sharedViewModel;
    List<Calendar> datePartenza = new ArrayList<>();
    List<Calendar> dateRitorno = new ArrayList<>();
    List<EventDay> events = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inizializza il calendario
        calendarView = view.findViewById(R.id.calendarView);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getStartDate().observe(requireActivity(), new Observer<String>() {
            @Override
            public void onChanged(String parameter) {
                String[] date = extractDate(parameter);
                int day = Integer.parseInt(date[0]);
                int month = Integer.parseInt(date[1])-1;
                int year = Integer.parseInt(date[2]);
                Calendar startDate = Calendar.getInstance();
                startDate.set(year, month, day);
                datePartenza.add(startDate);
                Log.d("DEBUG", "Lista date partenza: "+datePartenza.size());
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
                Calendar endDate = Calendar.getInstance();
                endDate.set(year, month, day);
                dateRitorno.add(endDate);
                Log.d("DEBUG", "Lista date ritorno: "+dateRitorno.size());
                loadEvents();
            }
        });

        /*// Seleziona la data di partenza e ritorno quando l'utente clicca su una data
        calendarView.setOnDayClickListener(eventDay -> {
            Calendar selectedDate = eventDay.getCalendar();

            if (startDate == null || endDate != null) {
                // Se non è stata ancora selezionata la data di partenza o se la data di ritorno è già stata impostata
                startDate = selectedDate;
                endDate = null;
                Toast.makeText(requireActivity(), "Data di partenza selezionata: " + selectedDate.getTime(), Toast.LENGTH_SHORT).show();
            } else {
                // Se la data di ritorno viene selezionata
                if (selectedDate.after(startDate)) {
                    endDate = selectedDate;
                    Toast.makeText(requireActivity(), "Data di ritorno selezionata: " + selectedDate.getTime(), Toast.LENGTH_SHORT).show();
                }
            }

            // Ricarica gli eventi (per applicare i cambiamenti)
            loadEvents();
        });

        // Aggiungi eventi alle date selezionate
        loadEvents();*/
    }

    private void loadEvents() {
        Log.d("DEBUG", "Lista date partenza: "+datePartenza.size());
        Log.d("DEBUG", "Lista date ritorno: "+dateRitorno.size());
        events.clear();
        for(Calendar c : datePartenza) {
            events.add(new EventDay(c, R.drawable.baseline_flight_takeoff_24));
        }
        for(Calendar c : dateRitorno) {
            events.add(new EventDay(c, R.drawable.baseline_flight_land_24));
        }
        Log.d("DEBUG", "Lista eventi: "+events.size());
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
