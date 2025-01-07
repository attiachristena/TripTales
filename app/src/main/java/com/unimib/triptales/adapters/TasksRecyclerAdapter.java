package com.unimib.triptales.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.TaskDao;
import com.unimib.triptales.model.Task;
import java.util.List;

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.ViewHolder> {

    private List<Task> tasksList;
    private Context context;
    private FloatingActionButton addTask;
    private FloatingActionButton deleteTask;
    private FloatingActionButton modifyTask;

    public Context getContext() { return context; }
    public FloatingActionButton getAddTask() { return addTask; }
    public FloatingActionButton getDeleteTask() { return deleteTask; }
    public FloatingActionButton getModifyTask() { return modifyTask; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        CheckBox taskCheckBox;

        public ViewHolder(View view) {
            super(view);
            taskNameTextView = view.findViewById(R.id.listItemNameTextView);
            taskCheckBox = view.findViewById(R.id.cardListCheckBox);
        }
    }

    public TasksRecyclerAdapter(List<Task> tasksList, Context context,
                                FloatingActionButton addGoal, FloatingActionButton modifyGoal,
                                FloatingActionButton deleteGoal) {
        this.tasksList = tasksList;
        this.context = context;
        this.addTask = addGoal;
        this.modifyTask = modifyGoal;
        this.deleteTask = deleteGoal;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card_list, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Task task = tasksList.get(position);

        TaskDao taskDao = AppRoomDatabase.getDatabase(getContext()).taskDao();

        viewHolder.taskNameTextView.setText(task.getName());

        MaterialCardView card = (MaterialCardView) viewHolder.itemView;
        card.setCheckable(true);

        if (task.isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
        }

        if (task.isChecked()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            viewHolder.taskCheckBox.setChecked(true);
            viewHolder.taskNameTextView.setPaintFlags(viewHolder.taskNameTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            viewHolder.taskCheckBox.setChecked(false);
            viewHolder.taskNameTextView.setPaintFlags(viewHolder.taskNameTextView.getPaintFlags()
                    & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FloatingActionButton addGoal = getAddTask();
                FloatingActionButton modifyGoal = getModifyTask();
                FloatingActionButton deleteGoal = getDeleteTask();

                addGoal.setEnabled(false);

                if (task.isSelected()) {
                    //Deseleziona l'attività
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
                    card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
                    task.setSelected(false);
                    taskDao.updateIsSelected(task.getId(), false);
                    viewHolder.taskCheckBox.setEnabled(true);

                    boolean notSelectedAll = true;
                    for (Task t : tasksList) {
                        if (t.isSelected()) {
                            notSelectedAll = false;
                            break;
                        }
                    }
                    if (notSelectedAll) {
                        modifyGoal.setVisibility(View.GONE);
                        deleteGoal.setVisibility(View.GONE);
                        viewHolder.taskCheckBox.setEnabled(true);
                        addGoal.setEnabled(true);
                    }
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
                    card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
                    task.setSelected(true);
                    taskDao.updateIsSelected(task.getId(), true);
                    viewHolder.taskCheckBox.setEnabled(false);
                }

                List<Task> selectedTasks = taskDao.getSelectedTasks();
                if (selectedTasks.size() == 1) {
                    modifyGoal.setVisibility(View.VISIBLE);
                    deleteGoal.setVisibility(View.VISIBLE);
                } else if (selectedTasks.size() == 2) {
                    modifyGoal.setVisibility(View.GONE);
                }

                return false;
            }
        });

        viewHolder.taskCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!task.isChecked()){
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray));
                    viewHolder.taskNameTextView.setPaintFlags(viewHolder.taskNameTextView.getPaintFlags()
                            | Paint.STRIKE_THRU_TEXT_FLAG);
                    card.setChecked(true);
                    task.setChecked(true);
                    taskDao.updateIsChecked(task.getId(), true);
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
                    viewHolder.taskNameTextView.setPaintFlags(viewHolder.taskNameTextView.getPaintFlags()
                            & ~Paint.STRIKE_THRU_TEXT_FLAG);
                    card.setChecked(false);
                    task.setChecked(false);
                    taskDao.updateIsChecked(task.getId(), false);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

}
