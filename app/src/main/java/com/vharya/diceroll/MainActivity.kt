package com.vharya.diceroll

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import com.vharya.diceroll.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private var fileJson: JSONArray = JSONArray()
    private var rollNumberHistory: MutableList<String> = ArrayList()
    private var rollDateHistory: MutableList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val viewResult = binding.result
        val viewRollButton = binding.rollButton
        val viewMaxInput = binding.maxInput
        val viewHistoryList = binding.history
        val viewNoHistory = binding.noHistory

        readRollHistoryFile()
        val historyListAdapter = AdapterHistory(this, rollNumberHistory, rollDateHistory)
        viewHistoryList.adapter = historyListAdapter

        if (historyListAdapter.isEmpty) {
            viewNoHistory.visibility = View.VISIBLE
        } else {
            viewNoHistory.visibility = View.GONE
        }

        var maxDice = 6
        var isTimerRunning = false
        val timer = object : CountDownTimer(2000, 80) { // 2s timer, 80ms tick
            override fun onTick(p0: Long) {
                isTimerRunning = true
                viewRollButton.isClickable = false

                viewResult.text = (1..maxDice).random().toString()
                viewRollButton.text = getString(R.string.rolling_dice)
            }

            override fun onFinish() {
                isTimerRunning = false
                viewRollButton.isClickable = true

                viewRollButton.text = getString(R.string.roll_button)
                if (viewMaxInput.text.toString() != "") maxDice = Integer.parseInt(viewMaxInput.text.toString())
                val result = rollDice(maxDice).toString()
                viewResult.text = result

                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm")
                val formatted = current.format(formatter)

                val item = "{\"roll_number\":${result},\"date\":\"${formatted}\"}"
                fileJson.put(item)

                val file = File(applicationContext.filesDir, "history.json")
                openFileOutput(file.name, Context.MODE_PRIVATE).use {
                    it.write(fileJson.toString().toByteArray())
                }

                readRollHistoryFile()
                historyListAdapter.updateReceiptsList(rollNumberHistory, rollDateHistory)

                if (!historyListAdapter.isEmpty) {
                    viewNoHistory.visibility = View.GONE
                }
            }
        }

        viewRollButton.setOnClickListener {
            if (!isTimerRunning) {
                timer.start()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        readRollHistoryFile()
    }

    private fun readRollHistoryFile() {
        val file = File(applicationContext.filesDir, "history.json")
        if (file.exists()) {
            var fileVal = ""
            applicationContext.openFileInput("history.json").bufferedReader().useLines {
                fileVal = it.fold("") { some, text ->
                    "$some\n$text"
                }
            }

            fileJson = JSONArray(fileVal)

            rollNumberHistory.clear()
            rollDateHistory.clear()

            for (item in 0 until fileJson.length()) {
                val rollNumber = JSONObject(fileJson.getString(item)).getInt("roll_number")
                val rollDate = JSONObject(fileJson.getString(item)).getString("date")

                rollNumberHistory.add("Rolled a $rollNumber")
                rollDateHistory.add(rollDate)
            }

            rollNumberHistory = rollNumberHistory.reversed().toMutableList()
            rollDateHistory = rollDateHistory.reversed().toMutableList()
        }
    }

    private fun rollDice(max: Int) = (1..max).random()
}