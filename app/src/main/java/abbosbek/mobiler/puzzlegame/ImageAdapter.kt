package abbosbek.mobiler.puzzlegame

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.loader.content.AsyncTaskLoader
import java.io.IOException

class ImageAdapter(private val mContext : Context) : BaseAdapter() {

    val assetM : AssetManager
    private var files : Array<String> ?= null

    override fun getCount(): Int = files!!.size

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(p0: Int): Long = 0

    @SuppressLint("MissingInflatedId")
    override fun getView(position: Int, view: View?, viewGroup: ViewGroup?): View {

        val v = LayoutInflater.from(mContext).inflate(R.layout.grid_element,null)
        val imageView = v.findViewById<ImageView>(R.id.gridImageView)
        imageView.post {
            object : AsyncTask<Any?, Any?, Any?>(){
                private var bitmap : Bitmap ?= null
                override fun doInBackground(vararg p0: Any?): Any? {
                    bitmap = getPicFromAsset(imageView,files!![position])
                    return null
                }

                override fun onPostExecute(result: Any?) {
                    super.onPostExecute(result)
                    imageView.setImageBitmap(bitmap)
                }
            }.execute()
        }

        return v
    }

    private fun getPicFromAsset(imageView: ImageView?, assetName: String): Bitmap? {
        val targetW = imageView!!.width
        val targetH = imageView!!.height

        return if (targetW == 0 || targetH == 0){
            null
        }else try{
            val `is` = assetM.open("img/$assetName")
            val bmOptions = BitmapFactory.Options()
            bmOptions.inJustDecodeBounds = true
            BitmapFactory.decodeStream(`is`, Rect(-1,-1,-1,-1),bmOptions)

            val photoW = bmOptions.outWidth
            val photoH = bmOptions.outHeight

            val scaleFactor = Math.min(photoW/targetW/photoH,targetH)

            bmOptions.inJustDecodeBounds = false
            bmOptions.inSampleSize = scaleFactor
            bmOptions.inPurgeable = true
            BitmapFactory.decodeStream(`is`, Rect(-1,-1,-1,-1),bmOptions)
        }catch (e : IOException){
            e.printStackTrace()
            null
        }
    }
    init {
        assetM = mContext.assets
        try {
            files = assetM.list("img")
        }catch (e : IOException){
            e.printStackTrace()
        }
    }
}