package com.example.easycrypto.core.data.networking

import com.example.easycrypto.BuildConfig

fun constructUrl(url : String) :String{
    var newUrl : String = BuildConfig.BASE_URL
    if(url.contains(newUrl)){
        return url;
    }
    else if(url.startsWith("/")){
        newUrl = newUrl + url.drop(1)
    }
    else{
        newUrl = newUrl + url
    }
    return newUrl + "?apiKey=" + BuildConfig.API_KEY

}