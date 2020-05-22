package com.example.csdfcommunityapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
public class UploadFragment extends Fragment {
    /*
    The plan is to change the layout in the upload fragment.
    We can't start playing every video the user selects.
    We also need to make like a list view which lists out the video (even multiple videos at a single go)
    There's also a bug that makes the progress bar goes haywire.....that needs to be sorted
     */
    private static final int PICK_VIDEO_REQUEST = 2;
    private Button mButtonChooseVideo;
    private Button mVideoUpload;
    private VideoView mVideoView;
    private Uri mVideoURI;
    private ProgressBar mProgressBar;
    private StorageReference mStorageReference;
    private DatabaseReference mDataBaseReference;
    private StorageTask mUploadTask;
    FirebaseAuth mFirebaseAuth2;

    // Required empty public constructor
    public UploadFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        mButtonChooseVideo = view.findViewById(R.id.button);
        mVideoUpload = view.findViewById(R.id.buttonLogIn);
        mVideoView = view.findViewById(R.id.videoView);
        mProgressBar = view.findViewById(R.id.progressBar);
        mFirebaseAuth2 = FirebaseAuth.getInstance();
        //Log.i("User MAil: ",mFirebaseAuth2.getCurrentUser().getUid());

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
                uploadFile();
            }
        });

        return view;
    }

    private void uploadFile() {
        if (mVideoURI != null) {
            StorageReference fileReference = mStorageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mVideoURI));

            mUploadTask = fileReference.putFile(mVideoURI)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //The progress bar is literally for show. This needs to be sorted out
                                    mProgressBar.setProgress(0);
                                }
                            }, 300);

                            Toast.makeText(getActivity(), "Upload successful", Toast.LENGTH_LONG).show();
                            Upload upload = new Upload("",
                                    Objects.requireNonNull(taskSnapshot.getMetadata()).toString());
                            String uploadId = mDataBaseReference.push().getKey();
                            assert uploadId != null;
                            mDataBaseReference.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }
    }

    // This function just gets the file extension. The code didn't seem to work without this
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getActivity().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }


    // The intent Type is specifically set to mp4 to only allow mp4 video types, might wanna change later
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("video/mp4");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
        mVideoView.start();
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
            mVideoView.setVideoURI(mVideoURI);
        }
    }

}
