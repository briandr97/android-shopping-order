package woowacourse.shopping.presentation.ui.productDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.defaultRepository.DefaultProductRepository
import woowacourse.shopping.data.defaultRepository.DefaultRecentlyViewedRepository
import woowacourse.shopping.data.defaultRepository.DefaultShoppingCartRepository
import woowacourse.shopping.data.local.recentlyViewed.RecentlyViewedDao
import woowacourse.shopping.data.remote.product.ProductRemoteDataSource
import woowacourse.shopping.data.remote.shoppingCart.ShoppingCartRemoteDataSource
import woowacourse.shopping.databinding.ActivityProductDetailBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyViewedProduct

class ProductDetailActivity : AppCompatActivity(), ProductDetailContract.View {
    private lateinit var binding: ActivityProductDetailBinding
    override lateinit var presenter: ProductDetailPresenter
    private fun initPresenter(productId: Long): ProductDetailPresenter {
        return ProductDetailPresenter(
            this,
            productId,
            DefaultProductRepository(ProductRemoteDataSource()),
            DefaultRecentlyViewedRepository(RecentlyViewedDao(this)),
            DefaultShoppingCartRepository(ShoppingCartRemoteDataSource()),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = initPresenter(intent.getLongExtra(PRODUCT_ID, DEFAULT_ID))
        setClickEvent()
    }

    override fun setBindingData(product: Product, lastViewedProduct: RecentlyViewedProduct) {
        binding.product = product
        binding.lastViewedProduct = lastViewedProduct
    }

    override fun handleNoSuchProductError() {
        Toast.makeText(this, "상품을 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setClickEvent() {
        clickClose()
        clickShoppingCart()
        clickLastViewedProduct()
    }

    private fun clickClose() {
        binding.buttonProductDetailClose.setOnClickListener { finish() }
    }

    private fun clickShoppingCart() {
        binding.buttonProductDetailPutInShoppingCart.setOnClickListener {
            /*presenter.addProductInCart()
            startActivity(Intent(this, ShoppingCartActivity::class.java))*/
            OrderDialog.makeDialog(binding.product?.id ?: -1).show(supportFragmentManager, "order")
            // finish()
        }
    }

    private fun clickLastViewedProduct() {
        binding.layoutLastViewedProudct.setOnClickListener {
            val id: Long = binding.lastViewedProduct?.id ?: return@setOnClickListener
            val intent = getIntent(this, id)
            startActivity(intent)
            finish()
        }
    }

    companion object {
        private const val DEFAULT_ID: Long = 0
        private const val PRODUCT_ID = "productId"
        fun getIntent(context: Context, productId: Long): Intent {
            return Intent(context, ProductDetailActivity::class.java).apply {
                putExtra(PRODUCT_ID, productId)
            }
        }
    }
}
