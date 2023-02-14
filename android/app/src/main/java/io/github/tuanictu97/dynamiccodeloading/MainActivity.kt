package io.github.tuanictu97.dynamiccodeloading

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import dalvik.system.DexClassLoader
import dalvik.system.InMemoryDexClassLoader
import dalvik.system.PathClassLoader
import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.File
import java.net.URL
import java.net.URLConnection
import java.nio.ByteBuffer

class MainActivity : AppCompatActivity() {
    private lateinit var loader: DexClassLoader
    private lateinit var pathLoader: PathClassLoader
    private lateinit var buffer: ByteArray
    private lateinit var btBuffer: ByteBuffer
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // DexClassLoader
        try {
            loader = classLoadDexFile(DEX_NAME)
        }catch (e: Exception) {
            // show log when load file error
        }

        // PathClassLoader
        pathLoader = pathCl(DEX_NAME)
        val loadClassPath = pathLoader.loadClass("io.github.tuanictu97.codedynamic")
        val checkMethodPath = loadClassPath.getMethod("getDemoCodeLoading")
        val clInPath = loadClassPath.newInstance()

        // InMemoryDexClassLoader
        btBuffer = ByteBuffer.wrap(buffer)
        val lder = InMemoryDexClassLoader(btBuffer, this.javaClass.classLoader)
        val mt = lder.loadClass("io.github.tuanictu97.codedynamic")
        val checkMethodInMemory = mt.getMethod("getDemoCodeLoading")
        val newcl = mt.newInstance()

        Log.d("Result", checkMethodInMemory.invoke(newcl)!!.toString())
    }

    private fun classLoadDexFile(dexFileName: String): DexClassLoader {
        // Create a dex dir to hold the DEX file to be loaded
        val dexFile: File = File.createTempFile("pref", ".dex")
        val inStr: ByteArrayInputStream =
            ByteArrayInputStream(baseContext.assets.open(dexFileName).readBytes())
        inStr.use { input ->
            dexFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return DexClassLoader(
            dexFile.absolutePath,
            null,
            null,
            javaClass.classLoader
        )
    }

    private fun downloadFile(url: String) {
        val thread = Thread {
            try {
                val u = URL(url)
                val conn: URLConnection = u.openConnection()
                val contentLength: Int = conn.getContentLength()
                val stream = DataInputStream(u.openStream())
                buffer = ByteArray(contentLength)
                stream.readFully(buffer)
                stream.close()
                Log.d("seccheck", "Success of download to buffer")
            } catch (e: Exception) {
                Log.e("seccheck", e.message!!)
            }
        }
        thread.start()
    }

    private fun pathCl(filename: String): PathClassLoader {
        // Create a dex dir to hold the DEX file to be loaded
        val dexFile: File = File.createTempFile("pref", ".dex")
        val inStr: ByteArrayInputStream =
            ByteArrayInputStream(baseContext.assets.open(filename).readBytes())
        inStr.use { input ->
            dexFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return PathClassLoader(dexFile.absolutePath, javaClass.classLoader)
    }
    companion object {
        // todo: currently, we use local server to demo
        const val BASE_URL_DEX_FILE = "http://10.0.2.2:8080/"
        const val DEX_NAME = "classes3.dex"
        const val DEX_VERSION_CODE = 1
    }
}