package edu.njust.hshw;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import edu.njust.hshw.undolib.UndoEditText;

import static android.R.id.undo;

public class MainActivity extends AppCompatActivity {

    private ImageButton undoBtn;
    private ImageButton redoBtn;
    private UndoEditText undoEdit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get views
        undoBtn = (ImageButton) findViewById(R.id.imgbtn_undo);
        redoBtn = (ImageButton) findViewById(R.id.imgbtn_redo);
        undoEdit = (UndoEditText) findViewById(R.id.uet);

        //set click listener
        undoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoEdit.undo();
            }
        });
        redoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                undoEdit.redo();
            }
        });
        undoBtn.setClickable(false);
        redoBtn.setClickable(false);

        //set UndoEditText's listener
        undoEdit.setUndoListener(new UndoEditText.UndoListener() {
            @Override
            public void undoStateChange(boolean canUndo) {
                if (canUndo) {
                    undoBtn.setClickable(true);
                    undoBtn.setImageResource(R.drawable.ic_undo_able);
                }else {
                    undoBtn.setClickable(false);
                    undoBtn.setImageResource(R.drawable.ic_undo_disable);
                }
            }

            @Override
            public void redoStateChange(boolean canRedo) {
                if (canRedo) {
                    redoBtn.setClickable(true);
                    redoBtn.setImageResource(R.drawable.ic_redo_able);
                }else {
                    redoBtn.setClickable(false);
                    redoBtn.setImageResource(R.drawable.ic_redo_disable);
                }
            }
        });
    }
}
