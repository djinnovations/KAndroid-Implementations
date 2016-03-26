package com.goldadorn.main.activities.cart;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.assist.ObjectAsyncLoader;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.model.Address;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class AddressFragment extends Fragment {
    AddressCallBacks mAddressCallBacks = new AddressCallBacks();
    ArrayList<Address> mAddresses = new ArrayList<>(5);
    AddressesViewHolder mAddressesHolder;
    private TextView mAddButton;
    ProgressBar mProgressBar;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_addresses, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAddressesHolder = new AddressesViewHolder((LinearLayout) view.findViewById(R.id.container_addresses_payment), mAddressSelectedListener);
        mAddButton = (TextView) view.findViewById(R.id.action_add);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mAddButton.setText("Add new shipping address");
        mAddButton.setOnClickListener(mClick);
        getLoaderManager().initLoader(mAddressCallBacks.hashCode(), null, mAddressCallBacks);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(mAddressCallBacks.hashCode());
    }

    private void onAddressesChanged() {
        if (mAddresses.size() > 0) {
            mAddressesHolder.setVisibility(View.VISIBLE);
            mAddressesHolder.bindUI(mAddresses);
        } else {
            mAddressesHolder.setVisibility(View.GONE);
        }
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ((CartManagerActivity) getActivity()).configureUI(CartManagerActivity.UISTATE_OVERLAY_ADD_ADDRESS);
        }
    };

    IResultListener<Integer> mAddressSelectedListener = new IResultListener<Integer>() {
        @Override
        public void onResult(Integer addressId) {

        }
    };

    private class AddressCallBacks implements LoaderManager.LoaderCallbacks<ObjectAsyncLoader.Result> {
        @Override
        public Loader<ObjectAsyncLoader.Result> onCreateLoader(int id, Bundle args) {
            return new ObjectAsyncLoader(getContext()) {
                @Override
                protected void registerContentObserver(ContentObserver observer) {
                    mContext.getContentResolver().registerContentObserver(Tables.Addresses.CONTENT_URI, true, observer);
                }

                @Override
                protected void unRegisterContentObserver(ContentObserver observer) {
                    mContext.getContentResolver().unregisterContentObserver(observer);
                }

                @Override
                public Result loadInBackground() {
                    Result result = new Result();
                    Cursor c = mContext.getContentResolver().query(Tables.Addresses.CONTENT_URI, null, null, null, null);
                    List<Address> t = null;
                    if (c != null) {
                        t = new ArrayList<>(c.getCount());
                        if (c.moveToFirst()) do {
                            t.add(Address.extractFromCursor(c));
                        } while (c.moveToNext());
                        c.close();
                    }
                    result.object = t;

                    if (t.size() == 0) {
                        Address product = new Address(123123);
                        product.name = "Home";
                        product.street = "no 45, heather street";
                        product.city = "Bangalore";
                        product.state = "Karnataka";
                        product.pincode = 560095;
                        t.add(product);
                    }
                    return result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ObjectAsyncLoader.Result> loader, ObjectAsyncLoader.Result data) {
            mAddresses.clear();
            if (data.object != null)
                mAddresses.addAll((Collection<? extends Address>) data.object);
            mProgressBar.setVisibility(View.GONE);
            onAddressesChanged();
        }

        @Override
        public void onLoaderReset(Loader<ObjectAsyncLoader.Result> loader) {

        }
    }
}
