package com.example.calculator4

//package com.example.calculator4.ui.faultcurrent

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.calculator4.FaultCurrentCalculator
import com.example.calculator4.R
import com.example.calculator4.TypeOfWire
import com.example.calculator4.Isolation

class FaultCurrentFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_fault_current, container, false)

        val inputI = view.findViewById<EditText>(R.id.inputI)
        val inputT = view.findViewById<EditText>(R.id.inputT)
        val inputS = view.findViewById<EditText>(R.id.inputS)
        val inputTins = view.findViewById<EditText>(R.id.inputTins)
        val resultText = view.findViewById<TextView>(R.id.resultText)
        val calculateButton = view.findViewById<Button>(R.id.calculateButton)

        calculateButton.setOnClickListener {
            try {
                val i = inputI.text.toString().toDouble()
                val t = inputT.text.toString().toDouble()
                val s = inputS.text.toString().toDouble()
                val tins = inputTins.text.toString().toDouble()

                val calculator = FaultCurrentCalculator(
                    wire = TypeOfWire.Copper,
                    typeIsolation = Isolation.RubberOrPlastic,
                    I = i,
                    t = t,
                    S = s,
                    Tins = tins
                )

                resultText.text = calculator.calculate()

            } catch (e: Exception) {
                resultText.text = "⚠️ Введіть правильні числові значення."
            }
        }

        return view
    }
}
