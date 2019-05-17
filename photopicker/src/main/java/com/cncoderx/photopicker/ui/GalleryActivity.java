package com.cncoderx.photopicker.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.cncoderx.photopicker.R;
import com.cncoderx.photopicker.adapter.AlbumListAdapter;
import com.cncoderx.photopicker.adapter.PhotoGridAdapter;
import com.cncoderx.photopicker.bean.Album;
import com.cncoderx.photopicker.bean.Photo;
import com.cncoderx.photopicker.io.BytesBufferPool;
import com.cncoderx.photopicker.io.GalleryBitmapPool;
import com.cncoderx.photopicker.utils.BucketHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wujie
 */
public class GalleryActivity extends AppCompatActivity {
    Toolbar mToolbar;
    MenuItem mPositiveItem;
    DrawerLayout mDrawer;

    private AlbumListAdapter mAlbumAdapter;
    private PhotoGridAdapter mPhotoAdapter;

    private int mAlbumIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gallery);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
                    mDrawer.closeDrawer(Gravity.LEFT);
                } else {
                    mDrawer.openDrawer(Gravity.LEFT);
                }
            }
        });

        initAlbumList();
        initPhotoGrid();
        updatePhotoGrid("");
    }

    private void initAlbumList() {
        ListView listView = (ListView) findViewById(R.id.lv_album);
        List<Album> albums = BucketHelper.getAlbumList(this);

        mAlbumAdapter = new AlbumListAdapter(this);
        mAlbumAdapter.addAll(albums);

        listView.setAdapter(mAlbumAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
                    mDrawer.closeDrawer(Gravity.LEFT);
                }

                if (mAlbumIndex != position) {
                    Album album = mAlbumAdapter.get(position);
                    updatePhotoGrid(album.getId());
                }
                mAlbumIndex = position;
            }
        });
    }

    private void initPhotoGrid() {
        final int maxSize = getIntent().getIntExtra("maxSize", 1);

        GridView gridView = (GridView) findViewById(R.id.grid);

        mPhotoAdapter = new PhotoGridAdapter(this);
        mPhotoAdapter.setMaxCount(maxSize);
        mPhotoAdapter.setOnSelectedChangeListener(new PhotoGridAdapter.OnSelectedChangeListener() {

            @Override
            public void select(int index) {
                int count = mPhotoAdapter.getSelectedCount();
                if (0 < count && !mPositiveItem.isVisible()) {
                    mPositiveItem.setVisible(true);
                }
                mPositiveItem.setTitle(getString(R.string.commit_count, count, maxSize));
            }

            @Override
            public void unselect(int index) {
                int count = mPhotoAdapter.getSelectedCount();
                if (0 == count && mPositiveItem.isVisible()) {
                    mPositiveItem.setVisible(false);
                }
                mPositiveItem.setTitle(getString(R.string.commit_count, count, 9));
            }
        });

        gridView.setAdapter(mPhotoAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Photo photo = mPhotoAdapter.get(position);
                List<Photo> photos = mPhotoAdapter.getSelectedData();
                int current = -1;
                int selected = photos.size();
                String[] urls = new String[selected];
                for (int i = 0; i < selected; i++) {
                    Photo p = photos.get(i);
                    if (current == -1 && photo.filePath.equals(p.filePath)) {
                        current = i;
                    }
                    urls[i] = Uri.fromFile(new File(p.filePath)).toString();
                }
                if (current == -1) {
                    String url = Uri.fromFile(new File(photo.filePath)).toString();
                    Intent intent = new Intent(GalleryActivity.this, PreviewActivity.class);
                    intent.putExtra("urls", new String[] {url});
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(GalleryActivity.this, PreviewActivity.class);
                    intent.putExtra("current", current);
                    intent.putExtra("urls", urls);
                    startActivity(intent);
                }
            }
        });
    }

    public void updatePhotoGrid(String albumId) {
        if (mPhotoAdapter != null) {
            List<Photo> photos;
            if (TextUtils.isEmpty(albumId)) {
                photos = BucketHelper.getPhotoList(getApplicationContext());
            } else {
                photos = BucketHelper.getPhotoListByBucketId(getApplicationContext(), albumId);
            }

            mPhotoAdapter.clear();
            mPhotoAdapter.addAll(new ArrayList<>(photos));
            mPhotoAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mPositiveItem = menu.findItem(R.id.action_positive);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_positive) {
            List<Photo> selectedPhotos = mPhotoAdapter.getSelectedData();
            int size = selectedPhotos.size();
            String[] data = new String[size];
            for (int i = 0; i < size; i++) {
                data[i] = selectedPhotos.get(i).filePath;
            }
            Intent intent = new Intent();
            intent.putExtra("data", data);
            setResult(RESULT_OK, intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(Gravity.LEFT)) {
            mDrawer.closeDrawer(Gravity.LEFT);
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GalleryBitmapPool.getInstance().clear();
        BytesBufferPool.getInstance().clear();
    }
}
