package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.ADD_TASK;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.EDIT_TASK;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.TasksRecyclerAdapter;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.repository.task.ITaskRepository;
import com.unimib.triptales.ui.diary.DiaryActivity;
import com.unimib.triptales.ui.diary.viewmodel.TaskViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;

import java.util.List;

public class TasksFragment extends Fragment {

    private FloatingActionButton addTaskButton;
    private View overlay_add_edit_task;
    private EditText taskNameEditText;
    private FloatingActionButton editTaskButton;
    private FloatingActionButton deleteTaskButton;
    private TextView noTasksTextView;
    private TaskViewModel taskViewModel;
    private TasksRecyclerAdapter tasksRecyclerAdapter;
    private boolean bAdd;
    private boolean bEdit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);
        ITaskRepository taskRepository = ServiceLocator.getINSTANCE().getTaskRepository(getContext());
        taskViewModel = new ViewModelProvider(requireActivity(),
                new ViewModelFactory(taskRepository)).get(TaskViewModel.class);

        taskViewModel.deselectAllTasks();

        RecyclerView recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        tasksRecyclerAdapter = new TasksRecyclerAdapter(getContext());
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTasks.setAdapter(tasksRecyclerAdapter);

        tasksRecyclerAdapter.setOnTaskClickListener(new TasksRecyclerAdapter.OnTaskClickListener() {
            @Override
            public void onTaskClick(Task task) {
                taskViewModel.toggleTaskSelection(task);
            }
        });

        tasksRecyclerAdapter.setOnTaskCheckBoxClickListener(new TasksRecyclerAdapter.OnTaskCheckBoxClickListener() {
            @Override
            public void onTaskCheckBoxClick(Task task) {
                taskViewModel.toggleTaskCheck(task);
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout rootLayoutCheckList = view.findViewById(R.id.rootLayoutCheckList);
        LayoutInflater inflater = LayoutInflater.from(view.getContext());

        addTaskButton = view.findViewById(R.id.addTaskButton);
        editTaskButton = view.findViewById(R.id.modifyTask);
        deleteTaskButton = view.findViewById(R.id.deleteTask);
        bAdd = false;
        bEdit = false;

        overlay_add_edit_task = inflater.inflate(R.layout.overlay_add_edit_task, rootLayoutCheckList, false);
        rootLayoutCheckList.addView(overlay_add_edit_task);
        overlay_add_edit_task.setVisibility(View.GONE);

        ImageButton backButtonTask = view.findViewById(R.id.backButtonTask);
        Button saveTask = view.findViewById(R.id.saveTask);
        noTasksTextView = view.findViewById(R.id.noTasksString);

        taskViewModel.getTasksLiveData().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> tasks) {
                if(tasks != null) {
                    tasksRecyclerAdapter.setTasksList(tasks);
                    if (tasks.isEmpty()) {
                        noTasksTextView.setVisibility(View.VISIBLE);
                    } else {
                        noTasksTextView.setVisibility(View.GONE);
                    }
                }
            }
        });

        taskViewModel.getSelectedTasksLiveData().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
            @Override
            public void onChanged(List<Task> selectedTasks) {
                if(selectedTasks != null){
                    if(selectedTasks.size() == 1){
                        if(overlay_add_edit_task.getVisibility() == View.VISIBLE){
                            editTaskButton.setVisibility(View.GONE);
                            deleteTaskButton.setVisibility(View.GONE);
                        } else {
                            addTaskButton.setEnabled(false);
                            editTaskButton.setVisibility(View.VISIBLE);
                            deleteTaskButton.setVisibility(View.VISIBLE);
                        }
                    } else if(selectedTasks.size() == 2) {
                        addTaskButton.setEnabled(false);
                        editTaskButton.setVisibility(View.GONE);
                    } else if(selectedTasks.isEmpty()){
                        editTaskButton.setVisibility(View.GONE);
                        deleteTaskButton.setVisibility(View.GONE);
                        addTaskButton.setEnabled(true);
                    }
                }
            }
        });

        taskViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if(errorMessage != null){
                    Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

        taskViewModel.getTaskEvent().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String message) {
                if(message != null){
                    switch (message) {
                        case ADDED:
                            Toast.makeText(requireActivity(), R.string.snackbarTaskAdded, Toast.LENGTH_SHORT).show();
                            break;
                        case UPDATED:
                            Toast.makeText(requireActivity(), R.string.snackbarTaskUpdated, Toast.LENGTH_SHORT).show();
                            break;
                        case DELETED:
                            Toast.makeText(requireActivity(), R.string.snackbarTaskDeleted, Toast.LENGTH_SHORT).show();
                            break;
                        case INVALID_DELETE:
                            Toast.makeText(requireActivity(), R.string.snackbarTaskNotDeleted, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        });

        backButtonTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bEdit){
                    editTaskButton.setVisibility(View.VISIBLE);
                    deleteTaskButton.setVisibility(View.VISIBLE);
                }
                taskViewModel.setTaskOverlayVisibility(false);
            }
        });

        taskNameEditText = view.findViewById(R.id.inputTaskName);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bAdd = true;
                taskViewModel.setTaskOverlayVisibility(true);
            }
        });

        taskViewModel.getTaskOverlayVisibility().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean visible) {
                if(visible){
                    if(bAdd){
                        showOverlay(ADD_TASK);
                    } else if(bEdit){
                        showOverlay(EDIT_TASK);
                        editTaskButton.setVisibility(View.GONE);
                        deleteTaskButton.setVisibility(View.GONE);
                    }
                } else {
                    enableSwipeAndButtons(view);
                    if(bAdd){
                        hideOverlay(ADD_TASK);
                    } else if(bEdit){
                        hideOverlay(EDIT_TASK);
                    }
                }
            }
        });

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputTaskName = taskNameEditText.getText().toString().trim();

                boolean correct = taskViewModel.validateInputTask(inputTaskName);

                if(correct){
                    if(bAdd){
                        taskViewModel.insertTask(inputTaskName, getContext());
                    } else if(bEdit){
                        List<Task> selectedTasks = taskViewModel.getSelectedTasksLiveData().getValue();
                        if(selectedTasks != null && !selectedTasks.isEmpty()){
                            Task currentTask = selectedTasks.get(0);
                            taskViewModel.updateTask(currentTask, inputTaskName);
                            taskViewModel.deselectAllTasks();
                        }
                    }
                    taskViewModel.setTaskOverlayVisibility(false);
                }
            }
        });

        editTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bEdit = true;
                taskViewModel.setTaskOverlayVisibility(true);
            }
        });

        deleteTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskViewModel.deleteSelectedTasks();
            }
        });

    }

    private void disableSwipeAndButtons(){
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
        addTaskButton.setVisibility(View.GONE);
    }

    private void enableSwipeAndButtons(View view){
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
        Constants.hideKeyboard(view, requireActivity());
        addTaskButton.setVisibility(View.VISIBLE);
    }

    private void showOverlay(String overlayType){
        disableSwipeAndButtons();
        overlay_add_edit_task.setVisibility(View.VISIBLE);
        switch(overlayType){
            case ADD_TASK:
                taskNameEditText.setText("");
                break;
            case EDIT_TASK:
                populateGoalField();
        }
    }

    private void hideOverlay(String overlayType){
        overlay_add_edit_task.setVisibility(View.GONE);
        switch(overlayType){
            case ADD_TASK:
                bAdd = false;
                break;
            case EDIT_TASK:
                bEdit = false;
                break;
        }
    }

    private void populateGoalField(){
        List<Task> selectedTasks = taskViewModel.getSelectedTasksLiveData().getValue();
        if(selectedTasks != null && !selectedTasks.isEmpty()){
            Task currentTask = selectedTasks.get(0);
            taskNameEditText.setText(currentTask.getName());
        }
    }

}