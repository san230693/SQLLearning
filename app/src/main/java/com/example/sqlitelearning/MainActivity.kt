package com.example.sqlitelearning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //db Helper
    lateinit var dbHelper:MyDbHelper

    //orderby /or quries
    private val NEWEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} DESC"
    private val OLDEST_FIRST = "${Constants.C_ADDED_TIMESTAMP} ASC"
    private val TITLE_ASC = "${Constants.C_NAME} ASC"
    private val TITLE_DESC = "${Constants.C_NAME} DESC"

    private var recentSortOrder = NEWEST_FIRST

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init db helper
        dbHelper = MyDbHelper(this)

        loadRecords(NEWEST_FIRST)  //By default load newest first

        //Click Floating Btn to Record activity
        addRecordBtn.setOnClickListener{
            val intent = Intent(this,addUpdateRecordActivity::class.java)
            intent.putExtra("isEditMode",false) // want to add new record ,set it false
            startActivity(intent)
            //startActivity(Intent(this,addUpdateRecordActivity::class.java))
        }
    }

    private fun loadRecords(orderBy:String) {
        recentSortOrder = orderBy
        val adapterRecord = AdapterRecord(this, dbHelper.getAllRecords(orderBy))
        recordsRv.adapter = adapterRecord
    }

    private fun searchRecords(query:String) {
        val adapterRecord = AdapterRecord(this,dbHelper.searchRecords(query))

        recordsRv.adapter = adapterRecord
    }

    private fun sortDialoge() {
        //option to display in dialoge
        val options = arrayOf("Name Ascending","Name Desending","NEWEST","OLDEST")
        //dialoge
        val builder:AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Sort By")
            .setItems(options){_, which ->
                //handle items
                if (which == 0){
                    //ascending
                    loadRecords(TITLE_ASC)
                }
                else if (which == 1)
                {
                    //desecnding
                    loadRecords(TITLE_DESC)
                }
                else if (which == 2){
                    //newest
                    loadRecords(NEWEST_FIRST)
                }
                else if (which == 3){
                    //oldest
                    loadRecords(OLDEST_FIRST)
                }
            }
            .show()
    }

    public override fun onResume() {
        super.onResume()
        loadRecords(recentSortOrder)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        //inflate menu
        menuInflater.inflate(R.menu.menu_main,menu)

        //searchView
        val item = menu.findItem(R.id.action_search)
        val searchView = item.actionView as SearchView

        searchView.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                //search as you type
                if (newText != null) {
                    searchRecords(newText)
                }
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    searchRecords(query)
                }
                return true
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //handle menu items clicks
        val id = item.itemId
        if (id == R.id.action_sort){
            sortDialoge()
        }else if (id == R.id.action_deleteall){
            //delete all records
            dbHelper.deleteAllRecords()
            onResume()
        }
        return super.onOptionsItemSelected(item)
    }


}
