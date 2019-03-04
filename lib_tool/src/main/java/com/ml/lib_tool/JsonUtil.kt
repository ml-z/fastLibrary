package com.ml.lib_tool

import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONArray
import android.content.ContentValues







class JsonUtil private constructor(){

    companion object {
        val instance by lazy { JsonUtil() }
    }

    private val gson = Gson()


    fun getGson() = gson

    /**
     * 将Json字符串转换成对应对象
     * @param json    Json字符串
     * @param clazz     对应字节码文件.class
     * @return
     */
    fun <T> fromJson(json: String, clazz: Class<T>): T {
        return if (clazz == String::class.java) {
            json  as T
        } else {
            gson.fromJson(json, clazz) as T
        }
    }


    /**
     * 将一个对象转换成一个Json字符串
     * @param t
     * @return
     */
    fun <T> toJson(t: T): String {
        return if (t is String) {
            t.toString()
        } else {
            gson.toJson(t)
        }
    }




    /**
     * 获取json字符串中的值
     * @param json  json字符串
     * @param key   键值
     * @param clazz 所取数据类型，例如：Integer.class，String.class，Double.class，JSONObject.class
     * @return  存在则返回正确值，不存在返回null
     */
    fun <T> getJsonObjectValue(json: String, key: String, clazz: Class<T>): T? {
        try {
            return getJsonObjectValue(JSONObject(json), key, clazz)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return null
    }


    /**
     * 获取jsonObject对象中的值
     * @param jsonObject    jsonObject对象
     * @param key   键值
     * @param clazz 所取数据类型，例如：Integer.class，String.class，Double.class，JSONObject.class
     * @return  存在则返回正确值，不存在返回null
     */
    fun <T> getJsonObjectValue(jsonObject: JSONObject, key: String, clazz: Class<T>): T? {
        var t: T? = null
        try {
            when (clazz) {
                Int::class.java -> t = Integer.valueOf(jsonObject.getInt(key)) as T
                Boolean::class.java -> t = java.lang.Boolean.valueOf(jsonObject.getBoolean(key)) as T
                String::class.java -> t = jsonObject.getString(key).toString() as T
                Double::class.java -> t = java.lang.Double.valueOf(jsonObject.getDouble(key)) as T
                JSONObject::class.java -> t = jsonObject.getJSONObject(key) as T
                JSONArray::class.java -> t = jsonObject.getJSONArray(key) as T
                Long::class.java -> t = java.lang.Long.valueOf(jsonObject.getLong(key)) as T
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return t
    }


    /**
     * json字符串转换为ContentValues
     * @param json  json字符串
     * @return
     */
    fun jsonToContentValues(json: String): ContentValues {
        val contentValues = ContentValues()
        try {
            val jsonObject = JSONObject(json)
            val iterator = jsonObject.keys()
            var key: String
            var value: Any
            while (iterator.hasNext()) {
                key = iterator.next().toString()
                value = jsonObject.get(key)
                val valueString = value.toString()
                when (value) {
                    is String -> contentValues.put(key, valueString)
                    is Int -> contentValues.put(key, Integer.valueOf(valueString))
                    is Long -> contentValues.put(key, java.lang.Long.valueOf(valueString))
                    is Double -> contentValues.put(key, java.lang.Double.valueOf(valueString))
                    is Float -> contentValues.put(key, java.lang.Float.valueOf(valueString))
                    is Boolean -> contentValues.put(key, java.lang.Boolean.valueOf(valueString))
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            throw Error("Json字符串不合法：$json")
        }

        return contentValues
    }

}