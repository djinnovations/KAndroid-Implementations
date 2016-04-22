package com.goldadorn.main.activities.productListing;

import com.github.siyamed.shapeimageview.ShaderImageView;

import android.content.Context;
import android.util.AttributeSet;

import com.github.siyamed.shapeimageview.shader.ShaderHelper;
import com.github.siyamed.shapeimageview.shader.SvgShader;
import com.goldadorn.main.R;

/**
 * Created by bpa001 on 4/22/16.
 */
public class DiamondImageView extends ShaderImageView
{
    public DiamondImageView(Context context) {
        super(context);
    }

    public DiamondImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiamondImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public ShaderHelper createImageViewHelper() {
        return new SvgShader(R.raw.silhouette);
    }
}
