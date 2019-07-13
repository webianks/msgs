package com.webianks.hatkemessenger.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.utils.ColorGeneratorModified;
import com.webianks.hatkemessenger.utils.Helpers;

/**
 * Created by R Ankit on 25-12-2016.
 */

public class SingleGroupAdapter extends RecyclerView.Adapter<SingleGroupAdapter.MyViewHolder> {

    private ColorGeneratorModified generator;
    private Context context;
    private Cursor dataCursor;
    private int color;

    public SingleGroupAdapter(Context context, Cursor dataCursor, int color) {

        this.context = context;
        this.dataCursor = dataCursor;
        this.color = color;

        if (color == 0)
            generator = ColorGeneratorModified.MATERIAL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_sms_detailed, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        dataCursor.moveToPosition(position);
        holder.message.setText(dataCursor.getString(dataCursor.getColumnIndexOrThrow("body")));

        long time = dataCursor.getLong(dataCursor.getColumnIndexOrThrow("date"));
        holder.time.setText(Helpers.getDate(time));

        String name = dataCursor.getString(dataCursor.getColumnIndexOrThrow("address"));
        String firstChar = String.valueOf(name.charAt(0));

        if (color == 0){
            if (generator!=null)
                color = generator.getColor(name);
        }

        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, color);
        holder.image.setImageDrawable(drawable);


    }

    public void swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return;
        }
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView message;
        private ImageView image;
        private TextView time;

        MyViewHolder(View itemView) {
            super(itemView);

            message =  itemView.findViewById(R.id.message);
            image =  itemView.findViewById(R.id.smsImage);
            time =  itemView.findViewById(R.id.time);

        }

    }
}
