package jp.techacademy.kenta.kakumoto.apiapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class FavoriteAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //お気に入り登録したShopを格納
    private val items = mutableListOf<FavoriteShop>()

    //お気に入り画面から削除するときのCallback(ApiFragmentへ通知)
    var onClickDeleteFavorite: ((FavoriteShop) -> Unit)? = null

    var onClickItem: ((String) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_EMPTY -> EmptyViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite_empty, parent, false))
            else -> FavoriteItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false))
        }
    }

    class EmptyViewHolder(view: View): RecyclerView.ViewHolder(view)
    class FavoriteItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val rootView: ConstraintLayout = view.findViewById(R.id.rootView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
    }

    //更新用
    fun refresh(list: List<FavoriteShop>){
        items.apply{
            clear()
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return if(items.isEmpty()) 1 else items.size //アイテムが空の時に「お気に入りはありません」を表示するため
    }

    override fun getItemViewType(position: Int): Int {
        return if(items.isEmpty()) VIEW_TYPE_EMPTY else VIEW_TYPE_ITEM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is FavoriteItemViewHolder){
            updateFavoriteItemViewHolder(holder, position)
        }
    }

    private fun updateFavoriteItemViewHolder(holder: FavoriteItemViewHolder, position: Int){
        val data = items[position]
        holder.apply{
            rootView.apply{
                setBackgroundColor(ContextCompat.getColor(context, if(position % 2 == 0) android.R.color.white else android.R.color.darker_gray))
                setOnClickListener{
                    onClickItem?.invoke(data.url)
                }
            }
            nameTextView.text = data.name
            addressTextView.text = data.address
            Picasso.get().load(data.imageUrl).into(imageView)
            favoriteImageView.setOnClickListener{
                onClickDeleteFavorite?.invoke(data)
                notifyItemChanged(position)
            }
        }
    }

    companion object{
        //viewの種類を表現する定数
        private const val VIEW_TYPE_EMPTY = 1 //お気に入りが一件もない時
        private const val VIEW_TYPE_ITEM = 0 //お気に入りのお店
    }

}