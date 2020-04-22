package edu.stanford.iogalle.tippy

import android.animation.ArgbEvaluator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val TAG = "MainActivity"
private const val INITIAL_TIP_PERCENT = 15

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        seekBarTip.progress = INITIAL_TIP_PERCENT
        tvTipPercent.text = "$INITIAL_TIP_PERCENT%"
        updateTipDescription(INITIAL_TIP_PERCENT)
        seekBarTip.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.i(TAG, "onProgressChanged $progress")
                tvTipPercent.text = "$progress%"
                updateTipDescription(progress)
                computeTipAndTotal()
                computePerGuest()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}

        })

        etBase.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterBaseTextChanged $s")
                computeTipAndTotal()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        etGuests.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.i(TAG, "afterGuestsTextChanged $s")
                computePerGuest()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun updateTipDescription(tipPercent: Int) {
        val tipDescription : String
        when (tipPercent) {
            in 0..9 -> tipDescription = "Poor \u2639\uFE0F"
            in 10..14 -> tipDescription = "Acceptable \uD83D\uDE10"
            in 15..19 -> tipDescription =  "Good \uD83D\uDE42"
            in 20..24 -> tipDescription = "Great \uD83D\uDE00"
            else -> tipDescription = "Amazing \uD83D\uDE01"
        }
        tvTipDescription.text = tipDescription
        val color = ArgbEvaluator().evaluate(tipPercent.toFloat() / seekBarTip.max,
            ContextCompat.getColor(this, R.color.colorWorstTip),
            ContextCompat.getColor(this, R.color.colorBestTip)
        ) as Int
        tvTipDescription.setTextColor(color)
    }

    private fun computeTipAndTotal() {
        // Get the value of the base and tip percent
        if (etBase.text.isEmpty()) {
            tvTipAmount.text = ""
            tvTotalAmount.text = ""
            return
        }
        val baseAmount = etBase.text.toString().toDouble()
        val tipPercent = seekBarTip.progress
        val tipAmount = baseAmount * tipPercent / 100
        val totalAmount = baseAmount + tipAmount
        tvTipAmount.text = "$%.2f".format(tipAmount)
        tvTotalAmount.text = "$%.2f".format(totalAmount)
    }

    private fun computePerGuest() {
        // Get the value of the base and number of guests
        if (etBase.text.isEmpty()) {
            tvAmountPerGuest.text = ""
            return
        }

        if (etGuests.text.isEmpty() || etGuests.text.toString().toInt() == 0) {
            tvAmountPerGuest.text = tvTotalAmount.text
            return
        }

        val totalAmount = tvTotalAmount.text.toString().removePrefix("$").toDouble()
        val perGuest = totalAmount / etGuests.text.toString().toInt()
        tvAmountPerGuest.text = "$%.2f".format(perGuest)
    }
}
