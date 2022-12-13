package net.redboxgames.redlyrics.ui.lyrics;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.MovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.redboxgames.redlyrics.MainActivity;
import net.redboxgames.redlyrics.R;
import net.redboxgames.redlyrics.getLyrics;

import org.w3c.dom.Text;

public class LyricsFragment extends Fragment {

    private LyricsViewModel dashboardViewModel;

    public LyricsFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = ViewModelProviders.of(this).get(LyricsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_lyrics, container, false);

        TextView currentplayingsong = root.findViewById(R.id.songforlyricTxt);
        TextView lyrics = root.findViewById(R.id.lyricsTxt);
        lyrics.setMovementMethod(new ScrollingMovementMethod());

        MainActivity activity = (MainActivity) getActivity();

        return root;
    }

}
