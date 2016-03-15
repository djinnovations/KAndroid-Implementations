package com.goldadorn.main.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daprlabs.cardstack.SwipeDeck;
import com.goldadorn.main.R;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {

    private SwipeDeck cardStack;
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_products);
        cardStack = (SwipeDeck) findViewById(R.id.swipe_deck);
        final ArrayList<String> testData = new ArrayList<>();
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");
        final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, TestActivity.this);
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                if(mToast!=null)
                    mToast.cancel();
                mToast = Toast.makeText(TestActivity.this,"Product "+position+" liked",Toast.LENGTH_LONG);
                mToast.show();

                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                if(mToast!=null)
                    mToast.cancel();
                mToast = Toast.makeText(TestActivity.this,"Product "+position+" dis-liked",Toast.LENGTH_LONG);
                mToast.show();
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");if(mToast!=null)
                    mToast.cancel();
                mToast = Toast.makeText(TestActivity.this,"Products depleted ",Toast.LENGTH_LONG);
                mToast.show();
            }
        });
    }

    public class SwipeDeckAdapter extends BaseAdapter {

        private List<String> data;
        private Context context;

        public SwipeDeckAdapter(List<String> data, Context context) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            if(v == null){
                // normally use a viewholder
                v = View.inflate(context,R.layout.layout_product_card, null);
            }
            ((TextView)v.findViewById(R.id.likes_count)).setText(Integer.toString(position));

            return v;
        }
    }
}
