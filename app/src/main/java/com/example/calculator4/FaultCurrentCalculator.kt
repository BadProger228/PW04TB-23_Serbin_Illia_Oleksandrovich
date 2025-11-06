package com.example.calculator4

import kotlin.math.pow
import kotlin.math.sqrt

enum class TypeOfWire { Copper, Aluminium }
enum class Isolation { NoIsolate, Paper, RubberOrPlastic }

class FaultCurrentCalculator(
    private val U: Double = 10.0,

    private val wire: TypeOfWire = TypeOfWire.Aluminium,
    private val typeIsolation: Isolation = Isolation.Paper,
    private val I: Double = 2.5,
    private val t: Double = 2.5,
    private val S: Double = 1300.0,
    private val Tins: Double = 4000.0,
    private val finalTempC: Int = 250,
    private val Idyn: Double = 10.0
) {
    private var J: Double = 0.0
    private val standardSections = listOf(16,25,35,50,70,95,120,150,185,240,300,400,500)

    private fun findJ() {
        // твій код для вибору J — залишаю без змін
        when (typeIsolation) {
            Isolation.NoIsolate -> {
                if (Tins in 1000.0..2999.0) J = if (wire == TypeOfWire.Copper) 2.5 else 1.3
                else if (Tins in 3000.0..4999.0) J = if (wire == TypeOfWire.Copper) 2.1 else 1.8
                else if (Tins > 5000.0) J = if (wire == TypeOfWire.Copper) 1.8 else 1.0
            }
            Isolation.Paper -> {
                if (Tins in 1000.0..2999.0) J = if (wire == TypeOfWire.Copper) 3.0 else 1.6
                else if (Tins in 3000.0..4999.0) J = if (wire == TypeOfWire.Copper) 2.5 else 1.4
                else if (Tins > 5000.0) J = if (wire == TypeOfWire.Copper) 2.0 else 1.2
            }
            Isolation.RubberOrPlastic -> {
                if (Tins in 1000.0..2999.0) J = if (wire == TypeOfWire.Copper) 3.5 else 1.9
                else if (Tins in 3000.0..4999.0) J = if (wire == TypeOfWire.Copper) 3.1 else 1.7
                else if (Tins > 5000.0) J = if (wire == TypeOfWire.Copper) 2.7 else 1.6
            }
        }
    }

    private fun getK(): Double {
        return when (wire) {
            TypeOfWire.Copper -> when (finalTempC) {
                160 -> 115.0
                250 -> 143.0
                300 -> 152.0
                else -> 115.0
            }
            TypeOfWire.Aluminium -> when (finalTempC) {
                160 -> 76.0
                250 -> 92.0
                300 -> 100.0
                else -> 92.0
            }
        }
    }

    fun sMin(): Double {
        val k = getK()
        return (I*1000 * sqrt(t)) / k
    }

    fun currentNormal(): Double = (S / 2.0) / (sqrt(3.0) * U)
    fun currentCritical(): Double = 2.0 * currentNormal()
    fun economicalSection(): Double {
        findJ()
        return if (J != 0.0) currentNormal() / J else 0.0
    }
    fun dynamicCheck(): Boolean = currentCritical() <= Idyn
    fun chooseCableSection(): Int {
        val required = maxOf(economicalSection(), sMin())
        return standardSections.firstOrNull { it >= required } ?: standardSections.last()
    }

    fun calculate(): String {
        findJ()
        val normal = currentNormal()
        val critical = currentCritical()
        val econ = economicalSection()
        val smin = sMin()
        val chosen = chooseCableSection()

        return buildString {
            append("=== Розрахунок кабелю ===\n")
            append("Струм у нормальному режимі: %.2f A\n".format(normal))
            append("Струм у критичному режимі: %.2f A\n".format(critical))
            append("Економічний переріз: %.2f мм²\n".format(econ))
            append("Мінімальний переріз по термостійкості (Smin): %.2f мм² (K=%.1f)\n".format(smin, getK()))
            append("Динамічна стійкість: ${if (dynamicCheck()) "Виконується" else "Не виконується"}\n")
            append(">>> Рекомендований стандартний переріз: $chosen мм²\n")
        }
    }
}

class ShortCircuitCalculator(
    private val Ucn: Double = 10.5,
    private val Sk: Double = 200.0,
    private val Uk: Double = 10.5,
    private val Snom: Double = 6.3
) {

    fun Xc(): Double {
        return (Ucn * Ucn) / Sk
    }

    fun Xt(): Double {
        return (Uk * Ucn * Uk) / (100 * Snom)
    }

    fun Xtotal(): Double {
        return Xc() + Xt()
    }

    fun Ip0(): Double {
        return (Ucn * 1000) / (sqrt(3.0) * Xtotal())
    }

    fun calculate(): String {
        val xc = Xc()
        val xt = Xt()
        val xsum = Xtotal()
        val ip0 = Ip0()

        return buildString {
            append("=== Розрахунок струму КЗ (Приклад 7.2) ===\n")
            append("Xc = %.3f Ом\n".format(xc))
            append("Xt = %.3f Ом\n".format(xt))
            append("XΣ = %.3f Ом\n".format(xsum))
            append("Iп0 = %.2f A\n".format(ip0))
        }
    }
}

class KShortCircuitCalculator(ucn: Double, str: Double, uk: Double, ract: Double, ulow: Double) {

    private val U_nom = 110.0 // кВ
    private val S_tr = 63000.0 // кВА
    private val U_k_percent = 10.5 // %
    private val R_active_percent = 1.9 // %
    private val U_low = 10.0 // кВ
    private val k_tr = 115.0 / 10.0
    private val U_phase = U_low / sqrt(3.0)
    fun calculate(): String {
        val sb = StringBuilder()

        sb.appendLine("=== Розрахунок струмів короткого замикання (КЗ) ===\n")
        sb.appendLine("Вихідні дані:")
        sb.appendLine("Uн = $U_nom кВ, Sтр = $S_tr кВА, Uk = $U_k_percent %, Rактивне = $R_active_percent %, Uвн = $U_low кВ, kтр = ${"%.3f".format(k_tr)}")
        sb.appendLine()

        val R_tr = (R_active_percent / 100) * (U_nom * U_nom) / S_tr
        val X_tr = sqrt((U_k_percent / 100).pow(2) - (R_active_percent / 100).pow(2)) * (U_nom * U_nom) / S_tr

        sb.appendLine("Розрахунок опорів трансформатора (приведені до 110 кВ):")
        sb.appendLine("Rтр = ${"%.3f".format(R_tr)} Ом")
        sb.appendLine("Xтр = ${"%.3f".format(X_tr)} Ом")
        sb.appendLine()

        val R_mer = 10.65
        val X_mer_norm = 257.02
        val X_mer_min = 233.0

        val Z_mer_norm = sqrt(R_mer.pow(2) + X_mer_norm.pow(2))
        val Z_mer_min = sqrt(R_mer.pow(2) + X_mer_min.pow(2))

        sb.appendLine("Опір мережі:")
        sb.appendLine("Rмер = $R_mer Ом")
        sb.appendLine("Xмер(норм.) = $X_mer_norm Ом, Xмер(мін.) = $X_mer_min Ом")
        sb.appendLine("Zмер(норм.) = ${"%.2f".format(Z_mer_norm)} Ом")
        sb.appendLine("Zмер(мін.) = ${"%.2f".format(Z_mer_min)} Ом")
        sb.appendLine()

        val X_total_norm = X_mer_norm + X_tr
        val X_total_min = X_mer_min + X_tr
        val Z_total_norm = sqrt(R_mer.pow(2) + X_total_norm.pow(2))
        val Z_total_min = sqrt(R_mer.pow(2) + X_total_min.pow(2))

        sb.appendLine("Повні опори на шинах 10 кВ:")
        sb.appendLine("XΣ(норм.) = ${"%.2f".format(X_total_norm)} Ом, XΣ(мін.) = ${"%.2f".format(X_total_min)} Ом")
        sb.appendLine("ZΣ(норм.) = ${"%.2f".format(Z_total_norm)} Ом, ZΣ(мін.) = ${"%.2f".format(Z_total_min)} Ом")
        sb.appendLine()

        val I3_norm = (U_nom * 1000) / (sqrt(3.0) * Z_total_norm)
        val I3_min = (U_nom * 1000) / (sqrt(3.0) * Z_total_min)

        sb.appendLine("Струми трифазного короткого замикання (приведені до 110 кВ):")
        sb.appendLine("I(3)норм. = ${"%.0f".format(I3_norm)} А")
        sb.appendLine("I(3)мін. = ${"%.0f".format(I3_min)} А")
        sb.appendLine()

        val I3_norm_10 = I3_norm * k_tr
        val I3_min_10 = I3_min * k_tr

        sb.appendLine("Приведення струмів КЗ до напруги 10 кВ:")
        sb.appendLine("I(3)норм.(10кВ) = ${"%.0f".format(I3_norm_10)} А")
        sb.appendLine("I(3)мін.(10кВ) = ${"%.0f".format(I3_min_10)} А")
        sb.appendLine()

        val k_tr_line = 10.0 / 11.0

        val Z_line_norm = sqrt(R_mer.pow(2) + (X_total_norm * k_tr_line).pow(2))
        val Z_line_min = sqrt(R_mer.pow(2) + (X_total_min * k_tr_line).pow(2))

        val I3_line_norm = (U_low * 1000) / (sqrt(3.0) * Z_line_norm)
        val I3_line_min = (U_low * 1000) / (sqrt(3.0) * Z_line_min)

        sb.appendLine("Струми трифазного короткого замикання на шинах 10 кВ (фактичні):")
        sb.appendLine("I(3)норм. = ${"%.0f".format(I3_line_norm)} А")
        sb.appendLine("I(3)мін. = ${"%.0f".format(I3_line_min)} А")
        sb.appendLine()

        sb.appendLine("=== Кінець розрахунку ===")

        return sb.toString()
    }
}

