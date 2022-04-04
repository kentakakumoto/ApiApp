package jp.techacademy.kenta.kakumoto.apiapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso

class ApiAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //取得したjsonデータを解析し、Shop型オブジェクトとして生成したものを格納する
    private val items = mutableListOf<Shop>()

    //一覧画面から登録・削除する時のCallback(FavoriteFragmentへの通知）
    var onClickAddFavorite: ((Shop) -> Unit)? = null
    var onClickDeleteFavorite: ((Shop) -> Unit)? = null

    //Itemを押した時のメソッド
    var onClickItem: ((Shop) -> Unit)? = null

    //リスト更新・refresh:全リストクリア、add:リストクリアしない
    fun refresh(list: List<Shop>){
        update(list, false)
    }

    fun add(list: List<Shop>){
        update(list, true)
    }

    fun update(list: List<Shop>, isAdd: Boolean){
        items.apply{
            if(!isAdd){
                clear()
            }
            addAll(list)
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        //viewHolderを継承したApiItemViewHolderオブジェクトを生成して戻す
        return ApiItemViewHolder(LayoutInflater.from(context).inflate(R.layout.recycler_favorite, parent, false))
    }

    class ApiItemViewHolder(view: View): RecyclerView.ViewHolder(view){
        val rootView: ConstraintLayout = view.findViewById(R.id.rootView)
        val nameTextView: TextView = view.findViewById(R.id.nameTextView)
        val addressTextView: TextView = view.findViewById(R.id.addressTextView)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val favoriteImageView: ImageView = view.findViewById(R.id.favoriteImageView)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ApiItemViewHolder){
            updateApiItemViewHolder(holder, position)
        }
    }

    private fun updateApiItemViewHolder(holder: ApiItemViewHolder, position: Int){
        //生成されたViewHolderの位置を指定しオブジェクトを代入
        val data = items[position]
        val isFavorite = FavoriteShop.findBy(data.id) != null
        Log.d("TEST", "updateApiItemViewHolder position"+position+"id:"+data.id+data.name)
        holder.apply{
            rootView.apply{
                setBackgroundColor(ContextCompat.getColor(context,
                    if(position % 2 == 0) android.R.color.white else android.R.color.darker_gray))
                setOnClickListener{
                    onClickItem?.invoke(data)
                }
            }
            nameTextView.text = data.name
            addressTextView.text = data.address
            Picasso.get().load(data.logo_image).into(imageView)
            //白抜きの星マークの画像を指定
            favoriteImageView.setImageResource(R.drawable.ic_star_border)

            favoriteImageView.apply{
                setImageResource(if(isFavorite) R.drawable.ic_star else R.drawable.ic_star_border)
                setOnClickListener{
                    if(isFavorite){
                        onClickDeleteFavorite?.invoke(data)
                    }else{
                        onClickAddFavorite?.invoke(data)
                    }
                    notifyItemChanged(position)
                }
            }

        }
    }
}