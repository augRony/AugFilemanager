package com.augustro.filemanager.adapters.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.augustro.filemanager.R;
import com.augustro.filemanager.ui.views.RoundedImageView;
import com.augustro.filemanager.ui.views.ThemedTextView;


public class ItemViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public final RoundedImageView pictureIcon;
    public final ImageView genericIcon, apkIcon;
    public final ImageView imageView1;
    public final ThemedTextView txtTitle;
    public final TextView txtDesc;
    public final TextView date;
    public final TextView perm;
    public final View rl;
    public final TextView genericText;
    public final ImageButton about;
    public final ImageView checkImageView;
    public final ImageView checkImageViewGrid;
    public final RelativeLayout iconLayout;

    public ItemViewHolder(View view) {
        super(view);

        txtTitle = view.findViewById(R.id.firstline);
        pictureIcon = view.findViewById(R.id.picture_icon);
        rl = view.findViewById(R.id.second);
        perm = view.findViewById(R.id.permis);
        date = view.findViewById(R.id.date);
        txtDesc = view.findViewById(R.id.secondLine);
        apkIcon = view.findViewById(R.id.apk_icon);
        genericText = view.findViewById(R.id.generictext);
        imageView1 = view.findViewById(R.id.icon_thumb);
        about = view.findViewById(R.id.properties);
        checkImageView = view.findViewById(R.id.check_icon);
        genericIcon = view.findViewById(R.id.generic_icon);
        checkImageViewGrid = view.findViewById(R.id.check_icon_grid);
        iconLayout = view.findViewById(R.id.icon_frame_grid);
    }

}
