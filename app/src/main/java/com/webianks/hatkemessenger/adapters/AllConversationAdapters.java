package com.webianks.hatkemessenger.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.webianks.hatkemessenger.R;
import com.webianks.hatkemessenger.Sms;
import com.webianks.hatkemessenger.customViews.RoundedImageView;
import java.util.List;

/**
 * Created by R Ankit on 25-12-2016.
 */

public class AllConversationAdapters extends RecyclerView.Adapter<AllConversationAdapters.MyHolder> {

    private Context context;
    private List<Sms> smsList;

    public AllConversationAdapters(Context context, List<Sms> smsList) {
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    public AllConversationAdapters.MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.single_sms_small_layout, parent, false);
        MyHolder myHolder = new MyHolder(view);
        return myHolder;
    }

    @Override
    public void onBindViewHolder(AllConversationAdapters.MyHolder holder, int position) {

        holder.senderContact.setText(smsList.get(position).getAddress());
        holder.message.setText(smsList.get(position).getMsg());

    }

    @Override
    public int getItemCount() {
        return smsList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        private RoundedImageView senderImage;
        private TextView senderContact;
        private TextView message;

        public MyHolder(View itemView) {
            super(itemView);
            senderImage = (RoundedImageView) itemView.findViewById(R.id.smsImage);
            senderContact = (TextView) itemView.findViewById(R.id.smsSender);
            message = (TextView) itemView.findViewById(R.id.smsContent);
        }
    }
}
