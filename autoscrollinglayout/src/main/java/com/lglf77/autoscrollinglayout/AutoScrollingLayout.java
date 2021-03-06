package com.lglf77.autoscrollinglayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AutoScrollingLayout extends FrameLayout implements OnPositionCalled{
    private static final int DEFAULT_TINT_COLOR = Color.TRANSPARENT;
    private static final float DEFAULT_SPEED = 1600f;
    private static final float DEFAULT_ALPHA = 0.9f;
    private float mAlpha;
    private float mSpeed;
    private Drawable mBackgroundImage;
    private int mTintColor;
    private Context mContext;
    private RecyclerView recyclerView;
    private FrameLayout emptyView;

    public AutoScrollingLayout(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    public AutoScrollingLayout(Context context, AttributeSet attr) {
        this(context,attr,0);
        this.mContext = context;
        init();
    }

    public AutoScrollingLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AutoScrollingLayout,
                defStyle, 0);
        mTintColor = a.getColor(R.styleable.AutoScrollingLayout_tint_color, DEFAULT_TINT_COLOR);
        mSpeed = a.getFloat(R.styleable.AutoScrollingLayout_scroll_speed, DEFAULT_SPEED);
        mAlpha = a.getFloat(R.styleable.AutoScrollingLayout_tint_alpha, DEFAULT_ALPHA);
        mBackgroundImage = a.getDrawable(R.styleable.AutoScrollingLayout_img_src);

        init();
    }

    // region Helper Methods
    public void init() {
        if (!isInEditMode()) {
            View root = inflate(getContext(), R.layout.auto_scrolling_layout, this);
            recyclerView = (RecyclerView) root.findViewById(R.id.rv);
            emptyView = (FrameLayout) root.findViewById(R.id.fl_parent);
            emptyView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            emptyView.setBackgroundColor(mTintColor);
            emptyView.setAlpha(mAlpha);
            ArrayList<Drawable> imgList = new ArrayList<>();

            if(mBackgroundImage!=null) {
                mBackgroundImage = ContextCompat.getDrawable(mContext,R.drawable.longimage);
            }
                for (int i = 0; i < 1000; i++) {
                    imgList.add(mBackgroundImage);
                }
                AutoScrollingAdapter mAdapter = new AutoScrollingAdapter(imgList, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext()) {
                    @Override
                    public void smoothScrollToPosition(RecyclerView recyclerView,
                                                       RecyclerView.State state, int position) {
                        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {

                            @Override
                            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                                return mSpeed / displayMetrics.densityDpi;
                            }

                        };
                        smoothScroller.setTargetPosition(position);
                        startSmoothScroll(smoothScroller);
                    }
                };
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setAdapter(mAdapter);
        }

    }
    @Override
    public void onPositionCalled(int position) {
        if (position + 1 == 1000) {
            recyclerView.scrollToPosition(0);
        } else {
            recyclerView.smoothScrollToPosition(position + 1);
        }
    }
    public class AutoScrollingAdapter extends RecyclerView.Adapter<AutoScrollingAdapter.MyViewHolder> {

        private final OnPositionCalled callback;
        private ArrayList<Drawable> imgList;
        private ArrayList<String> data;

//        public void setData(ArrayList<String> data) {
//            this.data = data;
//            notifyItemInserted(data.size()-1);
//        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView img;

            public MyViewHolder(View view) {
                super(view);
                img = (ImageView)view.findViewById(R.id.img);
            }
        }


        public AutoScrollingAdapter(ArrayList<Drawable> moviesList, OnPositionCalled callback) {
            this.imgList = moviesList;
            this.callback=callback;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.imageitem, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {

            callback.onPositionCalled(position);

            holder.img.setImageDrawable(imgList.get(position));
        }

        @Override
        public int getItemCount() {
            return imgList.size();
        }
    }
}
