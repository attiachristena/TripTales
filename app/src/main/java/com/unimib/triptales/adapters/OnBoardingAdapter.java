package com.unimib.triptales.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.unimib.triptales.R;

import org.jetbrains.annotations.NotNull;

import org.jetbrains.annotations.NotNull;

    public class OnBoardingAdapter extends PagerAdapter {

        Context context;

        int []sliderAllImages = {R.drawable.img_intro1, R.drawable.img_intro2, R.drawable.img_intro3, R.drawable.img_intro4};
        int []sliderAllTitle = {R.string.intro1_title,R.string.intro2_title,R.string.intro3_title,R.string.intro4_title};
        int []sliderAllDesc = {R.string.intro1_body,R.string.intro2_body,R.string.intro3_body,R.string.intro4_body};
        int []sliderAllBackgrounds = {R.drawable.bg1,R.drawable.bg2,R.drawable.bg3,R.drawable.bg4};

        public OnBoardingAdapter(Context context){
            this.context = context;
        }

        @Override
        public int getCount() {
            return sliderAllTitle.length;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == (LinearLayout)object;
        }

        @NotNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position){

            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.fragment_intro1, container, false);

            ImageView sliderImage = (ImageView) view.findViewById(R.id.slideImg);
            TextView sliderTitle =(TextView) view.findViewById(R.id.sliderTitle);
            TextView sliderDesc = (TextView) view.findViewById(R.id.sliderSubtitle);
            LinearLayout sliderLayout = view.findViewById(R.id.sliderLayout);

            sliderImage.setImageResource(sliderAllImages[position]);
            sliderTitle.setText(this.sliderAllTitle[position]);
            sliderDesc.setText(this.sliderAllDesc[position]);
            sliderLayout.setBackgroundResource(sliderAllBackgrounds[position]);

            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object){
            container.removeView((LinearLayout)object);
        }
}
