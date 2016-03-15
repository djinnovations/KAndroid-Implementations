package com.goldadorn.main.activities.showcase;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.goldadorn.main.R;
import com.goldadorn.main.activities.CollectionsActivity;
import com.goldadorn.main.activities.ShowcaseActivity;
import com.goldadorn.main.db.Tables;
import com.goldadorn.main.model.Collection;
import com.goldadorn.main.model.User;
import com.mikepenz.iconics.view.IconicsButton;

/**
 * Created by Kiran BH on 10/03/16.
 */
public class CollectionsFragment extends Fragment implements UserChangeListener {

    private CollectionsAdapter mCollectionAdapter;
    RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    private CollectionCallback mCollectionCallback = new CollectionCallback();
    private User mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

        mUser = getShowcaseActivity().getCurrentUser();
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
        private View.OnClickListener mCollectionClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CollectionsActivity.getLaunchIntent(v.getContext(), getCollection((Integer) v.getTag())));
            }
        };

        public CollectionsAdapter(Context context) {
            this.context = context;
        }

        @Override
        public CollectionHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_showcase_brand_item, null);
            CollectionHolder rcv = new CollectionHolder(layoutView);
            rcv.itemView.setOnClickListener(mCollectionClick);
            return rcv;
        }

        @Override
        public void onBindViewHolder(CollectionHolder holder, int position) {
            Collection collection = getCollection(position);
            holder.itemView.setTag(position);
            holder.image.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    (float) ((Math.random() + 1) * 100), getResources().getDisplayMetrics());
            if (cursor.moveToPosition(position)) {
                holder.name.setText(collection.name);
            }
        }


        @Override
        public int getItemCount() {
            return cursor == null || cursor.isClosed() ? 0 : cursor.getCount();
        }

        public Collection getCollection(int position) {
            cursor.moveToPosition(position);
            return Collection.extractFromCursor(cursor);
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
            return new CursorLoader(getContext(), Tables.Collections.CONTENT_URI, null, Tables.Collections.USER_ID + " = ?", new String[]{String.valueOf(mUser == null ? -1 : mUser.id)}, null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (cursor != null) cursor.close();
            this.cursor = data;
            mCollectionAdapter.changeCursor(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            if (cursor != null) cursor.close();
        }
    }
}
