package com.app.ecolive.common_screen.adapters

import com.app.ecolive.R
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep

class TrackingStepAdapter(private val items: List<MyItem>) : SequenceAdapter<TrackingStepAdapter.MyItem>()  {
    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): MyItem {
        return items[position]
    }

    override fun bindView(sequenceStep: SequenceStep, item: MyItem) {
        with(sequenceStep) {
            setActive(item.isActive)
            setAnchor(item.formattedDate)
            setAnchorTextAppearance(R.style.titleAppearance)
            setTitle(item.title)
            setTitleTextAppearance(R.style.titleAppearance)
        }
    }

    data class MyItem(val isActive: Boolean,
                      val formattedDate: String,
                      val title: String)
}