package com.ilya.core.appCommon

interface Mapper<in T, out R> {
    operator fun invoke(additionalData: T?): R
}