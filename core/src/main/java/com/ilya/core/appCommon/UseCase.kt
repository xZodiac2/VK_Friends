package com.ilya.core.appCommon

interface UseCase<in T, out R> {
    suspend operator fun invoke(data: T): R
}