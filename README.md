# UndoEditText
一个内容可撤销的EditText控件，并且在横竖屏切换时可以保存、恢复记录。
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
undoEdit.setMaxHistory(200);  //设置最多可以撤销的步骤，默认为Integer.MAX_VALUE
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
