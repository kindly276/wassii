package com.vn.wassii.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.thao.mylibrary.GsonRequest;
import com.vn.wassii.R;
import com.vn.wassii.activity.MainActivity;
import com.vn.wassii.dialog.PopupCancel;
import com.vn.wassii.fragment.ListLanguageFragment;
import com.vn.wassii.model.Language;
import com.vn.wassii.model.LoginReponse;
import com.vn.wassii.utils.Constants;
import com.vn.wassii.utils.UrlHelper;
import com.vn.wassii.utils.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by PC0280 on 1/11/2016.
 */
public class LanguageAdapter extends RecyclerView.Adapter {


    private List<Language> MessageList;
    private MainActivity mainActivity;
    private OnPopupConfirmListener listener;
    private SharedPreferences sharedPref;
    private ProgressDialog prDialog;

    public LanguageAdapter(List<Language> Messages, MainActivity mainActivity, OnPopupConfirmListener listener) {
        this.MessageList = Messages;
        this.mainActivity = mainActivity;
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.item_list_language, parent, false);

        vh = new LanguageViewHolder(v, mainActivity, MessageList, listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof LanguageViewHolder) {

            Language language = (Language) MessageList.get(position);
            if (language.isSelect()) {
                ((LanguageViewHolder) holder).image_logo.setImageResource(language.getDrawablepress());
                ((LanguageViewHolder) holder).text_name.setTextColor(mainActivity.getResources().getColor(R.color.color_common));

            } else {
                ((LanguageViewHolder) holder).image_logo.setImageResource(language.getDrawablenormal());
                ((LanguageViewHolder) holder).text_name.setTextColor(mainActivity.getResources().getColor(R.color.black));


            }
            ((LanguageViewHolder) holder).text_name.setText(language.getName());
            ((LanguageViewHolder) holder).language = language;

        }
    }


    @Override
    public int getItemCount() {
        if (MessageList != null) {
            return MessageList.size();
        }
        return 0;


    }

    //
    public class LanguageViewHolder extends RecyclerView.ViewHolder {
        public TextView text_name;
        public Language language;
        public ImageView image_logo;
        private PopupCancel popup;

        public LanguageViewHolder(View v, final MainActivity activity, final List<Language> languageList, final OnPopupConfirmListener listener) {
            super(v);
            text_name = (TextView) v.findViewById(R.id.text_name);
            image_logo = (ImageView) v.findViewById(R.id.image_logo);
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.logE("thaond", "language" + language.getName());
                    if (language.getId().equals(Constants.EN_LANGUAGE) || language.getId().equals(Constants.VN_LANGUAGE)) {
                        if (Utils.hasConnection(activity)) {
                            if (!language.isSelect()) {
                                doSave(language);
                            }
                        } else {
                            Utils.showMessage(activity, activity.getResources().getString(R.string.no_connection));
                        }
                    } else {
                        popup = new PopupCancel(activity, String.format(activity.getResources().getString(R.string.st_update_language_later), language.getName()), new PopupCancel.OnPopupConfirmListener() {
                            @Override
                            public void onConfirmOK() {
                                popup.dismiss();
                            }

                        });
                        popup.show();
                    }
                }
            });
        }
    }

    public interface OnPopupConfirmListener {
        void onConfirmOK(Language language);
    }

    private void doSave(final Language language) {
        sharedPref = mainActivity.getSharedPreferences(
                mainActivity.getResources().getString(R.string.login_share_private),
                Context.MODE_PRIVATE);

        showProgressDialog(true, mainActivity.getString(R.string.contact_please_wait));
        RequestQueue queue = Constants.getQueue(mainActivity);
        Map<String, String> params = new HashMap<String, String>();
        try {
            params.put(Constants.AUTHORIZATION, sharedPref.getString(Constants.TOKEN, ""));
        } catch (Exception e) {

        }
        String headers = "?" + Constants.FULLNAME + "=" + Utils.validateString("") + "&" + Constants.ADDRESS + "=" + Utils.validateString("") + "&" + Constants.LANGUAGEID + "=" + Utils.validateString(language.getId());
        Log.e("thaond", "headers" + headers);
        GsonRequest<LoginReponse> postRequest = new GsonRequest<LoginReponse>(
                Request.Method.GET, UrlHelper.UPDATE_USER_INFO + headers, LoginReponse.class, params, null,
                new Response.Listener<LoginReponse>() {
                    public void onResponse(LoginReponse response) {
                        procesAfterLogin(response, language);
                        showProgressDialog(false, "");
                        // processResponse(response);
                    }
                }, new Response.ErrorListener(

        ) {
            public void onErrorResponse(VolleyError error) {
                showProgressDialog(false, "");
                Utils.showMessage(mainActivity,
                        mainActivity.getResources().getString(R.string.error));

            }
        });
        queue.add(postRequest);
        postRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constants.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }

    private void procesAfterLogin(LoginReponse response, Language language) {
        Log.e("thaond", "response" + response.getException());
        if (response.getException() == null) {
            try {
                Utils.showMessage(mainActivity,
                        response.getMessage());
                if (language.isSelect() == false) {
                    language.setIsSelect(true);
                    for (Language language1 : MessageList) {
                        if (!language1.getName().equals(language.getName())) {
                            language1.setIsSelect(false);
                        }
                    }
                }
                notifyDataSetChanged();
                Utils.saveLanguage(sharedPref, language.getId());
                mainActivity.selectFragment(ListLanguageFragment.newInstance());
                mainActivity.refresh();
            } catch (Exception e) {
                Utils.showMessage(mainActivity,
                        mainActivity.getResources().getString(R.string.error));
            }
        } else {
            Utils.showMessage(mainActivity,
                    response.getMessage());
        }
    }

    private void showProgressDialog(boolean show, String message) {
        if (show) {
            if (prDialog == null) {
                prDialog = new ProgressDialog(mainActivity);
            }
            if (message != null) {
                prDialog.setMessage(message);
            }

            prDialog.setCancelable(false);
            prDialog.show();
        } else {
            if (prDialog != null) {
                prDialog.dismiss();

            }
        }
    }
}