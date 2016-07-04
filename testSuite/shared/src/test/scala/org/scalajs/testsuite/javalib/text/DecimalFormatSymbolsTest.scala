package org.scalajs.testsuite.javalib.text

import java.text.DecimalFormatSymbols
import java.util.Locale

import org.junit.{Before, Ignore, Test}
import org.junit.Assert._
import org.scalajs.testsuite.utils.LocaleTestSetup
import org.scalajs.testsuite.utils.Platform
import org.scalajs.testsuite.utils.AssertThrows.expectThrows

import scala.scalajs.locale.LocaleRegistry
import scala.scalajs.locale.cldr.LDML
import scala.scalajs.locale.cldr.data._

class DecimalFormatSymbolsTest extends LocaleTestSetup {
  // Clean up the locale database, there are different implementations for
  // the JVM and JS
  @Before def cleanup: Unit = super.cleanDatabase

  case class LocaleTestItem(ldml: LDML, tag: String, cldr21: Boolean = false)

  val englishSymbols = List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E")

  val standardLocalesData = List(
    Locale.ROOT                -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.ENGLISH             -> englishSymbols,
    Locale.FRENCH              -> List("0", ",", "\u00A0", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.GERMAN              -> List("0", ",", ".", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.ITALIAN             -> List("0", ",", ".", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.KOREAN              -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.CHINESE             -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.SIMPLIFIED_CHINESE  -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.TRADITIONAL_CHINESE -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "非數值", "-", "E"),
    Locale.FRANCE              -> List("0", ",", "\u00A0", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.GERMANY             -> List("0", ",", ".", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.ITALY               -> List("0", ",", ".", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.KOREA               -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.CHINA               -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.PRC                 -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.TAIWAN              -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "非數值", "-", "E"),
    Locale.UK                  -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.US                  -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.CANADA              -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    Locale.CANADA_FRENCH       -> List("0", ",", "\u00A0", "‰", "%", "#", ";", "∞", "NaN", "-", "E")
  )

  val extraLocalesData = List(
    // af uses latn
    LocaleTestItem(af, "af") -> List("0", ",", "\u00A0", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(az, "az") -> List("0", ",", ".", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(az_Cyrl, "az_Cyrl") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    // bn has a default ns but it is a latn alias
    LocaleTestItem(bn, "bn") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(es_CL, "es_CL") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(fi_FI, "fi_FI") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(it_CH, "it_CH") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(ru_RU, "ru_RU") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(smn_FI, "smn_FI") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(zh, "zh") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"),
    LocaleTestItem(zh_Hant, "zh_Hant") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E")
  )

  // These locales show differences with Java due to a different CLDR version
  val localesDiff = List(
    // fa uses arabext
    LocaleTestItem(fa, "fa", true) -> List("۰", "٫", "٬", "؉", "٪", "#", "؛", "∞", "NaN", "-", "×۱۰^"), // JVM
    LocaleTestItem(fa, "fa") -> List("۰", "٫", "٬", "؉", "٪", "#", "؛", "∞", "ناعدد", "−", "×۱۰^"), // JS
    LocaleTestItem(ka, "ka", true) -> List("0", ",", ".", "‰", "%", "#", ";", "∞", "NaN", "-", "E"), // JVM
    LocaleTestItem(ka, "ka") -> List("0", ",", "\u00A0", "‰", "%", "#", ";", "∞", "არ არის რიცხვი", "-", "E"), // JS
    // ar has a default arab set of symbols
    LocaleTestItem(ar, "ar", true) -> List("٠", "٫", "٬", "؉", "٪", "#", "؛", "∞", "ليس رقم", "\u002D", "اس"), // JVM
    LocaleTestItem(ar, "ar") -> List("٠", "٫", "٬", "؉", "٪", "#", "؛", "∞", "ليس رقم", "\u002D", "اس"), // JS
    LocaleTestItem(lv, "lv", true) -> List("0", ",", "\u00A0", "‰", "%", "#", ";", "∞", "nav skaitlis", "\u2212", "E"), // JVM
    LocaleTestItem(lv, "lv") -> List("0", ",", "\u00A0", "‰", "%", "#", ";", "∞", "nav skaitlis", "-", "E"), // JS
    LocaleTestItem(my, "my", true) -> List("၀", ".", ",", "‰", "%", "#", "၊", "∞", "NaN", "-", "E"), // JVM
    LocaleTestItem(my, "my") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "ဂဏန်းမဟုတ်သော", "-", "E"), // JS
    LocaleTestItem(smn, "smn", true) -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E"), // JVM
    LocaleTestItem(smn, "smn") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "epiloho", "-", "E"), // JS
    LocaleTestItem(ja, "ja", true) -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN（非数）", "-", "E"), // JVM
    LocaleTestItem(ja, "ja") -> List("0", ".", ",", "‰", "%", "#", ";", "∞", "NaN", "-", "E") // JS
  )

  def test_dfs(dfs: DecimalFormatSymbols, symbols: List[String]): Unit = {
    assertEquals(symbols(0).charAt(0), dfs.getZeroDigit)
    assertEquals(symbols(1).charAt(0), dfs.getDecimalSeparator)
    assertEquals(symbols(2).charAt(0), dfs.getGroupingSeparator)
    assertEquals(symbols(3).charAt(0), dfs.getPerMill)
    assertEquals(symbols(4).charAt(0), dfs.getPercent)
    assertEquals(symbols(5).charAt(0), dfs.getDigit)
    assertEquals(symbols(6).charAt(0), dfs.getPatternSeparator)
    assertEquals(symbols(7), dfs.getInfinity)
    assertEquals(symbols(8), dfs.getNaN)
    assertEquals(symbols(9).charAt(0), dfs.getMinusSign)
    assertEquals(symbols(10), dfs.getExponentSeparator)
  }

  @Test def test_default_locales_decimal_format_symbol(): Unit = {
    standardLocalesData.foreach {
      case (l, symbols) =>
        val dfs = DecimalFormatSymbols.getInstance(l)
        test_dfs(dfs, symbols)
    }
  }

  @Test def test_extra_locales_decimal_format_symbol(): Unit = {
    extraLocalesData.foreach {
      case (LocaleTestItem(d, tag, _), symbols) =>
        if (!Platform.executingInJVM) {
          LocaleRegistry.installLocale(d)
        }
        val l = Locale.forLanguageTag(tag)
        val dfs = DecimalFormatSymbols.getInstance(l)
        test_dfs(dfs, symbols)
    }
  }

  // These tests give the same data on CLDR 21
  @Test def test_extra_locales_not_agreeing_decimal_format_symbol(): Unit = {
    localesDiff.foreach {
      case (LocaleTestItem(d, tag, cldr21), symbols) =>
        if (!Platform.executingInJVM) {
          LocaleRegistry.installLocale(d)
        }
        val l = Locale.forLanguageTag(tag)
        val dfs = DecimalFormatSymbols.getInstance(l)
        if (Platform.executingInJVM && cldr21) {
          test_dfs(dfs, symbols)
        }
        if (!Platform.executingInJVM && !cldr21) {
          test_dfs(dfs, symbols)
        }
    }
  }

  @Test def test_available_locales(): Unit = {
    val initial = DecimalFormatSymbols.getAvailableLocales.length
    assertTrue(initial > 0)
    if (!Platform.executingInJVM) {
      LocaleRegistry.installLocale(af)
      // In JS all locales have a decimal format symbols instance
      assertEquals(initial + 1, DecimalFormatSymbols.getAvailableLocales.length)
    }
  }

  @Test def test_defaults(): Unit = {
    val dfs = new DecimalFormatSymbols()
    test_dfs(dfs, englishSymbols)
  }

  @Test def test_setters(): Unit = {
    val dfs = new DecimalFormatSymbols()
    dfs.setZeroDigit('1')
    assertEquals('1', dfs.getZeroDigit)
    dfs.setGroupingSeparator('1')
    assertEquals('1', dfs.getGroupingSeparator)
    dfs.setDecimalSeparator('1')
    assertEquals('1', dfs.getDecimalSeparator)
    dfs.setPerMill('1')
    assertEquals('1', dfs.getPerMill)
    dfs.setPercent('1')
    assertEquals('1', dfs.getPercent)
    dfs.setDigit('1')
    assertEquals('1', dfs.getDigit)
    dfs.setPatternSeparator('1')
    assertEquals('1', dfs.getPatternSeparator)
    dfs.setMinusSign('1')
    assertEquals('1', dfs.getMinusSign)

    dfs.setInfinity(null)
    assertNull(dfs.getInfinity)
    dfs.setInfinity("Inf")
    assertEquals("Inf", dfs.getInfinity)

    dfs.setNaN(null)
    assertNull(dfs.getNaN)
    dfs.setNaN("nan")
    assertEquals("nan", dfs.getNaN)

    expectThrows(classOf[NullPointerException], dfs.setExponentSeparator(null))
    dfs.setExponentSeparator("exp")
    assertEquals("exp", dfs.getExponentSeparator)
  }

  @Test def test_clone(): Unit = {
    val dfs = new DecimalFormatSymbols()
    assertEquals(dfs, dfs.clone())
    assertNotSame(dfs, dfs.clone())
  }

  @Test def test_equals(): Unit = {
    val dfs = new DecimalFormatSymbols()
    assertEquals(dfs, dfs)
    assertSame(dfs, dfs)
    assertFalse(dfs.equals(null))
    assertFalse(dfs.equals(1))
    val dfs2 = new DecimalFormatSymbols()
    assertEquals(dfs, dfs2)
    assertNotSame(dfs, dfs2)
    dfs2.setDigit('i')
    assertFalse(dfs.equals(dfs2))
  }

  @Ignore @Test def test_hash_code(): Unit = {
    val dfs = new DecimalFormatSymbols()
    assertEquals(dfs.hashCode, dfs.hashCode)
    val dfs2 = new DecimalFormatSymbols()
    assertEquals(dfs.hashCode, dfs2.hashCode)
    dfs2.setExponentSeparator("abc")
    // These tests should fail but they pass on the JVM
    assertEquals(dfs.hashCode, dfs2.hashCode)
    standardLocalesData.filter(_._1 != Locale.ROOT).foreach {
      case (l, symbols) =>
        val df = DecimalFormatSymbols.getInstance(l)
        assertFalse(dfs.hashCode.equals(df.hashCode))
    }
  }
}
