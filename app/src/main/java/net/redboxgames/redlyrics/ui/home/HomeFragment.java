package net.redboxgames.redlyrics.ui.home;

import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import net.redboxgames.redlyrics.MainActivity;
import net.redboxgames.redlyrics.R;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.types.Track;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class HomeFragment extends Fragment implements View.OnClickListener {

    private HomeViewModel homeViewModel;

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();

        return fragment;
    }


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        Button getLyricfunc = (Button) root.findViewById(R.id.getlyricBtn);
        getLyricfunc.setOnClickListener(this);


        return root;

    }

    @Override
    public void onClick(View v) {

        MainActivity activity = (MainActivity) getActivity();

        if (hasConnectivity(activity)) {

            activity.getLyricLink();
        }

        else {

            AlertDialog.Builder alrdialog = new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.sorry))
                    .setMessage("\n" + getString(R.string.connection_problem) + "\n")
                    .setCancelable(true)

                    .setPositiveButton(
                            getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })

                    .setIcon(R.drawable.warning);
            alrdialog.show();

        }

    }

    public boolean hasConnectivity(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getApplicationContext().getSystemService(
                            Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager
                    .getActiveNetworkInfo();

            return networkInfo != null && networkInfo.isAvailable()
                    && networkInfo.isConnected();
        } else {
            return false;
        }
    }

}
