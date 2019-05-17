package com.cncoderx.photopicker.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.cncoderx.photopicker.R;
import com.cncoderx.photopicker.bean.Photo;
import com.cncoderx.photopicker.core.ImageCacheService;
import com.cncoderx.photopicker.graphics.BitmapDrawable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wujie
 */
public class PhotoGridAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater mLayoutInflater;
    private ImageCacheService mImageCacheService;
    private List<Photo> mData = new ArrayList<>();
    private Set<Integer> mSelectedIdSet = new HashSet<>();
    private OnSelectedChangeListener onSelectedChangeListener;
    private boolean isSelectable = true;
    private int maxCount = 1;

    public PhotoGridAdapter(Context context) {
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
        mImageCacheService = new ImageCacheService(context);
    }

    public int getSize() {
        return mData.size();
    }

    public Photo get(int index) {
        return mData.get(index);
    }

    public void add(Photo photo) {
        mData.add(photo);
    }

    public void addAll(Collection<Photo> photos) {
        mData.addAll(photos);
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
    public Photo getItem(int position) {
        return get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void setSelectable(boolean selectable) {
        isSelectable = selectable;
    }

    public int getSelectedCount() {
        return mSelectedIdSet.size();
    }

    public List<Photo> getSelectedData() {
        List<Photo> result = new ArrayList<>();
        for(int index : mSelectedIdSet) {
            Photo photo = get(index);
            result.add(photo);
        }
        return result;
    }

    public void setOnSelectedChangeListener(OnSelectedChangeListener listener) {
        this.onSelectedChangeListener = listener;
    }

    void notifySelectedChangeListener(int index, boolean isSelected) {
        if (onSelectedChangeListener != null) {
            if (isSelected) {
                onSelectedChangeListener.select(index);
            } else {
                onSelectedChangeListener.unselect(index);
            }
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v;
        ViewHolder vh;
        if (convertView == null) {
            v = mLayoutInflater.inflate(R.layout.item_photo_grid, parent, false);
            v.setTag(vh = new ViewHolder(v));
        } else {
            v = convertView;
            vh = (ViewHolder) v.getTag();
        }
        vh.onBind(get(position), position);
        return v;
    }

    class ViewHolder implements View.OnClickListener {
        int position;
        View itemView;
        ImageView ivPhoto;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            this.itemView = itemView;
            ivPhoto = (ImageView) itemView.findViewById(R.id.photo);
            checkBox = (CheckBox) itemView.findViewById(R.id.checkbox);
            checkBox.setOnClickListener(this);
        }

        public void onBind(Photo photo, int position) {
            this.position = position;

            Drawable recycle = ivPhoto.getDrawable();
            Drawable drawable = drawableForItem(photo, recycle);
            if (recycle != drawable) {
                ivPhoto.setImageDrawable(drawable);
            }

            if (mSelectedIdSet.contains(position)) {
                checkBox.setChecked(true);
            } else {
                checkBox.setChecked(false);
            }
        }

        private Drawable drawableForItem(Photo photo, Drawable recycle) {
            final BitmapDrawable drawable;
            if (recycle == null || !(recycle instanceof BitmapDrawable)) {
                drawable = new BitmapDrawable(mImageCacheService);
            } else {
                drawable = (BitmapDrawable) recycle;
            }
            int imageSize = context.getResources().getDimensionPixelSize(R.dimen.photo_grid_size);
            drawable.setImagePath(photo.filePath, imageSize, imageSize);
            return drawable;
        }

        @Override
        public void onClick(View v) {
            if (maxCount <= 0)
                return;

            if (!isSelectable) {
                checkBox.toggle();
                return;
            }
            boolean isChecked = checkBox.isChecked();
            if (isChecked) {
                if (getSelectedCount() >= maxCount) {
                    checkBox.toggle();
                    Toast.makeText(context.getApplicationContext(),
                            context.getString(R.string.image_max_count_tip, maxCount),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                mSelectedIdSet.add(position);
            } else {
                mSelectedIdSet.remove(position);
            }
            notifySelectedChangeListener(position, isChecked);
        }
    }

    public static interface OnSelectedChangeListener {
        void select(int index);
        void unselect(int index);
    }
}
