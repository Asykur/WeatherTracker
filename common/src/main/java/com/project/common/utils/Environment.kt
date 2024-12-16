package com.project.common.utils

import com.project.common.network.UrlInterface

class Environment: UrlInterface {
    companion object {
        init {
            System.loadLibrary("securekeys")
        }
    }

    private external fun baseUrl(): String
    private external fun apiKey(): String
    private external fun certPinning(): String

    override val baseUrl = baseUrl()
    override val apiKey = apiKey()
    override val certPinning = certPinning()

}