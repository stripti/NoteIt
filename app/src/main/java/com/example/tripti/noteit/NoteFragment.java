package com.example.tripti.noteit;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends Fragment implements Button.OnClickListener{

    View mRootView;
    EditText mSubjectEditText, mBodyEditText;
    Button mSaveButton;



    public NoteFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mRootView = inflater.inflate(R.layout.fragment_note, container, false);
        mSubjectEditText = (EditText) mRootView.findViewById(R.id.edit_text_subject);
        mBodyEditText = (EditText) mRootView.findViewById(R.id.edit_text_body);
        mSaveButton = (Button) mRootView.findViewById(R.id.button_save);
        mSaveButton.setOnClickListener(this);
        return mRootView;
    }



    private void createPdf() throws FileNotFoundException
    {
        File pdfFolder = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "pdfdemo");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdir();
        }

        //Creating time stamp
        Date date = new Date() ;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);

        File myFile = new File(pdfFolder + timeStamp + ".pdf");
        OutputStream output = new FileOutputStream(myFile);

        //Step 1
        Document document = new Document();

        //Step 2
        try {
            PdfWriter.getInstance(document, output);

            //Step 3
            document.open();

            //Step 4 Add content
            document.add(new Paragraph(mSubjectEditText.getText().toString()));
            document.add(new Paragraph(mBodyEditText.getText().toString()));

            //Step 5: Close the document
            document.close();
        }catch (DocumentException e)
        {
            e.printStackTrace();
        }

    }


    private void viewPdf(){
        File file = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sample.pdf");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/pdf");
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(intent);
    }

    private void emailNote()
    {
        File file = new File( Environment.getExternalStorageDirectory().getAbsolutePath() + "/Sample.pdf");
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_SUBJECT,mSubjectEditText.getText().toString());
        email.putExtra(Intent.EXTRA_TEXT, mBodyEditText.getText().toString());
        Uri uri = Uri.parse(file.getAbsolutePath());
        email.putExtra(Intent.EXTRA_STREAM, uri);
        email.setType("message/rfc822");
        startActivity(email);
    }

    private void promptForNextAction()
    {
        final String[] options = { "E-mail", "Preview","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Note Saved, What Next?");
        builder.setItems(options, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (options[which].equals("E-mail")){
                    emailNote();
                }
                else if (options[which].equals("Preview")){
                    viewPdf();
                }else if (options[which].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }

    @Override
    public void onClick(View v) {
        if (mSubjectEditText.getText().toString().isEmpty()){
            mSubjectEditText.setError("Subject is empty");
            mSubjectEditText.requestFocus();
            return;
        }

        if (mBodyEditText.getText().toString().isEmpty()){
            mBodyEditText.setError("Body is empty");
            mBodyEditText.requestFocus();
            return;
        }
        else{
            promptForNextAction();
        }
        try {
            createPdf();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
