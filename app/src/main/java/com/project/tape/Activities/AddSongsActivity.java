package com.project.tape.Activities;

import static com.project.tape.Fragments.FragmentGeneral.songsList;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.tape.Adapters.AddSongsAdapter;
import com.project.tape.R;

import java.io.IOException;

public class AddSongsActivity extends AppCompatActivity implements AddSongsAdapter.OnAddSongListener {

    AddSongsAdapter addSongsAdapter;
    RecyclerView addSongsRecyclerView;
    ImageButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_add_songs);
        setContentView(R.layout.activity_add_songs);
        getSupportActionBar().hide();

        backBtn = findViewById(R.id.backBtn_addSongs);
        backBtn.setOnClickListener(btnL);

        addSongsAdapter = new AddSongsAdapter(AddSongsActivity.this, songsList, this);
        addSongsAdapter.updateAddSongsListList(songsList);
        addSongsRecyclerView = findViewById(R.id.add_songs_recyclerView);
        addSongsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        addSongsRecyclerView.setAdapter(addSongsAdapter);
    }

    @Override
    public void onAddSongClick(int position) throws IOException {
    }

    View.OnClickListener btnL = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.backBtn_addSongs:
                    finish();
                    overridePendingTransition(0, R.anim.hold);
                    break;
            }
        }
    };

}

