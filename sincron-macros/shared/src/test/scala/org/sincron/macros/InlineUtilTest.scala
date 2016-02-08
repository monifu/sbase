/*
 * Copyright (c) 2016 by its authors. Some rights reserved.
 * See the project homepage at: https://sincron.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sincron.macros

import minitest.SimpleTestSuite

import scala.util.control.NonFatal

object InlineUtilTest extends SimpleTestSuite {
  case class DummyException(msg: String) extends RuntimeException(msg)

  test("Inline function with underscore") {
    val box = TestBox(1)
    val mapped = box.map(_ + 1)
    assertEquals(mapped, TestBox(2))
  }

  test("Inline function with underscore and unclean prefix") {
    def box = TestBox(1)
    def mapped = box.map(_ + 1)
    assertEquals(mapped, TestBox(2))
  }

  test("Inline anonymous function") {
    val box = TestBox(1)
    val mapped = box.map(x => x + 1)
    assertEquals(mapped, TestBox(2))
  }

  test("Inline anonymous function with unclean prefix") {
    def box = TestBox(1)
    def mapped = box.map(x => x + 1)
    assertEquals(mapped, TestBox(2))
  }

  test("Inline unclean function") {
    val box = TestBox(1)
    val mapped = box.map {
      def incr = 1
      x: Int => x + incr
    }

    assertEquals(mapped, TestBox(2))
  }

  test("Inline matched partial function") {
    val box = TestBox(1)
    val mapped = box.map { case 1 => 2 }
    assertEquals(mapped, TestBox(2))
  }

  test("Inline unmatched partial function") {
    val box = TestBox(2)
    intercept[MatchError] {
      box.map { case 1 => 2 }
    }
  }

  test("Inline NonFatal clause") {
    val box = TestBox(1)
    val dummy = DummyException("dummy")
    def increment(x: Int): Int = throw dummy

    val mapped = box.map { x =>
      try increment(x) catch {
        case NonFatal(ex) =>
          assertEquals(ex, dummy)
          x + 1
      }
    }

    assertEquals(mapped, TestBox(2))
  }
}
