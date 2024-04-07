package com.ilya.core.appCommon

interface BaseFactory<in T, out R> {
    fun newInstance(initializationData: T): R
}