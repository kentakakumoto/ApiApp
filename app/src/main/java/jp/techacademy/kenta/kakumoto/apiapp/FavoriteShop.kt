package jp.techacademy.kenta.kakumoto.apiapp

import io.realm.Realm
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class FavoriteShop: RealmObject() {
    @PrimaryKey
    var id: String = ""
    var imageUrl: String = ""
    var name: String = ""
    var address: String = ""
    var url: String = ""
    var status:Boolean = true

    companion object{

        fun findAll(): List<FavoriteShop> = //全件表示
            Realm.getDefaultInstance().use{ realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::status.name, true)
                    .findAll().let{
                        realm.copyFromRealm(it)
                    }
            }

        fun findBy(id:String): FavoriteShop? = //お気に入り登録されてるものだけ
            Realm.getDefaultInstance().use{ realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .equalTo(FavoriteShop::status.name, true)
                    .findFirst()?.also{
                        realm.copyFromRealm(it)
                    }
            }

        fun insert(favoriteShop: FavoriteShop) =
            Realm.getDefaultInstance().executeTransaction{
                it.insertOrUpdate(favoriteShop)
            }

        fun delete(id: String) =
            Realm.getDefaultInstance().use{ realm ->
                realm.where(FavoriteShop::class.java)
                    .equalTo(FavoriteShop::id.name, id)
                    .findFirst()?.also{ deleteShop ->
                        realm.executeTransaction{
                            deleteShop.status = false //deleteShop.deleteFromRealm()
                        }
                    }
            }

    }
}