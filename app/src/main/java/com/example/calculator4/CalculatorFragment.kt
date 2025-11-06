package com.example.calculator4.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.calculator4.FaultCurrentCalculator
import com.example.calculator4.Isolation
import com.example.calculator4.R
import com.example.calculator4.TypeOfWire

class CalculatorFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_calculator, container, false)

        val editI = view.findViewById<EditText>(R.id.editI)
        val editT = view.findViewById<EditText>(R.id.editT)
        val editS = view.findViewById<EditText>(R.id.editS)
        val editTins = view.findViewById<EditText>(R.id.editTins)
        val editTemp = view.findViewById<EditText>(R.id.editTemp)
        val editIdyn = view.findViewById<EditText>(R.id.editIdyn)

        val spinnerWire = view.findViewById<Spinner>(R.id.spinnerWire)
        val spinnerIsolation = view.findViewById<Spinner>(R.id.spinnerIsolation)

        val btnCalc = view.findViewById<Button>(R.id.btnCalc)
        val txtResult = view.findViewById<TextView>(R.id.txtResult)

        spinnerWire.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            TypeOfWire.values().map {
                when (it) {
                    TypeOfWire.Copper -> "Мідний"
                    TypeOfWire.Aluminium -> "Алюмінієвий"
                }
            }
        )

        spinnerIsolation.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            Isolation.values().map {
                when (it) {
                    Isolation.NoIsolate -> "Без ізоляції"
                    Isolation.Paper -> "Паперова"
                    Isolation.RubberOrPlastic -> "Гума / Пластик"
                }
            }
        )

        btnCalc.setOnClickListener {
            try {
                val calculator = FaultCurrentCalculator(
                    wire = when (spinnerWire.selectedItem.toString()) {
                        "Мідний" -> TypeOfWire.Copper
                        else -> TypeOfWire.Aluminium
                    },
                    typeIsolation = when (spinnerIsolation.selectedItem.toString()) {
                        "Без ізоляції" -> Isolation.NoIsolate
                        "Паперова" -> Isolation.Paper
                        else -> Isolation.RubberOrPlastic
                    },
                    I = editI.text.toString().toDouble(),
                    t = editT.text.toString().toDouble(),
                    S = editS.text.toString().toDouble(),
                    Tins = editTins.text.toString().toDouble(),
                    finalTempC = editTemp.text.toString().toInt(),
                    Idyn = editIdyn.text.toString().toDouble()
                )

                txtResult.text = calculator.calculate()

            } catch (e: Exception) {
                txtResult.text = "❌ Помилка: перевір введення даних."
            }
        }

        return view
    }
}
