package jp.techacademy.kenta.kakumoto.apiapp

interface FragmentCallback {
    fun onClickItem(shop: Shop)
    fun onAddFavorite(shop: Shop)
    fun onDeleteFavorite(id: String)
}