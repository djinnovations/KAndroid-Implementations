package com.goldadorn.main.assist;

import android.content.Context;
import android.database.Cursor;

public abstract class ObjectAsyncLoader extends AsyncLoader<ObjectAsyncLoader.Result> {
    public ObjectAsyncLoader(Context context) {
        super(context);
    }


    public class Result extends BaseResult {
        public Object object;

        @Override
        protected void clear() {
            if (object != null && object instanceof Cursor)
                ((Cursor) object).close();
            super.clear();
        }
    }
}