package jp.techacademy.kenta.kakumoto.apiapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_favorite.*

class FavoriteFragment: Fragment() {
    private val favoriteAdapter by lazy{ FavoriteAdapter(requireContext()) }
    //FavoriteFragment→Main Activityに削除を通知
    private var fragmentCallback : FragmentCallback ?= null

    override fun onAttach(context: Context){
        super.onAttach(context)
        if(context is FragmentCallback){
            fragmentCallback = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //ここから初期化処理
        //FavoriteAdapterのお気に入り削除用
        favoriteAdapter.apply{
            //Adapterの処理を Activityに通知
            onClickDeleteFavorite ={
                fragmentCallback?.onDeleteFavorite(it.id)
            }

            //Itemをクリックした時
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }

        //recyclerViewの初期化
        recyclerView.apply{
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        swipeRefreshLayout.setOnRefreshListener {
            updateData()
        }
        updateData()
    }

    fun updateData(){
        favoriteAdapter.refresh(FavoriteShop.findAll())
        swipeRefreshLayout.isRefreshing = false
    }
}