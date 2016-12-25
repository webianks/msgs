package com.webianks.hatkemessenger.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webianks.hatkemessenger.R;

/**
 * Created by R Ankit on 25-12-2016.
 */

public class SingleGroupAdapter extends RecyclerView.Adapter<SingleGroupAdapter.MyViewHolder> {

    private Context context;
    private Cursor dataCursor;

    public SingleGroupAdapter(Context context, Cursor dataCursor) {
        this.context = context;
        this.dataCursor = dataCursor;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_sms_detailed, parent, false);
        SingleGroupAdapter.MyViewHolder myHolder = new SingleGroupAdapter.MyViewHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        dataCursor.moveToPosition(position);
        holder.message.setText(dataCursor.getString(dataCursor.getColumnIndexOrThrow("body")));

    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView message;

        public MyViewHolder(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.message);

        }

    }
}
