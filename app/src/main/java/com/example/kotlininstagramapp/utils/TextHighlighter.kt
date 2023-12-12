package com.example.kotlininstagramapp.utils

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView

object TextHighlighter {
    fun highlightWords(input: String): SpannableString {
        val spannableString = SpannableString(input)
        val words = input.split("\\s+".toRegex()).toTypedArray() // Splitting the text into words

        for (word in words) {
            if (word.startsWith("@") || word.startsWith("#")) {
                val start = input.indexOf(word)
                val end = start + word.length
                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLUE),
                    start,
                    end,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }
        return spannableString
    }

    fun highlightWordsTextView(textView: TextView) {
        val text = textView.text.toString()
        val spannableString = SpannableString(text)
        val words = text.split(" ")

        for (word in words) {
            if (word.startsWith("@") || word.startsWith("#")) {
                val startIndex = text.indexOf(word)
                val endIndex = startIndex + word.length
                spannableString.setSpan(
                    ForegroundColorSpan(Color.BLUE),
                    startIndex,
                    endIndex,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
        }

        textView.text = spannableString
    }
}
