import sbt.Compile
import sbt.Keys.compile
import wartremover.Wart

object  WartRemoverSettings {

  lazy val wartRemoverWarning = {
    val warningWarts = Seq(
      Wart.JavaSerializable,
      Wart.StringPlusAny,
      Wart.AsInstanceOf,
      Wart.IsInstanceOf
      // Wart.Any
    )
    wartremover.WartRemover.autoImport.wartremoverWarnings in(Compile, compile) ++= warningWarts
  }
  lazy val wartRemoverError = {
    // Error
    val errorWarts = Seq(
      Wart.ArrayEquals,
      Wart.AnyVal,
      Wart.EitherProjectionPartial,
      Wart.Enumeration,
      Wart.ExplicitImplicitTypes,
      Wart.FinalVal,
      Wart.JavaConversions,
      Wart.JavaSerializable,
      Wart.LeakingSealed,
      Wart.MutableDataStructures,
      Wart.Null,
      Wart.OptionPartial,
      Wart.Recursion,
      Wart.Return,
      Wart.TryPartial,
      Wart.Var,
      Wart.While)

    wartremover.WartRemover.autoImport.wartremoverErrors in(Compile, compile) ++= errorWarts
  }
}
