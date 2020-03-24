package com.example.sqlitelearning

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//Adaper class for review
class AdapterRecord() : RecyclerView.Adapter<AdapterRecord.HolderRecord>() {

    private var context:Context?=null
    private var recordList:ArrayList<ModelRecord>?=null

    lateinit var dbHelper:MyDbHelper

    constructor(context: Context?,recordList: ArrayList<ModelRecord>?) : this(){
        this.context = context
        this.recordList = recordList

        dbHelper = MyDbHelper(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderRecord {
        //inflate the layout reo_record.xml
        return HolderRecord(
            LayoutInflater.from(context).inflate(R.layout.row_record,parent,false)
        )
    }

    override fun getItemCount(): Int {
        //return items/records/list size
        return recordList!!.size
    }

    override fun onBindViewHolder(holder: HolderRecord, position: Int) {
        //get data,set data,handle clicks

        //get data
        val model = recordList!!.get(position)

        val id = model.id
        val name = model.name
        val image = model.image
        val bio = model.bio
        val phone = model.phone
        val email = model.email
        val dob = model.dob
        val addedTime = model.addedTime
        val updatedTime = model.updatedTime

        holder.nameTv.text = name
        holder.phoneTv.text = phone
        holder.emailTv.text = email
        holder.dobTv.text = dob

        //if user does not attach image then image uri will be null ,so set default image
        if (image == "null"){
            holder.profileTv.setImageResource(R.drawable.ic_person_black)
        }
        else
        {
            //have image in record
            holder.profileTv.setImageURI(Uri.parse(image))
        }

        //show record in new activity on clicking record
        holder.itemView.setOnClickListener{
            //pass is to next activity to show record
            val intent = Intent(context,RecordDetailActivity::class.java)
            intent.putExtra("RECORD_ID",id)
            context!!.startActivity(intent)
        }

        //handle more btn click show edit /show option show
        holder.moreBtn.setOnClickListener{
            //Show more options
            showMoreOptions(
                position,
                id,
                name,
                phone,
                email,
                dob,
                bio,
                image,
                addedTime,
                updatedTime
            )
        }
    }

    private fun showMoreOptions(
        position: Int,
        id: String,
        name: String,
        phone: String,
        email: String,
        dob: String,
        bio: String,
        image: String,
        addedTime: String,
        updatedTime: String
    ) {
    //option to display in disloge
        val options = arrayOf("Edit","Delete")
        //dialog
        val dialog:AlertDialog.Builder = AlertDialog.Builder(context)
        //set items and click listener
        dialog.setItems(options) { dialog, which ->
            //handle item click
            if (which == 0){
                //edit clicked
                val intent = Intent(context,addUpdateRecordActivity::class.java)
                intent.putExtra("ID",id)
                intent.putExtra("NAME",name)
                intent.putExtra("PHONE",phone)
                intent.putExtra("EMAIL",email)
                intent.putExtra("DOB",dob)
                intent.putExtra("BIO",bio)
                intent.putExtra("IMAGE",image)
                intent.putExtra("ADDED_TIME",addedTime)
                intent.putExtra("UPDATED_TIME",updatedTime)
                intent.putExtra("isEditMode",true)
                context!!.startActivity(intent)
            }
            else
            {
                //delete clicked

                dbHelper.deleteRecord(id)
                //refresh record by calling activities onResume method
                (context as MainActivity)!!.onResume();
            }
        }
        dialog.show()
    }

    inner class HolderRecord(itemView: View): RecyclerView.ViewHolder(itemView) {

        var profileTv : ImageView = itemView.findViewById(R.id.profileIv)
        var nameTv : TextView = itemView.findViewById(R.id.nameTv)
        var phoneTv : TextView = itemView.findViewById(R.id.phoneTv)
        var emailTv : TextView = itemView.findViewById(R.id.emailTv)
        var dobTv : TextView = itemView.findViewById(R.id.dobTv)
        var moreBtn : ImageButton = itemView.findViewById(R.id.moreBtn)

    }

}