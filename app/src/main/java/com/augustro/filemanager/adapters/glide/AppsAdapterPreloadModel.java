package com.augustro.filemanager.adapters.glide;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.augustro.filemanager.GlideApp;
import com.augustro.filemanager.GlideRequest;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;

import java.util.Collections;
import java.util.List;

/**
 * @author Emmanuel Messulam <emmanuelbendavid@gmail.com>
 *         on 10/12/2017, at 15:38.
 */

public class AppsAdapterPreloadModel implements ListPreloader.PreloadModelProvider<String> {

    private GlideRequest<Drawable> request;
    private List<String> items;

    public AppsAdapterPreloadModel(Fragment f) {
        request = GlideApp.with(f).asDrawable().fitCenter();
    }

    public void setItemList(List<String> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public List<String> getPreloadItems(int position) {
        if(items == null) return Collections.emptyList();
        else return Collections.singletonList(items.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder getPreloadRequestBuilder(String item) {
        return request.clone().load(item);
    }

    public void loadApkImage(String item, ImageView v) {
        request.load(item).into(v);
    }
}
