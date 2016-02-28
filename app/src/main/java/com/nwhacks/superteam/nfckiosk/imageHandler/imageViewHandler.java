package com.nwhacks.superteam.nfckiosk.imageHandler;




import android.app.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

import android.media.Image;

import android.net.Uri;


import com.nwhacks.superteam.nfckiosk.R;
import java.io.File;

/**
 * Created by jeffreydoyle on 2016-02-27.
 */
public class imageViewHandler extends Activity{

    private ImageView imageView;

    private Image currentImage;


    @Override
    protected void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);


    }
    /*

    Sets the image from the image in memory.
        -Papa Mario (Jeff)
     */

    public void setImage(){

        Uri uri = Uri.fromFile(new File("data/data/NFCKioskAndroidApp/app_data/imageDir/profile.png"));
        imageView.setImageURI(uri);

    }

    /*

    Removes the image from the imageView
        ***DOESN'T DELETE THE IMAGE***
        -The Big Cheese (Jeff)
     */

    public void removeImage(){

        imageView.setImageDrawable(null);

    }




}
