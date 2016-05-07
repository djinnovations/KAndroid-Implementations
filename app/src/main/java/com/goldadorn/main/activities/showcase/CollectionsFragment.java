package com.goldadorn.main.activities.showcase;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.goldadorn.main.R;
import com.goldadorn.main.assist.IResultListener;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.User;
import com.goldadorn.main.server.UIController;
import com.goldadorn.main.server.response.LikeResponse;
import com.mikepenz.iconics.view.IconicsButton;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class CollectionsFragment extends Fragment implements UserChangeListener {

    public static final String EXTRA_DATA = "data";
    private CollectionsAdapter mCollectionAdapter;
    RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private CollectionCallback mCollectionCallback = new CollectionCallback();
    private User mUser;
    private UpdateLikes updateLikes;

    public static CollectionsFragment newInstance(User user) {
        CollectionsFragment f = new CollectionsFragment();
        Bundle b = new Bundle();
        b.putSerializable(EXTRA_DATA, user);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle b = getArguments();
        if (b != null) {
            mUser = (User) b.getSerializable(EXTRA_DATA);
        }
        return inflater.inflate(R.layout.fragment_recyclerview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);
        mCollectionAdapter = new CollectionsAdapter(view.getContext());
        mRecyclerView.setAdapter(mCollectionAdapter);

        getShowcaseActivity().registerUserChangeListener(this);

        getLoaderManager().initLoader(mCollectionCallback.hashCode(), null, mCollectionCallback);

    }

    @Override
    public void onDestroy() {
        getShowcaseActivity().unRegisterUserChangeListener(this);
        getLoaderManager().destroyLoader(mCollectionCallback.hashCode());
        super.onDestroy();
    }

    private ShowcaseActivity getShowcaseActivity() {
        return ((ShowcaseActivity) getActivity());
    }

    private View.OnClickListener mClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    @Override
    public void onUserChange(User user) {
        mUser = user;
        getLoaderManager().restartLoader(mCollectionCallback.hashCode(), null, mCollectionCallback);
    }

    class CollectionsAdapter extends RecyclerView.Adapter<CollectionHolder> {
        private Cursor cursor;
        Context context;
        private int cardWidth;
        private HashMap<Integer, Boolean> likeMap = new HashMap<>();
        private View.OnClickListener mCollectionClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                startActivity(CollectionsActivity
                        .getLaunchIntent(v.getContext(), getCollection((Integer) v.getTag())));
            }
        };
       /* private View.OnClickListener mLikeClick = new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                try {
                    v.setEnabled(false);
                    final Collection collection = (Collection) v.getTag();
                    final boolean isLiked = v.isSelected();
                    UIController.like(v.getContext(), collection, !isLiked, new
                            IResultListener<LikeResponse>() {
                                @Override
                                public void onResult(LikeResponse result) {
                                    v.setEnabled(true);
                                    v.setSelected(result.success != isLiked);
                                   likeMap.put(collection.id,v.isSelected());
                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(v.getContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
                }
            }
        };*/

        public CollectionsAdapter(Context context) {
            this.context = context;
            Resources res = context.getResources();
            cardWidth = res.getDisplayMetrics().widthPixels / 2 -
                    3 * res.getDimensionPixelSize(R.dimen.griditem_margin);
        }

        @Override
        public CollectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.layout_showcase_brand_item, parent, false);
            final CollectionHolder rcv = new CollectionHolder(layoutView);
            rcv.itemView.setOnClickListener(mCollectionClick);
            rcv.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    try {
                        v.setEnabled(false);
                        final Collection collection = (Collection) v.getTag();
                        final boolean isLiked = v.isSelected();
                        UIController.like(v.getContext(), collection, !isLiked, new
                                IResultListener<LikeResponse>() {
                                    @Override
                                    public void onResult(LikeResponse result) {
                                        v.setEnabled(true);
                                        v.setSelected(result.success != isLiked);
                                        likeMap.put(collection.id, v.isSelected());
                                        if(isLiked){
                                            collection.likecount=collection.likecount-1;
                                            rcv.likeCount.setText(String.format(Locale.getDefault(), "%d", collection.likecount));
                                        }else{
                                            collection.likecount=collection.likecount+1;
                                            rcv.likeCount.setText(String.format(Locale.getDefault(), "%d", collection.likecount));
                                        }



                                    }
                                });
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(v.getContext(), "Something wrong happened!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return rcv;
        }

        @Override
        public void onBindViewHolder(final CollectionHolder holder, int position) {
            Collection collection = getCollection(position);
            holder.name.setText(collection.name);
            holder.description.setText(collection.category);
            holder.likeCount.setText(String.format(Locale.getDefault(), "%d", collection.likecount));
            holder.extra.setText(String.format(Locale.getDefault(), "%d Products", collection.productcount));
            holder.itemView.setTag(position);

            Boolean t = likeMap.get(collection.id);
            holder.like.setSelected(t == null ? collection.isLiked : t);
            holder.like.setTag(collection);
            holder.image.getLayoutParams().height = (int) (cardWidth / collection.image_a_r);
            Log.e("iii",collection.getImageUrl());
            Picasso.with(context).load(collection.getImageUrl()).into(holder.image);

        }


        @Override
        public int getItemCount() {

            return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        public Collection getCollection(int position) {
            cursor.moveToPosition(position);
            Collection mCollection=Collection.extractFromCursor(cursor);
            mCollection.selectedPos=position;
            return mCollection;
        }

        public void changeCursor(Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }

    }

    private static class CollectionHolder extends RecyclerView.ViewHolder {

        public TextView name;
        public TextView description;
        public TextView extra;
        public TextView likeCount;
        public ImageView image;
        public IconicsButton like;

        public CollectionHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.collection_name);
            description = (TextView) itemView.findViewById(R.id.collection_description);
            extra = (TextView) itemView.findViewById(R.id.extra);
            likeCount = (TextView) itemView.findViewById(R.id.collection_likes);
            like = (IconicsButton) itemView.findViewById(R.id.likeButton);
            image = (ImageView) itemView.findViewById(R.id.collection_image);
        }

    }

    private class CollectionCallback implements LoaderManager.LoaderCallbacks<Cursor> {
        Cursor cursor;

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new CursorLoader(getContext(), Tables.Collections.CONTENT_URI, null,
                    Tables.Collections.USER_ID + " = ?",
                    new String[]{String.valueOf(mUser == null ? -1 : mUser.id)}, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
            Log.e("iiii---",data.getCount()+"");
            mCollectionAdapter.changeCursor(data);
            updateLikes.updateCollectionCount(data.getCount());
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }

    public interface UpdateLikes {
        public void updateCollectionCount(int count);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            updateLikes = (UpdateLikes) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement onViewSelected");
        }

    }
}
