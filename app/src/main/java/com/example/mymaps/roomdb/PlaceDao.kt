package com.example.mymaps.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.mymaps.model.place
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {
    //Dao veriye erişim objesi demektir.
    @Query("SELECT * FROM place")
    fun getAll(): Flowable<List<place>>

    //Flowable ve Completable Rxjava özgü metodlar ve liste türleri.
    //Completable birşey dönmeyen görevini yapan metodlara denilir.
    //Flowtable ise Rxjava listeleridir. Normal database gibi id'ye göre felan listelenebilinir. Where gibi metodlar kullanılarak.
    @Insert
    fun insert(place: place) : Completable

    @Delete
    fun delete(place: place) : Completable

}