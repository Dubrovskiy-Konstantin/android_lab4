package com.example.ball

import java.io.Serializable

class Player(var Name : String, var Rate : Float?, var Games : Int?) : Comparable<Player>, Serializable {
    init {
        Name = Name.replace('_', ' ')
    }

    override fun compareTo(other: Player): Int {
        return when {
            this.Rate == null -> 1
            other.Rate == null -> -1
            this.Rate!! > other.Rate!! -> -1
            this.Rate!! < other.Rate!! -> 1
            else -> 0
        }
    }

    override fun toString(): String {
        val maxlen = 36 // pattern "__________________ -- ______ --- ___" (36 chars)
        val parsedString = StringBuilder(Name)
        parsedString.append(' ')
        while (parsedString.length < 21)
            parsedString.append('_')
        parsedString.append(' ')
        parsedString.append(Rate.toString())
        parsedString.append(' ')
        while (parsedString.length < 32)
            parsedString.append('_')
        parsedString.append(' ')
        parsedString.append(Games.toString())
        parsedString.append(' ')
        while (parsedString.length < maxlen)
            parsedString.append('_')
        return parsedString.toString()
    }
}