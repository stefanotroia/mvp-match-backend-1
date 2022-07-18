package it.stefanotroia.mvpmatch.utils

import java.util.*
import kotlin.reflect.full.declaredMemberProperties

fun Any.isAllNull(): Boolean {
    if(this::class.declaredMemberProperties.any { !it.returnType.isMarkedNullable }) return false
    return this::class.declaredMemberProperties.none { it.getter.call(this) != null }
}

fun String.toUUID(): UUID {
    return UUID.fromString(this)
}