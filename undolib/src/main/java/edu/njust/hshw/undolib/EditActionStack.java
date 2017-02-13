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

import java.util.LinkedList;

/**
 * 构造具有观察者模式的栈容器
 */
public class EditActionStack implements Parcelable {
    private LinkedList<EditAction> list;
    private InnerObservable observable;
    private int maxCapacity = Integer.MAX_VALUE;

    public EditActionStack(InnerObservable observable) {
        this.list = new LinkedList<>();
        this.observable = observable;
    }

    public void setObservable(InnerObservable observable) {
        this.observable = observable;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public void push(EditAction entity) {
        int beforeSize = list.size();

        if (list.size() >= maxCapacity) {
            list.removeLast();
        }
        list.push(entity);

        int afterSize = list.size();
        //broadcast
        if (observable != null) {
            observable.change(beforeSize, afterSize);
        }
    }

    public EditAction poll() {
        int beforeSize = list.size();

        EditAction result = list.poll();

        int afterSize = list.size();

        //broadcast
        if (observable != null) {
            observable.change(beforeSize, afterSize);
        }

        return result;
    }

    public void copyDataFrom(EditActionStack undoStack) {
        maxCapacity = undoStack.maxCapacity;

        int beforeSize = list.size();
        list.clear();
        list.addAll(undoStack.list);
        int afterSize = list.size();
        //broadcast
        if (observable != null) {
            observable.change(beforeSize, afterSize);
        }

    }

    public void clear() {
        int beforeSize = list.size();
        list.clear();
        int afterSize = list.size();

        //broadcast
        if (observable != null) {
            observable.change(beforeSize, afterSize);
        }
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.list);
        dest.writeInt(this.maxCapacity);
    }

    protected EditActionStack(Parcel in) {
        this.list = new LinkedList<>();
        in.readList(this.list, EditAction.class.getClassLoader());
        this.maxCapacity = in.readInt();
    }

    public static final Creator<EditActionStack> CREATOR = new Creator<EditActionStack>() {
        @Override
        public EditActionStack createFromParcel(Parcel source) {
            return new EditActionStack(source);
        }

        @Override
        public EditActionStack[] newArray(int size) {
            return new EditActionStack[size];
        }
    };



    interface InnerObservable {
        void change(int beforeSize, int afterSize);
    }
}
