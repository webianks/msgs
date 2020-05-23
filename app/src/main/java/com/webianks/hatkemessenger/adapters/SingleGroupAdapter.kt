package com.webianks.hatkemessenger.adapters

import android.content.Context
import android.database.Cursor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.webianks.hatkemessenger.R
import com.webianks.hatkemessenger.adapters.SingleGroupAdapter.MyViewHolder
import com.webianks.hatkemessenger.utils.ColorGeneratorModified
import com.webianks.hatkemessenger.utils.Helpers

/**
 * Created by R Ankit on 25-12-2016.
 */
class SingleGroupAdapter(private val context: Context,
                         private var dataCursor: Cursor?,
                         private var color: Int,
                         private  val savedContactName: String?) : RecyclerView.Adapter<MyViewHolder>() {

    private lateinit var generator: ColorGeneratorModified

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.single_sms_detailed, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        dataCursor!!.moveToPosition(position)
        holder.message.text = dataCursor!!.getString(dataCursor!!.getColumnIndexOrThrow("body"))
        val time = dataCursor!!.getLong(dataCursor!!.getColumnIndexOrThrow("date"))
        holder.time.text = Helpers.getDate(time)
        val name = dataCursor!!.getString(dataCursor!!.getColumnIndexOrThrow("address"))
        val firstChar = savedContactName?.get(0).toString() ?: name[0].toString()

        if (color == 0)
            color = generator.getColor(name)

        val drawable = TextDrawable.builder().buildRound(firstChar, color)
        holder.image.setImageDrawable(drawable)
    }

    fun swapCursor(cursor: Cursor?) {
        if (dataCursor === cursor) {
            return
        }
        dataCursor = cursor
        if (cursor != null) {
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return if (dataCursor == null) 0 else dataCursor!!.count
    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val message: TextView = itemView.findViewById(R.id.message)
        val image: ImageView = itemView.findViewById(R.id.smsImage)
        val time: TextView = itemView.findViewById(R.id.time)
    }

    init {
        if (color == 0) generator = ColorGeneratorModified.MATERIAL!!
    }
}