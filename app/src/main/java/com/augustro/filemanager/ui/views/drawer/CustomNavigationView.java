package com.augustro.filemanager.ui.views.drawer;

import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.util.AttributeSet;
import android.view.MenuItem;

/**
 * This class if for intercepting item selections so that they can be saved and restored.
 */
public class CustomNavigationView extends NavigationView
        implements NavigationView.OnNavigationItemSelectedListener {

    private OnNavigationItemSelectedListener subclassListener;
    private int checkedId = -1;

    public CustomNavigationView(Context context, AttributeSet attrs) {
        super(context, attrs);

        super.setNavigationItemSelectedListener(this);
    }

    @Override
    public void setNavigationItemSelectedListener(@Nullable OnNavigationItemSelectedListener listener) {
        subclassListener = listener;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(subclassListener != null) {
            boolean shouldBeSelected = subclassListener.onNavigationItemSelected(item);

            if(shouldBeSelected) {
                onItemChecked(item);
            }

            return shouldBeSelected;
        } else {
            onItemChecked(item);
            return true;
        }
    }

    private void onItemChecked(MenuItem item) {
        checkedId = item.getItemId();
    }

    public void setCheckedItem(MenuItem item) {
        this.checkedId = item.getItemId();
        item.setChecked(true);
    }

    public void deselectItems() {
        checkedId = -1;
    }

    public @Nullable MenuItem getSelected() {
        if(checkedId == -1) return null;
        return getMenu().findItem(checkedId);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        if (!doesNavigationViewSavedStateExist()) {
            return super.onSaveInstanceState();
        }

        //begin boilerplate code that allows parent classes to save state
        Parcelable superState = super.onSaveInstanceState();

        SavedState ss = new SavedState(superState);
        //end

        ss.selectedId = this.checkedId;

        return ss;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if(!doesNavigationViewSavedStateExist()) {
            super.onRestoreInstanceState(state);
            return;
        }

        //begin boilerplate code so parent classes can restore state
        if(!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState)state;
        super.onRestoreInstanceState(ss.getSuperState());
        //end

        this.checkedId = ss.selectedId;
    }

    /**
     * This is a hack, when the SavedState class is unmarshalled a "ClassNotFoundException"
     * will be thrown (the actual class not found is "android.support.design.widget.NavigationView$SavedState")
     * and I seem to only be able to replicate on Marshmallow.
     * Trying to find the class and returning false if Class.forName() throws "ClassNotFoundException"
     * doesn't work because the class seems to have been loaded with the current loader
     * (not the one the unmarshaller uses); of course I have no idea of what any of this means so
     * I could be wrong.
     * For the crash see https://github.com/TeamAmaze/AmazeFileManager/issues/1101.
     */
    public boolean doesNavigationViewSavedStateExist() {
        return Build.VERSION.SDK_INT != Build.VERSION_CODES.M;
    }

    static class SavedState extends BaseSavedState {
        Parcelable superState;
        int selectedId;

        SavedState(Parcelable superState) {
            super(EMPTY_STATE);
            this.superState = superState;
        }

        private SavedState(Parcel in) {
            super(in);
            this.superState = in.readParcelable(NavigationView.SavedState.class.getClassLoader());
            this.selectedId = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(superState, flags);
            out.writeInt(this.selectedId);
        }

        //required field that makes Parcelables from a Parcel
        public static final Creator<SavedState> CREATOR =
                new Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }
                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
