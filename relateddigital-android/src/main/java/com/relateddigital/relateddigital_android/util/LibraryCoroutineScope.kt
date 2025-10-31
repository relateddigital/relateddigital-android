package com.relateddigital.relateddigital_android.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

/**
 * Kütüphanenin genelinde kullanılacak, yaşam döngüsünden bağımsız,
 * uygulama çapında bir CoroutineScope.
 * Arka plan işlemleri (DataStore, Network vb.) için kullanılır.
 */
object LibraryCoroutineScope {
    /**
     * SupervisorJob(): Bu scope'taki bir coroutine hata verirse diğerlerini etkilemez.
     * Dispatchers.IO: Disk ve network işlemleri için optimize edilmiştir.
     */
    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
}