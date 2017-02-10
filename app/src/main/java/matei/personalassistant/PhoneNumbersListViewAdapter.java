package matei.personalassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by alex_ on 30.12.2016.
 */
public class PhoneNumbersListViewAdapter extends ArrayAdapter<String> {

    private List<String> list;
    private static LayoutInflater inflater;
    private Context mContext;

    public PhoneNumbersListViewAdapter(Context context, int resource, List<String> phoneNumbers) {
        super(context, resource);
        mContext = context;
        inflater = LayoutInflater.from(mContext);
        list = phoneNumbers;
    }

    public int getCount() {
        return list.size();
    }

    public String getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {

        TextView numberTV;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View newView = convertView;
        ViewHolder holder;

        if (null == convertView) {
            holder = new ViewHolder();
            newView = inflater
                    .inflate(R.layout.phone_number_item, parent, false);
            linkUI(holder, newView);
        } else {
            holder = (ViewHolder) newView.getTag();
        }
        setData(holder, position);

        return newView;
    }

    private void setData(ViewHolder holder, int position) {
        String currentItem = list.get(position);
        holder.numberTV.setText(currentItem);
    }

    private void linkUI(ViewHolder holder, View newView) {
        holder.numberTV = (TextView) newView.findViewById(R.id.item_phone_number_tv);
        newView.setTag(holder);
    }
}
