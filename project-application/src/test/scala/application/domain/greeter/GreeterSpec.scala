package application
package domain.greeter

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class GreeterSpec extends AnyFlatSpec with should.Matchers {
  it should "say hello" in {
    Greeter.hello shouldEqual "Hello World!"
  }
}
