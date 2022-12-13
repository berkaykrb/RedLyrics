package net.redboxgames.redlyrics.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import net.redboxgames.redlyrics.R;

public class AboutFragment extends Fragment {

    private AboutViewModel notificationsViewModel;

    public AboutFragment() {
        // Required empty public constructor
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(AboutViewModel.class);
        View root = inflater.inflate(R.layout.fragment_about, container, false);
        TextView redLyricsTxt = root.findViewById(R.id.redLyricsonAbout);
        TextView redboxTxt = root.findViewById(R.id.redboxonAbout);

        String textwHTML = "<font color=#FF0000>" + "Red" + "</font><font color=#FFFFFF>" +  "Lyrics." + "</font>";
        redLyricsTxt.setText(Html.fromHtml(textwHTML));

        String textwHTML2 = "<font color=#FFFFFF>" + "by" + "</font> <font color=#FF0000>" +  "RedBox" + "</font>";
        redboxTxt.setText(Html.fromHtml(textwHTML2));

        Button websiteBtn = root.findViewById(R.id.websiteBtn);
        Button instaBtn = root.findViewById(R.id.instaBtn);
        Button twitterBtn = root.findViewById(R.id.twitterBtn);

        websiteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("http://www.redboxgames.net");
            }

        });

        twitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.twitter.com/redsoft34/");
            }

        });

        instaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToUrl("https://www.instagram.com/redsoft34/");
            }

        });
        return root;
    }

    private void goToUrl (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        startActivity(launchBrowser);
    }

}
