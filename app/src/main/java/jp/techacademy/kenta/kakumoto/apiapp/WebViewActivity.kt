package jp.techacademy.kenta.kakumoto.apiapp

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_web_view.*
import android.util.Log

class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)
        //webView.loadUrl(intent.getStringExtra(KEY_URL).toString())
        var shop: Shop = (intent.getSerializableExtra(KEY_SHOP) as Shop)
        Log.d("TEST", "WebViewActivity onCreate")
        Log.d("TEST", "WebView onCreate shop name:"+shop.name+"shop.coupon_urls:"+shop.coupon_urls.sp+":"+shop.coupon_urls.pc)
        webView.loadUrl( if(shop.coupon_urls.sp.isNotEmpty()) shop.coupon_urls.sp else shop.coupon_urls.pc)

        var isFavorite = FavoriteShop.findBy(shop.id) != null

        favoriteButton.apply{
                text =(if (isFavorite) "お気に入りから削除" else "お気に入りに追加")
                setOnClickListener {
                    if (isFavorite) {
                        Log.d("TEST",shop.name+"のお気に入りを解除")
                        favoriteButton.text = "お気に入りに追加"
                        FavoriteShop.delete(shop.id)
                        Log.d("TEST","FavoriteShopからDelete")
                        isFavorite = false
                    } else {
                        Log.d("TEST",shop.name+"をお気に入りに追加")
                        favoriteButton.text = "お気に入りから削除"
                        FavoriteShop.insert(FavoriteShop().apply{
                            id = shop.id
                            name = shop.name
                            address = shop.address
                            imageUrl = shop.logo_image
                            url = if(shop.coupon_urls.sp.isNotEmpty()) shop.coupon_urls.sp else shop.coupon_urls.pc
                        })
                        Log.d("TEST","id:${shop.id}, name:${shop.name}を追加")
                        isFavorite = true}
                }
            }
    }

    companion object{
        //private const val KEY_URL = "key_url"
        //fun start(activity: Activity, url: String){
        //    activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_URL, url))
        private val KEY_SHOP = "key_shop"
        fun start(activity: Activity, shop:Shop){
            Log.d("TEST", "webView start: keyshop="+KEY_SHOP)
            Log.d("TEST", "webView shop id:${shop.id}, shop name:${shop.name}")
            activity.startActivity(Intent(activity, WebViewActivity::class.java).putExtra(KEY_SHOP, shop))
        }
    }
}