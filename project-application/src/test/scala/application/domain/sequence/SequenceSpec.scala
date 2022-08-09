package application
package domain.sequence

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class SequenceSpec extends AnyFlatSpec with should.Matchers {
  it should "generate a seq from min and max" in {
    Sequence.generate(0, 5) should equal(Seq(0, 1, 2, 3, 4))
  }
}
