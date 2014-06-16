package la.funka.deptolibre.app;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MeliAdapter extends ArrayAdapter<MeliListItem> {

    Context context;
    int resource;
    ArrayList<MeliListItem> data = null;

    public MeliAdapter(Context context, int resource, ArrayList<MeliListItem> data) {
        super(context, resource, data);
        this.context = context;
        this.resource = resource;
        this.data = data;
    }

    public View getView(int position, View converView, ViewGroup parent) {
        View row = converView;
        MeliHolder holder = null;

        if ( row == null ) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            holder = new MeliHolder();
            holder.meli_img = (ImageView) row.findViewById(R.id.meli_img);
            holder.meli_price = (TextView) row.findViewById(R.id.meli_price);
            holder.meli_title = (TextView) row.findViewById(R.id.meli_title);
            holder.meli_description = (TextView) row.findViewById(R.id.meli_description);

            row.setTag(holder);
        } else {
            holder = (MeliHolder) row.getTag();
        }
        return row;
    }

    static class MeliHolder {
        ImageView meli_img;
        TextView meli_price;
        TextView meli_title;
        TextView meli_description;
    }
}
