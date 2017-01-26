package ar.wallify.com.wallify_new_way;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JAYAN on 12/10/2016.
 */

public class SingleImageActivity extends AppCompatActivity
{
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int progress_bar_type = 0;


    FloatingActionButton fab_save,fab_download,setW,fab_favourite;
    Boolean setWallpaperSelected=false;
    Animation fabOpen,fabClose,fabRCw,fabRCCw;
    Boolean isOpen=false;
    Boolean flagIsCanccelled=false;
    final InterstitialAd mInterstitialAd = new InterstitialAd(this);
    String halfDUrl,halfTUrl;
    public Boolean isRated=false;




    String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_image);
        Bundle extras = getIntent().getExtras();


        if (extras != null)
        {
            filename=extras.getString("key");
            halfDUrl=extras.getString("dUrl")+extras.getString("key")+".jpg";
            halfTUrl=extras.getString("tUrl")+extras.getString("key")+".jpg";
            //value = "https://archive.org/download/ajulrajar_gmail_159/"+extras.getString("key")+".jpg";


            //The key argument here must match that used in the other activity
        }

        ///////////////////////////////////////FIREBASE//////////////////////////////////
        final DatabaseReference database= FirebaseDatabase.getInstance().getReference();
        //////////////////////////////////////////////////////////////////////////////////

        /////////////loading ads//////////////
        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3690357492073975~7952027242");
        AdView mAdView = (AdView) findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAd.setAdUnitId("ca-app-pub-3690357492073975/6227628444");


        mInterstitialAd.loadAd(adRequest);

        /*if(Integer.parseInt(extras.getString("key"))%3==0)
        mInterstitialAd.setAdListener(new AdListener(){
            public void onAdLoaded(){
                mInterstitialAd.show();
            }
        });**/
        //////////////////////////////////


        fab_download=(FloatingActionButton)findViewById(R.id.button1);
        fab_save=(FloatingActionButton)findViewById(R.id.button2);
         fab_favourite=(FloatingActionButton)findViewById(R.id.button3);
        setW=(FloatingActionButton)findViewById(R.id.button);
        fabOpen= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fabClose= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fabRCCw= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        fabRCw= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        final ImageView imageView=(ImageView)findViewById(R.id.imageView);

        // instantiate it within the onCreate method
        final ProgressDialog mProgressDialog = new ProgressDialog(SingleImageActivity.this,R.style.MyDialogTheme);
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                flagIsCanccelled=true;

            }
        });







        //String url =value;

        //url = url.replace("_thumbs", "_thumbs2");



        //USED fit() to load images efficiently
        //USED centerCrop() to maintain aspect ratio

       Picasso.with(this).load(halfTUrl).centerCrop().fit().error(R.drawable.bi).into(imageView, new Callback()
        {
            @Override
            public void onSuccess()
            {

            }

            @Override
            public void onError()
            {
                Toast.makeText(getApplicationContext(),"Couldn't load image!",Toast.LENGTH_SHORT).show();


            }
        });

        fab_favourite.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplicationContext(),"Rated!",Toast.LENGTH_SHORT).show();
                database.child("rates").addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {

                        if(!dataSnapshot.child(filename).exists())
                        {
                            ImageDetails imageDetails=new ImageDetails(filename,1);
                            database.child("rates").child(filename).setValue(imageDetails);
                        }
                        else
                        {
                            ImageDetails imageDetails = dataSnapshot.child(filename).getValue(ImageDetails.class);
                            imageDetails.setiRate(imageDetails.getiRate()+ 1);
                            database.child("rates").child(filename).setValue(imageDetails);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {
                        Toast.makeText(getApplicationContext(),"An error occurred!",Toast.LENGTH_SHORT).show();

                    }

                });

                if(!isRated )
                {
                    Log.d("inside fab fav click",String.valueOf(isRated));
                    fab_favourite.startAnimation(fabClose);
                    isRated = true;

                }
                fab_favourite.setClickable(false);
                fab_favourite.setEnabled(false);
                fab_favourite.setVisibility(View.GONE);
            }
        });




        setW=(FloatingActionButton)findViewById(R.id.button);

        setW.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    /////////////////////////////////////////////////////////////////////////////////////////////////


                if(!isOpen)
               {

                   fab_download=(FloatingActionButton)findViewById(R.id.button1);
                   fab_save=(FloatingActionButton)findViewById(R.id.button2);

                   fab_save.startAnimation(fabOpen);
                   fab_download.startAnimation(fabOpen);
                   setW.startAnimation(fabRCw);
                   fab_download.setClickable(true);
                   fab_save.setClickable(true);
                   if(!isRated)
                   {
                       fab_favourite.startAnimation(fabOpen);
                       fab_favourite.setClickable(true);
                   }

                       isOpen=true;
               }
                else
                {
                    fab_download=(FloatingActionButton)findViewById(R.id.button1);
                    fab_save=(FloatingActionButton)findViewById(R.id.button2);
                    fab_save.startAnimation(fabClose);
                    fab_download.startAnimation(fabClose);

                    setW.startAnimation(fabRCCw);
                    fab_download.setClickable(false);
                    fab_save.setClickable(false);
                    if(!isRated)
                    {
                        fab_favourite.startAnimation(fabClose);
                        fab_favourite.setClickable(false);
                    }


                    isOpen=false;

                }


            }
        });

        class DownloadTask extends AsyncTask<String, Integer, String>
        {

            private Context context;
            //private PowerManager.WakeLock mWakeLock;

            public DownloadTask(Context context)
            {
                this.context = context;
            }

            @Override
            protected String doInBackground(String... sUrl)
            {
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try
                {
                    URL url = new URL(sUrl[0]);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    // expect HTTP 200 OK, so we don't mistakenly save error report
                    // instead of the file
                    if (connection.getResponseCode() != HttpURLConnection.HTTP_OK && connection.getResponseCode() != HttpURLConnection.HTTP_MOVED_TEMP )
                    {
                        return "Server returned HTTP " + connection.getResponseCode()
                                + " " + connection.getResponseMessage();
                    }

                    Log.d("Server http ",connection.getResponseMessage());

                    // this will be useful to display download percentage
                    // might be -1: server did not report the length
                    int fileLength = connection.getContentLength();

                    // download the file
                    input = connection.getInputStream();
                    Log.d("file name",filename);
                    /*if (Environment.getExternalStorageState() == null)
                       output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Wallify/"+filename+".jpg");
                    else*/
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/" +"Wallify"+"/");
                    if(!dir.exists())
                    {
                        dir.mkdirs();

                    }

                    //File dir = new File(Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_DOWNLOADS). getAbsoluteFile() + "/Walify");
                    output = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() +"/" +"Wallify"+"/"+filename+".jpg");
                    Log.d("file path",String.valueOf(output));


                    byte data[] = new byte[4096];
                    long total = 0;
                    int count;
                    while ((count = input.read(data)) != -1)
                    {
                        // allow canceling with back button
                        if (isCancelled())
                        {
                            input.close();
                            return null;
                        }
                        total += count;
                        // publishing the progress....
                        if (fileLength > 0) // only if total length is known
                            publishProgress((int) (total * 100 / fileLength));
                        output.write(data, 0, count);
                    }
                } catch (Exception e)
                {
                    Log.d("excep",e.toString());
                    return e.toString();
                } finally
                {
                    try
                    {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored)
                    {
                    }

                    if (connection != null)
                        connection.disconnect();
                }

                return null;
            }

            @Override
            protected void onPreExecute()
            {
                super.onPreExecute();
                // take CPU lock to prevent CPU from going off if the user
                // presses the power button during download
                //PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                //mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                  //      getClass().getName());
                //mWakeLock.acquire();
                mProgressDialog.show();


            }

            @Override
            protected void onProgressUpdate(Integer... progress)
            {
                super.onProgressUpdate(progress);
                // if we get here, length is known, now set indeterminate to false
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgress(progress[0]);
            }


            @Override
            protected void onPostExecute(final String result)
            {
                //mWakeLock.release();

                mProgressDialog.dismiss();
                //String path = Environment.getExternalStorageDirectory().toString();
                //String imageName = filename;
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/" +"Wallify"+"/"+filename+".jpg");
                Log.d("Filezz",String.valueOf(file));


                if (result != null)
                {   Log.d("result",result);
                    database.child("Errors").addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            database.child("Errors").child("errors").setValue(String.valueOf(result + " 2.0"));
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {
                            Toast.makeText(getApplicationContext(),"An error occurred!",Toast.LENGTH_SHORT).show();

                        }

                    });


                    Toast.makeText(context, "Download error!", Toast.LENGTH_LONG).show();
                    boolean deleted = file.delete();
                }
                else
                {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        Intent mediaScanIntent = new Intent(
                                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri contentUri = Uri.fromFile(file); //out is your output file
                        mediaScanIntent.setData(contentUri);
                        SingleImageActivity.this.sendBroadcast(mediaScanIntent);
                    } else {
                        sendBroadcast(new Intent(
                                Intent.ACTION_MEDIA_MOUNTED,
                                Uri.parse("file://"
                                        + Environment.getExternalStorageDirectory())));
                    }


                    if(setWallpaperSelected && !flagIsCanccelled)
                    {
                        Toast.makeText(context, "Select the image from /Wallify/", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SET_WALLPAPER);
                        //intent.setData(yourUri);
                        startActivity(Intent.createChooser(intent, "Set Using: "));
                        setWallpaperSelected = false;
                    }
                    else if(!flagIsCanccelled)
                        Toast.makeText(context, "Wallpaper downloaded.", Toast.LENGTH_SHORT).show();
                }
            }


        }

        fab_download=(FloatingActionButton)findViewById(R.id.button1);
        fab_download.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //value=value.replace("_thumbs", "");
                Log.d("download only url",halfDUrl);
                final DownloadTask downloadTask = new DownloadTask(SingleImageActivity.this);
                downloadTask.execute(halfDUrl);

            }
        });

        fab_save=(FloatingActionButton)findViewById(R.id.button2);
        fab_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                //value=value.replace("_thumbs", "");
                Log.d("download n set wall url",halfDUrl);

                File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/" +"Wallify"+"/"+filename+".jpg");
                Log.d("directory",String.valueOf(myFile));
                if(!myFile.exists())
                {    setWallpaperSelected=true;
                    final DownloadTask downloadTask = new DownloadTask(SingleImageActivity.this);
                    downloadTask.execute(halfDUrl);
                }

                else
                {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SET_WALLPAPER);
                    //intent.setData(yourUri);
                    startActivity(Intent.createChooser(intent, "Set Using: "));
                }
            }
        });





    }
    @Override
    public void onBackPressed()
    {
      if(isOpen)
      {
          fab_save.startAnimation(fabClose);
          fab_download.startAnimation(fabClose);
          if(!isRated)
          {
              fab_favourite.startAnimation(fabClose);
              fab_favourite.setClickable(false);
          }

          setW.startAnimation(fabRCCw);
          fab_download.setClickable(false);
          fab_save.setClickable(false);
          isOpen=false;

      }
        else finish();



    }

}

