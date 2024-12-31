package com.unimib.triptales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unimib.triptales.R;
import com.unimib.triptales.database.AppRoomDatabase;
import com.unimib.triptales.database.ExpenseDao;
import com.unimib.triptales.model.Expense;

import java.util.List;

public class ExpensesRecyclerAdapter extends RecyclerView.Adapter<ExpensesRecyclerAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private Context context;
    private FloatingActionButton addExpense;
    private FloatingActionButton deleteExpense;
    private FloatingActionButton modifyExpense;

    public List<Expense> getExpenseList() { return expenseList; }
    public Context getContext() { return context; }
    public FloatingActionButton getAddExpense() { return addExpense; }
    public FloatingActionButton getDeleteExpense() { return deleteExpense; }
    public FloatingActionButton getModifyExpense() { return modifyExpense; }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountTextView, categoryTextView, descriptionTextView, dateTextView;
        ImageView icon;

        public ViewHolder(View view) {
            super(view);
            amountTextView = view.findViewById(R.id.amountTextView);
            categoryTextView = view.findViewById(R.id.categoryTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
            dateTextView = view.findViewById(R.id.dateTextView);
            icon = view.findViewById(R.id.cardSpesaIcon);
        }
    }

    public ExpensesRecyclerAdapter(List<Expense> expenseList, Context context,
                                   FloatingActionButton addExpense, FloatingActionButton modifyExpense,
                                   FloatingActionButton deleteExpense) {
        this.expenseList = expenseList;
        this.context = context;
        this.addExpense = addExpense;
        this.modifyExpense = modifyExpense;
        this.deleteExpense = deleteExpense;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card_spesa, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Expense expense = expenseList.get(position);

        viewHolder.amountTextView.setText(expense.getAmount());
        viewHolder.categoryTextView.setText(expense.getCategory());
        viewHolder.descriptionTextView.setText(expense.getDescription());
        viewHolder.dateTextView.setText(expense.getDate());
        viewHolder.icon.setImageResource(expense.getIconId());

        MaterialCardView card = (MaterialCardView) viewHolder.itemView;

        if (expense.isExpense_isSelected()) {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
        } else {
            card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
            card.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
        }

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FloatingActionButton addExpense = getAddExpense();
                FloatingActionButton modifyExpense = getModifyExpense();
                FloatingActionButton deleteExpense = getDeleteExpense();

                ExpenseDao expenseDao = AppRoomDatabase.getDatabase(getContext()).expenseDao();

                addExpense.setEnabled(false);

                if (expense.isExpense_isSelected()) {
                    //Deseleziona la spesa
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary));
                    card.setStrokeColor(ContextCompat.getColor(context, R.color.primary));
                    expense.setExpense_isSelected(false);
                    expenseDao.updateIsSelected(expense.getId(), false);
                    boolean notSelectedAll = true;
                    for (Expense e : expenseList) {
                        if (e.isExpense_isSelected()) {
                            notSelectedAll = false;
                            break;
                        }
                    }
                    if (notSelectedAll) {
                        modifyExpense.setVisibility(View.GONE);
                        deleteExpense.setVisibility(View.GONE);
                        addExpense.setEnabled(true);
                    }
                } else {
                    card.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
                    card.setStrokeColor(ContextCompat.getColor(context, R.color.background_dark));
                    expense.setExpense_isSelected(true);
                    expenseDao.updateIsSelected(expense.getId(), true);
                }

                List<Expense> selectedExpenses = expenseDao.getSelectedExpenses();

                if (selectedExpenses.size() == 1) {
                    modifyExpense.setVisibility(View.VISIBLE);
                    deleteExpense.setVisibility(View.VISIBLE);
                } else if (selectedExpenses.size() == 2) {
                    modifyExpense.setVisibility(View.GONE);
                }

                return false;
            }
        });

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return expenseList.size();
    }
}
