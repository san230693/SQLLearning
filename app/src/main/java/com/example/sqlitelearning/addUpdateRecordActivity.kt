package com.example.sqlitelearning

import android.app.Activity
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_add_update_record.*
import java.util.jar.Manifest

class addUpdateRecordActivity : AppCompatActivity() {

    private val CAMERA_REQUEST_CODE = 100
    private val STORAGE_REQUEST_CODE = 101
    //image pick constants
    private val  IMAGE_PICK_CAMERA_CODE = 102
    private val  IMAGE_PICK_GALLERY_CODE = 103

    private lateinit var CameraPermissions:Array<String> //camera and storage
    private lateinit var StoragePermissions:Array<String> //only storage

    private var imageuri:Uri? = null
    private var id:String? = ""
    private var name:String? = ""
    private var phone:String? = ""
    private var email:String? = ""
    private var dob:String? = ""
    private var bio:String? = ""
    private var addedTime:String? = ""
    private var updatedTime:String? = ""

    private var isEditMode = false

    //actionbar
    private var actionBar:ActionBar? = null

    lateinit var dbHelper : MyDbHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_record)

        //init actionbar
        actionBar = supportActionBar
        actionBar!!.title ="Add Record"
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        actionBar!!.setDisplayShowHomeEnabled(true)

        //get data from intent
        val intent = intent
        isEditMode = intent.getBooleanExtra("isEditMode",false)
        if (isEditMode){
            //editing data came here from adapter
            actionBar!!.title = "Update Record"

            id = intent.getStringExtra("ID")
            name = intent.getStringExtra("NAME")
            phone = intent.getStringExtra("PHONE")
            email = intent.getStringExtra("EMAIL")
            dob = intent.getStringExtra("DOB")
            bio = intent.getStringExtra("BIO")
            imageuri = Uri.parse(intent.getStringExtra("IMAGE"))
            addedTime = intent.getStringExtra("ADDED_TIME")
            updatedTime = intent.getStringExtra("UPDATED_TIME")

            //set data to view
            //if user didnot attach image  while saving record then
            //then image uri will be "null",so set default image
            if (imageuri.toString() == "null"){
                //no image
                profileIv.setImageResource(R.drawable.ic_person_black)
            }
            else
            {
                //image have
                profileIv.setImageURI(imageuri)
            }
            nameEt.setText(name)
            phoneEt.setText(phone)
            emailEt.setText(email)
            dobEt.setText(dob)
            bioEt.setText(bio)
        }
        else
        {
            //adding new data came here from mainactivity
            actionBar!!.title = "Add Record"
        }

        //init db helper class
        dbHelper = MyDbHelper(this)

        //init permission array
        CameraPermissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        StoragePermissions = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE )

        //click Image VIew to pick image
        profileIv.setOnClickListener{
            //Image pick dialoge
            imagePickDialoge()
        }

        saveBtn.setOnClickListener{
            inputData()
        }

    }

    private fun inputData() {
        //get Data
        name = "" + nameEt.text.toString().trim()
        phone = "" + phoneEt.text.toString().trim()
        email = "" + emailEt.text.toString().trim()
        dob = "" + dobEt.text.toString().trim()
        bio = "" + bioEt.text.toString().trim()

        if (isEditMode){
            //editing
            val timeStamp = "${System.currentTimeMillis()}"
            dbHelper?.updateRecord(
                "$id",
                "$name",
                "$imageuri",
                "$bio",
                "$phone",
                "$email",
                "$dob",
                "$addedTime",
                "$updatedTime"
            )

            Toast.makeText(this,"Updated...",Toast.LENGTH_SHORT).show()
        }
        else
        {
            //save data to db
            val timeStamp = System.currentTimeMillis()
            val id =  dbHelper.insertRecord(
                ""+name,
                ""+imageuri,
                ""+bio,
                ""+phone,
                ""+email,
                ""+dob,
                ""+timeStamp,
                ""+timeStamp)

            Toast.makeText(this,"Record added against ID $id",Toast.LENGTH_SHORT).show()
        }

    }

    private fun imagePickDialoge() {
        //Option to choose
        val options = arrayOf("Camera","Gallery")
        //Dialoge
        val builder = AlertDialog.Builder(this)
        //title
        builder.setTitle("Pick Image From")
        //set Items Options
        builder.setItems(options){dialog, which ->  
            //handle item clicks
            if (which == 0){
                //camera Clicked
                if (!checkCameraPermissions()){
                    //Permissions not granted
                    requestCameraPermissions()
                }
                else{
                    //Permissions already granted
                    pickFromCamera()
                }
            }
            else
            {
                //Gallery Clicked
                if (!checkStoragePermissions()){
                    //Permissions not granted
                    requestStoragePermissions()
                }
                else
                {
                    //Permissions already granted
                    pickFromGallery()
                }
            }
        }
        //show dialoge
        builder.show()

    }

    private fun requestStoragePermissions() {
        ActivityCompat.requestPermissions(this,StoragePermissions,STORAGE_REQUEST_CODE)
    }

    private fun pickFromGallery() {
        //pick image from intent
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*" //only image to be picked
        startActivityForResult(galleryIntent,IMAGE_PICK_GALLERY_CODE)
    }

    private fun checkStoragePermissions(): Boolean {
        //Check if storage permission enable or not
        return ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    }

    private fun pickFromCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE,"Image Title")
        values.put(MediaStore.Images.Media.DESCRIPTION,"Image Description")
        //put image uri
        imageuri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values)
        //intent to open camera
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,imageuri)
        startActivityForResult(cameraIntent,IMAGE_PICK_CAMERA_CODE)
    }

    private fun requestCameraPermissions() {
        //request the camera permission
        ActivityCompat.requestPermissions(this,CameraPermissions,CAMERA_REQUEST_CODE)
    }

    private fun checkCameraPermissions(): Boolean {
        //Check if camera permission (Camera and Storage) is enable or not
        val results = ContextCompat.checkSelfPermission(this
        ,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        val results1 = ContextCompat.checkSelfPermission(this
            ,android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

        return results && results1
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed() //Go back to previous Avtivity
        return super.onSupportNavigateUp()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()){
                    //if allowed returns true otherwise false
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val StorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                    if (cameraAccepted && StorageAccepted){
                        pickFromCamera()
                    }else{
                        Toast.makeText(this,"Camera and storage permissions are required",Toast.LENGTH_SHORT).show()
                    }
                }
            }
            STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()){
                    val StorageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (StorageAccepted){
                        pickFromGallery()
                    }else
                    {
                        Toast.makeText(this,"storage permissions is required",Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //image picked from camera or gallery will be received here
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            //image is picked
            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //picked from gallery
                //crop Image
                CropImage.activity(imageuri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE)
            {
                //picked from Camera
                //crop Image
                CropImage.activity(data!!.data)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .start(this)
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
                //Cropped Image received
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK){
                    val resultUri = result.uri
                    imageuri = resultUri
                    //set Image URI
                    profileIv.setImageURI(resultUri)
                }
                else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    //error
                    val error = result.error
                    Toast.makeText(this,"$error",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}
