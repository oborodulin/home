package com.oborodulin.home.common.util

import android.content.Context
import android.content.res.XmlResourceParser
import org.xmlpull.v1.XmlPullParser
import timber.log.Timber

private const val TAG = "ResourceUtils"

class ResourceUtils {
    companion object {
        /**
         * https://techhelpnotes.com/android-creating-hashmap-map-from-xml-resources/
         */
        fun getHashMapResource(c: Context, hashMapResId: Int): Map<String, String>? {
            var map: MutableMap<String, String>? = null
            val parser: XmlResourceParser = c.resources.getXml(hashMapResId)

            var key: String? = null
            var value: String? = null
            try {
                var eventType: Int = parser.eventType

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_DOCUMENT -> Timber.tag(TAG).d("Start document")
                        XmlPullParser.START_TAG -> {
                            if (parser.name.equals("map")) {
                                map = if (parser.getAttributeBooleanValue(null, "linked", false)) {
                                    LinkedHashMap()
                                } else {
                                    HashMap()
                                }
                            } else if (parser.name.equals("entry")) {
                                key = parser.getAttributeValue(null, "key")
                                if (null == key) {
                                    parser.close()
                                    return null
                                }
                            }
                        }
                        XmlPullParser.END_TAG ->
                            if (parser.name.equals("entry")) {
                                map?.put(key!!, value!!)
                                key = null
                                value = null
                            }
                        XmlPullParser.TEXT -> key?.let { value = parser.text }
                    }
                    eventType = parser.next()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
            return map
        }
    }
}