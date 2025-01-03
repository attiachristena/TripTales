package com.unimib.triptales.ui.homepage.fragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.applandeo.materialcalendarview.CalendarDay;
import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.datepicker.DayViewDecorator;
import com.unimib.triptales.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarFragment extends Fragment {

    CalendarView calendarView;
    Calendar startDate, endDate;

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

        // Variabili per la data di partenza e ritorno
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        // Seleziona la data di partenza e ritorno quando l'utente clicca su una data
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
        loadEvents();
    }

    private void loadEvents() {
        // Lista di eventi da aggiungere
        List<EventDay> events = new ArrayList<>();

        // Se la data di partenza è stata selezionata, aggiungi l'evento con l'icona dell'aereo in partenza
        if (startDate != null) {
            events.add(new EventDay(startDate, R.drawable.baseline_flight_takeoff_24)); // Aereo che parte
        }

        // Se la data di ritorno è stata selezionata, aggiungi l'evento con l'icona dell'aereo in arrivo
        if (endDate != null) {
            events.add(new EventDay(endDate, R.drawable.baseline_flight_land_24)); // Aereo che atterra
        }

        // Aggiungi gli eventi al calendario
        calendarView.setEvents(events);

    }
}
