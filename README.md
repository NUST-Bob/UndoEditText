# UndoEditText
一个内容可撤销的EditText控件，并且在横竖屏切换时可以保存、恢复记录。
#### 重复造这轮子的原因
网上相同的轮子已经有很多了，但是作为自定义view，他们对view的实现大都
不完整。这些控件在遇到横竖屏类似情形的时候，不能够保存他们应有的状态
，也就是没有实现自定义控件自动保存状态和恢复状态的机制。相关的状态自
动保存机制，请参考博客：http://www.codeceo.com/article/android-save-view-state.html
#### UndoEditText的使用
- 添加undolib库，并在需要使用的工程的build.gradle的dependencies中加入依赖
```
dependencies {
    ...
    compile project(':undolib')
}
```
- 在需要用到撤销EditText的地方，将EditText替换为UndoEditText。

```
<edu.njust.hshw.undolib.UndoEditText
        android:id="@+id/uet"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_below="@id/imgbtn_undo"
        android:layout_marginTop="30dp"
        android:paddingLeft="20dp"
        android:paddingRight="20dp"
        android:text="default value " />
```

```
UndoEditText undoEdit = (UndoEditText) findViewById(R.id.uet);
```

- 撤销和恢复撤销等操作：

```
undoEdit.undo();    //撤销修改
undoEdit.redo();    //恢复撤销
//设置最多可以撤销的步骤，默认为Integer.MAX_VALUE
undoEdit.setMaxHistory(200);
undoEdit.canUndo(); //控件是否还可以撤销
undoEdit.canRedo(); //控件是否还可以恢复（反撤销）
```
- 撤销状态的回调接口

```
//设置UndoEditText的回调接口
undoEdit.setUndoListener(new UndoEditText.UndoListener() {
    @Override
    public void undoStateChange(boolean canUndo) {
        //当控件状态从不能撤销，到可以撤销的状态时，该接口被调用canUndo=ture；
        //相反，从可以撤销到不能撤销时，该接口被调用canUndo=false
    }

    @Override
    public void redoStateChange(boolean canRedo) {
        //当控件状态从不能恢复撤销，到可以恢复撤销的状态时，
        //该接口被调用canRedo=ture；
        //相反，该接口被调用canRedo=false
    }
});
```
- 演示图

![image](https://github.com/NUST-Bob/UndoEditText/blob/master/image/UndoEditText.gif)