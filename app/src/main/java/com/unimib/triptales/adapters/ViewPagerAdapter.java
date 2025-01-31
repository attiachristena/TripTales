    package com.unimib.triptales.adapters;

    import android.net.Uri;
    import android.os.Bundle;

    import androidx.annotation.NonNull;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentActivity;
    import androidx.viewpager2.adapter.FragmentStateAdapter;

    import com.unimib.triptales.ui.diary.fragment.TasksFragment;
    import com.unimib.triptales.ui.diary.fragment.GoalsFragment;
    import com.unimib.triptales.ui.diary.fragment.ExpensesFragment;
    import com.unimib.triptales.ui.diary.fragment.CheckpointsFragment;

    public class ViewPagerAdapter extends FragmentStateAdapter {

        private String diaryName;
        private String startDate;
        private String endDate;
        private Uri coverImageUri;

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, String diaryName, String startDate, String endDate, Uri coverImageUri) {
            super(fragmentActivity);
            this.diaryName = diaryName;
            this.startDate = startDate;
            this.endDate = endDate;
            this.coverImageUri = coverImageUri;
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            Fragment fragment;
            Bundle args = new Bundle();
            args.putString("diaryName", diaryName);
            args.putString("startDate", startDate);
            args.putString("endDate", endDate);
            if (coverImageUri != null) {
                args.putParcelable("coverImageUri", coverImageUri); // Store as Parcelable
            }


            switch (position) {
                case 0:
                    fragment = new CheckpointsFragment();
                    break;
                case 1:
                    fragment = new ExpensesFragment();
                    break;
                case 2:
                    fragment = new GoalsFragment();
                    break;
                case 3:
                    fragment = new TasksFragment();
                    break;
                default:
                    fragment = new CheckpointsFragment();
            }

            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getItemCount() {
            return 4;  // Numero di tab
        }
    }

