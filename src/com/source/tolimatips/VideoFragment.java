package com.source.tolimatips;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import com.google.android.youtube.player.YouTubePlayer.Provider;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

public class VideoFragment extends YouTubePlayerFragment {

	
	public static final String DEVELOPER_KEY = "AIzaSyBJOSTZLd7wLOeo-K9cwRClhg0AJ3FkRZQ";
	
    public static VideoFragment newInstance(String url) {

        VideoFragment f = new VideoFragment();

        Bundle b = new Bundle();
        
        b.putString("url", url);

        f.setArguments(b);
        
        f.init();

        return f;
    }

    private void init() {

        initialize(DEVELOPER_KEY, new OnInitializedListener() {

            @Override
            public void onInitializationFailure(Provider arg0, YouTubeInitializationResult arg1) { }

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                if (!wasRestored) { 
                    player.cueVideo(getArguments().getString("url"));
                }
            }
        });
    }
}