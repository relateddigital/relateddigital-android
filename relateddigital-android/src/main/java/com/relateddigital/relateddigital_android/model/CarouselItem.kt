package com.relateddigital.relateddigital_android.model

import android.os.Parcel
import android.os.Parcelable

class CarouselItem : Parcelable {
    var id: String?
    var title: String?
    var description: String?
    var photoUrl: String?
    var imageFileLocation: String? = null
    var imageFileName: String? = null
    var type: String? = null

    constructor(photoUrl: String?) : this(null, null, null, photoUrl) {}

    constructor() : this(null, null, null, null) {}

    constructor(
        id: String? = null,
        title: String? = null,
        description: String? = null,
        photoUrl: String? = null
    ) {
        this.id = id
        this.title = title
        this.description = description
        this.photoUrl = photoUrl
    }

    private constructor(`in`: Parcel) {
        id = `in`.readString()
        title = `in`.readString()
        description = `in`.readString()
        photoUrl = `in`.readString()
        imageFileLocation = `in`.readString()
        imageFileName = `in`.readString()
        type = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(id)
        dest.writeString(title)
        dest.writeString(description)
        dest.writeString(photoUrl)
        dest.writeString(imageFileLocation)
        dest.writeString(imageFileName)
        dest.writeString(type)
    }

    companion object CREATOR : Parcelable.Creator<CarouselItem> {
        override fun createFromParcel(parcel: Parcel): CarouselItem {
            return CarouselItem(parcel)
        }

        override fun newArray(size: Int): Array<CarouselItem?> {
            return arrayOfNulls(size)
        }
    }
}