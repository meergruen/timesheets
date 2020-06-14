package com.meergruen.time_sheets

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File

fun sendFileByMail(context: Context, file: File, emailAddress: String, subject: String){
    val uri = Uri.fromFile(file) // not use cache directory but external
    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.type = "vnd.android.cursor.dir/email"
    emailIntent.data = Uri.parse("mailto:$emailAddress")

    //String to[] = {"asd@gmail.com"};
    //emailIntent .putExtra(Intent.EXTRA_EMAIL, to);

    emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
    context.startActivity(emailIntent)

   // context.startActivity(Intent.createChooser(emailIntent , "Send email..."))

}
