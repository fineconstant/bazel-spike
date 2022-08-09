package application
package domain.sequence

private[application] object Sequence {
  def generate(min: Int, max: Int) = Range(min, max)
}
