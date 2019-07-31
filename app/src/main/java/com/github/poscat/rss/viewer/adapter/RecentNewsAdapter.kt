package com.github.poscat.rss.viewer.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.github.poscat.rss.viewer.R
import com.github.poscat.rss.viewer.model.Item
import com.github.poscat.rss.viewer.utility.ImageFilter
import com.github.poscat.rss.viewer.utility.TimeFormat
import java.lang.ref.WeakReference

class RecentNewsAdapter<T>(private var mClickHandler: T): RecyclerView.Adapter<RecentNewsAdapter<T>.CellListAdapterViewHolder>() {

    private lateinit var context: Context
    private var mData: ArrayList<Item>? = null

    interface ListOnClickListener {
        fun openLink(link: String, title: String?)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CellListAdapterViewHolder, position: Int) {
        val thisItem = mData!![position]

        if (thisItem.author == null || thisItem.author!!.name == null) holder.mCreatorTime.text =
            TimeFormat.formatTimeString(thisItem.published)
        else holder.mCreatorTime.text =
            thisItem.author!!.name + "  ·  " + TimeFormat.formatTimeString(thisItem.published)

        holder.mTitle.text = thisItem.title
        holder.mDescription.text = thisItem.description

        if (thisItem.enclosures != null) {
            Glide.with(context).load(thisItem.enclosures!![0].url)
                .placeholder(R.drawable.loading_img)
                .transform(ImageFilter())
                .error(R.drawable.default_img)
                .into(holder.mImage)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellListAdapterViewHolder {
        context = parent.context
        val layoutIdForListItem = R.layout.recent_news_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately)
        return CellListAdapterViewHolder(view, mClickHandler as ListOnClickListener)
    }

    override fun getItemCount(): Int {
        return if (mData == null) 0 else mData!!.size
    }

    inner class CellListAdapterViewHolder(v: View, listener: ListOnClickListener) : RecyclerView.ViewHolder(v),
        View.OnClickListener {

        private val listenerRef: WeakReference<ListOnClickListener>
        private val mItem: LinearLayout = v.findViewById(R.id.item)
        var mTitle: TextView = v.findViewById(R.id.title)
        var mDescription: TextView = v.findViewById(R.id.description)
        var mCreatorTime: TextView = v.findViewById(R.id.creator_time)
        var mImage: ImageView = v.findViewById(R.id.image)

        init {
            mItem.setOnClickListener(this)
            listenerRef = WeakReference(listener)
        }

        override fun onClick(v: View) {
            listenerRef.get()!!.openLink(mData!![adapterPosition].link, mData!![adapterPosition].title)
        }
    }

    fun setData(data: ArrayList<Item>?){
        mData = data
        notifyDataSetChanged()
    }
}