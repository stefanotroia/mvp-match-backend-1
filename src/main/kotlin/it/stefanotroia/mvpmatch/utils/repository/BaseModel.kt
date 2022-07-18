package it.stefanotroia.mvpmatch.utils.repository

import java.time.LocalDateTime
import java.util.*
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField

open class BaseModel(
    @NotUpdate
    open var id: UUID? = null,
    @NotUpdate
    open var createdAt: LocalDateTime? = null,
    @NotUpdate
    open var updatedAt: LocalDateTime? = null,
): Cloneable {

    public override fun clone(): Any {
        return super.clone()
    }

    fun update(b: BaseModel) {
        BaseModel::class.memberProperties
            .filter { it.javaField?.getAnnotation(NotUpdate::class.java) == null }
            .forEach { property ->
                if (property is KMutableProperty<*>)
                    property.setter.call(this, property.get(b))
            }
    }

}
