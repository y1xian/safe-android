package com.yyx.yyxframe.utils.imageload;


import android.widget.ImageView;

public class YImage implements ImageLoader {

    private static ImageLoader imageLoader;
    private static YImage mImage;

    public static void init(ImageLoader loader) {
        imageLoader = loader;
    }

    public static YImage getInstance() {
        if (imageLoader == null) {
            throw new NullPointerException("Call YFrame.initXImageLoader(ImageLoader loader) within your Application onCreate() method." +
                    "Or extends YApplication");
        }
        if (mImage == null) {
            mImage = new YImage();
        }
        return mImage;
    }

    @Override
    public void load(ImageView imageView, Object imageUrl) {
        imageLoader.load(imageView, imageUrl);
    }

    @Override
    public void load(ImageView imageView, Object imageUrl, int defaultImage) {
        imageLoader.load(imageView, imageUrl, defaultImage);
    }

    @Override
    public void load(ImageView imageView, Object imageUrl, int defaultImage, int errorImage) {
        imageLoader.load(imageView, imageUrl, defaultImage, errorImage);
    }

    @Override
    public void load(ImageView imageView, Object imageUrl, Object transformation) {
        imageLoader.load(imageView, imageUrl, transformation);
    }
}
