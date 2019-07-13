package com.webianks.hatkemessenger.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.SMS;
import com.webianks.hatkemessenger.utils.ColorGeneratorModified;
import com.webianks.hatkemessenger.utils.Helpers;

import java.util.List;

/**
 * Created by R Ankit on 25-12-2016.
 */

public class AllConversationAdapter extends RecyclerView.Adapter<AllConversationAdapter.MyHolder> {

    private Context context;
    private List<SMS> data;
    private ItemCLickListener itemClickListener;
    private ColorGeneratorModified generator = ColorGeneratorModified.MATERIAL;


    public AllConversationAdapter(Context context, List<SMS> data) {
        this.context = context;
        this.data = data;

    }

    @NonNull
    @Override
    public AllConversationAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_sms_small_layout, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllConversationAdapter.MyHolder holder, int position) {

        SMS SMS = data.get(position);

        holder.senderContact.setText(SMS.getAddress());
        holder.message.setText(SMS.getMsg());

        int color = generator.getColor(SMS.getAddress());
        String firstChar = String.valueOf(SMS.getAddress().charAt(0));
        TextDrawable drawable = TextDrawable.builder().buildRound(firstChar, color);
        holder.senderImage.setImageDrawable(drawable);

        SMS.setColor(color);


        if (SMS.getReadState().equals("0")) {
            holder.senderContact.setTypeface(holder.senderContact.getTypeface(), Typeface.BOLD);
            holder.message.setTypeface(holder.message.getTypeface(), Typeface.BOLD);
            holder.message.setTextColor(ContextCompat.getColor(context, R.color.black));
            holder.time.setTypeface(holder.time.getTypeface(), Typeface.BOLD);
            holder.time.setTextColor(ContextCompat.getColor(context,R.color.black));
        } else {
            holder.senderContact.setTypeface(null, Typeface.NORMAL);
            holder.message.setTypeface(null, Typeface.NORMAL);
            holder.time.setTypeface(null, Typeface.NORMAL);

        }

        holder.time.setText(Helpers.getDate(SMS.getTime()));

    }



    @Override
    public int getItemCount() {
        return (data == null) ? 0 : data.size();
    }


    public void setItemClickListener(ItemCLickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView senderImage;
        private TextView senderContact;
        private TextView message;
        private TextView time;
        private RelativeLayout mainLayout;

        MyHolder(View itemView) {
            super(itemView);
            senderImage =  itemView.findViewById(R.id.smsImage);
            senderContact =  itemView.findViewById(R.id.smsSender);
            message =  itemView.findViewById(R.id.smsContent);
            time =  itemView.findViewById(R.id.time);
            mainLayout =  itemView.findViewById(R.id.small_layout_main);

            mainLayout.setOnClickListener(this);
            mainLayout.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (itemClickListener != null) {

                data.get(getAdapterPosition()).setReadState("1");
                notifyItemChanged(getAdapterPosition());

                itemClickListener.itemClicked(data.get(getAdapterPosition()).getColor(),
                        senderContact.getText().toString(),
                        data.get(getAdapterPosition()).getId(),
                        data.get(getAdapterPosition()).getReadState());
            }

        }

        @Override
        public boolean onLongClick(View view) {

            String[] items = {"Delete"};

            ArrayAdapter<String> adapter = new ArrayAdapter<>(context
                    , android.R.layout.simple_list_item_1, android.R.id.text1, items);

            new MaterialAlertDialogBuilder(context)
                    .setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            deleteDialog();
                        }
                    })
                    .show();

            return true;
        }

        private void deleteDialog() {

            MaterialAlertDialogBuilder alert = new MaterialAlertDialogBuilder(context);
            alert.setMessage("Are you sure you want to delete this message?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    deleteSMS(data.get(getAdapterPosition()).getId(), getAdapterPosition());

                }

            });
            alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    dialog.dismiss();
                }
            });
            alert.create();
            alert.show();
        }
    }

    private void deleteSMS(long messageId, int position) {

        long affected = context.getContentResolver().delete(
                Uri.parse("content://sms/" + messageId), null, null);

        if (affected != 0) {
            data.remove(position);
            notifyItemRemoved(position);
        }

    }
}
