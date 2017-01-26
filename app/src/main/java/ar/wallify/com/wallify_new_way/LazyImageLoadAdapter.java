package ar.wallify.com.wallify_new_way;



import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

//Adapter class extends with BaseAdapter and implements with OnClickListener
public class LazyImageLoadAdapter extends BaseAdapter implements OnClickListener{
    //final String half_url="http://wallifiles.coolpage.biz/images_thumbs/";
    private Activity activity;
    private String[] data;
    private static LayoutInflater inflater=null;


    public LazyImageLoadAdapter(Activity a, String[] d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Create ImageLoader object to download and show image in list
        // Call ImageLoader constructor to initialize FileCache

    }



    public int getCount() {
        return data.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public ImageView image;
        public ProgressBar p;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi=convertView;
        final ViewHolder holder;
        Log.e("Progress",String.valueOf(position));

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.singlerow, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.image=(ImageView)vi.findViewById(R.id.grid_item_image);
            holder.p= (ProgressBar)vi.findViewById(R.id.progressBar);
            Log.e("Imageview", String.valueOf(holder.image));


            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
        {
            //vi.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT, heightPixels/3));
            holder = (ViewHolder) vi.getTag();

        }



        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int heightPixels = metrics.heightPixels;
        holder.image.setMinimumHeight(heightPixels/2);
        holder.p.setVisibility(View.VISIBLE);

        Picasso.with(activity).load(DnTUrls.halfUrl_thumbs+data[position]+".jpg").fit().centerCrop().error(R.drawable.bi).into(holder.image, new Callback()
        {
            @Override
            public void onSuccess()
            {

                holder.p.setVisibility(View.GONE);
            }

            @Override
            public void onError()
            {
                holder.p.setVisibility(View.GONE);
            }
        });

        return vi;
    }

    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }

}