package com.example.sqlitelearning

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView;
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    //db Helper
    lateinit var dbHelper:MyDbHelper

    //orderby /or quries
    private val NEWEST_FIRST = Constants.C_ADDED_TIMESTAMP + " DESC"// "${Constants.C_ADDED_TIMESTAMP} DESC"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //init db helper
        dbHelper = MyDbHelper(this)

        loadRecords()

        //Click Floating Btn to Record activity
        addRecordBtn.setOnClickListener{
            startActivity(Intent(this,addUpdateRecordActivity::class.java))
        }
    }

    private fun loadRecords() {
        val adapterRecord = AdapterRecord(this, dbHelper.getAllRecords(NEWEST_FIRST))
        recordsRv.adapter = adapterRecord
    }

    private fun searchRecords(query:String) {
        val adapterRecord = AdapterRecord(this,dbHelper.searchRecords(query))

        recordsRv.adapter = adapterRecord
    }

    override fun onResume() {
        super.onResume()
        loadRecords()
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
        return super.onOptionsItemSelected(item)
    }
}
