package com.unimib.triptales.ui.diary.fragment;

import static com.unimib.triptales.util.Constants.ADDED;
import static com.unimib.triptales.util.Constants.DELETED;
import static com.unimib.triptales.util.Constants.INVALID_DELETE;
import static com.unimib.triptales.util.Constants.UPDATED;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.adapters.TasksRecyclerAdapter;
import com.unimib.triptales.model.Task;
import com.unimib.triptales.repository.task.ITaskRepository;
import com.unimib.triptales.ui.diary.DiaryActivity;
import com.unimib.triptales.ui.diary.overlay.OverlayAddEditTask;
import com.unimib.triptales.ui.diary.overlay.OverlayDelete;
import com.unimib.triptales.ui.diary.viewmodel.TaskViewModel;
import com.unimib.triptales.ui.diary.viewmodel.ViewModelFactory;
import com.unimib.triptales.util.Constants;
import com.unimib.triptales.util.ServiceLocator;

import java.util.List;

public class TasksFragment extends Fragment {

    private FloatingActionButton addTaskButton;
    private OverlayAddEditTask overlayAddEditTask;
    private FloatingActionButton editTaskButton;
    private FloatingActionButton deleteTaskButton;
    private TextView noTasksTextView;
    private TaskViewModel taskViewModel;
    private TasksRecyclerAdapter tasksRecyclerAdapter;
    private boolean bAdd;
    private boolean bEdit;
    private OverlayDelete overlayDelete;
    private FrameLayout darkBackground;

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
                new ViewModelFactory(taskRepository, requireActivity().getApplication())).get(TaskViewModel.class);

        taskViewModel.deselectAllTasks();

        RecyclerView recyclerViewTasks = view.findViewById(R.id.recyclerViewTasks);
        tasksRecyclerAdapter = new TasksRecyclerAdapter(getContext());
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewTasks.setAdapter(tasksRecyclerAdapter);

        tasksRecyclerAdapter.setOnTaskClickListener(task ->
                taskViewModel.toggleTaskSelection(task));

        tasksRecyclerAdapter.setOnTaskCheckBoxClickListener(task ->
                taskViewModel.toggleTaskCheck(task));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ConstraintLayout diaryRootLayout = requireActivity().findViewById(R.id.rootLayoutDiary);
        darkBackground = requireActivity().findViewById(R.id.dark_background);

        addTaskButton = view.findViewById(R.id.addTaskButton);
        editTaskButton = view.findViewById(R.id.modifyTask);
        deleteTaskButton = view.findViewById(R.id.deleteTask);
        bAdd = false;
        bEdit = false;

        // gestione aggiunta e modifica di attività
        overlayAddEditTask = new OverlayAddEditTask(diaryRootLayout, requireContext(), taskViewModel);

        noTasksTextView = view.findViewById(R.id.noTasksString);

        taskViewModel.getTasksLiveData().observe(getViewLifecycleOwner(), tasks -> {
            if(tasks != null) {
                tasksRecyclerAdapter.setTasksList(tasks);
                if (tasks.isEmpty()) {
                    noTasksTextView.setVisibility(View.VISIBLE);
                } else {
                    noTasksTextView.setVisibility(View.GONE);
                }
            }
        });

        taskViewModel.getSelectedTasksLiveData().observe(getViewLifecycleOwner(),
                selectedTasks -> {
            if(selectedTasks != null){
                if(selectedTasks.size() == 1){
                    if(Boolean.TRUE.equals(taskViewModel.getTaskOverlayVisibility().getValue())){
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
        });

        taskViewModel.getErrorLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if(errorMessage != null){
                Toast.makeText(requireActivity(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        taskViewModel.getTaskEvent().observe(getViewLifecycleOwner(), message -> {
            if(message != null){
                switch (message) {
                    case ADDED:
                        Toast.makeText(requireActivity(), R.string.snackbarTaskAdded,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case UPDATED:
                        Toast.makeText(requireActivity(), R.string.snackbarTaskUpdated,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case DELETED:
                        Toast.makeText(requireActivity(), R.string.snackbarTaskDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                    case INVALID_DELETE:
                        Toast.makeText(requireActivity(), R.string.snackbarTaskNotDeleted,
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        addTaskButton.setOnClickListener(addTaskButtonListener -> {
            bAdd = true;
            taskViewModel.setTaskOverlayVisibility(true);
            overlayAddEditTask.showOverlay(bAdd, bEdit);
        });

        taskViewModel.getTaskOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                disableSwipeAndButtons();
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                enableSwipeAndButtons(view);
                darkBackground.setVisibility(View.GONE);
                if(bAdd){
                    bAdd = false;
                } else if(bEdit){
                    bEdit = false;
                }
            }
        });

        editTaskButton.setOnClickListener(editTaskButtonListener -> {
            bEdit = true;
            taskViewModel.setTaskOverlayVisibility(true);
            overlayAddEditTask.showOverlay(bAdd, bEdit);
        });

        // gestione eliminazione di attività
        overlayDelete = new OverlayDelete(diaryRootLayout, requireContext(), taskViewModel);

        taskViewModel.getDeleteOverlayVisibility().observe(getViewLifecycleOwner(), visible -> {
            if(visible){
                disableSwipeAndButtons();
                darkBackground.setVisibility(View.VISIBLE);
            } else {
                enableSwipeAndButtons(view);
                darkBackground.setVisibility(View.GONE);
                List<Task> selectedTasks = taskViewModel.getSelectedTasksLiveData().getValue();
                if(selectedTasks != null && !selectedTasks.isEmpty()){
                    addTaskButton.setEnabled(false);
                }
            }
        });

        deleteTaskButton.setOnClickListener(deleteTaskButtonListener -> {
            taskViewModel.setDeleteOverlayVisibility(true);
            overlayDelete.showOverlay();
            TextView deleteText = requireActivity().findViewById(R.id.deleteText);
            TextView deleteDescriptionText = requireActivity().findViewById(R.id.deleteDescriptionText);
            deleteText.setText(R.string.delete_task);
            deleteDescriptionText.setText(R.string.delete_task_description);
        });
    }

    private void disableSwipeAndButtons(){
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(false);
        addTaskButton.setEnabled(false);
        editTaskButton.setEnabled(false);
        deleteTaskButton.setEnabled(false);
    }

    private void enableSwipeAndButtons(View view){
        ((DiaryActivity) requireActivity()).setViewPagerSwipeEnabled(true);
        Constants.hideKeyboard(view, requireActivity());
        addTaskButton.setEnabled(true);
        editTaskButton.setEnabled(true);
        deleteTaskButton.setEnabled(true);
    }
}