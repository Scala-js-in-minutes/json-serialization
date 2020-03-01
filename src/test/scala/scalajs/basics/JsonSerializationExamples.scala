package scalajs.basics

import org.scalatest.funsuite.AnyFunSuite

import scala.scalajs.js
import scala.scalajs.js.JSON

class JsonSerializationExamples extends AnyFunSuite {

  test("parsing JSON to a nested object hierarchy") {
    val currentTimestamp = System.currentTimeMillis()
    val json = JSON.parse(
      s"""
      {
         "full-name": "Sarek",
         "age": 100,
         "knows-scala": false,
         "knows-kotlin": true,
         "favourite-colours": ["red", "green", "blue"],
         "friends": [
            {
               "full-name": "Spock",
               "age": 40,
               "knows-scala": true,
               "friends": [
                  {
                    "full-name": "James Kirk",
                    "age": 30,
                    "favourite-colours": ["green"],
                    "knows-scala": false
                  }
               ]
            }
         ],
         "record-update-timestamp": "$currentTimestamp"
      }
      """)
    val person = json.asInstanceOf[PersonDto]
    assert(person.`full-name`.contains("Sarek"))
    assert(person.age.contains(100))
    assert(person.`knows-scala`.contains(false))
    assert(person.`knows-kotlin`) // <-- this is not Option-like type but will fail when undefined
    assert(person.`favourite-colours`.toList == List("red", "green", "blue"))
    assert(person.friends.length == 1)
    assert(person.`record-update-timestamp`.contains(currentTimestamp.toString)) //js doesn't handle large integer numbers
    assert(js.isUndefined(person.friends(0).`favourite-colours`))
    assert(person.`full-name` + " knows " + person.friends(0).`full-name` + " who knows " +
      person.friends(0).friends(0).`full-name` == "Sarek knows Spock who knows James Kirk")
  }

  test("javascript undefined values are handled nicely with union types") {
    val person = JSON.parse("""{"age": 100}""").asInstanceOf[PersonDto]
    assert(js.isUndefined(person.`full-name`))
    assert(person.`full-name`.getOrElse("unknown citizen") == "unknown citizen")
    assert(person.age.getOrElse(3333) == 100)

    // but will fail for a field that doesn't assume undefined value
    assertThrows[Throwable] {
      person.`knows-kotlin`
    }
  }

  test("with a small effort it's possible to rename JSON fields") {
    val renamedFields = Map("name" -> "full-name", "years-alive" -> "age")
    val person = parseJson[PersonDto](
      """
        {
          "name": "Zarek",
          "years-alive": 100,
          "friends": [
            {
              "name": "Spock",
              "years-alive": 40
            }
          ]
        }""", renamedFields)
    assert(person.`full-name`.contains("Zarek"))
    assert(person.age.contains(100))
    assert(person.friends(0).`full-name`.contains("Spock"))
    assert(person.friends(0).age.contains(40))
  }

  test("serializing object to JSON") {
    val person = new PersonDto("Nikola Tesla", 86, js.Array(), false, false,
      friends = js.Array(new PersonDto("Mark Twain", 74)))
    val json = JSON.stringify(person)
    assert(json == """{"full-name":"Nikola Tesla","age":86,"favourite-colours":[],"knows-scala":false,"knows-kotlin":false,"friends":[{"full-name":"Mark Twain","age":74,"favourite-colours":[],"knows-scala":false,"knows-kotlin":false,"friends":[],"record-update-timestamp":"0"}],"record-update-timestamp":"0"}""")
  }

  test("parsing JSON to a dictionary") {
    val json = JSON.parse(
      """
        {
          "car": "Bugatti Veyron 16.4 Super Sport",
          "max-speed": 431
        }
        """)

    val dictionary = json.asInstanceOf[js.Dictionary[js.Any]]
    assert(dictionary.keys.size == 2)
    assert(dictionary.get("car").contains("Bugatti Veyron 16.4 Super Sport"))
    assert(dictionary.get("max-speed").contains(431))
  }

  test("dynamic access to JSON properties") {
    val timestamp = System.currentTimeMillis()
    val json = JSON.parse(
      s"""
      {
         "full-name": "Sarek",
         "age": 100,
         "favourite-colours": ["red", "green", "blue"],
         "record-update-timestamp": "$timestamp"
      }
      """)

    // Explicit type conversion is necessary to avoid compiler warnings
    assert(json.`full-name`.asInstanceOf[String] == "Sarek")
    assert(json.age.asInstanceOf[Int] == 100)
    assert(json.`record-update-timestamp`.asInstanceOf[String] == timestamp.toString)

    // When property's name is not known during compilation time then use selectDynamic()
    assert(json.selectDynamic("full-name").asInstanceOf[String] == "Sarek")

    // Below is how to check if property is undefined
    assert(js.isUndefined(json.`unknown-property`))
  }
}
