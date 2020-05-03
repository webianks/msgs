package com.webianks.hatkemessenger.utils

import java.util.*

/**
 * @author amulya
 * @datetime 14 Oct 2014, 5:20 PM
 */
class ColorGeneratorModified private constructor(private val mColors: List<Int>) {
    companion object {
        private var DEFAULT: ColorGeneratorModified? = null
        var MATERIAL: ColorGeneratorModified? = null
        private fun create(colorList: List<Int>): ColorGeneratorModified {
            return ColorGeneratorModified(colorList)
        }

        init {
            DEFAULT = create(Arrays.asList(
                    -0xe9c9c,
                    -0xa7aa7,
                    -0x65bc2,
                    -0x1b39d2,
                    -0x98408c,
                    -0xa65d42,
                    -0xdf6c33,
                    -0x529d59,
                    -0x7fa87f
            ))
            MATERIAL = create(Arrays.asList(
                    -0xbbcca,
                    -0x63d850,
                    -0x16e19d,
                    -0x98c549,
                    -0xc0ae4b,
                    -0xde690d,
                    -0xfc560c,
                    -0xff432c,
                    -0xff6978,
                    -0xb350b0,
                    -0x743cb6,
                    -0x6800,
                    -0xa8de,
                    -0x86aab8,
                    -0x616162,
                    -0x9f8275
            ))
        }
    }

    private val mRandom: Random
    val randomColor: Int
        get() = mColors[mRandom.nextInt(mColors.size)]

    fun getColor(key: Any): Int {
        return mColors[Math.abs(key.hashCode()) % mColors.size]
    }

    init {
        mRandom = Random(System.currentTimeMillis())
    }
}