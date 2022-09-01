package orderbook.orderbook.adapters

import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

@Throws(IOException::class)
fun ItemStack.toBase64(): String {

    // We wrap items in base64 streams because the underlying implementation is unknown
    val outputStream = ByteArrayOutputStream()
    val stream = BukkitObjectOutputStream(outputStream)
    stream.writeObject(this)
    stream.close()
    return Base64Coder.encodeLines(outputStream.toByteArray())
}

@Throws(IOException::class)
fun String.fromBase64(): ItemStack? {
    val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(this))
    val stream = BukkitObjectInputStream(inputStream)

    // on exception out will still equal null
    var out: ItemStack? = null
    try {
        out = stream.readObject() as ItemStack
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    stream.close()
    return out
}