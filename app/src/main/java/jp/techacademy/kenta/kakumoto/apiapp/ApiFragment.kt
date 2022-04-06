package jp.techacademy.kenta.kakumoto.apiapp

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_api.*
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException

class ApiFragment: Fragment() {

    private val apiAdapter by lazy { ApiAdapter(requireContext())}
    private val handler = Handler(Looper.getMainLooper())

    private var fragmentCallback: FragmentCallback? = null //Fragment -> ActivityにFavoriteの変更を通知

    //ページ連続読み込みのため
    private var page = 0
    private var isLoading = false

    override fun onAttach(context: Context) {
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
        //fragment_api.xmlが反映されたviewを返す
        return inflater.inflate(R.layout.fragment_api, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var keyword: String ="ランチ"

        //ここから初期化
        //ApiAdapterのお気に入り追加・削除用のメソッド追加
        apiAdapter.apply{
            onClickAddFavorite ={
                fragmentCallback?.onAddFavorite(it)
            }
            onClickDeleteFavorite ={
                fragmentCallback?.onDeleteFavorite(it.id)
            }
            onClickItem = {
                fragmentCallback?.onClickItem(it)
            }
        }
        //RecyclerViewの初期化
        recyclerView.apply{
            adapter = apiAdapter
            layoutManager = LinearLayoutManager(requireContext())

            addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(dy == 0){
                        return
                    }
                    val totalCount = apiAdapter.itemCount
                    val lastVisibleItem = (layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                   //下から５番目を表示した時に追加読み込みをする
                    if(!isLoading && lastVisibleItem >= totalCount -6){
                        updateData(true, keyword)
                    }
                }
            })
        }

        swipeRefreshLayout.setOnRefreshListener{
            updateData(false, keyword)
        }
        Log.d("TEST","updateData() called keyword:"+ keyword)
        updateData(false, keyword)

        searchButton.setOnClickListener {
            keyword = searchEditText.text.toString() ?:"ランチ"
            if(keyword.length == 0){
                keyword = "ランチ"
            }
            Log.d("TEST","keyword定義:"+keyword)
            updateData(false, keyword)
        }
    }

    fun updateView(){ //お気に入りが削除された時の処理（ Activityからコールされる）
        recyclerView.adapter?.notifyDataSetChanged()
    }
    private fun updateData(isAdd: Boolean = false, keyword: String){
        if(isLoading){
            return
        }else{
            isLoading = true
        }

        if(isAdd){
            page ++
        }else{
            page = 0
        }

        val start = page * COUNT + 1

        val url = StringBuilder()
            .append(getString(R.string.base_url))
            .append("?key=").append(getString(R.string.api_key))
            .append("&start=").append(start) //何件目
            .append("&count=").append(COUNT)//１回で２０件取得
            .append("&keyword=").append(keyword)
            .append("&format=json")//戻りの型
            .toString()

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply{
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException){
                e.printStackTrace()
                handler.post{
                    updateRecyclerView(listOf(), isAdd)
                }
                isLoading = false //追加読み込みを折る
            }

            override fun onResponse(call: Call, response: Response){
                var list = listOf<Shop>()
                response.body?.string()?.also{
                    val apiResponse = Gson().fromJson(it, ApiResponse::class.java)
                    list = apiResponse.results.shop
                }
                handler.post{
                    updateRecyclerView(list, isAdd)
                }
                isLoading = false //追加読み込みを折る
            }
        })
    }

    private fun updateRecyclerView(list: List<Shop>,isAdd: Boolean){
        if(isAdd) {
            apiAdapter.add(list)
        }else{
            apiAdapter.refresh(list)
        }
        swipeRefreshLayout.isRefreshing = false
    }

    companion object{
        private const val COUNT = 20
    }
}
