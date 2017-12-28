package ar.wallify.com.wallify_new_way;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class About extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ListView listView = (ListView) findViewById(R.id.list_about);

        // Defined Array values to show in ListView
        String[] values = new String[]{"Wallify",
                "Developed by Rizal Rovins",
                "Email - wallsbywallify@gmail.com",
                "Image credit goes to their respective creators.\nContact via email for credits.\nSend your wallpapers via email to be featured with your name.",
                "Creators List:\n Rutwik Patel\n Derek Laufman\n Nick Nice"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);
    }
}
