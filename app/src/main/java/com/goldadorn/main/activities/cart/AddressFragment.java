package com.goldadorn.main.activities.cart;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
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
import com.goldadorn.main.dj.utils.GAAnalyticsEventNames;
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
        mAddressesHolder = new AddressesViewHolder(getActivity(), (LinearLayout) view.findViewById(R.id.container_addresses_payment), mAddressSelectedListener);
        mAddButton = (TextView) view.findViewById(R.id.action_add);
        mAddButton.setVisibility(View.GONE);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        ((CartManagerActivity) getActivity()).logAnEvent(GAAnalyticsEventNames.CART_ADDRESS);
        //((CartManagerActivity) getActivity()).getPayInfoView().setVisibility(View.GONE);
        //((CartManagerActivity) getActivity()).getPlaceOrderBtn().setVisibility(View.VISIBLE);
        //((CartManagerActivity) getActivity()).displayStaticBar();
        mProgressBar.setVisibility(View.GONE);
        mAddButton.setText("Add/Update Shipping address");
        mAddButton.setOnClickListener(mClick);
        //getLoaderManager().initLoader(mAddressCallBacks.hashCode(), null, mAddressCallBacks);
        refreshAddr();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //getLoaderManager().destroyLoader(mAddressCallBacks.hashCode());
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
            ((CartManagerActivity) getActivity()).showAddAddress(null);
        }
    };

    IResultListener<Address> mAddressSelectedListener = new IResultListener<Address>() {
        @Override
        public void onResult(Address address) {
            ((ICartData) getActivity()).storeAddressData(address);
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
                    Log.d("djcart", "loadInBackground - AddressFragment");
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
                    return result;
                }
            };
        }

        @Override
        public void onLoadFinished(Loader<ObjectAsyncLoader.Result> loader, ObjectAsyncLoader.Result data) {
            Log.d("djcart", "onLoadFinished - AddressFragment");
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

    public void refreshAddr(){
        //mAddresses.clear();
        mAddresses =((CartManagerActivity) getActivity()).getShippingAdress();
        if (mAddresses != null) {
            if (mAddresses.size() > 0)
                ((ICartData) getActivity()).storeAddressData(mAddresses.get(0));
        }
        onAddressesChanged();
    }
}
