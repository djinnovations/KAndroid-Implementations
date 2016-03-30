package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daprlabs.cardstack.SwipeDeck;
import com.goldadorn.main.R;
import com.goldadorn.main.db.Tables.Products;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.ProductResponse;
import com.mikepenz.iconics.view.IconicsButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Vijith Menon on 11/3/16.
 */
public class ProductsFragment extends Fragment {
    private final static String TAG = ProductsFragment.class.getSimpleName();
    public static final String EXTRA_MODE = "mode";
    public static final String EXTRA_DATA = "data";
    public static final int MODE_COLLECTION = 0;
    public static final int MODE_USER = 1;
    private final static boolean DEBUG = true;

    @Bind(R.id.swipe_deck)
    SwipeDeck cardStack;

    @Bind(R.id.product_price)
    TextView price;
    @Bind(R.id.product_name)
    TextView name;

    @Bind(R.id.buyButton)
    Button buyButton;

    Toast mToast;

    private User mUser;
    private Collection mCollection;
    private int mMode = MODE_COLLECTION;
    private ProductCallback mProductCallback = new ProductCallback();
    private View.OnClickListener mBuyClick=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //todo kiran go to product info page
            goToProductPage(v.getContext());
        }
    };

    public static ProductsFragment newInstance(int mode, User user, Collection collection) {
        ProductsFragment f = new ProductsFragment();
        Bundle b = new Bundle();
        b.putInt(EXTRA_MODE, mode);
        b.putSerializable(EXTRA_DATA, mode == MODE_COLLECTION ? collection : user);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (b != null) {
            mMode = b.getInt(EXTRA_MODE);
            if (mMode == MODE_COLLECTION)
                mCollection = (Collection) b.getSerializable(EXTRA_DATA);
            else mUser = (User) b.getSerializable(EXTRA_DATA);
        }
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        final ArrayList<String> testData = new ArrayList<>();
        testData.add("0");
        testData.add("1");
        testData.add("2");
        testData.add("3");
        testData.add("4");

//        cardStack = (SwipeDeck) view.findViewById(R.id.swipe_deck);
        final SwipeDeckAdapter adapter = new SwipeDeckAdapter(testData, getActivity());
        cardStack.setAdapter(adapter);

        cardStack.setEventCallback(new SwipeDeck.SwipeEventCallback() {
            @Override
            public void cardSwipedLeft(int position) {
                Log.i("MainActivity", "card was swiped right, position in adapter: " + position);
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(getActivity(), "Product " + position + " dis-liked", Toast.LENGTH_LONG);
                mToast.show();
            }

            @Override
            public void cardSwipedRight(int position) {
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(getActivity(), "Product " + position + " liked", Toast.LENGTH_LONG);
                mToast.show();

                Log.i("MainActivity", "card was swiped left, position in adapter: " + position);
            }

            @Override
            public void cardsDepleted() {
                Log.i("MainActivity", "no more cards");
                if (mToast != null)
                    mToast.cancel();
                mToast = Toast.makeText(getActivity(), "Products depleted ", Toast.LENGTH_LONG);
                mToast.show();
            }
        });

        if (mMode == MODE_USER) {
            ((ShowcaseActivity) getActivity()).registerUserChangeListener(mUserChangeListener);
        } else {
            ((CollectionsActivity) getActivity()).registerCollectionChangeListener(mCollectionChangeListener);
        }
        getLoaderManager().initLoader(mProductCallback.hashCode(), null, mProductCallback);
        refreshData();

        buyButton.setOnClickListener(mBuyClick);
    }

    @Override
    public void onDestroy() {
        if (mMode == MODE_USER) {
            ((ShowcaseActivity) getActivity()).unRegisterUserChangeListener(mUserChangeListener);
        } else {
            ((CollectionsActivity) getActivity()).unRegisterCollectionChangeListener(mCollectionChangeListener);
        }
        getLoaderManager().destroyLoader(mProductCallback.hashCode());
        super.onDestroy();
    }

    public class SwipeDeckAdapter extends BaseAdapter implements View.OnClickListener{

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

            ProductViewHolder holder;
            if (convertView == null) {
                holder = new ProductViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_product_card,parent,false));
                convertView = holder.itemview;
                convertView.setTag(holder);
                convertView.setOnClickListener(this);

                holder.like.setOnClickListener(this);
                holder.productActionsToggle.setOnClickListener(this);
                holder.shareButton.setOnClickListener(this);
                holder.buyNoBuyButton.setOnClickListener(this);
                holder.wishlistButton.setOnClickListener(this);

            }else
                holder = (ProductViewHolder) convertView.getTag();

            holder.productActionsToggle.setTag(holder);
            holder.likesCount.setText(String.format(Locale.getDefault(),"%d",position));

            return convertView;
        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            if(id==R.id.likeButton){
                //todo like click
                Toast.makeText(v.getContext(),"Like click!",Toast.LENGTH_SHORT).show();
            }else if(id==R.id.product_actions_open){
                ProductViewHolder holder = (ProductViewHolder) v.getTag();
                boolean isVisible = holder.productActions.getVisibility()==View.VISIBLE;
                if(isVisible) {
                    holder.productActionsToggle.setImageResource(R.drawable.add);
                    holder.productActions.setVisibility(View.GONE);
                }else{
                    holder.productActionsToggle.setImageResource(R.drawable.close);
                    holder.productActions.setVisibility(View.VISIBLE);
                }
            }else if(id==R.id.shareButton){
                //todo like click
                Toast.makeText(v.getContext(),"Share click!",Toast.LENGTH_SHORT).show();
            }else if(id==R.id.buyNoBuyButton){
                //todo buy no buy click
                Toast.makeText(v.getContext(),"Buy No buy click!",Toast.LENGTH_SHORT).show();
            }else if(id==R.id.wishlistButton){
                //todo wishlist click
                Toast.makeText(v.getContext(),"wishlist click!",Toast.LENGTH_SHORT).show();
            }else{
                goToProductPage(v.getContext());
            }
        }

        class ProductViewHolder {
            View itemview;

            @Bind(R.id.likes_count)
            TextView likesCount;
            @Bind(R.id.likeButton)
            IconicsButton like;
            @Bind(R.id.product_image)
            ImageView image;
            @Bind(R.id.product_actions_open)
            ImageButton productActionsToggle;

            @Bind(R.id.layout_product_actions)
            View productActions;
            @Bind(R.id.shareButton)
            IconicsButton shareButton;
            @Bind(R.id.buyNoBuyButton)
            IconicsButton buyNoBuyButton;
            @Bind(R.id.wishlistButton)
            IconicsButton wishlistButton;


            public ProductViewHolder(View itemview) {
                this.itemview = itemview;
                ButterKnife.bind(this,itemview);
            }
        }
    }

    private void goToProductPage(Context context) {
        startActivity(ProductActivity
                .getLaunchIntent(context));
    }

    private UserChangeListener mUserChangeListener = new UserChangeListener() {
        @Override
        public void onUserChange(User user) {
            mUser = user;
            getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            refreshData();
        }
    };
    private CollectionChangeListener mCollectionChangeListener = new CollectionChangeListener() {
        @Override
        public void onCollectionChange(Collection user) {
            mCollection = user;
            getLoaderManager().restartLoader(mProductCallback.hashCode(), null, mProductCallback);
            refreshData();
        }
    };

    private void refreshData() {
        ProductResponse response = new ProductResponse();
        if (mMode == MODE_USER) {
            response.userId = mUser.id;
        } else {
            response.collectionId = mCollection.id;
        }
        UIController.getProducts(getContext(), response, null);
    }

    private class ProductCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selection;
            String[] selArgs;
            if (mMode == MODE_COLLECTION) {
                selection = Products.COLLECTION_ID + " = ?";
                selArgs = new String[]{String.valueOf(mCollection == null ? -1 : mCollection.id)};
            } else {
                selection = Products.USER_ID + " = ?";
                selArgs = new String[]{String.valueOf(mUser == null ? -1 : mUser.id)};
            }
            return new CursorLoader(getContext(), Products.CONTENT_URI, null, selection, selArgs, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
//            mCollectionAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }
}
