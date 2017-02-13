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

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * 具有撤销功能的EditText。并且可以自动保存编辑记录，不会因为横竖屏的原因导致撤销的记录丢失，前提是你给
 * UndoEditText设置了android:id属性
 */
public class UndoEditText extends EditText {

    /* 用户的每次手动编辑操作记录在其中 */
    private EditActionStack undoStack;
    /* 撤销的动作放在这里，用于恢复撤销操作 */
    private EditActionStack redoStack;
    /* 是否是用户在编辑，如果是操作undo和redo操作，isPersonEdit= false */
    private boolean isPersonEdit = true;
    /* 撤销状态变化的监听器，当从可撤销（可恢复）状态变化到不可撤销（不可恢复）状态时（反之也成立），监听器会被调用。
        当然，前提是你设置了该监听器
     */
    private UndoListener mUndoListener = null;

    public UndoEditText(Context context) {
        super(context);
        initUndoAbility();
    }

    public UndoEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initUndoAbility();
    }

    public UndoEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUndoAbility();
    }

    /**
     * 初始化控件的撤销功能
     */
    private void initUndoAbility() {
        setSelection(length());
        undoStack = new EditActionStack(new EditActionStack.InnerObservable() {
            @Override
            public void change(int beforeSize, int afterSize) {
                if (mUndoListener != null) {
                    if (beforeSize == 0 && afterSize > 0) {
                        mUndoListener.undoStateChange(true);
                    } else if (beforeSize > 0 && afterSize == 0) {
                        mUndoListener.undoStateChange(false);
                    }
                }
            }
        });
        redoStack = new EditActionStack(new EditActionStack.InnerObservable() {
            @Override
            public void change(int beforeSize, int afterSize) {
                if (mUndoListener != null) {
                    if (beforeSize == 0 && afterSize > 0) {
                        mUndoListener.redoStateChange(true);
                    } else if (beforeSize > 0 && afterSize == 0) {
                        mUndoListener.redoStateChange(false);
                    }
                }
            }
        });

        addTextChangedListener(new TextWatcher() {

            private String beforeContent = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (isPersonEdit) {
                    beforeContent = s.subSequence(start, start + count).toString();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (isPersonEdit) {
                    String afterContent = s.subSequence(start, start + count).toString();
                    EditAction action = new EditAction(start, beforeContent, afterContent);

                    undoStack.push(action);

                    if (!redoStack.isEmpty()) {
                        redoStack.clear();
                    }
                } else {
                    isPersonEdit = true;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState mSavedState = new SavedState(superState, undoStack, redoStack, isPersonEdit);
        return mSavedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState mSavedState = (SavedState) state;
        super.onRestoreInstanceState(mSavedState.getSuperState());

        undoStack.copyDataFrom(mSavedState.undoStack);
        redoStack.copyDataFrom(mSavedState.redoStack);
        isPersonEdit = mSavedState.isPersonEdit;
    }

    /**
     * 在控件的内容上，执行EditAction动作
     * @param action
     */
    private void applyEditAction(EditAction action) {
        if (action != null) {
            StringBuilder builder = new StringBuilder(getEditableText().toString());
            builder.replace(action.startIndex, action.startIndex + action.beforeContent.length(), action.afterContent);
            setText(builder);
            setSelection(action.startIndex + action.afterContent.length());
        }
    }

    /**
     * 在控件的内容上，撤销EditAction动作
     * @param action
     */
    private void revertEditAction(EditAction action) {
        EditAction revertAction = new EditAction(action.startIndex, action.afterContent, action.beforeContent);
        applyEditAction(revertAction);
    }

    /**
     * 最多保存的历史编辑记录条数，默认为{@link Integer#MAX_VALUE}
     *
     * @param max_history_num
     */
    public void setMaxHistory(int max_history_num) {
        this.undoStack.setMaxCapacity(max_history_num);
    }

    /**
     * 是否可以撤销
     *
     * @return
     */
    public boolean canUndo() {
        return !undoStack.isEmpty();
    }

    /**
     * 是否可以恢复撤销
     *
     * @return
     */
    public boolean canRedo() {
        return !redoStack.isEmpty();
    }

    /**
     * 撤销前一次编辑
     */
    public void undo() {
        if (canUndo()) {
            isPersonEdit = false;

            EditAction action = undoStack.poll();
            revertEditAction(action);

            redoStack.push(action);
        }

    }

    /**
     * 取消撤销
     */
    public void redo() {
        if (canRedo()) {
            isPersonEdit = false;

            EditAction action = redoStack.poll();
            applyEditAction(action);

            undoStack.push(action);
        }
    }

    /**
     * 设置撤销状态的监听器
     *
     * @param mListener
     */
    public void setUndoListener(UndoListener mListener) {
        this.mUndoListener = mListener;
    }

    /**
     * view状态存储类，用于在视图被销毁时保存自身状态，重新创建时恢复view状态
     */
    protected static class SavedState extends View.BaseSavedState {

        EditActionStack undoStack;
        EditActionStack redoStack;
        boolean isPersonEdit;

        public SavedState(Parcelable superState, EditActionStack undoStack, EditActionStack redoStack, boolean isPersonEdit) {
            super(superState);
            this.undoStack = undoStack;
            this.redoStack = redoStack;
            this.isPersonEdit = isPersonEdit;
        }

        private SavedState(Parcel in) {
            super(in);
            undoStack = in.readParcelable(EditActionStack.class.getClassLoader());
            redoStack = in.readParcelable(EditActionStack.class.getClassLoader());

            boolean[] temp = new boolean[1];
            in.readBooleanArray(temp);
            isPersonEdit = temp[0];
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeParcelable(undoStack, flags);
            out.writeParcelable(redoStack, flags);
            out.writeBooleanArray(new boolean[]{isPersonEdit});
        }

        @Override
        public int describeContents() {
            return super.describeContents();
        }

        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };

    }

    /**
     * 当撤销或恢复撤销的状态发生的变化的回调函数
     */
    public interface UndoListener {
        /**
         * 当撤销状态从一种状态变化为另一种状态的时候，会被调用
         * 注意：当UndoEditText被销毁后被系统重建的时候(例如横竖屏切换)，该函数也有可能被调用，以帮助可能存在的
         * undo按钮设置正确的状态
         * @param canUndo
         */
        void undoStateChange(boolean canUndo);

        /**
         * 当是否可以恢复撤销状态从一种状态变化为另一种状态的时候，会被调用
         * 注意：当UndoEditText被销毁后被系统重建的时候(例如横竖屏切换)，该函数也有可能被调用，以帮助可能存在的
         * redo按钮设置正确的状态
         * @param canRedo
         */
        void redoStateChange(boolean canRedo);
    }
}
