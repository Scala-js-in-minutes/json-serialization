package scalajs

import scala.scalajs.js
import scala.scalajs.js.{Any, JSON}

package object basics {

  // Parses JSON to an object[T] optionally renaming fields.
  def parseJson[T](input: String, rename: Map[String, String] = Map()): T = {
    def isJsObject(value: js.Dynamic) = value != null && js.typeOf(value) == "object" && !js.Array.isArray(value)

    val renameKeys = (_: Any, value: Any) => {
      val obj = value.asInstanceOf[js.Dynamic]
      if (isJsObject(obj)) {
        for (prop <- js.Object.keys(obj.asInstanceOf[js.Object]) if rename.contains(prop)) {
          val (oldKey, newKey) = (prop, rename(prop))
          obj.updateDynamic(newKey)(obj.selectDynamic(oldKey))
          js.special.delete(value, oldKey)
        }
      }
      value
    }

    JSON.parse(input, renameKeys).asInstanceOf[T]
  }
}
