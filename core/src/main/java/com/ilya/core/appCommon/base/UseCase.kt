package com.ilya.core.appCommon.base

interface UseCase<in T, out R> {
    suspend operator fun invoke(data: T): R
}