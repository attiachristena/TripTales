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
import com.unimib.triptales.model.Goal;
import com.unimib.triptales.model.Task;
import java.util.List;

public class TasksRecyclerAdapter extends RecyclerView.Adapter<TasksRecyclerAdapter.ViewHolder> {

    private List<Task> tasksList;
    private Context context;
    private OnTaskClickListener onTaskClickListener;
    private OnTaskCheckBoxClickListener onTaskCheckBoxClickListener;

    public Context getContext() { return context; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskNameTextView;
        CheckBox taskCheckBox;

        public ViewHolder(View view) {
            super(view);
            taskNameTextView = view.findViewById(R.id.listItemNameTextView);
            taskCheckBox = view.findViewById(R.id.cardListCheckBox);
        }
    }

    public TasksRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setTasksList(List<Task> tasksList) {
        this.tasksList = tasksList;
        notifyDataSetChanged();
    }

    public void setOnTaskClickListener(OnTaskClickListener listener) {
        this.onTaskClickListener = listener;
    }

    public void setOnTaskCheckBoxClickListener(OnTaskCheckBoxClickListener listener) {
        this.onTaskCheckBoxClickListener = listener;
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

        viewHolder.taskNameTextView.setText(task.getName());

        MaterialCardView card = (MaterialCardView) viewHolder.itemView;
        CheckBox checkBox = viewHolder.taskCheckBox;

        if (task.isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
            checkBox.setEnabled(false);
        } else if (task.isChecked()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.dark_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            checkBox.setEnabled(true);
            checkBox.setChecked(true);
            viewHolder.taskNameTextView.setPaintFlags(viewHolder.taskNameTextView.getPaintFlags()
                    | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.light_gray));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.light_gray));
            checkBox.setEnabled(true);
            checkBox.setChecked(false);
            viewHolder.taskNameTextView.setPaintFlags(viewHolder.taskNameTextView.getPaintFlags()
                    & ~Paint.STRIKE_THRU_TEXT_FLAG);
        }

        card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(onTaskClickListener != null){
                    onTaskClickListener.onTaskClick(task);
                }
                return false;
            }
        });

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onTaskCheckBoxClickListener != null){
                    onTaskCheckBoxClickListener.onTaskCheckBoxClick(task);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return tasksList.size();
    }

    public interface OnTaskClickListener {
        void onTaskClick(Task task);
    }

    public interface OnTaskCheckBoxClickListener {
        void onTaskCheckBoxClick(Task task);
    }

}
