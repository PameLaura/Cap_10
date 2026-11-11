package com.example.cap_10

import android.Manifest
import android.animation.ObjectAnimator
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.TextureView
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.*
import java.net.URI
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private val sections = listOf(
        "View", "ImageView", "WebView", "VideoView", "CalendarView", "TextClock",
        "ProgressBar indeterminado", "ProgressBar horizontal", "SeekBar continuo",
        "SeekBar discreto", "RatingBar", "SearchView", "TextureView y SurfaceView",
        "Separadores", "Práctica integradora"
    )
    private val clips = listOf(
        "https://samplelib.com/lib/preview/mp4/sample-5s.mp4",
        "https://samplelib.com/lib/preview/mp4/sample-10s.mp4"
    )
    private lateinit var content: LinearLayout
    private val cleanup = mutableListOf<() -> Unit>()
    private val handler = Handler(Looper.getMainLooper())
    private val backCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() = show(-1)
    }
    private var section = -1
    private var camera: Camera? = null
    private var preview: TextureView? = null
    private val cameraPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else toast("Permiso de cámara denegado")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        content = findViewById(R.id.contenido)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        onBackPressedDispatcher.addCallback(this, backCallback)
        show(savedInstanceState?.getInt("section") ?: -1)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("section", section)
        super.onSaveInstanceState(outState)
    }

    private fun show(index: Int) {
        clearDemo()
        section = index
        val title = findViewById<TextView>(R.id.titulo)
        for (i in content.childCount - 1 downTo 1) content.removeViewAt(i)
        title.text = if (index < 0) "Capítulo 10 · Palette Widgets" else "${index + 1}. ${sections[index]}"
        findViewById<ScrollView>(R.id.main).scrollTo(0, 0)
        backCallback.isEnabled = index >= 0
        if (index < 0) {
            label("Abre cada sección y prueba el control. En Android Studio, abre activity_main.xml o demo_image.xml en Design para explorar la Palette o en Code para revisar el XML.")
            sections.forEachIndexed { i, name -> button("${i + 1}. $name") { show(i) } }
            return
        }
        button("Volver al índice") { show(-1) }
        when (index) {
            0 -> viewDemo()
            1 -> imageDemo()
            2 -> webDemo()
            3 -> videoDemo()
            4 -> calendarDemo()
            5 -> clockDemo()
            6 -> loadingDemo()
            7 -> progressDemo()
            8 -> seekDemo(false)
            9 -> seekDemo(true)
            10 -> ratingDemo()
            11 -> searchDemo()
            12 -> surfacesDemo()
            13 -> dividersDemo()
            14 -> {
                imageDemo()
                progressDemo()
                seekDemo(false)
                ratingDemo()
                calendarDemo()
                searchDemo()
            }
        }
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()

    private fun label(value: String) = TextView(this).apply {
        text = value
        setPadding(0, dp(8), 0, dp(8))
        content.addView(this)
    }

    private fun button(value: String, action: () -> Unit) = Button(this).apply {
        text = value
        setOnClickListener { action() }
        content.addView(this, -1, -2)
    }

    private fun toast(value: String) = Toast.makeText(this, value, Toast.LENGTH_SHORT).show()

    private fun viewDemo() {
        label("Pulsa el botón para alternar la visibilidad del rectángulo.")
        val block = View(this).apply { setBackgroundColor(0xFF6750A4.toInt()) }
        content.addView(block, -1, dp(72))
        button("Mostrar u ocultar") { block.visibility = if (block.isShown) View.GONE else View.VISIBLE }
    }

    private fun imageDemo() {
        label("Cambia cómo encaja la imagen dentro del marco y toca la imagen.")
        val picture = layoutInflater.inflate(R.layout.demo_image, content, false) as ImageView
        picture.setOnClickListener { toast("Tocaste la imagen") }
        content.addView(picture)
        button("Alternar FIT_CENTER y CENTER_CROP") {
            picture.scaleType = if (picture.scaleType == ImageView.ScaleType.FIT_CENTER) {
                ImageView.ScaleType.CENTER_CROP
            } else {
                ImageView.ScaleType.FIT_CENTER
            }
        }
        val status = label("Imagen local.")
        var request: Thread? = null
        button("Cargar imagen de internet") {
            request?.interrupt()
            status.text = "Cargando imagen…"
            request = Thread {
                val bitmap = runCatching {
                    val connection = URI("https://picsum.photos/320/200").toURL().openConnection()
                    connection.connectTimeout = 5000
                    connection.readTimeout = 5000
                    connection.getInputStream().use { BitmapFactory.decodeStream(it) }
                }.getOrNull()
                val worker = Thread.currentThread()
                handler.post {
                    if (request === worker) {
                        if (bitmap != null) picture.setImageBitmap(bitmap)
                        status.text = if (bitmap != null) "Imagen descargada." else "No se pudo cargar la imagen."
                    }
                }
            }.apply { start() }
        }
        cleanup += {
            request?.interrupt()
            request = null
        }
    }

    private fun webDemo() {
        val status = label("Contenido HTML local listo.")
        val web = WebView(this)
        button("Cargar página de Android Developers") {
            status.text = "Cargando página…"
            web.loadUrl("https://developer.android.com/")
        }
        web.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                if (status.text == "Cargando página…") status.text = "Página cargada."
            }

            override fun onReceivedError(
                view: WebView,
                request: android.webkit.WebResourceRequest,
                error: android.webkit.WebResourceError
            ) {
                if (request.isForMainFrame) status.text = "No se pudo cargar la página."
            }
        }
        content.addView(web, -1, dp(360))
        web.loadDataWithBaseURL(null, "<h2>WebView</h2><p>Este HTML viene incluido en la app.</p>", "text/html", "UTF-8", null)
        cleanup += {
            content.removeView(web)
            web.destroy()
        }
    }

    private fun videoDemo() {
        val status = label("Selecciona un video. Se requiere internet.")
        val video = VideoView(this)
        content.addView(video, -1, dp(220))
        val controller = MediaController(this)
        controller.setAnchorView(video)
        video.setMediaController(controller)
        video.setOnPreparedListener { status.text = "Toca el video para ver los controles." }
        video.setOnErrorListener { _, _, _ ->
            status.text = "No se pudo reproducir el video."
            true
        }
        clips.forEachIndexed { index, url ->
            button("Reproducir video ${index + 1}") {
                status.text = "Cargando video…"
                video.setVideoURI(Uri.parse(url))
                video.start()
            }
        }
        cleanup += {
            controller.hide()
            video.stopPlayback()
        }
    }

    private fun calendarDemo() {
        val date = label("Selecciona una fecha.")
        val calendar = CalendarView(this)
        content.addView(calendar, -1, -2)
        calendar.setOnDateChangeListener { _, year, month, day ->
            date.text = "Fecha seleccionada: $day/${month + 1}/$year"
        }
    }

    private fun clockDemo() {
        for ((name, zone) in listOf("Lima" to "America/Lima", "UTC" to "UTC")) {
            label(name)
            content.addView(TextClock(this).apply {
                timeZone = zone
                format12Hour = "hh:mm:ss a"
                format24Hour = "HH:mm:ss"
                textSize = 28f
            })
        }
    }

    private fun loadingDemo() {
        val status = label("Inicia una espera de dos segundos.")
        val indicator = ProgressBar(this).apply { visibility = View.GONE }
        content.addView(indicator, dp(48), dp(48))
        button("Simular carga") {
            val control = content.getChildAt(content.childCount - 1)
            control.isEnabled = false
            indicator.visibility = View.VISIBLE
            status.text = "Cargando…"
            handler.postDelayed({
                indicator.visibility = View.GONE
                status.text = "Carga completada"
                control.isEnabled = true
            }, 2000)
        }
    }

    private fun progressDemo() {
        val status = label("Progreso: 0%")
        val progress = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        content.addView(progress, -1, -2)
        val animator = ObjectAnimator.ofInt(progress, "progress", 0, 100).apply {
            duration = 2500
            addUpdateListener { status.text = "Progreso: ${it.animatedValue}%" }
        }
        button("Iniciar progreso") { animator.start() }
        cleanup += { animator.cancel() }
    }

    private fun seekDemo(discrete: Boolean) {
        val preferences = getSharedPreferences("widgets", MODE_PRIVATE)
        val key = if (discrete) "nivel" else "volumen"
        val status = label("")
        val slider = SeekBar(this)
        slider.max = if (discrete) 4 else 100
        if (discrete) {
            slider.tickMark = getDrawable(android.R.drawable.presence_invisible)
        }
        slider.progress = preferences.getInt(key, if (discrete) 0 else 50)
        fun update(value: Int) {
            status.text = if (discrete) "Nivel: ${value + 1} de 5" else "Volumen simulado: $value%"
        }
        update(slider.progress)
        content.addView(slider, -1, -2)
        slider.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
                update(progress)
                if (fromUser) preferences.edit().putInt(key, progress).apply()
            }

            override fun onStartTrackingTouch(bar: SeekBar) = Unit
            override fun onStopTrackingTouch(bar: SeekBar) = Unit
        })
    }

    private fun ratingDemo() {
        val preferences = getSharedPreferences("widgets", MODE_PRIVATE)
        val saved = preferences.getFloat("rating", 0f)
        val status = label("Calificación: $saved / 5")
        val stars = RatingBar(this).apply {
            numStars = 5
            stepSize = 0.5f
            rating = saved
        }
        content.addView(stars, -2, -2)
        stars.setOnRatingBarChangeListener { _, rating, fromUser ->
            status.text = "Calificación: $rating / 5"
            if (fromUser) preferences.edit().putFloat("rating", rating).apply()
        }
    }

    private fun searchDemo() {
        val items = listOf("Android Studio", "Kotlin", "Java", "Firebase", "Retrofit", "Glide")
        val search = SearchView(this).apply {
            queryHint = "Busca una tecnología"
            isIconifiedByDefault = false
        }
        content.addView(search, -1, -2)
        val result = ListView(this)
        content.addView(result, -1, dp(260))
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items.toMutableList())
        result.adapter = adapter
        val filter = Runnable {
            val query = search.query.toString().trim()
            adapter.clear()
            adapter.addAll(items.filter { it.contains(query, ignoreCase = true) })
        }
        search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                handler.removeCallbacks(filter)
                filter.run()
                search.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                handler.removeCallbacks(filter)
                handler.postDelayed(filter, 250)
                return true
            }
        })
        result.setOnItemClickListener { _, _, position, _ -> toast("Seleccionaste: ${adapter.getItem(position)}") }
        search.clearFocus()
    }

    private fun surfacesDemo() {
        label("TextureView · Vista previa de la cámara")
        val texture = TextureView(this)
        preview = texture
        content.addView(texture, -1, dp(180))
        texture.surfaceTextureListener = object : TextureView.SurfaceTextureListener {
            override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) = Unit
            override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) = Unit
            override fun onSurfaceTextureUpdated(surface: SurfaceTexture) = Unit
            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                stopCamera()
                return true
            }
        }
        button("Activar cámara") {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                cameraPermission.launch(Manifest.permission.CAMERA)
            }
        }
        cleanup += {
            stopCamera()
            preview = null
        }

        val status = label("SurfaceView · Pulsa reproducir para transmitir un video.")
        val surface = SurfaceView(this)
        content.addView(surface, -1, dp(180))
        var player: MediaPlayer? = null
        fun release() {
            player?.release()
            player = null
        }
        surface.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder) = Unit
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit
            override fun surfaceDestroyed(holder: SurfaceHolder) = release()
        })
        button("Reproducir en SurfaceView") {
            if (surface.holder.surface.isValid) {
                release()
                val media = MediaPlayer()
                player = media
                try {
                    media.setDisplay(surface.holder)
                    media.setDataSource(clips.first())
                    media.setOnPreparedListener {
                        status.text = "Reproduciendo video"
                        it.start()
                    }
                    media.setOnCompletionListener { status.text = "Video terminado" }
                    media.setOnErrorListener { _, _, _ ->
                        status.text = "No se pudo reproducir el video."
                        release()
                        true
                    }
                    status.text = "Cargando video…"
                    media.prepareAsync()
                } catch (error: Exception) {
                    release()
                    status.text = "No se pudo abrir el video."
                }
            }
        }
        cleanup += { release() }
    }

    @Suppress("DEPRECATION")
    private fun startCamera() {
        val texture = preview?.surfaceTexture ?: return
        if (camera != null) return
        try {
            val device = Camera.open()
            camera = device
            val info = Camera.CameraInfo()
            val cameraId = (0 until Camera.getNumberOfCameras()).first { index ->
                Camera.getCameraInfo(index, info)
                info.facing == Camera.CameraInfo.CAMERA_FACING_BACK
            }
            Camera.getCameraInfo(cameraId, info)
            @Suppress("DEPRECATION")
            val rotation = windowManager.defaultDisplay.rotation * 90
            device.setDisplayOrientation((info.orientation - rotation + 360) % 360)
            device.setPreviewTexture(texture)
            device.startPreview()
        } catch (error: Exception) {
            stopCamera()
            toast("Cámara no disponible")
        }
    }

    private fun stopCamera() {
        camera?.release()
        camera = null
    }

    private fun dividersDemo() {
        label("Encabezado")
        content.addView(divider(), -1, dp(1))
        val row = LinearLayout(this).apply { gravity = android.view.Gravity.CENTER_VERTICAL }
        content.addView(row, -1, dp(64))
        row.addView(TextView(this).apply { text = "Columna izquierda" }, LinearLayout.LayoutParams(0, -2, 1f))
        row.addView(divider(), dp(1), -1)
        row.addView(TextView(this).apply {
            text = "Columna derecha"
            setPadding(dp(12), 0, 0, 0)
        }, LinearLayout.LayoutParams(0, -2, 1f))
    }

    private fun divider() = View(this).apply { setBackgroundColor(0xFF888888.toInt()) }

    private fun clearDemo() {
        handler.removeCallbacksAndMessages(null)
        cleanup.forEach { it() }
        cleanup.clear()
    }

    override fun onStop() {
        stopCamera()
        super.onStop()
    }

    override fun onDestroy() {
        clearDemo()
        super.onDestroy()
    }
}


