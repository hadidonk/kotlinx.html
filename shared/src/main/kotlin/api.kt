package html4k

import org.w3c.dom.events.Event
import java.util.LinkedHashSet

public interface TagConsumer<out R> {
    fun onTagStart(tag: Tag)
    fun onTagAttributeChange(tag: Tag, attribute: String, value: String?)
    fun onTagEvent(tag: Tag, event: String, value: (Event) -> Unit)
    fun onTagEnd(tag: Tag)
    fun onTagContent(content: CharSequence)
    fun onTagContentEntity(entity: Entities)
    fun finalize(): R
}

public interface Tag {
    val tagName: String
    val consumer: TagConsumer<*>

    val attributes: MutableMap<String, String>
}

interface AttributeEnum {
    val realValue: String
}

inline fun <T : Tag> T.visit(block: T.() -> Unit) {
    consumer.onTagStart(this)
    this.block()
    consumer.onTagEnd(this)
}

fun Iterable<Pair<String, String?>>.toAttributesMap(): Map<String, String> = filter { it.second != null }.map { it.first to it.second!! }.toMap()

fun <T, C : TagConsumer<T>, TAG : Tag> C.build(attributes: Map<String, String>, builder: (Map<String, String>, TagConsumer<T>, TAG.() -> Unit) -> Unit, block: TAG.() -> Unit): C {
    builder(attributes, this, block)
    return this
}

fun Map<*, *>.isNotEmpty(): Boolean = !isEmpty()
private val emptyMap: Map<String, String> = emptyMap()
private val String.realValue: String
    get() = this