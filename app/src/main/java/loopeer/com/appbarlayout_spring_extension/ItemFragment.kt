package loopeer.com.appbarlayout_spring_extension

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import loopeer.com.appbarlayout_spring_extension.dummy.DummyContent

class ItemFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)
        val recyclerView = view as RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = MyItemRecyclerViewAdapter(DummyContent.ITEMS)
        return view
    }

    companion object {

        fun newInstance(): ItemFragment {
            val fragment = ItemFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }


}
