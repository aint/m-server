package ua.softgroup.matrix.server

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
  * @author Oleksandr Tyshkovets <sg.olexander@gmail.com> 
  */
object Utils {

  implicit class StringImprovements(val str: String) {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    def parseToDate: LocalDate = LocalDate.parse(str, formatter)

  }

  implicit class DateImprovements(val date: LocalDate) {

    def validateEndRangeDate: LocalDate = if (date.isAfter(LocalDate.now)) LocalDate.now.plusDays(1) else date.plusDays(1)

  }

  def calculateIdlePercent(workSeconds: Int, idleSeconds: Int): Double =
    if (workSeconds != 0) idleSeconds.toDouble / workSeconds * 100 else 0.0

}
