package com.xujichang.example

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import java.lang.IllegalArgumentException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

class NewActivity : AppCompatActivity() {
    private val errorCreated = AtomicBoolean(false)
    private var contextA: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new)
        val random = Random.nextInt(0, 2)
        if (random % 2 == 0) {
            performError()
        }
    }

    private fun performError() {
        if (!errorCreated.get()) {
            contextA!!.resources.getString(R.string.app_name)
            errorCreated.set(true)
        }
    }

    override fun onResume() {
        super.onResume()
        val random = Random.nextInt(0, 2)
        if (random % 2 == 0) {
            performError()
        }
    }
}
