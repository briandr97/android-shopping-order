package woowacourse.shopping.presentation.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.TestServer
import woowacourse.shopping.data.ProductService
import woowacourse.shopping.data.product.ProductDao
import woowacourse.shopping.data.product.ProductRepositoryImpl
import woowacourse.shopping.data.product.recentlyViewed.RecentlyViewedDao
import woowacourse.shopping.data.product.recentlyViewed.RecentlyViewedRepositoryImpl
import woowacourse.shopping.data.shoppingCart.ShoppingCartDao
import woowacourse.shopping.data.shoppingCart.ShoppingCartRepositoryImpl
import woowacourse.shopping.databinding.ActivityHomeBinding
import woowacourse.shopping.domain.model.RecentlyViewedProduct
import woowacourse.shopping.presentation.model.HomeData
import woowacourse.shopping.presentation.ui.home.adapter.GridWeightLookedUp
import woowacourse.shopping.presentation.ui.home.adapter.HomeAdapter
import woowacourse.shopping.presentation.ui.home.adapter.ProductClickListener
import woowacourse.shopping.presentation.ui.home.adapter.RecentlyViewedProductAdapter
import woowacourse.shopping.presentation.ui.productDetail.ProductDetailActivity
import woowacourse.shopping.presentation.ui.shoppingCart.ShoppingCartActivity

class HomeActivity : AppCompatActivity(), HomeContract.View, ProductClickListener {
    private lateinit var binding: ActivityHomeBinding
    override val presenter: HomeContract.Presenter by lazy { initPresenter() }
    private val homeAdapter: HomeAdapter by lazy {
        HomeAdapter(
            recentlyViewedProductAdapter,
            this,
            ::clickShowMore,
            presenter::updateProductQuantity,
        )
    }
    private val recentlyViewedProductAdapter: RecentlyViewedProductAdapter by lazy {
        RecentlyViewedProductAdapter(this)
    }

    private fun initPresenter(): HomePresenter {
        return HomePresenter(
            this,
            ProductRepositoryImpl(
                productDataSource = ProductService(),
                shoppingCartDataSource = ShoppingCartDao(this),
            ),
            RecentlyViewedRepositoryImpl(
                productDataSource = ProductDao(this),
                recentlyViewedDataSource = RecentlyViewedDao(this),
            ),
            ShoppingCartRepositoryImpl(
                shoppingCartDataSource = ShoppingCartDao(this),
                productDataSource = ProductDao(this),
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //
        val skeletonAnim: Animation = AnimationUtils.loadAnimation(this, R.anim.anim_skeleton)
        binding.skeletonHomeProducts.startAnimation(skeletonAnim)
        initAdapter()
        initLayoutManager()
        // 목 데이터 추가 함수 :
        // initProducts(this)
        clickShoppingCart()
        presenter.setHome()
        presenter.fetchProducts()
        TestServer().getProducts()
    }

    override fun onStart() {
        super.onStart()
        presenter.fetchRecentlyViewed()
    }

    override fun changeSkeletonVisibility() {
        Handler(Looper.getMainLooper()).postDelayed({
            binding.skeletonHomeProducts.clearAnimation()
            binding.skeletonHomeProducts.visibility = View.GONE
            binding.listHomeProducts.visibility = View.VISIBLE
        }, 3000L)
    }

    private fun initAdapter() {
        binding.listHomeProducts.apply { adapter = homeAdapter }
    }

    private fun initLayoutManager() {
        val gridLayoutManager = GridLayoutManager(this, 2)
        gridLayoutManager.spanSizeLookup = GridWeightLookedUp { homeAdapter.getItemViewType(it) }
        binding.listHomeProducts.layoutManager = gridLayoutManager
    }

    override fun setHomeData(homeData: List<HomeData>) {
        homeAdapter.submitList(homeData)
    }

    override fun initRecentlyViewed() {
        homeAdapter.notifyItemInserted(0)
    }

    override fun updateRecentlyViewedProducts(products: List<RecentlyViewedProduct>) {
        recentlyViewedProductAdapter.submitList(products)
    }

    override fun appendProductItems(startPosition: Int, size: Int) {
        homeAdapter.notifyItemRangeInserted(startPosition, size)
    }

    override fun appendShowMoreItem(position: Int) {
        homeAdapter.notifyItemInserted(position)
    }

    override fun removeShowMoreItem(position: Int) {
        homeAdapter.notifyItemRemoved(position)
    }

    private fun clickShoppingCart() {
        binding.layoutHomeShoppingCart.setOnClickListener {
            startActivity(ShoppingCartActivity.getIntent(this))
        }
    }

    override fun clickProduct(productId: Long) {
        startActivity(ProductDetailActivity.getIntent(this, productId))
    }

    private fun clickShowMore() {
        presenter.fetchProducts()
    }

    override fun showUnexpectedError() {
        Toast.makeText(this, getString(R.string.unexpected_error), Toast.LENGTH_SHORT).show()
    }

    override fun updateProductQuantity(position: Int) {
        homeAdapter.notifyItemChanged(position)
        presenter.fetchTotalQuantity()
    }

    override fun updateTotalQuantity(size: Int) {
        if (size == 0) binding.textHomeCartSize.visibility = View.GONE
        if (size == 1) binding.textHomeCartSize.visibility = View.VISIBLE
        binding.textHomeCartSize.text = size.toString()
    }
}
