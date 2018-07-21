package org.afrikcode.hackathon.utils

import java.util.regex.Pattern

object Validator {

    fun validatePhone(phoneNumber: String): Boolean {
        val regex = "^\\+?[0-9. ()-]{10,25}$"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(phoneNumber)

        return matcher.matches()
    }

    fun validateEmail(email: String): Boolean {
        val EMAIL_REGEX = "^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$"
        val pattern = Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE)
        val matcher = pattern.matcher(email)

        return matcher.matches()
    }


}
