package com.vn.wassii.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.adapter.LanguageAdapter;
import com.vn.wassii.model.Language;
import com.vn.wassii.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by thaond on 1/11/2016.
 */
public class ListLanguageFragment extends Fragment implements LanguageAdapter.OnPopupConfirmListener {
    private View mView;
    private RecyclerView recycler_language;
    private String[] listLanguage, listLanguageId;
    private List<Language> languageList;
    private int[] drawableNormal = {
            R.mipmap.ic_england, R.mipmap.ic_vietnam, R.mipmap.ic_japan_normal, R.mipmap.ic_korea_normal
    };
    private int[] drawablePress = {
            R.mipmap.ic_england_press, R.mipmap.ic_vietnam_press, R.mipmap.ic_japan_press, R.mipmap.ic_korea_press
    };
    private SharedPreferences sharedPref;
    private MainActivity activity;
    private ProgressDialog prDialog;
    private String languageId;

    public static final ListLanguageFragment newInstance() {
        ListLanguageFragment myFragment = new ListLanguageFragment();
        return myFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mView == null) {
            activity = (MainActivity) getActivity();
            sharedPref = activity.getSharedPreferences(
                    getResources().getString(R.string.login_share_private),
                    Context.MODE_PRIVATE);
            languageId = sharedPref.getString(Constants.LANGUAGEID, "");

            mView = inflater.inflate(R.layout.fragment_list_language, container, false);
            activity.setToolbar((Toolbar) mView.findViewById(R.id.toolbar));
            recycler_language = (RecyclerView) mView.findViewById(R.id.recycler_language);
            listLanguage = getResources().getStringArray(R.array.list_language);
            listLanguageId = getResources().getStringArray(R.array.list_id_language);
            languageList = new ArrayList<Language>();
            for (int i = 0; i < listLanguage.length; i++) {
                Language language = new Language();
                language.setName(listLanguage[i].toString());
                language.setId(listLanguageId[i].toString());
                language.setDrawablenormal(drawableNormal[i]);
                language.setDrawablepress(drawablePress[i]);


                languageList.add(language);
            }
            if (languageId != null && !languageId.equals("")) {
                if (languageId.equals(Constants.JA_LANGUAGE)) {
                    languageList.get(2).setIsSelect(true);
                } else if (languageId.equals(Constants.KO_LANGUAGE)) {
                    languageList.get(3).setIsSelect(true);
                } else if (languageId.equals(Constants.EN_LANGUAGE)) {
                    languageList.get(0).setIsSelect(true);
                } else if (languageId.equals(Constants.VN_LANGUAGE)) {
                    languageList.get(1).setIsSelect(true);
                } else {
                    languageList.get(0).setIsSelect(true);

                }
            }
            LanguageAdapter adapter = new LanguageAdapter(languageList, (MainActivity) getActivity(), this);
            recycler_language.setLayoutManager(new LinearLayoutManager(getActivity()));
            recycler_language.setAdapter(adapter);
        }

        return mView;
    }

    @Override
    public void onConfirmOK(Language language) {

    }

}
