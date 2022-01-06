package com.relateddigital.relateddigital_android.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList

class Carousel : Parcelable {
    var carouselItems: ArrayList<CarouselItem>? = null
    var contentTitle: String?
    var contentText: String?
    var bigContentTitle: String?
    var bigContentText: String?
    var carouselNotificationId //Random id 9873715 for notification. Will cancel any notification that have existing same id.
            : Int
    var currentStartIndex //Variable that keeps track of where the startIndex is
            : Int
    var smallIcon: String?
    private var smallIconResourceId: Int
    var largeIcon: String?
    var caraousalPlaceholder: String?
    var leftItem: CarouselItem?
    var rightItem: CarouselItem?
    var isImagesInCarousel: Boolean

    constructor(
        carouselItems: ArrayList<CarouselItem>?, contentTitle: String?, contentText: String?,
        bigContentTitle: String?, bigContentText: String?, carouselNotificationId: Int,
        currentStartIndex: Int, smallIcon: String?, smallIconResourceId: Int,
        largeIcon: String?, caraousalPlaceholder: String?, leftItem: CarouselItem?,
        rightItem: CarouselItem?, isImagesInCarousel: Boolean
    ) {
        this.carouselItems = carouselItems
        this.contentTitle = contentTitle
        this.contentText = contentText
        this.bigContentTitle = bigContentTitle
        this.bigContentText = bigContentText
        this.carouselNotificationId = carouselNotificationId
        this.currentStartIndex = currentStartIndex
        this.smallIcon = smallIcon
        this.smallIconResourceId = -1
        this.smallIconResourceId = smallIconResourceId
        this.largeIcon = largeIcon
        this.caraousalPlaceholder = caraousalPlaceholder
        this.leftItem = leftItem
        this.rightItem = rightItem
        this.isImagesInCarousel = isImagesInCarousel
    }

    private constructor(`in`: Parcel) {
        if (`in`.readByte().toInt() == 0x01) {
            carouselItems = ArrayList<CarouselItem>()
            `in`.readList(carouselItems!!, CarouselItem::class.java.getClassLoader())
        } else {
            carouselItems = null
        }
        contentTitle = `in`.readString()
        contentText = `in`.readString()
        bigContentTitle = `in`.readString()
        bigContentText = `in`.readString()
        carouselNotificationId = `in`.readInt()
        currentStartIndex = `in`.readInt()
        smallIcon = `in`.readString()
        smallIconResourceId = -1
        smallIconResourceId = `in`.readInt()
        largeIcon = `in`.readString()
        caraousalPlaceholder = `in`.readString()
        leftItem = `in`.readValue(CarouselItem::class.java.getClassLoader()) as CarouselItem?
        rightItem = `in`.readValue(CarouselItem::class.java.getClassLoader()) as CarouselItem?
        isImagesInCarousel = `in`.readByte().toInt() != 0x00
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        if (carouselItems == null) {
            dest.writeByte(0x00.toByte())
        } else {
            dest.writeByte(0x01.toByte())
            dest.writeList(carouselItems)
        }
        dest.writeString(contentTitle)
        dest.writeString(contentText)
        dest.writeString(bigContentTitle)
        dest.writeString(bigContentText)
        dest.writeInt(carouselNotificationId)
        dest.writeInt(currentStartIndex)
        dest.writeString(smallIcon)
        dest.writeInt(smallIconResourceId)
        dest.writeString(largeIcon)
        dest.writeString(caraousalPlaceholder)
        dest.writeValue(leftItem)
        dest.writeValue(rightItem)
        dest.writeByte((if (isImagesInCarousel) 0x01 else 0x00).toByte())
    }

    companion object CREATOR : Parcelable.Creator<Carousel> {
        override fun createFromParcel(parcel: Parcel): Carousel {
            return Carousel(parcel)
        }

        override fun newArray(size: Int): Array<Carousel?> {
            return arrayOfNulls(size)
        }
    }
}