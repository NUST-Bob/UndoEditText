/*
 * Copyright 2016. njust_hshw<2431206120@qq.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.njust.hshw.undolib;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 编辑动作，记录每次编辑的位置和字符串。这里删除和插入动作都理解成替换操作。只是删除操作时，替换的字符串
 * ({@link #afterContent})为空字符串；插入操作时，被替换的字符串({@link #beforeContent})为空字符串
 */
public class EditAction implements Parcelable {
    /* 内容变化的开始位置 */
    int startIndex = 0;
    /* 从startIndex开始被替换的内容，当用户是插入动作的时候，这个内容为空 */
    String beforeContent;
    /* 从startIndex开始替换的新内容，当用户是删除动作内容的时候，这个内容为空 */
    String afterContent;

    public EditAction(int startIndex, String beforeContent, String afterContent) {
        this.startIndex = startIndex;
        this.beforeContent = beforeContent;
        this.afterContent = afterContent;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.startIndex);
        dest.writeString(this.beforeContent);
        dest.writeString(this.afterContent);
    }

    protected EditAction(Parcel in) {
        this.startIndex = in.readInt();
        this.beforeContent = in.readString();
        this.afterContent = in.readString();
    }

    public static final Creator<EditAction> CREATOR = new Creator<EditAction>() {
        @Override
        public EditAction createFromParcel(Parcel source) {
            return new EditAction(source);
        }

        @Override
        public EditAction[] newArray(int size) {
            return new EditAction[size];
        }
    };
}
