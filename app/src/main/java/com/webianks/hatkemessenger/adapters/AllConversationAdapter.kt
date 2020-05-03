package com.webianks.hatkemessenger.adapters

import android.content.Context
import android.graphics.Typeface
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.webianks.hatkemessenger.R
import com.webianks.hatkemessenger.SMS
import com.webianks.hatkemessenger.adapters.AllConversationAdapter.MyHolder
import com.webianks.hatkemessenger.utils.ColorGeneratorModified
import com.webianks.hatkemessenger.utils.Helpers

/**
 * Created by R Ankit on 25-12-2016.
 */
class AllConversationAdapter(private val context: Context, private val data: ArrayList<SMS>?)
    : RecyclerView.Adapter<MyHolder>() {

    private var itemClickListener: ItemCLickListener? = null
    private val generator = ColorGeneratorModified.MATERIAL

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.single_sms_small_layout, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val sms = data!![position]
        holder.senderContact.text = sms.address
        holder.message.text = sms.msg
        val color = sms.address?.let { generator?.getColor(it) }
        val firstChar = sms.address?.get(0).toString()
        val drawable = TextDrawable.builder().buildRound(firstChar, color!!)
        holder.senderImage.setImageDrawable(drawable)
        sms.color = color
        if (sms.readState == "0") {
            holder.senderContact.setTypeface(holder.senderContact.typeface, Typeface.BOLD)
            holder.message.setTypeface(holder.message.typeface, Typeface.BOLD)
            holder.message.setTextColor(ContextCompat.getColor(context, R.color.black))
            holder.time.setTypeface(holder.time.typeface, Typeface.BOLD)
            holder.time.setTextColor(ContextCompat.getColor(context, R.color.black))
        } else {
            holder.senderContact.setTypeface(null, Typeface.NORMAL)
            holder.message.setTypeface(null, Typeface.NORMAL)
            holder.time.setTypeface(null, Typeface.NORMAL)
        }
        holder.time.text = sms.time.let { Helpers.getDate(it) }
    }

    override fun getItemCount(): Int {
        return data?.size ?: 0
    }

    fun setItemClickListener(itemClickListener: ItemCLickListener?) {
        this.itemClickListener = itemClickListener
    }

    inner class MyHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener,
            OnLongClickListener {

        val senderImage: ImageView = itemView.findViewById(R.id.smsImage)
        val senderContact: TextView = itemView.findViewById(R.id.smsSender)
        val message: TextView = itemView.findViewById(R.id.smsContent)
        val time: TextView = itemView.findViewById(R.id.time)
        private val mainLayout: RelativeLayout = itemView.findViewById(R.id.small_layout_main)

        override fun onClick(view: View) {
            if (itemClickListener != null) {
                data!![adapterPosition]?.readState = "1"
                notifyItemChanged(adapterPosition)
                data[adapterPosition]?.color?.let { data[adapterPosition]?.id?.let { it1 -> itemClickListener!!.itemClicked(it, senderContact.text.toString(), it1, data[adapterPosition]?.readState) } }
            }
        }

        override fun onLongClick(view: View): Boolean {
            val items = arrayOf("Delete")
            val adapter = ArrayAdapter(context
                    , android.R.layout.simple_list_item_1, android.R.id.text1, items)
            MaterialAlertDialogBuilder(context)
                    .setAdapter(adapter) { dialogInterface, _ ->
                        dialogInterface.dismiss()
                        deleteDialog()
                    }
                    .show()
            return true
        }

        private fun deleteDialog() {
            val alert = MaterialAlertDialogBuilder(context)
            alert.setMessage("Are you sure you want to delete this message?")
            alert.setPositiveButton("Yes") { _, _ -> data!![adapterPosition]?.id?.let { deleteSMS(it, adapterPosition) } }
            alert.setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            alert.create()
            alert.show()
        }

        init {
            mainLayout.setOnClickListener(this)
            mainLayout.setOnLongClickListener(this)
        }
    }

    private fun deleteSMS(messageId: Long, position: Int) {
        val affected = context.contentResolver.delete(
                Uri.parse("content://sms/$messageId"), null, null).toLong()
        if (affected != 0L) {
            data?.removeAt(position)
            notifyItemRemoved(position)
        }
    }

}