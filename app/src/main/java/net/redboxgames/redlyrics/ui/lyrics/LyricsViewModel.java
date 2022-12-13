package net.redboxgames.redlyrics.ui.lyrics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LyricsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public LyricsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}