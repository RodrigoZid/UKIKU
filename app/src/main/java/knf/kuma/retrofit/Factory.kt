package knf.kuma.retrofit

import knf.kuma.directory.DirectoryPageCompact
import knf.kuma.pojos.AnimeObject
import knf.kuma.pojos.Recents
import knf.kuma.recents.RecentsPage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface Factory {
    @GET(".")
    fun getRecents(@Header("Cookie") cookies: String, @Header("User-Agent") userAgent: String, @Header("Referer") referer: String): Call<Recents>

    @GET(".")
    fun getRecentModels(@Header("Cookie") cookies: String, @Header("User-Agent") userAgent: String, @Header("Referer") referer: String): Call<RecentsPage>

    @GET("{rest}")
    fun getAnime(@Header("Cookie") cookies: String, @Header("User-Agent") userAgent: String, @Header("Referer") referer: String, @Path("rest") rest: String): Call<AnimeObject.WebInfo>

    @GET("/browse?order=title")
    fun getSearch(@Header("Cookie") cookies: String, @Header("User-Agent") userAgent: String, @Query("q") query: String, @Query("page") page: Int): Call<DirectoryPageCompact>

    @GET("/browse?order=title")
    fun getDirectory(@Header("Cookie") cookies: String, @Header("User-Agent") userAgent: String, @Query("type[]") type: String, @Query("page") page: Int): Call<DirectoryPageCompact>
}
