/*
 * Copyright (C) 2018 Emmanuel Messulam <emmanuelbendavid@gmail.com>
 * Copyright (C) 2014 Vishal Nehra <vishalmeham2@gmail.com>
 *
 * This file is part of Amaze File Manager.
 *
 * Amaze File Manager is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.augustro.filemanager.database.models;

import android.content.SharedPreferences;

import com.augustro.filemanager.utils.files.FileUtils;

/**
 * Created by Vishal on 9/17/2014
 */
public class Tab {
    public final int tabNumber;
    public final String path;
    public final String home;

    public Tab(int tabNo, String path, String home) {
        this.tabNumber = tabNo;
        this.path = path;
        this.home = home;
    }

    public String getOriginalPath(boolean savePaths, SharedPreferences sharedPreferences){
        if(savePaths && FileUtils.isPathAccessible(path, sharedPreferences)) {
            return path;
        } else {
            return home;
        }
    }

    @Override
    public String toString() {
        return "Tab{" +
                "tabNumber=" + tabNumber +
                ", path='" + path + '\'' +
                ", home='" + home + '\'' +
                '}';
    }
}
