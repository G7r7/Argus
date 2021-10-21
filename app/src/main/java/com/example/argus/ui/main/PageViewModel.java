package com.example.argus.ui.main;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.argus.R;

public class PageViewModel extends AndroidViewModel {

    public PageViewModel(@NonNull Application application) {
        super(application);
    }

    @StringRes
    private static final int[] TAB_STRINGS = new int[]{
            R.string.tab_text_type_text_here,
            R.string.tab_pictures_select_picture,
            R.string.tab_settings_type_parameter
    };

    private MutableLiveData<Integer> mIndex = new MutableLiveData<>();
    private LiveData<String> mText = Transformations.map(mIndex, new Function<Integer, String>() {
        @Override
        public String apply(Integer input) {
            return getApplication().getString(TAB_STRINGS[input - 1]);
        }
    });

    public void setIndex(int index) {
        mIndex.setValue(index);
    }

    public LiveData<String> getText() {
        return mText;
    }
}