import scala.language.experimental.macros

import scala.collection.mutable
import scala.reflect.macros.blackbox.Context

object AssertMacros {
  // This version of `assert` takes a single expression of type `Boolean`,
  // and expands into an `if` statement containing a `throw`.
  //
  // The macro identifies expressions of the form `a == b` and prints
  // an informative error message. It passes other expressions through.

  def assert(expr: Boolean): Unit =
    macro assertMacro

  def assertMacro(c: Context)(expr: c.Tree) = {
    import c.universe._

    expr match {
      // We use quasiquotes to match on the type of expression in which we are interested:

      case q"$a == $b" =>
        q"""
        val temp1 = $a
        val temp2 = $b
        if(temp1 != temp2) {
          throw new AssertionError(temp1 + " != " + temp2)
        }
        """

      // Other forms are passed through:

      case other =>
        q"""
        if(!$other) {
          throw new AssertionError("assertion failed")
        }
        """
    }
  }
}