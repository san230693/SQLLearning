package com.example.sqlitelearning

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.ActionBar
import kotlinx.android.synthetic.main.activity_record_detail.*
import kotlinx.android.synthetic.main.row_record.*
import kotlinx.android.synthetic.main.row_record.nameTv
import java.text.DateFormat
import java.util.*

class RecordDetailActivity : AppCompatActivity() {

    //actionBar
    private var actionBar:ActionBar?=null

    //dbHelper
    private var dbHelper:MyDbHelper?=null

    private var recordId:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_detail)

        //setting up action bar
        actionBar = supportActionBar
        actionBar!!.title = "Record Details"
        actionBar!!.setDisplayShowHomeEnabled(true)
        actionBar!!.setDisplayHomeAsUpEnabled(true)

        //init db helper
        dbHelper = MyDbHelper(this)

        //get record id from intent
        val intent = intent
        recordId = intent.getStringExtra("RECORD_ID")

        showRecordDetails()
    }

    private fun showRecordDetails() {
        //get record detail

        val selectQuery =
            "SELECT * FROM ${Constants.TABLE_NAME}WHERE${Constants.C_ID}=\"$recordId\""

        val db = dbHelper!!.writableDatabase
        val cursor = db.rawQuery(selectQuery,null)

        if (cursor.moveToFirst()){
            do {
                val id =  ""+cursor.getInt(cursor.getColumnIndex(Constants.C_ID))
                val name = ""+cursor.getString(cursor.getColumnIndex(Constants.C_NAME))
                val image =""+cursor.getString(cursor.getColumnIndex(Constants.C_IMAGE))
                val bio =""+cursor.getString(cursor.getColumnIndex(Constants.C_BIO))
                val phone =""+cursor.getString(cursor.getColumnIndex(Constants.C_PHONE))
                val email =""+cursor.getString(cursor.getColumnIndex(Constants.C_EMAIL))
                val dob =""+cursor.getString(cursor.getColumnIndex(Constants.C_DOB))
                val addedTimeStamp =""+cursor.getString(cursor.getColumnIndex(Constants.C_ADDED_TIMESTAMP))
                val updatedTimeStamp = ""+cursor.getString(cursor.getColumnIndex(Constants.C_UPDATED_TIMESTAMP))

                //cover time stamp to dd/mm/yyyy e.g 12/04/2020 03:21 PM
                val calender1 = Calendar.getInstance(Locale.getDefault())
                calender1.timeInMillis = addedTimeStamp.toLong()
                val timeAdded = android.text.format.DateFormat.format("dd/mm/yyy hh:mm aa",calender1)

                val calender2 = Calendar.getInstance(Locale.getDefault())
                calender2.timeInMillis = updatedTimeStamp.toLong()
                val timeUpdated = android.text.format.DateFormat.format("dd/mm/yyy hh:mm aa",calender2)

                //setData
                nameTv1.text = name
                bioTv1.text = bio
                phoneTv1.text = phone
                emailTv1.text = email
                dobTv1.text = dob
                addedDateTv1.text = timeAdded
                updatedDateTv1.text = timeUpdated

                //if user does not attach image then image uri will be null ,so set default image
                if (image == "null"){
                    profileIv1.setImageResource(R.drawable.ic_person_black)
                }
                else
                {
                    //have image in record
                    profileIv1.setImageURI(Uri.parse(image))
                }
            }while (cursor.moveToNext())
        }
        //db close
        db.close()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}
