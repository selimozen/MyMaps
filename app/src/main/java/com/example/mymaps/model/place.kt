package com.example.mymaps.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class place(

    @ColumnInfo(name = "name")
    var name : String,

    @ColumnInfo(name = "lang")
    var lang : Double,

    @ColumnInfo(name = "lat")
    var lat : Double) : Serializable {
//Bu yazdığımız kodlar ile kolon isimlerini vermiş, tablomuzu oluşturmuş olduk. Tablomuzun adı place.
//Şimdi yazacağımız kodlarla ise
    @PrimaryKey(autoGenerate = true)
    var id = 0

}