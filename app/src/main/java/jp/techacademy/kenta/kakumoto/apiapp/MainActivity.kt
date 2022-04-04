package jp.techacademy.kenta.kakumoto.apiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), FragmentCallback {

    private val viewPagerAdapter by lazy { ViewPagerAdapter(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //viewPager2の初期化
        viewPager2.apply{
            adapter = viewPagerAdapter
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = viewPagerAdapter.itemCount
        }

        //TabLayoutの初期化
        //tablayoutとviewPager2を紐付け
        TabLayoutMediator(tabLayout, viewPager2){ tab, position ->
            tab.setText(viewPagerAdapter.titleIds[position])
        }.attach()
    }

    override fun onClickItem(shop: Shop){
        Log.d("TEST","MainActivity onClickItem shop:"+shop.name+":"+shop.logo_image)
        WebViewActivity.start(this, shop)
    }

    override fun onAddFavorite(shop: Shop){
        FavoriteShop.insert(FavoriteShop().apply{
            id = shop.id
            name = shop.name
            address = shop.address
            imageUrl = shop.logo_image
            url = if(shop.coupon_urls.sp.isNotEmpty()) shop.coupon_urls.sp else shop.coupon_urls.pc
        })
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }

    override fun onDeleteFavorite(id: String) {
        showConfirmDeleteFavoriteDialog(id)
    }

    private fun showConfirmDeleteFavoriteDialog(id: String){
        AlertDialog.Builder(this)
            .setTitle(R.string.delete_favorite_dialog_title)
            .setMessage(R.string.delete_favorite_dialog_message)
            .setPositiveButton(android.R.string.ok){_, _ ->
                deleteFavorite(id)
            }
            .setNegativeButton(android.R.string.cancel){_, _ ->}
                .create()
                .show()
    }

    private fun deleteFavorite(id: String){
        FavoriteShop.delete(id)
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_API] as ApiFragment).updateView()
        (viewPagerAdapter.fragments[VIEW_PAGER_POSITION_FAVORITE] as FavoriteFragment).updateData()
    }

    companion object{
        private const val VIEW_PAGER_POSITION_API = 0
        private const val VIEW_PAGER_POSITION_FAVORITE = 1
    }
}