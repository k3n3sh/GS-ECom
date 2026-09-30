package com.k3n3sh.gsecom.data.repository

import com.k3n3sh.gsecom.data.network.ConnectivityChecker
import com.k3n3sh.gsecom.data.remote.ProductApi
import com.k3n3sh.gsecom.data.remote.dto.toDomain
import com.k3n3sh.gsecom.domain.model.DataError
import com.k3n3sh.gsecom.domain.model.Outcome
import com.k3n3sh.gsecom.domain.model.Product
import com.k3n3sh.gsecom.domain.repository.ProductRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

// Singleton, one shared cache
// TODO: Room for offline
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val connectivityChecker: ConnectivityChecker,
) : ProductRepository {

    // One request at a time
    private val mutex = Mutex()
    private var cachedProducts: List<Product>? = null

    override suspend fun getProducts(forceRefresh: Boolean): Outcome<List<Product>> {
        return mutex.withLock {
            val cached = cachedProducts
            if (cached != null && !forceRefresh) {
                Outcome.Success(cached)
            } else {
                fetchProducts()
            }
        }
    }

    override suspend fun getProduct(id: Long): Outcome<Product> {
        return when (val outcome = getProducts(forceRefresh = false)) {
            is Outcome.Success -> {
                val product = outcome.data.firstOrNull { it.id == id }
                if (product != null) Outcome.Success(product) else Outcome.Failure(DataError.NotFound)
            }
            is Outcome.Failure -> outcome
        }
    }

    private suspend fun fetchProducts(): Outcome<List<Product>> {
        if (!connectivityChecker.isOnline()) {
            return Outcome.Failure(DataError.NoInternet)
        }
        return try {
            val products = api.getProducts().hits.mapNotNull { it.toDomain() }
            cachedProducts = products
            Outcome.Success(products)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            Outcome.Failure(DataError.Network)
        } catch (e: HttpException) {
            Outcome.Failure(DataError.Server(e.code()))
        } catch (e: SerializationException) {
            Outcome.Failure(DataError.InvalidData)
        } catch (e: Exception) {
            Outcome.Failure(DataError.Unknown)
        }
    }
}
