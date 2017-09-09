package com.example.canopas.contact_contentprovider_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Phone

import android.content.ContentValues
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import android.provider.Contacts.People

import android.net.Uri.withAppendedPath

import android.content.ContentProviderOperation
import android.content.Intent
import android.content.OperationApplicationException
import android.os.RemoteException
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.content.ContentProviderResult
import java.util.*
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.Data
import android.provider.ContactsContract.RawContacts


class AddContact : AppCompatActivity() {

    var txt_name_: EditText? = null
    var txt_no: EditText? = null
    var add: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        txt_name_ = findViewById(R.id.add_name) as EditText?
        txt_no = findViewById(R.id.add_no) as EditText?
        add = findViewById(R.id.add) as Button?

        add?.setOnClickListener(View.OnClickListener {
            var name = txt_name_!!.text.toString()
            var no = txt_no!!.text.toString()



            if (no.isEmpty())
                Toast.makeText(this@AddContact, "enter contact no", Toast.LENGTH_SHORT).show()
            else {
               newContactADD(name, no);

            }
        })

    }
    
    fun newContactADD(name: String, no: String) {
        val ops = ArrayList<ContentProviderOperation>()
        val rawContactInsertIndex = ops.size

        ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                .withValue(RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build())
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, no)
                .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                .build())
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(Data.RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, name)
                .build())
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


}

