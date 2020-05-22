package com.example.csdfcommunityapp;
/*
To Upload into firebase, this class needs to be created.
 */

public class Upload {
    private String mName;
    private String mVideoUrl;

    public Upload() {
        //empty constructor needed
    }
    public Upload(String name, String videoUrl) {
        if (name.trim().equals("")) {
            name = "No Name";
        }

        mName = name;
        mVideoUrl = videoUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getVideoUrl() {
        return mVideoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        mVideoUrl = videoUrl;
    }
}
