package com.berry.traveldiary

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.berry.traveldiary.adapter.GalleryAdapter
import com.berry.traveldiary.data.MyDatabase
import com.berry.traveldiary.databinding.ActivityPhotoGallaryBinding
import com.berry.traveldiary.model.Photos
import com.berry.traveldiary.uitility.CommonUtils
import com.berry.traveldiary.uitility.CommonUtils.NO_IMAGE
import com.github.dhaval2404.imagepicker.ImagePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PhotoGalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoGallaryBinding
    private lateinit var adapter: GalleryAdapter

    private var imagesList: MutableList<Photos> = mutableListOf()
    private lateinit var myDatabase: MyDatabase
    private var imagePosition: Int = -1
    private var photoId: Int = 0

//    private val images = listOf(
//        R.drawable.ic_menu_gallery,
//        R.drawable.ic_menu_gallery,
//        R.drawable.ic_menu_gallery,
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoGallaryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        myDatabase = MyDatabase.getDatabase(this@PhotoGalleryActivity)





        CoroutineScope(Dispatchers.IO).launch {
            imagesList = myDatabase.photosDao().readAllData()
        }.invokeOnCompletion {
            binding.viewPager
            adapter = GalleryAdapter(imagesList, itemClickListener, itemSaveClickListener)
            binding.viewPager.adapter = adapter

            if (imagesList.size < 1) {
                imagesList.add(Photos(0, 0, NO_IMAGE, ""))
            }
        }


//        binding.viewPager
//        adapter = GalleryAdapter(imagesList, itemClickListener, itemSaveClickListener)
//        binding.viewPager.adapter = adapter


    }


    private val itemClickListener: GalleryAdapter.OnItemClickListener =
        object : GalleryAdapter.OnItemClickListener {
            override fun monItemClickListener(
                position: Int, imgPos: Int, view: View, forAddBlank: Boolean
            ) {
                imagePosition = position
                photoId = imgPos


                if (forAddBlank) {
                    if (imagesList.size < 4) {
                        imagesList.add(position + 1, Photos(0, 0, NO_IMAGE, ""))
                        adapter.notifyDataSetChanged()
                        CommonUtils.showToast(view, "Swipe right.")
                    } else {
                        CommonUtils.showToast(view, "You can't add more than 4 photos.")
                    }
                } else {
                    ImagePicker.with(this@PhotoGalleryActivity)
                        .crop()                    //Crop image(Optional), Check Customization for more option
                        .compress(1024)            //Final image size will be less than 1 MB(Optional)
                        .maxResultSize(
                            1080, 1080
                        )    //Final image resolution will be less than 1080 x 1080(Optional)
                        .start()
                }
            }

        }


    private val itemSaveClickListener: GalleryAdapter.SaveClickListener =
        object : GalleryAdapter.SaveClickListener {
            override fun mSaveClickListener(
                position: Int, imagepath: String, desc: String, view: View, isforDelete: Boolean
            ) {

                if (isforDelete) {
                    imagesList.removeAt(position)
                    val itemToDelete = Photos(0, 0, NO_IMAGE, "")
                    GlobalScope.launch(Dispatchers.IO) {
                        myDatabase.photosDao().delete(itemToDelete)
                    }.invokeOnCompletion {
                        runOnUiThread {
                            adapter.notifyDataSetChanged()
                            CommonUtils.showToast(view, "Deleted Successfully..")
                        }
                    }

                } else {

                    if (imagepath == NO_IMAGE) {
                        CommonUtils.showToast(view, "Please add an Image")
                    } else if (desc.isEmpty()) {
                        CommonUtils.showToast(view, "Please Enter Caption")
                    } else {
                        val photos = Photos(imagePosition, 0, imagepath, desc)
                        CoroutineScope(Dispatchers.IO).launch {
                            myDatabase.photosDao().addPhotoData(photos)
                        }.invokeOnCompletion { CommonUtils.showToast(view, "Saved Successfully..") }
                    }
                }
            }

        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            //Image Uri will not be null for RESULT_OK
            val uri: Uri = data?.data!!

            // Use Uri object instead of File to avoid storage permissions
//            imgProfile.setImageURI(fileUri)

            if (imagePosition != -1) {
                imagesList[imagePosition].imagePath = uri.toString()
                adapter.notifyDataSetChanged()
            }


////            images.add(uri)
//            imagesList.add(imagePosition, Photos(0, 0, uri.toString(), ""))
//            adapter.notifyDataSetChanged()

//            val photos = Photos(imagePosition, 0, uri.toString(), "")
//            CoroutineScope(Dispatchers.IO).launch {
//                myDatabase.photosDao().addPhotoData(photos)
//            }

//            val photos = Photos(0, 0, uri.toString(), "")


        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
        }
    }

}