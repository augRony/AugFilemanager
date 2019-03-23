package com.augustro.filemanager.asynchronous.asynctasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.widget.EditText;
import android.widget.ImageButton;

import com.augustro.filemanager.activities.TextEditorActivity;
import com.augustro.filemanager.utils.ImmutableEntry;
import com.augustro.filemanager.utils.MapEntry;
import com.augustro.filemanager.utils.theme.AppTheme;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Vishal on 2/1/16.
 */
public class SearchTextTask extends AsyncTask<Editable, Void, ArrayList<MapEntry>> {

    private EditText searchEditText, mInput;
    private ArrayList<MapEntry> nodes;
    private int searchTextLength;
    private ImageButton upButton, downButton;
    private TextEditorActivity textEditorActivity;
    private Editable editText;
    private String searchSubString;
    private StringReader stringReader;
    private LineNumberReader lineNumberReader;

    public SearchTextTask(TextEditorActivity textEditorActivity) {
        this.textEditorActivity = textEditorActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        this.searchEditText = textEditorActivity.searchEditText;
        this.nodes = textEditorActivity.nodes;
        this.upButton = textEditorActivity.upButton;
        this.downButton = textEditorActivity.downButton;
        this.mInput = textEditorActivity.mInput;
        searchTextLength = searchEditText.length();
        editText = mInput.getText();
        stringReader = new StringReader(editText.toString());
        lineNumberReader = new LineNumberReader(stringReader);
    }

    @Override
    protected ArrayList<MapEntry> doInBackground(Editable... params) {
        for (int i = 0; i < (editText.length() - params[0].length()); i++) {
            if (searchTextLength == 0 || isCancelled())
                break;

            searchSubString = editText.subSequence(i, i + params[0].length()).toString();

            // comparing and adding searched phrase to a list
            if (searchSubString.equalsIgnoreCase(params[0].toString())) {

                nodes.add(new MapEntry(new ImmutableEntry<>(i, i + params[0].length()),
                        lineNumberReader.getLineNumber()));
            }

            // comparing and incrementing line number
            // ended up using LineNumberReader api instead
            try {
                lineNumberReader.skip(params[0].length());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return nodes;
    }

    @Override
    protected void onPostExecute(final ArrayList<MapEntry> mapEntries) {
        super.onPostExecute(mapEntries);

        for (Map.Entry mapEntry : mapEntries) {

            Map.Entry keyMapEntry = (Map.Entry) mapEntry.getKey();
            if (textEditorActivity.getAppTheme().equals(AppTheme.LIGHT)) {
                mInput.getText().setSpan(new BackgroundColorSpan(Color.YELLOW),
                        (Integer) keyMapEntry.getKey(), (Integer) keyMapEntry.getValue(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            } else {
                mInput.getText().setSpan(new BackgroundColorSpan(Color.LTGRAY),
                        (Integer) keyMapEntry.getKey(), (Integer) keyMapEntry.getValue(),
                        Spanned.SPAN_INCLUSIVE_INCLUSIVE);
            }
        }

        if (mapEntries.size()!=0) {
            upButton.setEnabled(true);
            downButton.setEnabled(true);

            // downButton
            textEditorActivity.onClick(downButton);
        } else {
            upButton.setEnabled(false);
            downButton.setEnabled(false);
        }
    }

}
