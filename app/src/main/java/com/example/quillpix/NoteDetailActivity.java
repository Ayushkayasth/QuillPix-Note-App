package com.example.quillpix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import org.checkerframework.checker.units.qual.N;

public class NoteDetailActivity extends AppCompatActivity {

    EditText titleEdit, contentEdit;
    ImageButton saveNoteBtn;
    TextView pageTitleTv;
    String Title,Content,docId;
    boolean isEdit = false;
    TextView deleteNoteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_detail);

        titleEdit = findViewById(R.id.note_title);
        contentEdit = findViewById(R.id.note_content);
        saveNoteBtn = findViewById(R.id.save_note_btn);
        pageTitleTv = findViewById(R.id.page_title);
        deleteNoteTv = findViewById(R.id.delete_note);

        //Receiving data
        Title = getIntent().getStringExtra("Title");
        Content = getIntent().getStringExtra("Content");
        docId = getIntent().getStringExtra("docId");

        if(docId!=null && !docId.isEmpty()){
            isEdit = true;
        }

        titleEdit.setText(Title);
        contentEdit.setText(Content);
        if(isEdit){
            pageTitleTv.setText("Edit your note");
            deleteNoteTv.setVisibility(View.VISIBLE);
        }

        saveNoteBtn.setOnClickListener((v)->saveNote());
        deleteNoteTv.setOnClickListener((v)->deleteNoteFromFirebase());
    }

    void saveNote(){

        String noteTitle = titleEdit.getText().toString();
        String noteContent = contentEdit.getText().toString();
        if(noteTitle==null || noteTitle.isEmpty()){
            titleEdit.setError("Title is required");
            return;
        }
        Note note = new Note();
        note.setTitle(noteTitle);
        note.setContent(noteContent);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);

    }

    void saveNoteToFirebase(Note note){
        DocumentReference documentReference;
        if(isEdit){
            //update note
            documentReference = Utility.getCollectionReferenceNotes().document(docId);
        }
        else {
            //create new note
            documentReference = Utility.getCollectionReferenceNotes().document();
        }

        documentReference.set(note).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   Utility.showToast(NoteDetailActivity.this,"Note added successfully");
                   finish();
               }
               else{
                   Utility.showToast(NoteDetailActivity.this,"Failed while adding note");
               }
            }
        });
    }

    void deleteNoteFromFirebase(){
        DocumentReference documentReference;
           documentReference = Utility.getCollectionReferenceNotes().document(docId);


        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Utility.showToast(NoteDetailActivity.this,"Note deleted successfully");
                    finish();
                }
                else{
                    Utility.showToast(NoteDetailActivity.this,"Failed while deleting note");
                }
            }
        });

    }
}