package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daprlabs.cardstack.SwipeDeck;
import com.goldadorn.main.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class ProductsFragment extends Fragment {
    private final static String TAG = ProductsFragment.class.getSimpleName();
    private final static boolean DEBUG = true;

    @Bind(R.id.swipe_deck)
    SwipeDeck cardStack;

    Toast mToast;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products,container,false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ArrayList<String> testData = new ArrayList<>();
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");

        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
        final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, getActivity());
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                if(mToast!=null)
                    mToast.cancel();
                mToast = Toast.makeText(getActivity(),"Product "+position+" liked",Toast.LENGTH_LONG);
                mToast.show();

                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardSwipedRight(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                if(mToast!=null)
                    mToast.cancel();
                mToast = Toast.makeText(getActivity(),"Product "+position+" dis-liked",Toast.LENGTH_LONG);
                mToast.show();
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");if(mToast!=null)
                    mToast.cancel();
                mToast = Toast.makeText(getActivity(),"Products depleted ",Toast.LENGTH_LONG);
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
    interface SwipeDeckTouchListener{
        void onTouchStart();
        void onTouchEnd();
    }
}
