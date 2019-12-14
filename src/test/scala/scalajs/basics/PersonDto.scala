package scalajs.basics

import scala.scalajs.js
import scala.scalajs.js._

class PersonDto
(
  val `full-name`: String | Unit,
  val age: Int | Unit,
  val `favourite-colours`: js.Array[String] = js.Array(),
  val `knows-scala`: Boolean | Unit = false,
  val `knows-kotlin`: Boolean = false,
  val friends: js.Array[PersonDto] = js.Array(),
  val `record-update-timestamp`: String = "0"
) extends js.Object
