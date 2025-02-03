package com.unimib.triptales.source.expense;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unimib.triptales.model.Expense;

import java.util.Objects;

public class ExpenseRemoteDataSource implements BaseExpenseRemoteDataSource{
    private DatabaseReference databaseReference;

    /*public ExpenseRemoteDataSource(String userId, String diaryId) {
        this.databaseReference = FirebaseDatabase.getInstance()
                .getReference("users").child(userId).child("diaries").child(diaryId).child("expenses");

        // Abilitare sincronizzazione offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference.keepSynced(true);
    }

    public void addExpense(Expense expense) {
        String expenseId = databaseReference.push().getKey();
        expense.setId(Integer.parseInt(expenseId));
        databaseReference.child(expenseId).setValue(expense);
    }

*/


}
