package net.redboxgames.redlyrics;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import net.redboxgames.redlyrics.ui.lyrics.LyricsFragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static android.app.PendingIntent.getActivity;

public class getLyrics extends AsyncTask<Void,Void,Void>
    {
        String testURL= "https://genius.com/Travis-scott-stargazing-lyrics";
        public static String lyrics;
        String veri;
        Context context;
        private ProgressDialog dialog;
        private AlertDialog.Builder alrdialog;

        public getLyrics (Context context){

            this.context=context;
            dialog = new ProgressDialog(context);

            dialog.setMessage(context.getString(R.string.finding_lyrics));

            alrdialog = new AlertDialog.Builder(context)
                    .setTitle(context.getString(R.string.sorry))
                    .setMessage("\n" + context.getString(R.string.lyrics_error) + "\n")
                    .setCancelable(true)

                    .setPositiveButton(
                            context.getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    })

                    .setIcon(R.drawable.warning);

        }

        @Override
        protected  void onPreExecute()
        {
            dialog.show();
        }
        @Override
        protected Void doInBackground(Void... params) {



            try {
                Log.d("GidenURL", MainActivity.lyricURL);
                Document doc = Jsoup.connect(MainActivity.lyricURL).get();
                doc.outputSettings(new Document.OutputSettings().prettyPrint(false));
                Elements elements=doc.select("div[class=lyrics]");
                elements.select("p");
                elements.select("defer-compile").remove();

                veri=elements.html();
                lyrics=Jsoup.parse(veri).html();
                Log.d("PARSE",Jsoup.parse(veri).html());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
            }



        @Override
        protected void onPostExecute(Void avoid)
        {

            String linkKontrol = MainActivity.lyricURL;
            if (linkKontrol.endsWith("lyrics")) {

            TextView lyricsVar = ((Activity)context).findViewById(R.id.lyricsTxt);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                lyricsVar.setText(Html.fromHtml(lyrics, Html.FROM_HTML_MODE_COMPACT));
            } else {
                lyricsVar.setText(Html.fromHtml(lyrics));
            }

        }

        else {
                AlertDialog alert11 = alrdialog.create();
                alert11.show();

        }

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

        }
    }
