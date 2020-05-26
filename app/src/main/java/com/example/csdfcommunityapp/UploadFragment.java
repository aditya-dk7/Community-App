package com.example.csdfcommunityapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
public class UploadFragment extends Fragment {
    /*
    The plan is to change the layout in the upload fragment.
    We can't start playing every video the user selects.
    There's also a bug that makes the progress bar goes haywire.....that needs to be sorted

    Edit:
    The progressbar stuff was actually behaving weird due to that video view.....Therefore That is
    sorted out
    I actually fixed the video player bug...no issues now.
    I'm restricting single video upload
    this fragment seems like a green signal from me...no UI for now
     */
    private static final int PICK_VIDEO_REQUEST = 2;
    private Button mButtonChooseVideo,mPlayPause;
    private Button mVideoUpload;
    private VideoView videoView;
    private ListView listView;
    private Uri mVideoURI;
    private ProgressBar mProgressBar;
    private StorageReference mStorageReference;
    private DatabaseReference mDataBaseReference;
    private StorageTask mUploadTask;
    private TextView textView,textView3;
    private String mfileName;
    private boolean playPAUSE = false;

    FirebaseAuth mFirebaseAuth2;
    List <Upload> mUploads;

    // Required empty public constructor
    public UploadFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);
        videoView = view.findViewById(R.id.videoView);
        textView3=view.findViewById(R.id.textView3);
        mButtonChooseVideo = view.findViewById(R.id.button);
        mVideoUpload = view.findViewById(R.id.buttonLogIn);
        mProgressBar = view.findViewById(R.id.progressBar);
        mPlayPause = view.findViewById(R.id.button2);
        mFirebaseAuth2 = FirebaseAuth.getInstance();
        mUploads = new ArrayList<>();
        mfileName = null;
        textView = view.findViewById(R.id.textView);

        // This is top make separate directories for every user to upload video
        String path = "uploads_"+mFirebaseAuth2.getCurrentUser().getUid();

        mStorageReference = FirebaseStorage.getInstance().getReference(path);

        mDataBaseReference = FirebaseDatabase.getInstance().getReference(path);

        // This button onclick listener helps call the file chooser function to select a video file
        mButtonChooseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        // This button onclick listener helps call the upload function below
        mVideoUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoURI!=null) {
                    videoView.stopPlayback();
                    mPlayPause.setText("Play");
                    playPAUSE=false;
                    videoView.setVisibility(View.INVISIBLE);
                    uploadFile();

                }
                else {
                    Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mVideoURI!=null){
                    if(!playPAUSE){
                        videoView.start();
                        mPlayPause.setText("Pause");
                        playPAUSE = true;
                        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mPlayPause.setText("Play");
                                playPAUSE=false;
                            }
                        });

                    }else {
                        videoView.pause();
                        mPlayPause.setText("Play");
                        playPAUSE=false;

                    }


                }
                else{
                    Toast.makeText(getActivity(),"Choose a file",Toast.LENGTH_SHORT).show();
                }
            }
        });



        return view;
    }

    private void uploadFile(){
        mDataBaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int check = 0;
                            for (DataSnapshot letsgetsnapshot : dataSnapshot.getChildren()) {
                                Upload upload = letsgetsnapshot.getValue(com.example.csdfcommunityapp.Upload.class);
                                mUploads.add(upload);
                            }
                            String[] uploadName = new String[mUploads.size()];
                            for (int j = 0; j < uploadName.length; j++) {
                                uploadName[j] = mUploads.get(j).getName();
                                if (mfileName.equals(uploadName[j])) {
                                    check = 1;
                                    break;
                                }
                            }

                            if (mVideoURI != null) {
                                if (check == 1) {
                                    AlertDialog.Builder altdial = new AlertDialog.Builder
                                            (Objects.requireNonNull(getActivity()));
                                    altdial.setMessage("You have already uploaded the selected video file(s). " +
                                            "Do you wish to upload it again? \n(Warning: It will be overwritten " +
                                            "with the previous file)").setCancelable(false)
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();

                                            final StorageReference fileReference = mStorageReference.child(mfileName);
                                            mUploadTask = fileReference.putFile(mVideoURI)
                                                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                            Handler handler = new Handler();
                                                            handler.postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    mVideoUpload.setEnabled(true);
                                                                    mButtonChooseVideo.setEnabled(true);
                                                                    mPlayPause.setEnabled(true);
                                                                    textView3.setText("File Name: No file Chosen");
                                                                    mProgressBar.setProgress(0);
                                                                }
                                                            }, 300);

                                                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                                                            Upload upload = new Upload(mfileName,
                                                                    Objects.requireNonNull(taskSnapshot.getMetadata()).toString());
                                                            String uploadId = mDataBaseReference.push().getKey();
                                                            mDataBaseReference.child(uploadId).setValue(upload);

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            mVideoUpload.setEnabled(true);
                                                            mButtonChooseVideo.setEnabled(true);
                                                            textView3.setText("File Name: No file Chosen");
                                                            mPlayPause.setEnabled(true);
                                                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                        @Override
                                                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                                            mVideoUpload.setEnabled(false);
                                                            mButtonChooseVideo.setEnabled(false);
                                                            mPlayPause.setEnabled(false);
                                                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                            mProgressBar.setProgress((int) progress);
                                                        }
                                                    });

                                            mProgressBar.setProgress(0);
                                            mVideoURI = null;
                                            textView3.setText("Uploading: "+mfileName);
                                        }
                                    })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    mPlayPause.setText("Play");
                                                    playPAUSE=false;
                                                    videoView.setVisibility(View.VISIBLE);
                                                    dialog.cancel();
                                                }
                                            });
                                    AlertDialog alertDialog = altdial.create();
                                    alertDialog.setTitle("Upload Warning");
                                    alertDialog.show();

                                } else {

                                    final StorageReference fileReference = mStorageReference.child(mfileName);
                                    mUploadTask = fileReference.putFile(mVideoURI)
                                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                    Handler handler = new Handler();
                                                    handler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            mVideoUpload.setEnabled(true);
                                                            mButtonChooseVideo.setEnabled(true);
                                                            mPlayPause.setEnabled(true);
                                                            textView3.setText("File Name: No file Chosen");
                                                            mProgressBar.setProgress(0);
                                                        }
                                                    }, 300);

                                                    Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                                                    Upload upload = new Upload(mfileName,
                                                            Objects.requireNonNull(taskSnapshot.getMetadata()).toString());
                                                    String uploadId = mDataBaseReference.push().getKey();
                                                    mDataBaseReference.child(uploadId).setValue(upload);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    mVideoUpload.setEnabled(true);
                                                    mButtonChooseVideo.setEnabled(true);
                                                    textView3.setText("File Name: No file Chosen");
                                                    mPlayPause.setEnabled(true);
                                                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                                                    mVideoUpload.setEnabled(false);
                                                    mButtonChooseVideo.setEnabled(false);
                                                    mPlayPause.setEnabled(false);
                                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                                    mProgressBar.setProgress((int) progress);
                                                }
                                            });

                                    mProgressBar.setProgress(0);
                                    mVideoURI = null;
                                    textView3.setText("Uploading: "+mfileName);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.i("failed to read value ", databaseError.toException().toString());
                    }
                });

    }

    // The intent Type is specifically set to mp4 to only allow mp4 video types, might wanna change later
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/mp4");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);


    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    /*
     This function is invoked after the user clicks the choose button to verify that the user
          has absolutely given the permission
     */
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_VIDEO_REQUEST && resultCode==getActivity().RESULT_OK
                && data!=null && data.getData()!=null) {
            mVideoURI = data.getData();
            mfileName = getFileName(mVideoURI);
            String temp = "File Name: "+mfileName;
            textView3.setText(temp);
            videoView.setVideoURI(mVideoURI);
            videoView.setVisibility(View.VISIBLE);



        }
    }

}
