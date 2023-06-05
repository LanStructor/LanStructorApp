package com.lanstructor.android.general;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.lanstructor.android.R;
import com.lanstructor.android.model.Video;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayVideoFormYoutubeActivity extends YouTubeBaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video_form_youtube);

        Video video = (Video) getIntent().getSerializableExtra("video");

        YouTubePlayerView youTubePlayerView =  (YouTubePlayerView) findViewById(R.id.player);

        youTubePlayerView.initialize(getString(R.string.youtube_api_key),
                new YouTubePlayer.OnInitializedListener() {
                    @Override
                    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                                        YouTubePlayer youTubePlayer, boolean b) {


                        youTubePlayer.cueVideo(extractYoutubeVideoId(video.urlPath));
                    }
                    @Override
                    public void onInitializationFailure(YouTubePlayer.Provider provider,YouTubeInitializationResult youTubeInitializationResult) {

                    }
                });
    }

    public static String extractYoutubeVideoId(String ytUrl) {
        String vId = null;
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);

        if(matcher.find()){
            vId= matcher.group();
        }
        return vId;
    }
}