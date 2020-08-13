package id.zelory.compressor

import android.content.Context
import id.zelory.compressor.constraint.Compression
import id.zelory.compressor.constraint.default
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext

/**
 * Created on : January 22, 2020
 * Author     : zetbaitsu
 * Name       : Zetra
 * GitHub     : https://github.com/zetbaitsu
 */
class Compressor(private val context: Context) {

    suspend fun compress(
            imageFile: File,
            coroutineContext: CoroutineContext = Dispatchers.IO,
            compressionPatch: Compression.() -> Unit = { default() }
    ) = withContext(coroutineContext) {
        val compression = Compression().apply(compressionPatch)
        var result = copyToCache(context, imageFile)
        compression.constraints.forEach { constraint ->
            while (constraint.isSatisfied(result).not()) {
                result = constraint.satisfy(result)
            }
        }
        return@withContext result
    }

    fun compressAsFlowable(imageFile: File,
                           compressionPatch: Compression.() -> Unit = { default() }): Flowable<File> {
        return Flowable.fromCallable {
            val compression = Compression().apply(compressionPatch)
            var result = copyToCache(context, imageFile)
            compression.constraints.forEach { constraint ->
                while (constraint.isSatisfied(result).not()) {
                    result = constraint.satisfy(result)
                }
            }

            return@fromCallable result
        }
    }

    fun compressAsObservable(imageFile: File,
                           compressionPatch: Compression.() -> Unit = { default() }): Observable<File> {
        return Observable.fromCallable {
            val compression = Compression().apply(compressionPatch)
            var result = copyToCache(context, imageFile)
            compression.constraints.forEach { constraint ->
                while (constraint.isSatisfied(result).not()) {
                    result = constraint.satisfy(result)
                }
            }

            return@fromCallable result
        }
    }
}