package com.unimib.triptales.ui.diario.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.TasksRecyclerAdapter;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.TaskDao;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.ui.diario.DiaryActivity;
import com.unimib.triptales.util.Constants;
import java.util.Iterator;
import java.util.List;

public class TasksFragment extends Fragment {

    FloatingActionButton addTaskButton;
    View overlay_add_task;
    ConstraintLayout rootLayoutCheckList;
    LayoutInflater inflater;
    ImageButton backButtonTask;
    Button saveTask;
    EditText editTextTaskName;
    String inputTaskName;
    FloatingActionButton modifyTask;
    FloatingActionButton deleteTask;
    CheckBox cardListCheckBox;
    View overlay_modify_task;
    EditText editTextModifiedTaskName;
    String inputModifiedTaskName;
    ImageButton backButtonModifyTask;
    Button saveModifiedTask;
    TextView noTasksString;
    TaskDao taskDao;
    private List<Task> tasksList;
    private List<Task> selectedTasks;
    RecyclerView recyclerViewTasks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        taskDao = AppRoomDatabase.getDatabase(getContext()).taskDao();
        tasksList = taskDao.getAll();
        for(Task t : tasksList){
            t.setSelected(false);
            taskDao.updateIsSelected(t.getId(), false);
        }
        return inflater.inflate(R.layout.fragment_check_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rootLayoutCheckList = view.findViewById(R.id.rootLayoutCheckList);
        inflater = LayoutInflater.from(view.getContext());
        taskDao = AppRoomDatabase.getDatabase(getContext()).taskDao();

        addTaskButton = view.findViewById(R.id.addTaskButton);
        modifyTask = view.findViewById(R.id.modifyTask);
        deleteTask = view.findViewById(R.id.deleteTask);

        recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        TasksRecyclerAdapter recyclerAdapter = new TasksRecyclerAdapter(tasksList,  getContext(),
                addTaskButton, modifyTask, deleteTask);
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTasks.setAdapter(recyclerAdapter);

        overlay_add_task = inflater.inflate(R.layout.overlay_add_task, rootLayoutCheckList, false);
        rootLayoutCheckList.addView(overlay_add_task);
        overlay_add_task.setVisibility(View.GONE);

        backButtonTask = view.findViewById(R.id.backButtonTask);
        saveTask = view.findViewById(R.id.saveTask);
        noTasksString = view.findViewById(R.id.noTasksString);

        if(tasksList.isEmpty()){
            noTasksString.setVisibility(View.VISIBLE);
        } else {
            noTasksString.setVisibility(View.GONE);
        }

        backButtonTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_task.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                Constants.hideKeyboard(view, requireActivity());
                addTaskButton.setVisibility(View.VISIBLE);
            }
        });

        editTextTaskName = view.findViewById(R.id.inputTaskName);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_add_task.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
                addTaskButton.setVisibility(View.GONE);
                editTextTaskName.setText("");
            }
        });

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Task currentTask;
                inputTaskName = editTextTaskName.getText().toString().trim();

                if (inputTaskName.isEmpty()) {
                    editTextTaskName.setError("Inserisci un nome");
                } else {
                    editTextTaskName.setError(null);
                    currentTask = new Task(inputTaskName, false, false);

                    long id = taskDao.insert(currentTask);
                    currentTask.setId((int) id);
                    tasksList.add(currentTask);
                    recyclerAdapter.notifyItemInserted(tasksList.size() - 1);

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_add_task.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addTaskButton.setVisibility(View.VISIBLE);
                    noTasksString.setVisibility(View.GONE);
                }
            }
        });

        overlay_modify_task = inflater.inflate(R.layout.overlay_modify_task, rootLayoutCheckList, false);
        rootLayoutCheckList.addView(overlay_modify_task);
        overlay_modify_task.setVisibility(View.GONE);

        backButtonModifyTask = view.findViewById(R.id.backButtonModifyTask);
        saveModifiedTask = view.findViewById(R.id.saveModifiedTask);
        editTextModifiedTaskName = view.findViewById(R.id.inputModifiedTaskName);

        modifyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_task.setVisibility(View.VISIBLE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);

                selectedTasks = taskDao.getSelectedTasks();
                Task currentTask = selectedTasks.get(0);
                editTextModifiedTaskName.setText(currentTask.getName());

                addTaskButton.setVisibility(View.GONE);
                modifyTask.setVisibility(View.GONE);
                deleteTask.setVisibility(View.GONE);
            }
        });

        backButtonModifyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overlay_modify_task.setVisibility(View.GONE);
                ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                Constants.hideKeyboard(view, requireActivity());
                addTaskButton.setVisibility(View.VISIBLE);
                modifyTask.setVisibility(View.VISIBLE);
                deleteTask.setVisibility(View.VISIBLE);
            }
        });

        saveModifiedTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selectedTasks = taskDao.getSelectedTasks();
                Task currentTask = selectedTasks.get(0);

                inputModifiedTaskName = editTextModifiedTaskName.getText().toString().trim();

                if (inputModifiedTaskName.isEmpty()) {
                    editTextModifiedTaskName.setError("Inserisci il nome dell'attività");
                } else {
                    editTextModifiedTaskName.setError(null);
                    currentTask.setName(inputTaskName);
                    taskDao.updateName(currentTask.getId(), inputTaskName);

                    currentTask.setSelected(false);
                    taskDao.updateIsSelected(currentTask.getId(), false);

                    int position = tasksList.indexOf(currentTask);
                    if (position != -1) {
                        tasksList.set(position, currentTask);
                        recyclerAdapter.notifyItemChanged(position);
                    }

                    Constants.hideKeyboard(view, requireActivity());
                    overlay_modify_task.setVisibility(View.GONE);
                    ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
                    addTaskButton.setVisibility(View.VISIBLE);
                }
            }
        });

        deleteTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int index = 0;
                Iterator<Task> iterator = tasksList.iterator();
                while (iterator.hasNext()) {
                    Task t = iterator.next();
                    if (t.isSelected()) {
                        iterator.remove();
                        recyclerAdapter.notifyItemRemoved(index);
                        taskDao.delete(t);
                    } else {
                        index++;
                    }
                }
                modifyTask.setVisibility(View.GONE);
                deleteTask.setVisibility(View.GONE);
                addTaskButton.setEnabled(true);
                if(tasksList.isEmpty()){
                    noTasksString.setVisibility(View.VISIBLE);
                }
            }
        });

    }

}