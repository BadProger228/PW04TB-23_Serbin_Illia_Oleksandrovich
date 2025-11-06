package com.example.myapp.ui.kshortcircuit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.calculator4.KShortCircuitCalculator
import com.example.calculator4.R

class ShortCircuitFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_k_short_circuit, container, false)

        val inputUcn = view.findViewById<EditText>(R.id.inputUcn)
        val inputStr = view.findViewById<EditText>(R.id.inputStr)
        val inputUk = view.findViewById<EditText>(R.id.inputUk)
        val inputRact = view.findViewById<EditText>(R.id.inputRact)
        val inputUlow = view.findViewById<EditText>(R.id.inputUlow)
        val btn = view.findViewById<Button>(R.id.calculateButton)
        val resultText = view.findViewById<TextView>(R.id.resultText)

        btn.setOnClickListener {
            try {
                val ucn = inputUcn.text.toString().toDouble()
                val str = inputStr.text.toString().toDouble()
                val uk = inputUk.text.toString().toDouble()
                val ract = inputRact.text.toString().toDouble()
                val ulow = inputUlow.text.toString().toDouble()

                val calc = KShortCircuitCalculator(ucn, str, uk, ract, ulow)
                resultText.text = calc.calculate()

            } catch (ex: Exception) {
                resultText.text = "Помилка вхідних даних — перевір числові значення."
            }
        }

        return view
    }
}
