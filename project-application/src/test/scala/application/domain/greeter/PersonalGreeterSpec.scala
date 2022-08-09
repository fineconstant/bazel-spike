package application
package domain.greeter

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should

class PersonalGreeterSpec extends AnyFlatSpec with should.Matchers {
  it should "say hallo with name" in {
    PersonalGreeter.hello("Bazel") shouldEqual "Hello Bazel!"
  }
}
