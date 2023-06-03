package woowacourse.shopping.presentation.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import woowacourse.shopping.databinding.CustomCounterQuantityBinding

class QuantityCounter(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {
    private var layoutInflater: LayoutInflater? = null
    private lateinit var binding: CustomCounterQuantityBinding
    private var minValue: Int? = null
    private var maxValue: Int? = null
    val currentQuantity: Int get() = binding.textCounterQuantity.text.toString().toInt()

    init {
        initView()
    }

    private fun initView() {
        layoutInflater ?: run {
            layoutInflater = LayoutInflater.from(context)
            binding = CustomCounterQuantityBinding.inflate(layoutInflater!!, this, true)
        }
        binding.textCounterQuantity.text = DEFAULT_VALUE.toString()
    }

    fun setIncreaseClickListener(action: () -> Unit) {
        binding.buttoncounterIncrease.setOnClickListener {
            setQuantityText(currentQuantity + 1)
            action()
        }
    }

    fun setDecreaseClickListener(action: () -> Unit) {
        binding.buttonCounterDecrease.setOnClickListener {
            setQuantityText(currentQuantity - 1)
            action()
        }
    }

    fun setQuantityText(quantity: Int) {
        if (minValue != null && quantity < minValue!!) return
        binding.textCounterQuantity.text = quantity.toString()
        binding.buttonCounterDecrease.isEnabled = quantity != minValue
        binding.buttoncounterIncrease.isEnabled = quantity != maxValue
    }

    fun setMinValue(min: Int) {
        minValue = min
        binding.textCounterQuantity.text = minValue.toString()
    }

    fun setMaxValue(max: Int) {
        maxValue = max
    }

    fun setTextSize(size: Int) {
        binding.textCounterQuantity.textSize = size.toFloat()
        binding.buttonCounterDecrease.textSize = size.toFloat()
        binding.buttoncounterIncrease.textSize = size.toFloat()
    }

    companion object {
        private const val DEFAULT_VALUE = 1
    }
}
