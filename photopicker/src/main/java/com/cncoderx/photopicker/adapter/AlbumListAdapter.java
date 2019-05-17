package com.cncoderx.photopicker.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cncoderx.photopicker.R;
import com.cncoderx.photopicker.bean.Album;
import com.cncoderx.photopicker.bean.Photo;
import com.cncoderx.photopicker.core.ImageCacheService;
import com.cncoderx.photopicker.graphics.BitmapDrawable;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author wujie
 */
public class AlbumListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ImageCacheService mImageCacheService;
    private List<Album> mData = new ArrayList<>();

    public AlbumListAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        mImageCacheService = new ImageCacheService(context);
    }

    public int getSize() {
        return mData.size();
    }

    public Album get(int index) {
        return mData.get(index);
    }

    public void add(Album album) {
        mData.add(album);
    }

    public void addAll(Collection<Album> album) {
        mData.addAll(album);
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
    public Album getItem(int position) {
        return get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        ViewHolder vh;
        if (convertView == null) {
            v = mLayoutInflater.inflate(R.layout.item_album_list, parent, false);
            v.setTag(vh = new ViewHolder(v));
        } else {
            v = convertView;
            vh = (ViewHolder) v.getTag();
        }
        vh.onBind(get(position), position);
        return v;
    }

    class ViewHolder {
        int position;
        View itemView;
        ImageView ivCover;
        TextView tvName;
        TextView tvSize;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            ivCover = (ImageView) itemView.findViewById(R.id.iv_album_cover);
            tvName = (TextView) itemView.findViewById(R.id.tv_album_name);
            tvSize = (TextView) itemView.findViewById(R.id.tv_album_size);
        }

        public void onBind(Album album, int position) {
            this.position = position;

            Drawable recycle = ivCover.getDrawable();
            Drawable drawable = drawableForItem(album, recycle);
            if (recycle != drawable) {
                ivCover.setImageDrawable(drawable);
            }

            if (!TextUtils.isEmpty(album.getName())) {
                tvName.setText(album.getName());
            }

            tvSize.setText(context.getString(R.string.image_size, album.getPhotoCount()));
        }

        private Drawable drawableForItem(Album album, Drawable recycle) {
            final BitmapDrawable drawable;
            if (recycle == null || !(recycle instanceof BitmapDrawable)) {
                drawable = new BitmapDrawable(mImageCacheService);
            } else {
                drawable = (BitmapDrawable) recycle;
            }
            int imageSize = context.getResources().getDimensionPixelSize(R.dimen.album_grid_size);
            drawable.setImagePath(album.getCoverPath(), imageSize, imageSize);
            return drawable;
        }
    }
}
