package com.cncoderx.photopicker.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.cncoderx.photopicker.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author wujie
 */
public class PreviewAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> mData = new ArrayList<>();

    public PreviewAdapter(Context context) {
        mContext = context;
    }

    public int getSize() {
        return mData.size();
    }

    public String get(int index) {
        return mData.get(index);
    }

    public void add(String url) {
        mData.add(url);
    }

    public void addAll(String... urls) {
        Collections.addAll(mData, urls);
    }

    public void addAll(Collection<String> urls) {
        mData.addAll(urls);
    }

    public void remove(int index) {
        mData.remove(index);
    }

    public void clear() {
        mData.clear();
    }

    @Override
    public int getCount() {
        return getSize();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        view.findViewById(R.id.preview).setOnClickListener(null);

        container.removeView(view);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pager_preview, null);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.preview);
        ContentLoadingProgressBar progressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progress);

        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity) mContext).finish();
            }
        });

        String url = mData.get(position);
        if (URLUtil.isFileUrl(url)) {
            String path = Uri.parse(url).getPath();
            new LocalImageLoader(photoView, progressBar).execute(path);
        } else if (URLUtil.isNetworkUrl(url)) {
            new NetworkImageLoader(photoView, progressBar).execute(url);
        }

        container.addView(view);
        return view;
    }

    static class LocalImageLoader extends AsyncTask<String, Void, Bitmap> {
        WeakReference<ImageView> mImageView;
        WeakReference<ContentLoadingProgressBar> mProgressBar;

        public LocalImageLoader(ImageView imageView, ContentLoadingProgressBar progressBar) {
            mImageView = new WeakReference<ImageView>(imageView);
            mProgressBar = new WeakReference<ContentLoadingProgressBar>(progressBar);
        }

        @Override
        protected void onPreExecute() {
            ContentLoadingProgressBar progressBar = mProgressBar.get();
            if (progressBar != null) {
                progressBar.show();
            }
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            return BitmapFactory.decodeFile(urls[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = mImageView.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }

            ContentLoadingProgressBar progressBar = mProgressBar.get();
            if (progressBar != null) {
                progressBar.hide();
            }
        }
    }

    static class NetworkImageLoader extends AsyncTask<String, Void, Bitmap> {
        WeakReference<ImageView> mImageView;
        WeakReference<ContentLoadingProgressBar> mProgressBar;

        public NetworkImageLoader(ImageView imageView, ContentLoadingProgressBar progressBar) {
            mImageView = new WeakReference<ImageView>(imageView);
            mProgressBar = new WeakReference<ContentLoadingProgressBar>(progressBar);
        }

        @Override
        protected void onPreExecute() {
            ContentLoadingProgressBar progressBar = mProgressBar.get();
            if (progressBar != null) {
                progressBar.show();
            }
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            URLConnection connection = null;

            try {
                URL url = new URL(urls[0]);
                connection = url.openConnection();
                connection.setDoInput(true);
                connection.connect();

                InputStream stream = connection.getInputStream();
                try {
                    return BitmapFactory.decodeStream(stream);
                } finally {
                    stream.close();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            ImageView imageView = mImageView.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }

            ContentLoadingProgressBar progressBar = mProgressBar.get();
            if (progressBar != null) {
                progressBar.hide();
            }
        }
    }
}
