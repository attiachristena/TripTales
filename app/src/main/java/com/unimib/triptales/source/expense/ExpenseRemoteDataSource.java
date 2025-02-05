package com.unimib.triptales.source.expense;

import static com.unimib.triptales.util.Constants.UNEXPECTED_ERROR;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unimib.triptales.model.Expense;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseRemoteDataSource extends BaseExpenseRemoteDataSource{
    private final DatabaseReference databaseReference;

    public ExpenseRemoteDataSource(String userId, String diaryId) {
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        this.databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("diaries")
                .child(diaryId)
                .child("expenses");
        //databaseReference.keepSynced(true);
    }

    public void insertExpense(Expense expense) {
        if (expense != null) {
            databaseReference.child(expense.getId()).setValue(expense)
                    .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
        } else {
            expenseCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateExpense(Expense expense) {
        if(expense != null) {
            databaseReference.child(expense.getId()).setValue(expense)
                    .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
        } else {
            expenseCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateExpenseCategory(String expenseId, String newCategory) {
        if(newCategory != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("category", newCategory);

            databaseReference.child(expenseId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
        } else {
            expenseCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateExpenseDescription(String expenseId, String newDescription) {
        if(newDescription != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("description", newDescription);

            databaseReference.child(expenseId).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
        } else {
            expenseCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateExpenseAmount(String expenseId, String newAmount) {
        if(newAmount != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("amount", newAmount);

            databaseReference.child(expenseId).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
        } else {
            expenseCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateExpenseDate(String expenseId, String newDate) {
        if(newDate != null) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("date", newDate);

            databaseReference.child(expenseId).updateChildren(updates)
                    .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
        } else {
            expenseCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void updateExpenseIsSelected(String expenseId, boolean newIsSelected) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("expense_isSelected", newIsSelected);

        databaseReference.child(expenseId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
    }

    @Override
    public void deleteExpense(Expense expense) {
        if(expense != null){
            databaseReference.child(expense.getId()).removeValue()
                    .addOnSuccessListener(aVoid -> expenseCallback.onSuccessFromRemote())
                    .addOnFailureListener(e -> expenseCallback.onFailureFromRemote(e));
        } else {
            expenseCallback.onFailureFromRemote(new Exception(UNEXPECTED_ERROR));
        }
    }

    @Override
    public void deleteAllExpenses(List<Expense> expenses) {
        if(expenses != null) {
            for (Expense e : expenses) {
                deleteExpense(e);
            }
        }
    }

    @Override
    public void getAllExpenses() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                List<Expense> expensesList = new ArrayList<>();
                for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                    Expense expense = expenseSnapshot.getValue(Expense.class);
                    if (expense != null) {
                        expensesList.add(expense);
                    }
                }
                expenseCallback.onSuccessFromRemote(expensesList);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                expenseCallback.onFailureFromRemote(new Exception(error.getMessage()));
            }
        });
    }

    @Override
    public void getSelectedExpenses() {
        databaseReference.orderByChild("expense_isSelected").equalTo(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Expense> selectedExpenses = new ArrayList<>();
                        for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                            Expense expense = expenseSnapshot.getValue(Expense.class);
                            if (expense != null) {
                                selectedExpenses.add(expense);
                            }
                        }
                        expenseCallback.onSuccessFromRemote(selectedExpenses);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        expenseCallback.onFailureFromRemote(new Exception(error.getMessage()));
                    }
                });
    }

    @Override
    public void getFilteredExpenses(String category) {
        databaseReference.orderByChild("category").equalTo(category)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<Expense> filteredExpenses = new ArrayList<>();
                        for (DataSnapshot expenseSnapshot : snapshot.getChildren()) {
                            Expense expense = expenseSnapshot.getValue(Expense.class);
                            if (expense != null) {
                                filteredExpenses.add(expense);
                            }
                        }
                        expenseCallback.onSuccessFromRemote(filteredExpenses);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        expenseCallback.onFailureFromRemote(new Exception(error.getMessage()));
                    }
                });
    }
}


