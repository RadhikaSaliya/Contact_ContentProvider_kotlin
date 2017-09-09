package com.example.canopas.contact_contentprovider_kotlin

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast
import com.example.canopas.contact_contentprovider_kotlin.MainActivity
import android.R.id.button1
import android.content.ContentProviderOperation
import android.content.DialogInterface
import android.net.Uri
import android.widget.AdapterView
import android.widget.AdapterView.OnItemLongClickListener
import com.example.canopas.contact_contentprovider_kotlin.R.id.listview


class MainActivity : AppCompatActivity() {
    var list: ListView? = null
    var fab: FloatingActionButton? = null
    var arr_list: ArrayList<Contact>? = null
    var deleteName: String? = null
    var deleteNum: String? = null
    var cust_adptr: Cust_adptr? = null
    var cur: Cursor? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list = findViewById(R.id.listview) as ListView?
        fab = findViewById(R.id.fab) as FloatingActionButton?
        arr_list = ArrayList<Contact>()

        EnableRuntimePermission()
        LoadContact().execute()

        fab?.setOnClickListener(View.OnClickListener {
            var intent = Intent(this, AddContact::class.java)
            startActivity(intent)

        })
        list!!.setOnItemLongClickListener { adapterView, view, pos, l ->
            deleteName = arr_list!!.get(pos).name
            deleteNum = arr_list!!.get(pos).contact_no

            AlertDialog.Builder(this)
                    .setMessage("delete ?")
                    .setPositiveButton("yes", DialogInterface.OnClickListener { dialogInterface, i ->
                      
                        DeleteContact(deleteName, deleteNum)

                    })
                    .setNegativeButton("no", DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.dismiss()
                    })
                    .show()

            true
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun DeleteContact(deleteName: String?, deleteNum: String?) {
        var contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(deleteNum));
        cur = contentResolver.query(contactUri, null, null, null, null);
        try {
            if (cur!!.moveToNext()) {
                do {
                    if (cur!!.getString(cur!!.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equals(deleteName)) {
                        var key = cur!!.getString(cur!!.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        var uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, key);
                        getContentResolver().delete(uri, null, null);
                        Toast.makeText(this, "delete successfully", Toast.LENGTH_LONG).show();
                        startActivity(intent)
                    }
                } while (cur!!.moveToNext());


            }
        } catch (e: Exception) {
            e.printStackTrace();
        }

    }

    fun EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CONTACTS)) {

            Toast.makeText(this, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.CAMERA),
                    1);
        } else {

            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(
                    Manifest.permission.READ_CONTACTS), 1);

        }
    }


    inner class LoadContact : AsyncTask<Void, Void, Void>() {
        var pd: ProgressDialog? = null
        override fun onPreExecute() {
            super.onPreExecute()

            pd = ProgressDialog(this@MainActivity)
            pd!!.show()

        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            pd!!.dismiss()
            cust_adptr = Cust_adptr(this@MainActivity, arr_list)
            list!!.adapter = cust_adptr
        }

        override fun doInBackground(vararg p0: Void?): Void? {

            var cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)

            while (cursor.moveToNext()) {

                var name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                var phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                arr_list!!.add(Contact(name, phonenumber));

            }

            cursor.close();
            return null
        }


    }

    class Contact {
        var name: String? = null
        var contact_no: String? = null

        constructor(name: String, contact_no: String) {
            this.name = name
            this.contact_no = contact_no
        }
    }


    class Cust_adptr(internal var c: Context, internal var arr_list: ArrayList<MainActivity.Contact>?) : BaseAdapter() {
        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View? {
            var view = view
            view = LayoutInflater.from(c).inflate(R.layout.rowdesign, viewGroup, false)
            var name = view.findViewById<TextView>(R.id.name)
            var contact_no = view.findViewById<TextView>(R.id.contact_no)
            name?.text = arr_list!![i].name
            contact_no.text = arr_list!![i].contact_no
            return view
        }

        override fun getItem(i: Int) = arr_list!!.get(i)


        override fun getCount() = arr_list!!.size

    }
}
