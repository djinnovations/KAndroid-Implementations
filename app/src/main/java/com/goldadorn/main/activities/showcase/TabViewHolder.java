package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.goldadorn.main.R;

import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

class TabViewHolder implements View.OnClickListener {
    private final Context context;
    @Bind(R.id.tab1)
    View tab1;
    @Bind(R.id.tab1_name)
    TextView tabName1;
    @Bind(R.id.tab1_count)
    TextView tabCount1;

    @Bind(R.id.shadow_left_1)
    View leftShadow1;
    @Bind(R.id.shadow_top_1)
    View topShadow1;
    @Bind(R.id.shadow_round_1)
    View roundShadow1;
    @Bind(R.id.shadow_right_1)
    View rightShadow1;
    @Bind(R.id.shadow_right_round_1)
    View rightRoundShadow1;


    @Bind(R.id.tab2)
    View tab2;
    @Bind(R.id.tab2_name)
    TextView tabName2;
    @Bind(R.id.tab2_count)
    TextView tabCount2;

    @Bind(R.id.shadow_left_2)
    View leftShadow2;
    @Bind(R.id.shadow_top_2)
    View topShadow2;
    @Bind(R.id.shadow_round_2)
    View roundShadow2;
    @Bind(R.id.shadow_right_2)
    View rightShadow2;
    @Bind(R.id.shadow_right_round_2)
    View rightRoundShadow2;


    @Bind(R.id.tab3)
    View tab3;
    @Bind(R.id.tab3_name)
    TextView tabName3;

    @Bind(R.id.shadow_left_3)
    View leftShadow3;
    @Bind(R.id.shadow_top_3)
    View topShadow3;
    @Bind(R.id.shadow_round_3)
    View roundShadow3;
    @Bind(R.id.shadow_right_3)
    View rightShadow3;
    @Bind(R.id.shadow_right_round_3)
    View rightRoundShadow3;


    @Bind(R.id.divider1)
    View divider1;
    @Bind(R.id.divider2)
    View divider2;


    @Bind(R.id.shadow_left)
    View shadowLeft;
    @Bind(R.id.shadow_right)
    View shadowRight;
    @Bind(R.id.shadow_right_round)
    View shadowRoundRight;

    ViewGroup parent;

    int topMargin = 0;
    private TabClickListener tabClickListener;

    public TabViewHolder(Context context, View itemView) {
        this.context = context;
        ButterKnife.bind(this, itemView);
        parent = (ViewGroup) itemView;
        topMargin = context.getResources().getDimensionPixelSize(R.dimen.tab_top_margin);
        tab1.setOnClickListener(this);
        tab2.setOnClickListener(this);
        tab3.setOnClickListener(this);
    }

    public void initTabs(String tabName1, String tabName2, String tabName3,
                         TabClickListener tabClickListener) {
        this.tabName1.setText(tabName1);
        this.tabName2.setText(tabName2);
        if (TextUtils.isEmpty(tabName3)) {
            parent.removeView(divider2);
            parent.removeView(tab3);
        } else {
            this.tabName3.setText(tabName3);
        }
        this.tabClickListener = tabClickListener;
    }

    public void setCounts(int count1, int count2) {
        if (count1 > 0) {
            tabCount1.setVisibility(View.VISIBLE);
            tabCount1.setText(String.format(Locale.getDefault(), "%d", count1));
        } else
            tabCount1.setVisibility(View.GONE);

        if (count2 > 0) {
            tabCount2.setVisibility(View.VISIBLE);
            tabCount2.setText(String.format(Locale.getDefault(), "%d", count2));
        } else tabCount2.setVisibility(View.GONE);
    }

    public void collectionCount(int count) {
        if (count > 0) {
            tabCount1.setVisibility(View.VISIBLE);
            tabCount1.setText(String.format(Locale.getDefault(), "%d", count));
        } else
            tabCount1.setText(String.format(Locale.getDefault(), "%d", 0));
    }

    public void productsCount(int count) {
        if (count > 0) {
            tabCount2.setVisibility(View.VISIBLE);
            tabCount2.setText(String.format(Locale.getDefault(), "%d", count));
        } else tabCount2.setText(String.format(Locale.getDefault(), "%d", 0));
    }

    public void setSelected(int pos) {
        switch (pos) {
            case 0:
                setView(tabName1, tabCount1, true);
                setView(tabName2, tabCount2, false);
                if (tab3 != null) {
                    setView(tabName3, null, false);
                    divider2.setVisibility(View.VISIBLE);
                    leftShadow3.setVisibility(View.GONE);
                    topShadow3.setVisibility(View.GONE);
                    roundShadow3.setVisibility(View.GONE);
                    rightRoundShadow3.setVisibility(View.GONE);
                    rightShadow3.setVisibility(View.VISIBLE);
                }
                divider1.setVisibility(View.GONE);

                leftShadow1.setVisibility(View.VISIBLE);
                topShadow1.setVisibility(View.VISIBLE);
                roundShadow1.setVisibility(View.VISIBLE);
                rightShadow1.setVisibility(View.GONE);

                leftShadow2.setVisibility(View.GONE);
                topShadow2.setVisibility(View.GONE);
                roundShadow2.setVisibility(View.GONE);
                rightRoundShadow2.setVisibility(View.VISIBLE);
                rightShadow2.setVisibility(View.VISIBLE);

                shadowRoundRight.setVisibility(View.GONE);
                break;
            case 1:
                setView(tabName1, tabCount1, false);
                setView(tabName2, tabCount2, true);
                if (tab3 != null) {
                    setView(tabName3, null, false);
                    divider2.setVisibility(View.GONE);
                    leftShadow3.setVisibility(View.GONE);
                    topShadow3.setVisibility(View.GONE);
                    roundShadow3.setVisibility(View.GONE);
                    rightShadow3.setVisibility(View.VISIBLE);
                    rightRoundShadow3.setVisibility(View.VISIBLE);
                }
                divider1.setVisibility(View.GONE);

                leftShadow1.setVisibility(View.GONE);
                topShadow1.setVisibility(View.GONE);
                roundShadow1.setVisibility(View.GONE);
                rightShadow1.setVisibility(View.VISIBLE);
                rightRoundShadow1.setVisibility(View.GONE);


                leftShadow2.setVisibility(View.VISIBLE);
                topShadow2.setVisibility(View.VISIBLE);
                roundShadow2.setVisibility(View.VISIBLE);
                rightShadow2.setVisibility(View.GONE);

                shadowRoundRight.setVisibility(View.GONE);
                break;
            case 2:
                setView(tabName1, tabCount1, false);
                setView(tabName2, tabCount2, false);
                setView(tabName3, null, true);
                divider2.setVisibility(View.GONE);

                leftShadow1.setVisibility(View.GONE);
                topShadow1.setVisibility(View.GONE);
                roundShadow1.setVisibility(View.GONE);
                rightShadow1.setVisibility(View.VISIBLE);
                rightRoundShadow1.setVisibility(View.GONE);


                leftShadow2.setVisibility(View.GONE);
                topShadow2.setVisibility(View.GONE);
                roundShadow2.setVisibility(View.GONE);
                rightShadow2.setVisibility(View.VISIBLE);
                rightRoundShadow2.setVisibility(View.GONE);


                leftShadow3.setVisibility(View.VISIBLE);
                topShadow3.setVisibility(View.VISIBLE);
                roundShadow3.setVisibility(View.VISIBLE);
                rightShadow3.setVisibility(View.GONE);

                divider1.setVisibility(View.VISIBLE);

                shadowRoundRight.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void setSides(int pad) {
        LinearLayout.LayoutParams lp1 = (LinearLayout.LayoutParams) shadowLeft.getLayoutParams();
        lp1.width = pad;
        shadowLeft.setLayoutParams(lp1);
        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) shadowRight.getLayoutParams();
        lp2.width = pad;
        shadowRight.setLayoutParams(lp2);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tab1) {
            if (tabClickListener != null) tabClickListener.onTabClick(0);
            setSelected(0);
        } else if (id == R.id.tab2) {
            if (tabClickListener != null) tabClickListener.onTabClick(1);
            setSelected(1);
        } else if (id == R.id.tab3) {
            if (tabClickListener != null) tabClickListener.onTabClick(2);
            setSelected(2);
        }
    }

    private void setView(TextView view1, TextView view2, boolean selected) {
        view1.setTextColor(selected ? Color.BLACK : Color.WHITE);
        if (view2 != null) {
            view2.setTextColor(selected ? Color.BLACK : Color.WHITE);
            view2.setTextSize(TypedValue.COMPLEX_UNIT_SP, selected ? 34 : 12);
        }
        View parent = ((ViewGroup) view1.getParent());
        parent.setBackgroundColor(selected ? Color.WHITE : Color.BLACK);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) parent.getLayoutParams();
        lp.topMargin = selected ? 0 : topMargin;
        parent.setLayoutParams(lp);
    }

    interface TabClickListener {
        public void onTabClick(int position);
    }
}