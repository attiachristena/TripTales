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
import com.unimib.triptales.model.Expense;

import java.util.List;

public class ExpensesRecyclerAdapter extends RecyclerView.Adapter<ExpensesRecyclerAdapter.ViewHolder> {

    private List<Expense> expenseList;
    private Context context;
    private OnExpenseClickListener OnExpenseClickListener;

    public Context getContext() { return context; }

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

    public ExpensesRecyclerAdapter(Context context) {
        this.context = context;
    }

    public void setExpenseList(List<Expense> expenses) {
        /*for (int i = 0; i < expenses.size(); i++) {
            if (i >= expenseList.size() || !expenseList.get(i).equals(expenses.get(i))) {
                notifyItemChanged(i);
            }
            //aggiungere il controllo su ogni campo di una spesa?
        }*/
        this.expenseList = expenses;
        notifyDataSetChanged();
    }


    public void setOnExpenseClickListener(OnExpenseClickListener listener) {
        this.OnExpenseClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_card_expense, viewGroup, false);

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

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

        card.setOnLongClickListener(v -> {
            if (OnExpenseClickListener != null) {
                OnExpenseClickListener.onExpenseClick(expense, card);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }


    public interface OnExpenseClickListener {
        void onExpenseClick(Expense expense, MaterialCardView card);
    }
}
