package com.example.checkins.Interfaces

import com.example.checkins.Foursquare.Photo
import com.example.checkins.Foursquare.Photos

interface ImagePreviewInterface {
    fun obtenerImagePreview(photos: ArrayList<Photo>)
}