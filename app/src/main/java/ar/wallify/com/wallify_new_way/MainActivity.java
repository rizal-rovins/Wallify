package ar.wallify.com.wallify_new_way;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class MainActivity extends AppCompatActivity
{
    ParmetersForAsync para;

    private AdView mAdView;
    private int category=0;
    Integer number_of_images;

    GridView gv;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView navigationView;

    String list[];
    String list_rev[];
    String arr[];
    String top_rated [];




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ProgressDialog mProgressDialog = new ProgressDialog(MainActivity.this,R.style.MyDialogTheme);
        mProgressDialog.setMessage("Loading");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();


        ////////////////////////////FIREBASE///////////////////////////



        final DatabaseReference database=FirebaseDatabase.getInstance().getReference();

        database.child("rates").orderByChild("iRate").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d("Db",String.valueOf(dataSnapshot.getValue()));
                Iterator<DataSnapshot> items = dataSnapshot.getChildren().iterator();
                int i=Integer.parseInt(String.valueOf(dataSnapshot.getChildrenCount()));
                top_rated=new String[i];
                while(items.hasNext()){
                    DataSnapshot item = items.next();
                    String iName = item.child("iName").getValue(String.class);
                    top_rated[--i]=iName;
                }

                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                mProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(),"An error occurred.",Toast.LENGTH_SHORT).show();
            }
        });





        ////////////SETTING UP SUPPPORT ACTION BAR////////


        mDrawerLayout = (DrawerLayout) findViewById(R.id.activity_main2);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mDrawerLayout.bringToFront();
        mToggle.syncState();
        //navList=(ListView)findViewById(R.id.)
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout.requestLayout();
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {
                        try
                        {
                            gv.invalidateViews();
                            mDrawerLayout.closeDrawers();
                            switch (menuItem.getItemId())
                            {
                                case R.id.all:
                                    category = 0;
                                    MainActivity.this.setTitle("Wallify");
                                    gv.setAdapter(new LazyImageLoadAdapter(MainActivity.this, list));

                                    break;
                                case R.id.top:
                                    category = 4;
                                    MainActivity.this.setTitle("Top Rated");
                                    gv.setAdapter(new LazyImageLoadAdapter(MainActivity.this, top_rated));
                                    break;
                                case R.id.old:
                                    category = -1;
                                    MainActivity.this.setTitle("Oldest First");
                                    gv.setAdapter(new LazyImageLoadAdapter(MainActivity.this, list_rev));
                                    break;

                                case R.id.nature:
                                    // open a fragment
                                    category = 1;
                                    arr = null;
                                    MainActivity.this.setTitle("Nature And Landcsape");
                                    para = new ParmetersForAsync(MainActivity.this, "http://wallifiles.coolpage.biz/jsondata/nature.json");
                                    new getJsonCategory().execute(para);
                                    break;
                                case R.id.minimal:
                                    category = 2;
                                    arr = null;
                                    MainActivity.this.setTitle("Minimal");
                                    para = new ParmetersForAsync(MainActivity.this, "http://wallifiles.coolpage.biz/jsondata/minimal.json");
                                    new getJsonCategory().execute(para);
                                    break;

                                case R.id.cars:
                                    category = 3;
                                    arr = null;
                                    MainActivity.this.setTitle("Cars");
                                    para = new ParmetersForAsync(MainActivity.this, "http://wallifiles.coolpage.biz/jsondata/cars.json");
                                    new getJsonCategory().execute(para);
                                    break;
                                case R.id.artwork:
                                    category = 3;
                                    arr = null;
                                    MainActivity.this.setTitle("Artwork");
                                    para = new ParmetersForAsync(MainActivity.this, "http://wallifiles.coolpage.biz/jsondata/artwork.json");
                                    new getJsonCategory().execute(para);
                                    break;
                                case R.id.Abstract:
                                    category = 3;
                                    arr = null;
                                    MainActivity.this.setTitle("Abstract");
                                    para = new ParmetersForAsync(MainActivity.this, "http://wallifiles.coolpage.biz/jsondata/abstract.json");
                                    new getJsonCategory().execute(para);
                                    break;

                                case R.id.more:
                                    Toast.makeText(getApplicationContext(), "Coming Soon!", Toast.LENGTH_SHORT).show();
                                    break;

                            }
                        }catch (NullPointerException e)
                        {
                            Toast.makeText(getApplicationContext(),"Not Loaded",Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        //Asking for permissions

        final int REQUEST_WRITE_STORAGE = 112;
        {
            boolean hasPermission = (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission)
            {
                Toast.makeText(getApplicationContext(), "Storage permission is required to download wallpapers!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
                }
        }

        class getJson extends AsyncTask<MainActivity, Void, String>
        {
            private String jsonStr;
            MainActivity activity;

            @Override
            protected String doInBackground(MainActivity... params)
            {
                activity = params[0];

                HttpHandler sh = new HttpHandler();
                jsonStr = sh.makeServiceCall("http://wallifiles.coolpage.biz/jsondata/data.json");
                Integer number = new Integer(0);
                    if (jsonStr != null)
                    {
                        try
                        {
                            number = new JSONObject(jsonStr).getInt("number");

                            DnTUrls.halfUrl_download=new JSONObject(jsonStr).getString("download_url");
                            DnTUrls.halfUrl_thumbs=new JSONObject(jsonStr).getString("thumbs_url");

                        } catch (JSONException e)
                        {

                        }
                        return String.valueOf(number);
                    }

                return null;

            }

            @Override
            protected void onPostExecute(String jsonStr)
            {
                if(jsonStr==null)
                {
                    //Toast.makeText(getApplicationContext(),"Please check your internet connection and try later.",Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Please check your internet connection and try later.")
                            .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    finish();
                                }

                            }).show();


                }
                else
                {

                    Log.d("download url ",DnTUrls.halfUrl_download);
                    Log.d("thumbs url",DnTUrls.halfUrl_thumbs);
                    number_of_images=Integer.valueOf(jsonStr);
                     list=new String[number_of_images];
                     list_rev=new String[number_of_images];
                    for(int i=number_of_images;i>0;i--)
                    {
                        list[number_of_images - i] = String.valueOf(i);
                        list_rev[i-1]=String.valueOf(i);

                    }

                    LazyImageLoadAdapter l=new LazyImageLoadAdapter(activity, list);
                    gv.setAdapter(l);
                    l.notifyDataSetChanged();
                }


            }


        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        int widthPixels = metrics.widthPixels;
        gv = (GridView) findViewById(R.id.gv);
        gv.setColumnWidth(widthPixels/2);
        new getJson().execute(this);


        gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Intent i = new Intent(getApplicationContext(), SingleImageActivity.class);
                if(category==0)
                i.putExtra("key", list[position]);
                else    if(category==-1)
                    i.putExtra("key", list_rev[position]);
                else if(category==4)
                    i.putExtra("key",top_rated[position]);

                else
                i.putExtra("key",arr[position]);
                i.putExtra("dUrl", DnTUrls.halfUrl_download);
                i.putExtra("tUrl",DnTUrls.halfUrl_thumbs);
                startActivity(i);

            }
        });


    }


    ////////////SETTING ACTIONBAR MENU//////////////////////////////////
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    ////////////////////////////////////////////////////////////////////////
    //////////SETTING UP ACTION BAR OPTION METHODS///////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        if (mToggle.onOptionsItemSelected(item))
        {
            return true;
        }
        switch (item.getItemId())
        {
            case R.id.action_exit:
                finish();
                break;
            case R.id.action_about:
                Intent i = new Intent(getApplicationContext(),About.class);
                startActivity(i);
                break;
            case R.id.rate_app:
                Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                // To count with Play market backstack, After pressing back button,
                // to taken back to our application, we need to add following flags to intent.
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                        Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                        Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" +getApplicationContext().getPackageName())));
                }


        }
        return true;
    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();


    }

    //////////////////getting json for category/////////////////
    class getJsonCategory extends AsyncTask<ParmetersForAsync, Void, String[]>
    {
        private String jsonStr;
        MainActivity activity;

        @Override
        protected String[] doInBackground(ParmetersForAsync... params)
        {   jsonStr=new String();
            activity = params[0].a;
            jsonStr=params[0].categoryJSON;

            HttpHandler sh = new HttpHandler();
            jsonStr = sh.makeServiceCall(jsonStr);
            if (jsonStr != null)
            {
                try
                {
                    JSONArray jsonArray = new JSONObject(jsonStr).getJSONArray("numbers");
                    arr=new String[jsonArray.length()];
                    for(int i=0;i<jsonArray.length();i++)
                        arr[i]=String.valueOf(jsonArray.getInt(i));

                    return arr;


                } catch (JSONException e)
                {

                }


            }

            return null;

        }

        @Override
        protected void onPostExecute(String[] arr)
        {   Log.d("artwork",String.valueOf(arr));
            if(arr==null)
            {
                Toast.makeText(getApplicationContext(),"Please check your internet connection and try again.",Toast.LENGTH_SHORT).show();

            }
            else
            {
                LazyImageLoadAdapter l=new LazyImageLoadAdapter(activity,arr);
                l.notifyDataSetChanged();
                gv.setAdapter(l);
            }


        }


    }

}
