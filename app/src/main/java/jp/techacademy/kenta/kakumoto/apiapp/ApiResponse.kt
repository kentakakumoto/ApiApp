package jp.techacademy.kenta.kakumoto.apiapp

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ApiResponse(
    @SerializedName("results")
    var results: Results
)

data class Results(
    @SerializedName("shop")
    var shop: List<Shop>
)

data class Shop(
    //@SerializedName("address")
    var address: String,
    //@SerializedName("coupon_urls")
    var coupon_urls: CouponUrls,
    //@SerializedName("id")
    var id: String,
    //@SerializedName("logo_image")
    var logo_image: String,
    //@SerializedName("name")
    var name: String
): Serializable

data class CouponUrls(
    //@SerializedName("pc")
    var pc: String,
    //@SerializedName("sp")
    var sp: String
): Serializable