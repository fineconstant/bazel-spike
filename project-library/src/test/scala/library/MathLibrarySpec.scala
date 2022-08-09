package library

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class MathLibrarySpec extends AnyFlatSpec with should.Matchers {
  it should "add two integers" in {
    val actual = MathLibrary.add(1, 2)
    val expected = 3

    actual shouldBe expected
  }
}
