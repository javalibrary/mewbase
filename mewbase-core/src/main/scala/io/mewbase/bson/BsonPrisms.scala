package io.mewbase.bson

import java.math.BigDecimal

import io.mewbase.bson.syntax._
import monocle.{Lens, Optional, Prism}
import monocle.function.{At, AtFunctions, Index, IndexFunctions}

import scala.{BigDecimal => _}

trait BsonPrisms extends IndexFunctions with AtFunctions {

  private[this] val stringVisitor = new BsonValue.Visitor[Option[String]] {
    override def visit(nullValue: BsonValue.NullBsonValue): Option[String] = None

    override def visit(value: BsonValue.StringBsonValue): Option[String] = Some(value.getValue)

    override def visit(value: BsonValue.BigDecimalBsonValue): Option[String] = None

    override def visit(value: BsonValue.BooleanBsonValue): Option[String] = None

    override def visit(value: BsonValue.BsonObjectBsonValue): Option[String] = None

    override def visit(value: BsonValue.BsonArrayBsonValue): Option[String] = None
  }

  val bsonStringPrism: Prism[BsonValue, String] =
    Prism[BsonValue, String] { value =>
      value.visit(stringVisitor)
    }(BsonValue.of)

  private[this] val numberVisitor = new BsonValue.Visitor[Option[BigDecimal]] {
    override def visit(nullValue: BsonValue.NullBsonValue): Option[BigDecimal] = None

    override def visit(value: BsonValue.StringBsonValue): Option[BigDecimal] = None

    override def visit(value: BsonValue.BigDecimalBsonValue): Option[BigDecimal] = Some(value.getValue)

    override def visit(value: BsonValue.BooleanBsonValue): Option[BigDecimal] = None

    override def visit(value: BsonValue.BsonObjectBsonValue): Option[BigDecimal] = None

    override def visit(value: BsonValue.BsonArrayBsonValue): Option[BigDecimal] = None
  }

  val bsonNumberPrism: Prism[BsonValue, BigDecimal] =
    Prism[BsonValue, BigDecimal] { value =>
      value.visit(numberVisitor)
    }(BsonValue.of)

  private[this] val arrayVisitor = new BsonValue.Visitor[Option[BsonArray]] {
    override def visit(nullValue: BsonValue.NullBsonValue): Option[BsonArray] = None

    override def visit(value: BsonValue.StringBsonValue): Option[BsonArray] = None

    override def visit(value: BsonValue.BigDecimalBsonValue): Option[BsonArray] = None

    override def visit(value: BsonValue.BooleanBsonValue): Option[BsonArray] = None

    override def visit(value: BsonValue.BsonObjectBsonValue): Option[BsonArray] = None

    override def visit(value: BsonValue.BsonArrayBsonValue): Option[BsonArray] = Some(value.getValue)
  }

  val bsonArrayPrism: Prism[BsonValue, BsonArray] =
    Prism[BsonValue, BsonArray] { value =>
      value.visit(arrayVisitor)
    }(BsonValue.of)

  private[this] val objectVisitor = new BsonValue.Visitor[Option[BsonObject]] {
    override def visit(nullValue: BsonValue.NullBsonValue): Option[BsonObject] = None

    override def visit(value: BsonValue.StringBsonValue): Option[BsonObject] = None

    override def visit(value: BsonValue.BigDecimalBsonValue): Option[BsonObject] = None

    override def visit(value: BsonValue.BooleanBsonValue): Option[BsonObject] = None

    override def visit(value: BsonValue.BsonObjectBsonValue): Option[BsonObject] = Some(value.getValue)

    override def visit(value: BsonValue.BsonArrayBsonValue): Option[BsonObject] = None
  }

  val bsonObjectPrism: Prism[BsonValue, BsonObject] =
    Prism[BsonValue, BsonObject] { value =>
      value.visit(objectVisitor)
    }(BsonValue.of)

  implicit val BsonObjectIndex: Index[BsonObject, String, BsonValue] =
    (fieldName: String) =>
      Optional[BsonObject, BsonValue](_.apply(fieldName))(value => in => in.copy().put(fieldName, value))

  implicit val BsonArrayIndex: Index[BsonArray, Int, BsonValue] =
    (index: Int) =>
      Optional[BsonArray, BsonValue](_.apply(index))(value => in => in.copy().set(index, value))


  implicit val BsonObjectAt: At[BsonObject, String, Option[BsonValue]] =
    (fieldName: String) => Lens[BsonObject, Option[BsonValue]](obj =>
      obj(fieldName)
    ) { valueOpt => in =>
        val value = valueOpt.getOrElse(BsonValue.nullValue())
        in.copy().put(fieldName, value)
    }

  implicit val BsonArrayAt: At[BsonArray, Int, Option[BsonValue]] =
    (index: Int) => Lens[BsonArray, Option[BsonValue]] { arr =>
      arr(index)
    } { valueOpt => in =>
        val value = valueOpt.getOrElse(BsonValue.nullValue())
        in.copy().set(index, value)
    }
}

