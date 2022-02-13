package com.project.tape;

import static com.project.tape.SongsFragment.art;
import static com.project.tape.SongsFragment.mediaPlayer;
import static com.project.tape.SongsFragment.position;
import static com.project.tape.SongsFragment.songsList;
import static com.project.tape.SongsFragment.uri;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Random;

public class SongInfoTab extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_title, artist_name, time_duration_passed, time_duration_total, song_title_main, artist_name_main;
    ImageView album_cover, shuffleBtn, previousBtn, nextBtn, repeatBtn;
    ImageButton backBtn;
    FloatingActionButton playPauseBtn;
    private SeekBar seekBar;
    private final Handler handler = new Handler();

    static boolean repeatBtnClicked, shuffleBtnClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song_info_tab);

        initViews();
        getIntentMethod();


        song_title.setText(songsList.get(position).getTitle());
        artist_name.setText(songsList.get(position).getArtist());


        if (shuffleBtnClicked) {
            shuffleBtn.setImageResource(R.drawable.shuffle_songs_on);
        } else {
            shuffleBtn.setImageResource(R.drawable.shuffle_songs_off);
        }
        if (repeatBtnClicked) {
            repeatBtn.setImageResource(R.drawable.repeat_song_on);
        } else {
           repeatBtn.setImageResource(R.drawable.repeat_song_off);
        }


        mediaPlayer.setOnCompletionListener(this);


        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo((progress * 1000));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        SongInfoTab.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    time_duration_passed.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    //Sets pause button image and max value of seekBar
    private void getIntentMethod() {
            if (songsList != null) {
                if (mediaPlayer.isPlaying()) {
                    playPauseBtn.setImageResource(R.drawable.pause_song);
                } else {
                    playPauseBtn.setImageResource(R.drawable.play_song);
                }
                seekBar.setMax(mediaPlayer.getDuration() / 1000);
                metaDataInInfoTab(uri);
            }
        }

    //Sets song title and artist name
    private void sentIntent() {
        Intent data = new Intent();
        song_title.setText(songsList.get(position).getTitle());
        artist_name.setText(songsList.get(position).getArtist());
        setResult(RESULT_OK, data);
    }

    //Sets album cover when SongInfoTab is fully opened
    private void metaDataInInfoTab(Uri uri) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(songsList.get(position).getDuration()) / 1000;
        time_duration_total.setText(formattedTime(durationTotal));
        art = retriever.getEmbeddedPicture();

        if (art != null) {
            Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(album_cover);
        } else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.default_cover)
                    .into(album_cover);
        }
    }

    //Formats time to seconds and minutes to pass it to seekBar
    private String formattedTime(int mCurrentPosition) {
        String totalOut = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalOut = minutes + ":" + seconds;
        totalNew =  minutes + ":" + "0" + seconds;
        if (seconds.length() == 1) {
            return totalNew;
        } else {
            return totalOut;
        }
    }

    //Creates new Thread for play
    private void playThreadBtn() {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    //Sets play button image and sets progress of seekBar
    public void playPauseBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            playPauseBtn.setImageResource(R.drawable.play_song);
            mediaPlayer.pause();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            SongInfoTab.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        } else {
                playPauseBtn.setImageResource(R.drawable.pause_song);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration() / 1000);

                    SongInfoTab.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mediaPlayer != null)
                            {
                                int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                                seekBar.setProgress(mCurrentPosition);
                            }
                            handler.postDelayed(this, 1000);
                        }
                    });
            }
    }

    //Creates new Thread for next song
    private void nextThreadBtn() {
        Thread nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (repeatBtnClicked) {
                            repeatBtnClicked = false;
                            nextBtnClicked();
                            sentIntent();
                            SongInfoTab.this.getSharedPreferences("preferences_name", Context.MODE_PRIVATE).edit().putInt("progress", position).commit();
                            repeatBtnClicked = true;
                        } else {
                            nextBtnClicked();
                            sentIntent();
                            SongInfoTab.this.getSharedPreferences("preferences_name", Context.MODE_PRIVATE).edit().putInt("progress", position).commit();
                        }
                    }
                });
            }
        };
        nextThread.start();
    }

    /*When next button is clicked, sets position of songsList to +1. Also sets song title,
     artist name, album cover and seekBar position when SongInfoTab is fully opened*/
    public void nextBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBtnClicked && !repeatBtnClicked) {
                position = getRandom(songsList.size() - 1);
                repeatBtn.setImageResource(R.drawable.repeat_song_off);
            }
            else if (!shuffleBtnClicked && repeatBtnClicked) {
                uri = Uri.parse(songsList.get(position).getData());
            }
            else if (!shuffleBtnClicked && !repeatBtnClicked) {
                position = (position + 1 % songsList.size());
                if (position == songsList.size()) {
                    position = 0;
                }

            }
            else if (shuffleBtnClicked && repeatBtnClicked) {
                position = getRandom(songsList.size() - 1);
                repeatBtnClicked = false;
                repeatBtn.setImageResource(R.drawable.repeat_song_off);
            }

            uri = Uri.parse(songsList.get(position).getData());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaDataInInfoTab(uri);

            song_title.setText(songsList.get(position).getTitle());
            artist_name.setText(songsList.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            SongInfoTab.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.pause_song);
            mediaPlayer.start();
        } else {
                mediaPlayer.stop();
                mediaPlayer.release();

                if (shuffleBtnClicked && !repeatBtnClicked) {
                    position = getRandom(songsList.size() -1);
                    repeatBtn.setImageResource(R.drawable.repeat_song_off);
                }
                else if (!shuffleBtnClicked && repeatBtnClicked) {
                    uri = Uri.parse(songsList.get(position).getData());
                }
                else if (!shuffleBtnClicked && !repeatBtnClicked) {
                    position = (position + 1 % songsList.size());
                    if (position == songsList.size()) {
                        position = 0;
                    }
                    uri = Uri.parse(songsList.get(position).getData());
                }
                else if (shuffleBtnClicked && repeatBtnClicked) {
                    position = getRandom(songsList.size() -1);
                    repeatBtnClicked = false;
                    repeatBtn.setImageResource(R.drawable.repeat_song_off);
                }

                uri = Uri.parse(songsList.get(position).getData());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                metaDataInInfoTab(uri);

                song_title.setText(songsList.get(position).getTitle());
                artist_name.setText(songsList.get(position).getArtist());


                seekBar.setMax(mediaPlayer.getDuration() / 1000);

                SongInfoTab.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            seekBar.setProgress(mCurrentPosition);
                        }
                        handler.postDelayed(this, 1000);
                    }
                });
                mediaPlayer.setOnCompletionListener(this);
                playPauseBtn.setBackgroundResource(R.drawable.play_song);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i + 1);
    }

    //Creates new Thread for previous song
        private void previousThreadBtn () {
            Thread previousThread = new Thread() {
                @Override
                public void run() {
                    super.run();
                    previousBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                              if (repeatBtnClicked) {
                                  repeatBtnClicked = false;
                                  previousBtnClicked();
                                  sentIntent();
                                  SongInfoTab.this.getSharedPreferences("preferences_name", Context.MODE_PRIVATE).edit().putInt("progress", position).commit();
                                  repeatBtnClicked = true;
                              } else {
                                  previousBtnClicked();
                                  sentIntent();
                                  SongInfoTab.this.getSharedPreferences("preferences_name", Context.MODE_PRIVATE).edit().putInt("progress", position).commit();
                              }
                        }
                    });
                }
            };
            previousThread.start();
        }

    /*When previous button is clicked, sets position of songsList to -1. Also sets song title,
    artist name, album cover and seekBar position when SongInfoTab is fully opened*/
    private void previousBtnClicked() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();

            if (shuffleBtnClicked && !repeatBtnClicked) {
                position = getRandom(songsList.size() -1);
                repeatBtn.setImageResource(R.drawable.repeat_song_off);
            }
            else if (!shuffleBtnClicked && repeatBtnClicked) {
                uri = Uri.parse(songsList.get(position).getData());
            }

            else if (!shuffleBtnClicked && !repeatBtnClicked) {
                position = ((position - 1) < 0 ? (songsList.size() - 1) : (position - 1));
                uri = Uri.parse(songsList.get(position).getData());
            }
            else if (shuffleBtnClicked && repeatBtnClicked) {
                position = getRandom(songsList.size() -1);
                repeatBtnClicked = false;
                repeatBtn.setImageResource(R.drawable.repeat_song_off);
            }

            uri = Uri.parse(songsList.get(position).getData());

            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaDataInInfoTab(uri);

            song_title.setText(songsList.get(position).getTitle());
            artist_name.setText(songsList.get(position).getArtist());

            seekBar.setMax(mediaPlayer.getDuration() / 1000);

            SongInfoTab.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.pause_song);
            mediaPlayer.start();
        } else {
                mediaPlayer.stop();
                mediaPlayer.release();

                if (shuffleBtnClicked && !repeatBtnClicked) {
                    position = getRandom(songsList.size() -1);
                    repeatBtn.setImageResource(R.drawable.repeat_song_off);
                }
                else if (!shuffleBtnClicked && repeatBtnClicked) {
                    uri = Uri.parse(songsList.get(position).getData());
                }

                else if (!shuffleBtnClicked && !repeatBtnClicked) {
                    position = ((position - 1) < 0 ? (songsList.size() - 1) : (position - 1));
                    uri = Uri.parse(songsList.get(position).getData());
                }
                else if (shuffleBtnClicked && repeatBtnClicked) {
                    position = getRandom(songsList.size() -1);
                    repeatBtnClicked = false;
                    repeatBtn.setImageResource(R.drawable.repeat_song_off);
                }

                uri = Uri.parse(songsList.get(position).getData());


                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                metaDataInInfoTab(uri);

                song_title.setText(songsList.get(position).getTitle());
                artist_name.setText(songsList.get(position).getArtist());

                seekBar.setMax(mediaPlayer.getDuration() / 1000);

                SongInfoTab.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            seekBar.setProgress(mCurrentPosition);
                        }
                        handler.postDelayed(this, 1000);
                    }
                });
                mediaPlayer.setOnCompletionListener(this);
                playPauseBtn.setBackgroundResource(R.drawable.play_song);
            }
    }

    //When song is finished, switches to next song
    @Override
    public void onCompletion(MediaPlayer mp) {
            if (repeatBtnClicked) {
                nextBtnClicked();
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.start();
            } else {
                nextBtnClicked();
                mediaPlayer.setOnCompletionListener(this);
                mediaPlayer.start();
                SongInfoTab.this.getSharedPreferences("preferences_name", Context.MODE_PRIVATE).edit().putInt("progress", position).commit();
            }
    }

    @Override
    protected void onResume() {
        nextThreadBtn();
        playThreadBtn();
        previousThreadBtn();
        super.onResume();
    }

    @Override
    public void finish() {
        super.finish();
        SongInfoTab.this.getSharedPreferences("preferences_name", Context.MODE_PRIVATE).edit().putInt("progress", position).commit();
    }


    private void initViews() {
        song_title = findViewById(R.id.infoTab_song_title);
        artist_name = findViewById(R.id.infoTab_artist_title);
        time_duration_passed = findViewById(R.id.infoTab_duration_passed);
        time_duration_total = findViewById(R.id.infoTab_duration_left);
        album_cover = findViewById(R.id.infoTab_album_cover);
        shuffleBtn = findViewById(R.id.infoTab_shuffle_button);
        previousBtn = findViewById(R.id.infoTab_previous_button);
        nextBtn = findViewById(R.id.infoTab_next_button);
        repeatBtn = findViewById(R.id.infoTab_repeat_button);
        playPauseBtn = findViewById(R.id.infoTab_pause_button);
        seekBar = findViewById(R.id.infoTab_seekBar);
        backBtn = findViewById(R.id.backBtn_songInfo);

        artist_name_main = findViewById(R.id.artist_name_main);
        song_title_main = findViewById(R.id.song_title_main);

        shuffleBtn.setOnClickListener(btnListener);
        repeatBtn.setOnClickListener(btnListener);
        backBtn.setOnClickListener(btnListener);
    }

    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.infoTab_shuffle_button:
                    if (shuffleBtnClicked) {
                        shuffleBtnClicked = false;
                        shuffleBtn.setImageResource(R.drawable.shuffle_songs_off);
                    } else {
                        shuffleBtnClicked = true;
                        shuffleBtn.setImageResource(R.drawable.shuffle_songs_on);
                        repeatBtnClicked = false;
                        repeatBtn.setImageResource(R.drawable.repeat_song_off);
                    }
                    break;
                case R.id.infoTab_repeat_button:
                    if (repeatBtnClicked) {
                        repeatBtnClicked = false;
                        repeatBtn.setImageResource(R.drawable.repeat_song_off);
                    } else  {
                        repeatBtnClicked = true;
                        repeatBtn.setImageResource(R.drawable.repeat_song_on);
                    }
                    break;
                case R.id.backBtn_songInfo:
                    finish();
                    break;
            }
        }
    };


}

