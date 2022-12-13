package net.redboxgames.redlyrics;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import net.redboxgames.redlyrics.ui.about.AboutFragment;
import net.redboxgames.redlyrics.ui.home.HomeFragment;
import net.redboxgames.redlyrics.ui.lyrics.LyricsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    static final String CLIENT_ID = "5e4b0f71a8974825ab549218a5f1fb2a";
    private static final String REDIRECT_URI = "https://redboxgames.net/";
    private SpotifyAppRemote mSpotifyAppRemote;
    private RequestQueue mQueue;
    public static String lyricURL;
    final FragmentManager fm = getSupportFragmentManager();
    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new LyricsFragment();
    final Fragment fragment3 = new AboutFragment();
    Fragment active = fragment1;
    AlertDialog.Builder alrdialogg;
    public boolean bildirimAlindi = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container, fragment1, "1").commit();


        //STATUS BAR IKONLARINI BEYAZ YAPMA - BASLANGIC
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            this.getWindow().setStatusBarColor(Color.WHITE);
        }
        //STATUS BAR IKONLARINI BEYAZ YAPMA - BITIS

        //ACTIONBAR'I YUKLEME BASLANGIC
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar);
        //ACTIONBAR'I YUKLEME BITIS

        mQueue = Volley.newRequestQueue(this);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.navigation_lyrics:
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.navigation_about:
                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };


    @Override
    public void onStart() {

        boolean isAppInstalled = appInstalledOrNot("com.spotify.music");
        ConstraintLayout contentDgs = findViewById(R.id.constLay);

        super.onStart();

        if(isAppInstalled) {
            //SPOTIFY YUKLU INTERNETI KONTROL ET

            if (hasConnectivity(this)) {

            Log.d("Connectivity Status", "Connected");

            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .showAuthView(true)
                            .build();

            SpotifyAppRemote.connect(this, connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;
                            Log.d("MainActivity", "Connected! Yay!");
                            connected();
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e("MainActivity", throwable.getMessage(), throwable);
                        }
                    });

            }

            else {
                // BAGLANTI YOK, HATA VER
                Log.d("Connection", "No connection");
                connectionError();
            }

        }
        else {
            // SPOTIFY YUKLU DEGIL, HATA VER
            Log.d("Spotify App Status", "INSTALLED");
            installError();
        }

    }

    public void connected() {

        Toast.makeText(getApplicationContext(),"Connected to Spotify",Toast.LENGTH_SHORT).show();

        TextView artistTxt = findViewById(R.id.artistTxt);
        TextView songTxt = findViewById(R.id.songTxt);
        TextView albumTxt = findViewById(R.id.albumTxt);
        TextView nowplayingTxt = findViewById(R.id.nowplayingText);
        TextView nowplaying2Txt = findViewById(R.id.nowplayingText2);
        ImageView spotifyLogoimg = findViewById(R.id.spotifyLogo);
        Button getLyricsbtn = findViewById(R.id.getlyricBtn);
        ProgressBar loadingBar = findViewById(R.id.progressBar);
        ImageView playingsongSpacee = findViewById(R.id.playingsongSpace);
        TextView researchTxt = findViewById(R.id.researchTxt);
        TextView noLyricsFoundTxt = findViewById(R.id.lyricsTxt);

        loadingBar.setVisibility(View.INVISIBLE);
        playingsongSpacee.setVisibility(View.VISIBLE);
        artistTxt.setVisibility(View.VISIBLE);
        songTxt.setVisibility(View.VISIBLE);
        albumTxt.setVisibility(View.VISIBLE);
        nowplayingTxt.setVisibility(View.VISIBLE);
        nowplaying2Txt.setVisibility(View.VISIBLE);
        spotifyLogoimg.setVisibility(View.VISIBLE);
        getLyricsbtn.setVisibility(View.VISIBLE);

        artistTxt.setSelected(true);
        songTxt.setSelected(true);
        albumTxt.setSelected(true);

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        artistTxt.setText(track.artist.name);
                        songTxt.setText(track.name);
                        albumTxt.setText(track.album.name);
                        researchTxt.setText(track.name + " by " + track.artist.name);

                            if(!bildirimAlindi) {

                                if (getIntent().hasExtra("fromNotification")) {
                                    getLyricLink();
                                }

                                bildirimAlindi = true;
                            }

                        String songnameforResearch = track.name + " by " + track.artist.name;
                        Log.d("SONGNAME", songnameforResearch);
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // STOP
    }

    public void getLyricLink() {
        final TextView researchTxtt = findViewById(R.id.researchTxt);

        String apilink = "https://genius.com/api/search/multi?q=" + researchTxtt.getText().toString();
        Log.d("SEARCH API LINK", apilink);
        jsonParse(apilink);
    }

    public void getLyricswithLink() {

        new getLyrics(this).execute();

        fm.beginTransaction().hide(active).show(fragment2).commit();
        active = fragment2;
        BottomNavigationView navigation = findViewById(R.id.nav_view);
        navigation.getMenu().findItem(R.id.navigation_lyrics).setChecked(true);

    }

    public void openLyricspage(){
        fm.beginTransaction().hide(active).show(fragment2).commit();
        active = fragment2;
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    private void jsonParse(String url) {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            //            ŞEMA
                            //   response
                            // sections array in response
                            // hits array in section array
                            // result object in hits array
                            // url object in result object

                            JSONObject responseD = response.getJSONObject("response");
                            JSONArray sections2 = responseD.getJSONArray("sections");

                            for (int i = 0; i < 1; i++) {
                                JSONObject sections = sections2.getJSONObject(i);
                                JSONArray hits2 = sections.getJSONArray("hits");

                                for (int j = 0; j < 1; j++) {
                                    JSONObject hits = hits2.getJSONObject(j);
                                    lyricURL = hits.getJSONObject("result").getString("url");
                                    Log.d("LYRICS URL", lyricURL);
                                    getLyricswithLink();
                                }

                            }
                        } catch (JSONException e) {

                            alrdialogg = new AlertDialog.Builder(MainActivity.this)
                                    .setTitle(getString(R.string.sorry))
                                    .setMessage("\n" + getString(R.string.lyrics_error) + "\n")
                                    .setCancelable(true)

                                    .setPositiveButton(
                                            getString(R.string.ok),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            })

                                    .setIcon(R.drawable.warning);

                            AlertDialog alert11 = alrdialogg.create();
                            alert11.show();

                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
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

public void connectionError(){

    AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
           .setTitle(getString(R.string.error))
            .setMessage("\n" + getString(R.string.connection_problem) + "\n")
            .setCancelable(false)

            .setNegativeButton(
                    getString(R.string.exit_app),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                            ;
                        }
                    })

            .setIcon(R.drawable.warning);

    AlertDialog alert11 = builder1.create();
    alert11.show();

    }

    public void installError(){

        AlertDialog.Builder builder1 = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.sorry))
                .setMessage("\n" + getString(R.string.spotify_not_installed) + "\n")
                .setCancelable(false)

                .setNegativeButton(
                        getString(R.string.exit_app),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
                                System.exit(1);
                                ;
                            }
                        })

                .setIcon(R.drawable.warning);

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }

}
