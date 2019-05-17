package com.topevery.hybird.plugin.media.video;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.DefaultSsChunkSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.topevery.hybird.plugin.R;

/**
 * @author wujie
 */
public class VideoActivity extends Activity {
    private SimpleExoPlayer mPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_video);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        TrackSelector trackSelector = new DefaultTrackSelector();
        mPlayer = ExoPlayerFactory.newSimpleInstance(this, trackSelector);

        PlayerView playerView = findViewById(R.id.player_view);
        playerView.requestFocus();
        playerView.setPlayer(mPlayer);

        Uri uri = getIntent().getData();
        if (uri == null) {
            throw new RuntimeException();
        }

        String url = uri.toString();
        boolean isLocalSource = URLUtil.isAssetUrl(url) || URLUtil.isFileUrl(url);

        DataSource.Factory sourceFactory = buildDataSourceFactory(isLocalSource);
        MediaSource mediaSource = buildMediaSource(uri, sourceFactory);

        mPlayer.setPlayWhenReady(true);
        mPlayer.prepare(mediaSource);
    }

    private DataSource.Factory buildDataSourceFactory(boolean localSource) {
        DataSource.Factory sourceFactory = null;
        if (localSource) {
            sourceFactory = new DefaultDataSourceFactory(this, "ExoPlayer");
        } else {
            sourceFactory = new DefaultHttpDataSourceFactory(
                    "ExoPlayer",
                    null,
                    DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
                    DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS,
                    true);
        }
        return sourceFactory;
    }

    private MediaSource buildMediaSource(Uri uri, DataSource.Factory sourceFactory) {
        int type = Util.inferContentType(uri.getLastPathSegment());
        switch (type) {
            case C.TYPE_SS:
                return new SsMediaSource(
                        uri, null, new DefaultSsChunkSource.Factory(sourceFactory), null, null);
            case C.TYPE_DASH:
                return new DashMediaSource(
                        uri, null, new DefaultDashChunkSource.Factory(sourceFactory), null, null);
            case C.TYPE_HLS:
                return new HlsMediaSource(uri, sourceFactory, null, null);
            case C.TYPE_OTHER:
                return new ExtractorMediaSource(
                        uri, sourceFactory, new DefaultExtractorsFactory(), null, null);
            default: {
                throw new IllegalStateException("Unsupported type: " + type);
            }
        }
    }

    @Override
    public void onBackPressed() {
        mPlayer.stop();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        mPlayer.release();
        super.onDestroy();
    }
}
